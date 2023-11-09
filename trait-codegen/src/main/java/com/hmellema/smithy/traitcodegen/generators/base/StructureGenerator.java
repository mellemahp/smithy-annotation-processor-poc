package com.hmellema.smithy.traitcodegen.generators.base;

import com.hmellema.smithy.traitcodegen.TraitCodegenContext;
import com.hmellema.smithy.traitcodegen.TraitCodegenSettings;
import com.hmellema.smithy.traitcodegen.writer.TraitCodegenWriter;
import com.hmellema.smithy.traitcodegen.writer.sections.ClassSection;
import software.amazon.smithy.codegen.core.Symbol;
import software.amazon.smithy.codegen.core.SymbolProvider;
import software.amazon.smithy.codegen.core.directed.GenerateStructureDirective;
import software.amazon.smithy.model.Model;
import software.amazon.smithy.model.shapes.*;
import software.amazon.smithy.utils.BuilderRef;
import software.amazon.smithy.utils.SmithyBuilder;
import software.amazon.smithy.utils.StringUtils;

import java.nio.file.Path;
import java.util.EnumSet;
import java.util.function.Consumer;

public class StructureGenerator implements Consumer<GenerateStructureDirective<TraitCodegenContext, TraitCodegenSettings>> {
    private static final String BASE_CLASS_TEMPLATE_STRING = "public final class $T {";
    private static final String BUILDER_METHOD_TEMPLATE = "public static final Builder builder() {";
    private static final String BUILDER_CLASS_TEMPLATE = "public static final class Builder implements SmithyBuilder<$T> {";
    private static final String PROPERTY_TEMPLATE = "private final $T $L;";
    private static final EnumSet<ShapeType> BUILDER_REF_REQUIRED_TYPES = EnumSet.of(ShapeType.LIST, ShapeType.MAP);

    @Override
    public void accept(GenerateStructureDirective<TraitCodegenContext, TraitCodegenSettings> directive) {
        directive.context().writerDelegator().useShapeWriter(directive.shape(), writer -> {
            writer.pushState(new ClassSection(directive.shape()))
                .openBlock(BASE_CLASS_TEMPLATE_STRING, "}", directive.symbol(), () -> {
                    writeProperties(directive.shape(), directive.symbolProvider(), writer);
                    writeConstructor(directive.shape(), directive.symbol(), directive.model(), directive.symbolProvider(), writer);
                    writeGetters(directive.shape(),  directive.symbolProvider(), writer);
                    // TODO: Should these trait sub-structures have setters by default?
                    //writeSetters(directive.shape(), directive.symbolProvider(), writer);
                    writerBuilder(directive.shape(), directive.model(), directive.symbol(), directive.symbolProvider(), writer);
                })
                .popState();
        });
    }

    // TODO split out into its own generator
    private void writerBuilder(StructureShape shape, Model model, Symbol symbol, SymbolProvider symbolProvider, TraitCodegenWriter writer) {
        writer.addImport(SmithyBuilder.class);

        // Write builder method
        writer.openBlock(BUILDER_METHOD_TEMPLATE, "}", () -> writer.write("return new Builder();"));
        writer.write("");
        // Write builder class
        writer.openBlock(BUILDER_CLASS_TEMPLATE, "}", symbol, () -> {
            writeBuilderProperties(shape, model, symbolProvider, writer);
            writeBuildMethod(writer, symbol);
            writeBuilderSetters(writer, model, symbolProvider, symbol, shape);
        });
    }

    private void writeBuilderSetters(TraitCodegenWriter writer, Model model, SymbolProvider provider, Symbol symbol, StructureShape shape) {
        for (MemberShape member: shape.members()) {
            if (needsBuilderRef(member, model)) {
                ShapeType type = model.expectShape(member.getTarget()).getType();
                if (type == ShapeType.LIST) {
                    writeListAccessors(writer, model, provider, member);
                } else if (type == ShapeType.MAP) {
                    writeMapAccessors(writer, model, provider, member);
                }
            } else {
                writeStandardAccessors(writer, provider, member);
            }
        }
        writer.write("");
    }

    private void writeStandardAccessors(TraitCodegenWriter writer, SymbolProvider provider, MemberShape member) {
        writer.openBlock("public Builder $1L($2T $1L) {", "}",
                provider.toMemberName(member), provider.toSymbol(member), () -> {
            writer.write("this.$1L = $1L;", provider.toMemberName(member));
            writer.write("return this;");
        });
        writer.write("");
    }

    private void writeMapAccessors(TraitCodegenWriter writer, Model model, SymbolProvider provider, MemberShape memberShape) {
        String memberName = provider.toMemberName(memberShape);
        // TODO: add error
        MapShape mapShape = model.expectShape(memberShape.getTarget()).asMapShape().get();

        // Set all
        writer.openBlock("public Builder $1L($2T $1L) {", "}",
                memberName, provider.toSymbol(memberShape), () -> {
                    writer.write("clear$L()", StringUtils.capitalize(memberName));
                    writer.write("this.$1L.get().putAll($1L);", memberName);
                    writer.write("return this;");
                });
        writer.write("");

        // Clear all
        writer.openBlock("public Builder clear$L() {", "}", StringUtils.capitalize(memberName), () -> {
            writer.write("$L.get().clear();", memberName);
            writer.write("return this;");
        });
        writer.write("");

        // Set one
        MemberShape keyShape = mapShape.getKey();
        MemberShape valueShape = mapShape.getValue();
        writer.openBlock("public Builder put$L($T $L, $T $L) {", "}",
                StringUtils.capitalize(memberName), provider.toSymbol(keyShape), provider.toMemberName(keyShape),
                provider.toSymbol(valueShape), provider.toMemberName(valueShape), () -> {
                    writer.write("$L.get().put($L, $L);", memberName, provider.toMemberName(keyShape), provider.toMemberName(valueShape));
                    writer.write("return this;");
                });
        writer.write("");

        // Remove one
        writer.openBlock("public Builder remove$L($T $L) {", "}",
                StringUtils.capitalize(memberName), provider.toSymbol(keyShape), provider.toMemberName(keyShape), () -> {
                    writer.write("$1L.get().remove($1L);", provider.toMemberName(keyShape));
                    writer.write("return this;");
                });
        writer.write("");
    }

