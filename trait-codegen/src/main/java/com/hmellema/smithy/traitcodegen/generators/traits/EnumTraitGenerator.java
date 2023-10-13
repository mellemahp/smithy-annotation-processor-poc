package com.hmellema.smithy.traitcodegen.generators.traits;

import com.hmellema.smithy.traitcodegen.directives.GenerateTraitDirective;
import com.hmellema.smithy.traitcodegen.writer.TraitCodegenWriter;
import software.amazon.smithy.model.shapes.EnumShape;
import software.amazon.smithy.utils.StringUtils;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class EnumTraitGenerator extends StringTraitGenerator {
    @Override
    protected void writeAdditionalMethods(TraitCodegenWriter writer, GenerateTraitDirective directive) {
        EnumShape shape = directive.shape().asEnumShape().orElseThrow(() -> new RuntimeException("oops"));
        for (Map.Entry<String, String> memberEntry : shape.getEnumValues().entrySet()) {
            String methodName = getMethodName(memberEntry.getKey());
            writer.openBlock("public boolean $L() {", "}", methodName,
                    () -> writer.write("return getValue().equals($S);", memberEntry.getValue()))
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
        String camelCasedValue = Arrays.stream(enumValue.split("_"))
                .map(String::toLowerCase)
                .map(StringUtils::capitalize)
                .collect(Collectors.joining());
        return "is" + camelCasedValue;
    }
}
