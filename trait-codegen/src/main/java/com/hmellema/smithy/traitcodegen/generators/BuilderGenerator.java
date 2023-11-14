package com.hmellema.smithy.traitcodegen.generators;

import com.hmellema.smithy.traitcodegen.SymbolUtil;
import com.hmellema.smithy.traitcodegen.writer.TraitCodegenWriter;
import software.amazon.smithy.codegen.core.Symbol;
import software.amazon.smithy.codegen.core.SymbolProvider;
import software.amazon.smithy.model.Model;
import software.amazon.smithy.model.shapes.*;
import software.amazon.smithy.model.traits.AbstractTraitBuilder;
import software.amazon.smithy.utils.BuilderRef;
import software.amazon.smithy.utils.SmithyBuilder;
import software.amazon.smithy.utils.StringUtils;
import software.amazon.smithy.utils.ToSmithyBuilder;

import java.util.EnumSet;
import java.util.Iterator;

// TODO: StringListTrait.Builder<
final class BuilderGenerator implements Runnable {
    private static final String BUILDER_METHOD_TEMPLATE = "public static final Builder builder() {";
    private static final String ACCESSOR_TEMPLATE = "public Builder $1L($2T $1L) {";
    private static final String RETURN_THIS = "return this;";
    private static final EnumSet<ShapeType> BUILDER_REF_REQUIRED_TYPES = EnumSet.of(ShapeType.LIST, ShapeType.MAP);

    private final StructureShape shape;
    private final Model model;
    private final Symbol symbol;
    private final SymbolProvider symbolProvider;
    private final TraitCodegenWriter writer;
    private final boolean isTrait;

    public BuilderGenerator(StructureShape shape, Model model, Symbol symbol, SymbolProvider symbolProvider, TraitCodegenWriter writer, boolean isTrait) {
        this.shape = shape;
        this.model = model;
        this.symbol = symbol;
        this.symbolProvider = symbolProvider;
        this.writer = writer;
        this.isTrait = isTrait;
    }

    public void createConstructorWithBuilder() {
        writer.openBlock("private $T(Builder builder) {", "}", symbol, () -> {
            if (isTrait) {
                writer.write("super(ID, builder.getSourceLocation());");
            }

            for (MemberShape member : shape.members()) {
               if (member.isRequired()) {
                   writer.addImport(SmithyBuilder.class);
                   writer.write("this.$1L = SmithyBuilder.requiredState($1S, $2L);", symbolProvider.toMemberName(member), getBuilderValue(member));
               } else {
                   writer.write("this.$L = $L;", symbolProvider.toMemberName(member), getBuilderValue(member));
               }
            }
        });
        writer.write("");
    }

    private String getBuilderValue(MemberShape member) {
        if (needsBuilderRef(member)) {
            return writer.format("builder.$L.copy()", symbolProvider.toMemberName(member));
        } else {
            return writer.format("builder.$L", symbolProvider.toMemberName(member));
        }
    }

    public void createToBuilderMethod() {
        writer.addImport(ToSmithyBuilder.class);
        writer.write("@Override");
        writer.openBlock("public SmithyBuilder<$T> toBuilder() {", "}", symbol, () -> {
            writer.writeInline("return builder()");
            writer.indent();
            if (isTrait) {
                writer.write(".sourceLocation(getSourceLocation())");
            }
            Iterator<MemberShape> memberIterator = shape.members().iterator();
            while (memberIterator.hasNext()) {
                MemberShape member = memberIterator.next();
                writer.writeInline(".$1L($1L)", symbolProvider.toMemberName(member));
                if (memberIterator.hasNext()) {
                    writer.writeInline("\n");
                } else {
                    writer.writeInline(";\n");
                }
            }
            writer.dedent();
        });
        writer.write("");
    }

    @Override
    public void run() {
        // Write builder method
        writer.openBlock(BUILDER_METHOD_TEMPLATE, "}", () -> writer.write("return new Builder();"));
        writer.write("");
        // Write builder class
        String builderClassTemplate = "public static final class Builder ";
        if (isTrait) {
            writer.addImport(AbstractTraitBuilder.class);
            builderClassTemplate += "extends AbstractTraitBuilder<$T, Builder> {";
        } else {
            writer.addImport(SmithyBuilder.class);
            builderClassTemplate += "implements SmithyBuilder<$T> {";
        }
        writer.openBlock(builderClassTemplate, "}", symbol, () -> {
            writeBuilderConstructor();
            writeBuilderProperties();
            writeBuildMethod();
            writeBuilderSetters();
        });
    }

    private void writeBuilderConstructor() {
        writer.write("private Builder() {}");
        writer.write("");
    }

    private void writeBuilderSetters() {
        for (MemberShape member: shape.members()) {
            if (needsBuilderRef(member)) {
                ShapeType type = model.expectShape(member.getTarget()).getType();
                if (type == ShapeType.LIST) {
                    writeListAccessors(member);
                } else if (type == ShapeType.MAP) {
                    writeMapAccessors(member);
                }
            } else {
                writeStandardAccessors(member);
            }
        }
        writer.write("");
    }

