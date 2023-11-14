package com.hmellema.smithy.traitcodegen.generators;

import com.hmellema.smithy.traitcodegen.directives.GenerateTraitDirective;
import com.hmellema.smithy.traitcodegen.writer.TraitCodegenWriter;
import software.amazon.smithy.codegen.core.Symbol;
import software.amazon.smithy.model.FromSourceLocation;
import software.amazon.smithy.model.SourceLocation;
import software.amazon.smithy.model.shapes.IntEnumShape;
import software.amazon.smithy.utils.StringUtils;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class IntEnumTraitGenerator extends NumberTraitGenerator {
    @Override
    protected void writeAdditionalMethods(TraitCodegenWriter writer, GenerateTraitDirective directive) {
        super.writeAdditionalMethods(writer, directive);
        IntEnumShape shape = directive.shape().asIntEnumShape().orElseThrow(() -> new RuntimeException("oops"));
        for (String memberKey : shape.getEnumValues().keySet()) {
            writer.openBlock("public boolean is$L() {", "}", getMethodName(memberKey),
                            () -> writer.write("return $L.equals(getValue());", memberKey))
                    .writeInline("\n");
        }
    }

    @Override
    protected void writeConstructors(TraitCodegenWriter writer, GenerateTraitDirective directive) {
        writeConstructor(writer, directive.symbol());
        writeConstructorWithSourceLocation(writer, directive.symbol());
    }

    private void writeConstructorWithSourceLocation(TraitCodegenWriter writer, Symbol symbol) {
        writer.addImport(FromSourceLocation.class);
        writer.openBlock("public $T(Integer value, FromSourceLocation sourceLocation) {", "}", symbol, () -> {
            writer.write("super(ID, sourceLocation);");
            writer.write("this.value = value;");
        }).writeInline("\n");
    }

    private void writeConstructor(TraitCodegenWriter writer, Symbol symbol) {
        writer.addImport(SourceLocation.class);
        writer.openBlock("public $T(Integer value) {", "}", symbol, () -> {
            writer.write("super(ID, SourceLocation.NONE);");
            writer.write("this.value = value;");
        }).writeInline("\n");
    }

    @Override
    protected void writeAdditionalProperties(TraitCodegenWriter writer, GenerateTraitDirective directive) {
        writer.write("private final Integer value;").writeInline("\n");

        IntEnumShape shape = directive.shape().asIntEnumShape().orElseThrow(() -> new RuntimeException("oops"));
        for (Map.Entry<String, Integer> memberEntry : shape.getEnumValues().entrySet()) {
            writer.write("public static final Integer $L = $L;", memberEntry.getKey(), memberEntry.getValue());
        }
    }

    @Override
    protected void writeGetters(TraitCodegenWriter writer, GenerateTraitDirective directive) {
        writer.openBlock("public Integer getValue() {", "}",
                () -> writer.write("return value;")).writeInline("\n");
    }


    private String getMethodName(String enumValue) {
        return Arrays.stream(enumValue.split("_"))
                .map(String::toLowerCase)
                .map(StringUtils::capitalize)
                .collect(Collectors.joining());
    }
}
