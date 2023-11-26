package com.hmellema.smithy.traitcodegen.integrations.core;

import com.hmellema.smithy.traitcodegen.SymbolProperties;
import com.hmellema.smithy.traitcodegen.utils.SymbolUtil;
import com.hmellema.smithy.traitcodegen.writer.TraitCodegenWriter;
import com.hmellema.smithy.traitcodegen.writer.sections.BuilderClassSection;
import com.hmellema.smithy.traitcodegen.writer.sections.BuilderSection;
import software.amazon.smithy.codegen.core.Symbol;
import software.amazon.smithy.model.shapes.*;
import software.amazon.smithy.model.traits.AbstractTraitBuilder;
import software.amazon.smithy.model.traits.StringListTrait;
import software.amazon.smithy.utils.BuilderRef;
import software.amazon.smithy.utils.CodeInterceptor;
import software.amazon.smithy.utils.SmithyBuilder;
import software.amazon.smithy.utils.StringUtils;

import java.util.Optional;

public class BuilderSectionInterceptor implements CodeInterceptor<BuilderSection, TraitCodegenWriter> {
    private static final String VALUES = "values";
    private static final String ACCESSOR_TEMPLATE = "public Builder $1L($2T $1L) {";
    private static final String RETURN_THIS = "return this;";
    private static final String BUILDER_REF_TEMPLATE = "private final BuilderRef<$T> $L = BuilderRef.$L;";


    @Override
    public Class<BuilderSection> sectionType() {
        return BuilderSection.class;
    }

    @Override
    public void write(TraitCodegenWriter writer, String previousText, BuilderSection section) {
        writer.pushState(BuilderClassSection.fromBuilderSection(section));
        writer.openDocstring();
        writer.writeDocStringContents("Builder for {@link $T}.", section.symbol());
        writer.closeDocstring();

        String builderClassTemplate = "public static final class Builder ";
        if (SymbolUtil.isTrait(section.shape())) {
            if (SymbolUtil.isStringListTrait(section.shape(), section.symbolProvider())) {
                writer.addImport(StringListTrait.class);
                builderClassTemplate += "extends StringListTrait.Builder<$T, Builder> {";
            } else {
                writer.addImport(AbstractTraitBuilder.class);
                builderClassTemplate += "extends AbstractTraitBuilder<$T, Builder> {";
            }
        } else {
            writer.addImport(SmithyBuilder.class);
            builderClassTemplate += "implements SmithyBuilder<$T> {";
        }
        writer.openBlock(builderClassTemplate, "}", section.symbol(), () -> {
            section.shape().accept(new BuilderPropertyGenerator(writer, section));
            writer.newLine();
            writer.write("private Builder() {}").newLine();
            section.shape().accept(new BuilderSetterGenerator(writer, section));
            writer.newLine();
            writer.override();
            writer.openBlock("public $T build() {", "}", section.symbol(),
                    () -> writeBuildMethodBody(writer, section));
        });
        writer.popState();
    }

    private void writeBuildMethodBody(TraitCodegenWriter writer, BuilderSection section) {
        if (SymbolUtil.isStringListTrait(section.shape(), section.symbolProvider())) {
            writer.write("return new $T(getValues(), getSourceLocation());", section.symbol());
        } else {
            writer.write("return new $T(this);", section.symbol());
        }
    }

    private static final class BuilderPropertyGenerator extends ShapeVisitor.Default<Void> {
        private final TraitCodegenWriter writer;
        private final BuilderSection section;

        private BuilderPropertyGenerator(TraitCodegenWriter writer, BuilderSection section) {
            this.writer = writer;
            this.section = section;
        }

        @Override
        protected Void getDefault(Shape shape) {
            throw new UnsupportedOperationException("Does not support shape of type: " + shape.getType());
        }

        @Override
        public Void listShape(ListShape shape) {
            if (SymbolUtil.isStringListTrait(section.shape(), section.symbolProvider())) {
                // Don't write any builder properties for StringListTraits. They inherit all properties
                return null;
            }
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
            Optional<String> builderRefOptional = section.symbolProvider().toSymbol(shape).getProperty(SymbolProperties.BUILDER_REF_INITIALIZER, String.class);
            if (builderRefOptional.isPresent()) {
                writer.addImport(BuilderRef.class);
                writer.write(BUILDER_REF_TEMPLATE, section.symbolProvider().toSymbol(shape),
                        SymbolUtil.toMemberNameOrValues(shape, section.model(), section.symbolProvider()),
                        builderRefOptional.orElseThrow());
            } else {
                writer.write("private $T $L;", section.symbolProvider().toSymbol(shape),
                        SymbolUtil.toMemberNameOrValues(shape, section.model(), section.symbolProvider()));
            }
        }

