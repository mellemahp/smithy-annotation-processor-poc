package com.hmellema.smithy.traitcodegen.generators.common.builder;

import com.hmellema.smithy.traitcodegen.SymbolProperties;
import com.hmellema.smithy.traitcodegen.utils.SymbolUtil;
import com.hmellema.smithy.traitcodegen.writer.TraitCodegenWriter;
import com.hmellema.smithy.traitcodegen.writer.sections.BuilderClassSection;
import com.hmellema.smithy.traitcodegen.writer.sections.BuilderMethodSection;
import software.amazon.smithy.codegen.core.Symbol;
import software.amazon.smithy.codegen.core.SymbolProvider;
import software.amazon.smithy.model.Model;
import software.amazon.smithy.model.shapes.*;
import software.amazon.smithy.model.traits.AbstractTraitBuilder;
import software.amazon.smithy.model.traits.TraitDefinition;
import software.amazon.smithy.utils.BuilderRef;
import software.amazon.smithy.utils.SmithyBuilder;
import software.amazon.smithy.utils.StringUtils;

import java.util.Optional;

// TODO: add sections
// TODO: StringListTrait.Builder<
public final class BuilderGenerator implements Runnable {
    private static final String VALUES = "values";
    private static final String BUILDER_METHOD_TEMPLATE = "public static final Builder builder() {";
    private static final String ACCESSOR_TEMPLATE = "public Builder $1L($2T $1L) {";
    private static final String RETURN_THIS = "return this;";
    private static final String BUILDER_REF_TEMPLATE = "private final BuilderRef<$T> $L = BuilderRef.$L;";

    private final Shape shape;
    private final Model model;
    private final Symbol symbol;
    private final SymbolProvider symbolProvider;
    private final TraitCodegenWriter writer;
    private final boolean isTrait;

    public BuilderGenerator(Shape shape, Model model, Symbol symbol, SymbolProvider symbolProvider, TraitCodegenWriter writer) {
        this.shape = shape;
        this.model = model;
        this.symbol = symbol;
        this.symbolProvider = symbolProvider;
        this.writer = writer;
        this.isTrait = shape.hasTrait(TraitDefinition.class);
    }


    @Override
    public void run() {
        // Write builder method
        writer.pushState(new BuilderMethodSection(symbol));
        writer.openBlock(BUILDER_METHOD_TEMPLATE, "}", () -> writer.write("return new Builder();"));
        writer.newLine();
        writer.popState();

        // Write builder class
        writer.pushState(new BuilderClassSection(symbol));
        String builderClassTemplate = "public static final class Builder ";
        if (isTrait) {
            writer.addImport(AbstractTraitBuilder.class);
            builderClassTemplate += "extends AbstractTraitBuilder<$T, Builder> {";
        } else {
            writer.addImport(SmithyBuilder.class);
            builderClassTemplate += "implements SmithyBuilder<$T> {";
        }
        writer.openBlock(builderClassTemplate, "}", symbol, () -> {
            // Write properties
            shape.accept(new BuilderPropertyGenerator());
            writer.newLine();

            // Create private constructor
            writer.write("private Builder() {}").newLine();

            // Write setters
            shape.accept(new BuilderSetterGenerator());
            writer.newLine();

            // create build method
            writer.write("@Override");
            writer.openBlock("public $T build() {", "}", symbol,
                    () -> writer.write("return new $T(this);", symbol));
        });
        writer.popState();
    }

    private final class BuilderSetterGenerator extends ShapeVisitor.Default<Void> {
        @Override
        protected Void getDefault(Shape shape) {
            throw new UnsupportedOperationException("Does not support shape of type: " + shape.getType());
        }

        @Override
        public Void listShape(ListShape shape) {
            shape.accept(new SetterVisitor(VALUES));
            return null;
        }

        @Override
        public Void mapShape(MapShape shape) {
            shape.accept(new SetterVisitor(VALUES));
            return null;
        }

        @Override
        public Void structureShape(StructureShape shape) {
            shape.members().forEach(memberShape -> memberShape.accept(new SetterVisitor(symbolProvider.toMemberName(memberShape))));
            writer.newLine();
            return null;
        }
    }

    private final class BuilderPropertyGenerator extends ShapeVisitor.Default<Void> {
        @Override
        protected Void getDefault(Shape shape) {
            throw new UnsupportedOperationException("Does not support shape of type: " + shape.getType());
        }

        @Override
        public Void listShape(ListShape shape) {
            writeValuesProperty(shape);
            return null;
        }

        @Override
        public Void mapShape(MapShape shape) {
            writeValuesProperty(shape);
            return null;
        }

        @Override
        public Void structureShape(StructureShape shape) {
            shape.members().forEach(this::writeProperty);
            return null;
        }

