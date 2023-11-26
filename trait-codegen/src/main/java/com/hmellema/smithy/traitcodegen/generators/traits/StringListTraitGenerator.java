package com.hmellema.smithy.traitcodegen.generators.traits;

import com.hmellema.smithy.traitcodegen.directives.GenerateTraitDirective;
import com.hmellema.smithy.traitcodegen.generators.common.builder.BuilderGenerator;
import com.hmellema.smithy.traitcodegen.generators.common.builder.ToBuilderGenerator;
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
    protected void writeConstructors(TraitCodegenWriter writer, GenerateTraitDirective directive) {
        writeConstructorWithSourceLocation(writer, directive.traitSymbol(), directive.baseSymbol());
        writeConstructor(writer, directive.traitSymbol(), directive.baseSymbol());
    }

    private void writeConstructorWithSourceLocation(TraitCodegenWriter writer, Symbol traitSymbol, Symbol baseSymbol) {
        writer.addImport(FromSourceLocation.class);
        writer.openBlock("public $T($T values, FromSourceLocation sourceLocation) {", "}",
                traitSymbol, baseSymbol,
                () -> writer.write("super(ID, values, sourceLocation);")).writeInline("\n");
    }

    private void writeConstructor(TraitCodegenWriter writer, Symbol traitSymbol, Symbol baseSymbol) {
        writer.addImport(SourceLocation.class);
        writer.openBlock("public $T($T values) {", "}", traitSymbol, baseSymbol,
                () -> writer.write("super(ID, values, SourceLocation.NONE);")).writeInline("\n");
    }

    @Override
    protected void writeBuilder(TraitCodegenWriter writer, GenerateTraitDirective directive) {
        new BuilderGenerator(directive.shape(), directive.model(), directive.traitSymbol(), directive.symbolProvider(), writer).run();
    }

    @Override
    protected void writeAdditionalMethods(TraitCodegenWriter writer, GenerateTraitDirective directive) {
        new ToBuilderGenerator(writer, directive.traitSymbol(), directive.shape(), directive.symbolProvider(), directive.model()).run();
        writer.newLine();
    }
}
