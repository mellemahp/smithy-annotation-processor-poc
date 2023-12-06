package com.hmellema.smithy.traitcodegen.generators.traits;

import com.hmellema.smithy.traitcodegen.GenerateTraitDirective;
import com.hmellema.smithy.traitcodegen.writer.TraitCodegenWriter;
import software.amazon.smithy.model.shapes.EnumShape;
import software.amazon.smithy.utils.StringUtils;

import java.util.Arrays;
import java.util.stream.Collectors;

public final class EnumTraitGenerator extends StringTraitGenerator {
    @Override
    protected void writeTraitBody(TraitCodegenWriter writer, GenerateTraitDirective directive) {
        super.writeTraitBody(writer, directive);
        EnumShape shape = directive.shape().asEnumShape().orElseThrow(RuntimeException::new);
        for (String memberKey : shape.getEnumValues().keySet()) {
            writer.openBlock("public boolean is$L() {", "}", getMethodName(memberKey),
                            () -> writer.write("return $L.equals(getValue());", memberKey))
                    .writeInline("\n");
        }
    }

    private String getMethodName(String enumValue) {
        return Arrays.stream(enumValue.split("_"))
                .map(String::toLowerCase)
                .map(StringUtils::capitalize)
                .collect(Collectors.joining());
    }
}
