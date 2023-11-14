package com.hmellema.smithy.traitcodegen.generators;

import com.hmellema.smithy.traitcodegen.directives.GenerateTraitDirective;
import com.hmellema.smithy.traitcodegen.writer.TraitCodegenWriter;
import software.amazon.smithy.model.shapes.EnumShape;
import software.amazon.smithy.utils.StringUtils;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public final class EnumTraitGenerator extends StringTraitGenerator {
    @Override
    protected void writeAdditionalMethods(TraitCodegenWriter writer, GenerateTraitDirective directive) {
        EnumShape shape = directive.shape().asEnumShape().orElseThrow(() -> new RuntimeException("oops"));
        for (String memberKey : shape.getEnumValues().keySet()) {
            writer.openBlock("public boolean is$L() {", "}", getMethodName(memberKey),
                            () -> writer.write("return $L.equals(getValue());", memberKey))
                    .writeInline("\n");
        }
    }

    @Override
    protected void writeAdditionalProperties(TraitCodegenWriter writer, GenerateTraitDirective directive) {
        EnumShape shape = directive.shape().asEnumShape().orElseThrow(() -> new RuntimeException("oops"));
        for (Map.Entry<String, String> memberEntry : shape.getEnumValues().entrySet()) {
            writer.write("public static final String $L = $S;", memberEntry.getKey(), memberEntry.getValue());
        }
    }

    private String getMethodName(String enumValue) {
        return Arrays.stream(enumValue.split("_"))
                .map(String::toLowerCase)
                .map(StringUtils::capitalize)
                .collect(Collectors.joining());
    }
}