        private void writeValuesProperty(Shape shape) {
            Symbol collectionSymbol = section.symbolProvider().toSymbol(shape);
            writer.addImport(BuilderRef.class);
            writer.write(BUILDER_REF_TEMPLATE, collectionSymbol, VALUES,
                    collectionSymbol.expectProperty(SymbolProperties.BUILDER_REF_INITIALIZER));
        }
    }


    private static final class BuilderSetterGenerator extends ShapeVisitor.Default<Void> {
        private final TraitCodegenWriter writer;
        private final BuilderSection section;

        private BuilderSetterGenerator(TraitCodegenWriter writer, BuilderSection section) {
            this.writer = writer;
            this.section = section;
        }

        @Override
        protected Void getDefault(Shape shape) {
            throw new UnsupportedOperationException("Does not support shape of type: " + shape.getType());
        }

        @Override
        public Void listShape(ListShape shape) {
            shape.accept(new SetterVisitor(writer, section, VALUES));
            return null;
        }

        @Override
        public Void mapShape(MapShape shape) {
            shape.accept(new SetterVisitor(writer, section, VALUES));
            return null;
        }

        @Override
        public Void structureShape(StructureShape shape) {
            shape.members().forEach(memberShape -> memberShape.accept(new SetterVisitor(writer, section, section.symbolProvider().toMemberName(memberShape))));
            writer.newLine();
            return null;
        }
    }


    private static final class SetterVisitor extends ShapeVisitor.Default<Void> {
        private final TraitCodegenWriter writer;
        private final BuilderSection section;
        private final String memberName;

        private SetterVisitor(TraitCodegenWriter writer, BuilderSection section,  String memberName) {
            this.writer = writer;
            this.memberName = memberName;
            this.section = section;
        }

        @Override
        protected Void getDefault(Shape shape) {
            writeStandardAccessors(shape);
            return null;
        }

        @Override
        public Void listShape(ListShape shape) {
            if (SymbolUtil.isStringListTrait(section.shape(), section.symbolProvider())) {
                // Don't write any builder setters for StringListTraits. They inherit setters
                return null;
            }
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
            return section.model().expectShape(shape.getTarget()).accept(this);
        }

        private void writeStandardAccessors(Shape shape) {
            writer.openBlock(ACCESSOR_TEMPLATE, "}",
                    memberName, section.symbolProvider().toSymbol(shape), () -> {
                        writer.write("this.$1L = $1L;", memberName);
                        writer.write(RETURN_THIS);
                    });
            writer.newLine();
        }

        private void writeListAccessors(ListShape listShape) {
            writer.openBlock(ACCESSOR_TEMPLATE, "}",
                    memberName, section.symbolProvider().toSymbol(listShape), () -> {
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
                    StringUtils.capitalize(memberName), section.symbolProvider().toSymbol(listShape.getMember()), memberName, () -> {
                        writer.write("$1L.get().add($1LItem);", memberName);
                        writer.write(RETURN_THIS);
                    });
            writer.newLine();

            // Remove one
            writer.openBlock("public Builder remove$LItem($T $LItem) {", "}",
                    StringUtils.capitalize(memberName), section.symbolProvider().toSymbol(listShape.getMember()), memberName, () -> {
                        writer.write("$1L.get().remove($1LItem);", memberName);
                        writer.write(RETURN_THIS);
                    });
        }

        private void writeMapAccessors(MapShape mapShape) {
            // Set all
            writer.openBlock(ACCESSOR_TEMPLATE, "}",
                    memberName, section.symbolProvider().toSymbol(mapShape), () -> {
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
                    StringUtils.capitalize(memberName), section.symbolProvider().toSymbol(keyShape), section.symbolProvider().toSymbol(valueShape), () -> {
                        writer.write("this.$L.get().put(key, value);", memberName);
                        writer.write(RETURN_THIS);
                    });
            writer.newLine();

            // Remove one
            writer.openBlock("public Builder remove$L($T $L) {", "}",
                    StringUtils.capitalize(memberName), section.symbolProvider().toSymbol(keyShape), memberName, () -> {
                        writer.write("this.$1L.get().remove($1L);", memberName);
                        writer.write(RETURN_THIS);
                    });
            writer.newLine();
        }
    }
}
