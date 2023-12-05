package com.hmellema.smithy.traitcodegen.generators.traits;

import com.hmellema.smithy.traitcodegen.GenerateTraitDirective;
import com.hmellema.smithy.traitcodegen.generators.common.BuilderGenerator;
import com.hmellema.smithy.traitcodegen.generators.common.GetterGenerator;
import com.hmellema.smithy.traitcodegen.writer.TraitCodegenWriter;
import software.amazon.smithy.codegen.core.Symbol;
import software.amazon.smithy.model.FromSourceLocation;
import software.amazon.smithy.model.SourceLocation;
import software.amazon.smithy.model.traits.StringListTrait;

public final class StringListTraitGenerator extends TraitGenerator {
    private static final String CLASS_TEMPLATE = "public final class $1T extends StringListTrait implements ToSmithyBuilder<$1T> {";

    @Override
    protected void imports(TraitCodegenWriter writer) {
        writer.addImport(StringListTrait.class);
    }

    @Override
    protected String getClassDefinition() {
        return CLASS_TEMPLATE;
    }

    @Override
    protected void writeTraitBody(TraitCodegenWriter writer, GenerateTraitDirective directive) {
        writeConstructorWithSourceLocation(writer, directive.traitSymbol(), directive.baseSymbol());
        writeConstructor(writer, directive.traitSymbol(), directive.baseSymbol());
        new GetterGenerator(writer, directive.symbolProvider(), directive.shape(), directive.model()).run();
        new BuilderGenerator(writer, directive.traitSymbol(), directive.symbolProvider(), directive.shape(), directive.model()).run();
    }

    private void writeConstructorWithSourceLocation(TraitCodegenWriter writer, Symbol traitSymbol, Symbol baseSymbol) {
        writer.addImport(FromSourceLocation.class);
        writer.openBlock("public $T($T values, FromSourceLocation sourceLocation) {", "}",
                traitSymbol, baseSymbol,
                () -> writer.write("super(ID, values, sourceLocation);"));
        writer.newLine();
    }

    private void writeConstructor(TraitCodegenWriter writer, Symbol traitSymbol, Symbol baseSymbol) {
        writer.addImport(SourceLocation.class);
        writer.openBlock("public $T($T values) {", "}", traitSymbol, baseSymbol,
                () -> writer.write("super(ID, values, SourceLocation.NONE);"));
        writer.newLine();
    }
}