        private void writeProperty(MemberShape shape) {
            Optional<String> builderRefOptional = symbolProvider.toSymbol(shape).getProperty(SymbolProperties.BUILDER_REF_INITIALIZER, String.class);
            if (builderRefOptional.isPresent()) {
                writer.addImport(BuilderRef.class);
                writer.write(BUILDER_REF_TEMPLATE, symbolProvider.toSymbol(shape),
                        SymbolUtil.toMemberNameOrValues(shape, model, symbolProvider),
                        builderRefOptional.orElseThrow());
            } else {
                writer.write("private $T $L;", symbolProvider.toSymbol(shape),
                        SymbolUtil.toMemberNameOrValues(shape, model, symbolProvider));
            }
        }

        private void writeValuesProperty(Shape shape) {
            Symbol collectionSymbol = symbolProvider.toSymbol(shape);
            writer.addImport(BuilderRef.class);
            writer.write(BUILDER_REF_TEMPLATE, collectionSymbol, VALUES,
                    collectionSymbol.expectProperty(SymbolProperties.BUILDER_REF_INITIALIZER));
        }
    }

    private final class SetterVisitor extends ShapeVisitor.Default<Void> {
        private final String memberName;

        private SetterVisitor(String memberName) {
            this.memberName = memberName;
        }

        @Override
        protected Void getDefault(Shape shape) {
            writeStandardAccessors(shape);
            return null;
        }

        @Override
        public Void listShape(ListShape shape) {
            writeListAccessors(shape);
            return null;
        }

        @Override
        public Void mapShape(MapShape shape) {
            writeMapAccessors(shape);
            return null;
        }

        @Override
        public Void memberShape(MemberShape shape) {
            return model.expectShape(shape.getTarget()).accept(this);
        }

        private void writeStandardAccessors(Shape shape) {
            writer.openBlock(ACCESSOR_TEMPLATE, "}",
                    memberName, symbolProvider.toSymbol(shape), () -> {
                        writer.write("this.$1L = $1L;", memberName);
                        writer.write(RETURN_THIS);
                    });
            writer.newLine();
        }

        private void writeListAccessors(ListShape listShape) {
            writer.openBlock(ACCESSOR_TEMPLATE, "}",
                    memberName, symbolProvider.toSymbol(listShape), () -> {
                        writer.write("clear$L();", StringUtils.capitalize(memberName));
                        writer.write("this.$1L.get().addAll($1L);", memberName);
                        writer.write(RETURN_THIS);
                    });
            writer.newLine();

            // Clear all
            writer.openBlock("public Builder clear$L() {", "}",
                    StringUtils.capitalize(memberName), () -> {
                        writer.write("$L.get().clear();", memberName);
                        writer.write(RETURN_THIS);
                    });
            writer.newLine();

            // Set one
            writer.openBlock("public Builder add$LItem($T $LItem) {", "}",
                    StringUtils.capitalize(memberName), symbolProvider.toSymbol(listShape.getMember()), memberName, () -> {
                        writer.write("$1L.get().add($1LItem);", memberName);
                        writer.write(RETURN_THIS);
                    });
            writer.newLine();

            // Remove one
            writer.openBlock("public Builder remove$LItem($T $LItem) {", "}",
                    StringUtils.capitalize(memberName), symbolProvider.toSymbol(listShape.getMember()), memberName, () -> {
                        writer.write("$1L.get().remove($1LItem);", memberName);
                        writer.write(RETURN_THIS);
                    });
        }

        private void writeMapAccessors(MapShape mapShape) {
            // Set all
            writer.openBlock(ACCESSOR_TEMPLATE, "}",
                    memberName, symbolProvider.toSymbol(mapShape), () -> {
                        writer.write("clear$L();", StringUtils.capitalize(memberName));
                        writer.write("this.$1L.get().putAll($1L);", memberName);
                        writer.write(RETURN_THIS);
                    });
            writer.newLine();

            // Clear all
            writer.openBlock("public Builder clear$L() {", "}", StringUtils.capitalize(memberName), () -> {
                writer.write("this.$L.get().clear();", memberName);
                writer.write(RETURN_THIS);
            });
            writer.newLine();

            // Set one
            MemberShape keyShape = mapShape.getKey();
            MemberShape valueShape = mapShape.getValue();
            writer.openBlock("public Builder put$L($T key, $T value) {", "}",
                    StringUtils.capitalize(memberName), symbolProvider.toSymbol(keyShape), symbolProvider.toSymbol(valueShape), () -> {
                        writer.write("this.$L.get().put(key, value);", memberName);
                        writer.write(RETURN_THIS);
                    });
            writer.newLine();

            // Remove one
            writer.openBlock("public Builder remove$L($T $L) {", "}",
                    StringUtils.capitalize(memberName), symbolProvider.toSymbol(keyShape), memberName, () -> {
                        writer.write("this.$1L.get().remove($1L);", memberName);
                        writer.write(RETURN_THIS);
                    });
            writer.newLine();
        }
    }
}