    private void writeListAccessors(TraitCodegenWriter writer, Model model,  SymbolProvider provider, MemberShape memberShape) {
        String memberName = provider.toMemberName(memberShape);
        // TODO: add exception
        ListShape listShape = model.expectShape(memberShape.getTarget()).asListShape().get();

        // Set all
        writer.openBlock("public Builder $1L($2T $1L) {", "}",
                memberName, provider.toSymbol(memberShape), () -> {
                writer.write("clear$L()", StringUtils.capitalize(memberName));
                writer.write("this.$1L.get().addAll($1L);", memberName);
                writer.write("return this;");
        });
        writer.write("");

        // Clear all
        writer.openBlock("public Builder clear$L() {", "}",
                StringUtils.capitalize(memberName), () -> {
            writer.write("$L.get().clear();", memberName);
            writer.write("return this;");
        });
        writer.write("");

        // TODO: wont handleMostPlurals?
        String singularName = StringUtils.stripEnd(memberName, "s");
        MemberShape listTarget = listShape.getMember();
        // Set one
        writer.openBlock("public Builder add$L($T $L) {", "}",
                StringUtils.capitalize(singularName), provider.toSymbol(listTarget), singularName, () -> {
                    writer.write("$1L.get().add($1L);", singularName);
                    writer.write("return this;");
        });
        writer.write("");

        // Remove one
        writer.openBlock("public Builder remove$L($T $L) {", "}",
                StringUtils.capitalize(singularName), provider.toSymbol(listTarget), singularName, () -> {
                    writer.write("$1L.get().remove($1L);", singularName);
                    writer.write("return this;");
                });
        writer.write("");

    }

    private void writeBuildMethod(TraitCodegenWriter writer, Symbol symbol) {
        writer.write("@Override");
        writer.openBlock("public $T build() {", "}", symbol,
                () -> writer.write("return new $T(this);", symbol));
        writer.write("");
    }

    private void writeProperties(StructureShape shape, SymbolProvider provider, TraitCodegenWriter writer) {
        for (MemberShape member: shape.members()) {
            writer.write(PROPERTY_TEMPLATE, provider.toSymbol(member), member.getMemberName());
        }
        writer.write("");
    }

    private void writeBuilderProperties(StructureShape shape, Model model, SymbolProvider provider, TraitCodegenWriter writer) {
        for (MemberShape member: shape.members()) {
            if (needsBuilderRef(member, model)) {
                ShapeType type = model.expectShape(member.getTarget()).getType();
                writer.addImport(BuilderRef.class);
                String builderRefInitializer = null;
                if (type == ShapeType.LIST) {
                    builderRefInitializer = "BuilderRef.forList();";
                } else if (type == ShapeType.MAP) {
                    builderRefInitializer = "BuilderRef.forOrderedMap();";
                }
                writer.write("private final BuilderRef<$T> $L = $L;", provider.toSymbol(member), member.getMemberName(), builderRefInitializer);
            } else {
                writer.write("private $T $L;", provider.toSymbol(member), member.getMemberName());
            }
        }
        writer.write("");
    }

    // TODO: check for required properties
    private void writeConstructor(StructureShape shape, Symbol symbol, Model model, SymbolProvider symbolProvider, TraitCodegenWriter writer) {
        writer.openBlock("private $T(Builder builder) {", "}", symbol, () -> {
            for (MemberShape member : shape.members()) {
                if (needsBuilderRef(member, model)) {
                    writer.write("this.$1L = builder.$1L.copy();", symbolProvider.toMemberName(member));
                } else {
                    writer.write("this.$1L = builder.$1L;", symbolProvider.toMemberName(member));
                }
            }
        });
        writer.write("");
    }

    private boolean needsBuilderRef(MemberShape member, Model model) {
        return BUILDER_REF_REQUIRED_TYPES.contains(model.expectShape(member.getTarget()).getType());
    }

    private void writeGetters(StructureShape shape, SymbolProvider symbolProvider, TraitCodegenWriter writer) {
        for (MemberShape member : shape.members()) {
            writer.openBlock("public $T get$L() {","}",
                    symbolProvider.toSymbol(member), StringUtils.capitalize(symbolProvider.toMemberName(member)),
                    () -> writer.write("return $L;", symbolProvider.toMemberName(member)));
            writer.write("");
        }
    }

    private void writeSetters(StructureShape shape, SymbolProvider symbolProvider, TraitCodegenWriter writer) {
        for (MemberShape member : shape.members()) {
            writer.openBlock("public void set$L($T $L) {", "}",
                        StringUtils.capitalize(symbolProvider.toMemberName(member)),
                        symbolProvider.toSymbol(member),
                        symbolProvider.toMemberName(member),
                    () -> writer.write("this.$1L = $1L;", symbolProvider.toMemberName(member)));
            writer.write("");
        }
    }
}
