package com.hmellema.smithy.traitcodegen.generators.traits;

import com.hmellema.smithy.traitcodegen.directives.GenerateTraitDirective;
import com.hmellema.smithy.traitcodegen.generators.common.GetterGenerator;
import com.hmellema.smithy.traitcodegen.generators.common.ToNodeGenerator;
import com.hmellema.smithy.traitcodegen.writer.TraitCodegenWriter;
import software.amazon.smithy.codegen.core.Symbol;
import software.amazon.smithy.model.FromSourceLocation;
import software.amazon.smithy.model.SourceLocation;
import software.amazon.smithy.model.shapes.IntEnumShape;
import software.amazon.smithy.utils.StringUtils;

import java.util.Arrays;
import java.util.stream.Collectors;

// Should this even extend number any more?
public final class IntEnumTraitGenerator extends NumberTraitGenerator {
    @Override
    protected void writeTraitBody(TraitCodegenWriter writer, GenerateTraitDirective directive) {
        writeConstructor(writer, directive.traitSymbol());
        writeConstructorWithSourceLocation(writer, directive.traitSymbol());
        new ToNodeGenerator(writer, directive.shape(), directive.symbolProvider(), directive.model()).run();
        new GetterGenerator(writer, directive.symbolProvider(), directive.shape(), directive.model()).run();
        IntEnumShape shape = directive.shape().asIntEnumShape().orElseThrow(() -> new RuntimeException("oops"));
        for (String memberKey : shape.getEnumValues().keySet()) {
            writer.openBlock("public boolean is$L() {", "}", getMethodName(memberKey),
                    () -> writer.write("return $L.equals(getValue());", memberKey));
            writer.newLine();
        }
    }

    private void writeConstructorWithSourceLocation(TraitCodegenWriter writer, Symbol symbol) {
        writer.addImport(FromSourceLocation.class);
        writer.openBlock("public $T(Integer value, FromSourceLocation sourceLocation) {", "}", symbol, () -> {
            writer.write("super(ID, sourceLocation);");
            writer.write("this.value = value;");
        });
        writer.newLine();
    }

    private void writeConstructor(TraitCodegenWriter writer, Symbol symbol) {
        writer.addImport(SourceLocation.class);
        writer.openBlock("public $T(Integer value) {", "}", symbol, () -> {
            writer.write("super(ID, SourceLocation.NONE);");
            writer.write("this.value = value;");
        });
        writer.newLine();
    }

    private String getMethodName(String enumValue) {
        return Arrays.stream(enumValue.split("_"))
                .map(String::toLowerCase)
                .map(StringUtils::capitalize)
                .collect(Collectors.joining());
    }
}