    private void writeStandardAccessors(MemberShape member) {
        writer.openBlock(ACCESSOR_TEMPLATE, "}",
                symbolProvider.toMemberName(member), symbolProvider.toSymbol(member), () -> {
                    writer.write("this.$1L = $1L;", symbolProvider.toMemberName(member));
                    writer.write(RETURN_THIS);
                });
        writer.write("");
    }

    private void writeMapAccessors(MemberShape memberShape) {
        String memberName = symbolProvider.toMemberName(memberShape);
        MapShape mapShape = model.expectShape(memberShape.getTarget()).asMapShape()
                .orElseThrow(() -> new RuntimeException("Expected member shape"));

        // Set all
        writer.openBlock(ACCESSOR_TEMPLATE, "}",
                memberName, symbolProvider.toSymbol(memberShape), () -> {
                    writer.write("clear$L()", StringUtils.capitalize(memberName));
                    writer.write("this.$1L.get().putAll($1L);", memberName);
                    writer.write(RETURN_THIS);
                });
        writer.write("");

        // Clear all
        writer.openBlock("public Builder clear$L() {", "}", StringUtils.capitalize(memberName), () -> {
            writer.write("$L.get().clear();", memberName);
            writer.write(RETURN_THIS);
        });
        writer.write("");

        // Set one
        MemberShape keyShape = mapShape.getKey();
        MemberShape valueShape = mapShape.getValue();
        writer.openBlock("public Builder put$L($T $L, $T $L) {", "}",
                StringUtils.capitalize(memberName), symbolProvider.toSymbol(keyShape), symbolProvider.toMemberName(keyShape),
                symbolProvider.toSymbol(valueShape), symbolProvider.toMemberName(valueShape), () -> {
                    writer.write("$L.get().put($L, $L);", memberName, symbolProvider.toMemberName(keyShape), symbolProvider.toMemberName(valueShape));
                    writer.write(RETURN_THIS);
                });
        writer.write("");

        // Remove one
        writer.openBlock("public Builder remove$L($T $L) {", "}",
                StringUtils.capitalize(memberName), symbolProvider.toSymbol(keyShape), symbolProvider.toMemberName(keyShape), () -> {
                    writer.write("$1L.get().remove($1L);", symbolProvider.toMemberName(keyShape));
                    writer.write(RETURN_THIS);
                });
        writer.write("");
    }

    private void writeListAccessors(MemberShape memberShape) {
        String memberName = symbolProvider.toMemberName(memberShape);
        ListShape listShape = model.expectShape(memberShape.getTarget()).asListShape()
                .orElseThrow(() -> new RuntimeException("Expected list shape."));

        // Set all
        writer.openBlock(ACCESSOR_TEMPLATE, "}",
                memberName, symbolProvider.toSymbol(memberShape), () -> {
                    writer.write("clear$L()", StringUtils.capitalize(memberName));
                    writer.write("this.$1L.get().addAll($1L);", memberName);
                    writer.write(RETURN_THIS);
                });
        writer.write("");

        // Clear all
        writer.openBlock("public Builder clear$L() {", "}",
                StringUtils.capitalize(memberName), () -> {
                    writer.write("$L.get().clear();", memberName);
                    writer.write(RETURN_THIS);
                });
        writer.write("");

        MemberShape listTarget = listShape.getMember();
        // Set one
        writer.openBlock("public Builder add$LItem($T $LItem) {", "}",
                StringUtils.capitalize(memberName), symbolProvider.toSymbol(listTarget), memberName, () -> {
                    writer.write("$1L.get().add($1LItem);", memberName);
                    writer.write(RETURN_THIS);
                });
        writer.write("");

        // Remove one
        writer.openBlock("public Builder remove$LItem($T $LItem) {", "}",
                StringUtils.capitalize(memberName), symbolProvider.toSymbol(listTarget), memberName, () -> {
                    writer.write("$1L.get().remove($1LItem);", memberName);
                    writer.write(RETURN_THIS);
                });
        writer.write("");

    }

    private void writeBuildMethod() {
        writer.write("@Override");
        writer.openBlock("public $T build() {", "}", symbol,
                () -> writer.write("return new $T(this);", symbol));
        writer.write("");
    }

    private boolean needsBuilderRef(MemberShape member) {
        return BUILDER_REF_REQUIRED_TYPES.contains(model.expectShape(member.getTarget()).getType());
    }

    private void writeBuilderProperties() {
        for (MemberShape member: shape.members()) {
            if (needsBuilderRef(member)) {
                ShapeType type = model.expectShape(member.getTarget()).getType();
                writer.addImport(BuilderRef.class);
                String builderRefInitializer = null;
                if (type == ShapeType.LIST) {
                    builderRefInitializer = "BuilderRef.forList();";
                } else if (type == ShapeType.MAP) {
                    builderRefInitializer = "BuilderRef.forOrderedMap();";
                }
                writer.write("private final BuilderRef<$T> $L = $L;", symbolProvider.toSymbol(member), member.getMemberName(), builderRefInitializer);
            } else {
                writer.write("private $T $L;", symbolProvider.toSymbol(member), member.getMemberName());
            }
        }
        writer.write("");
    }
}
