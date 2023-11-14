package com.hmellema.smithy.traitcodegen.generators;

import com.hmellema.smithy.traitcodegen.SymbolUtil;
import com.hmellema.smithy.traitcodegen.directives.GenerateTraitDirective;
import com.hmellema.smithy.traitcodegen.writer.TraitCodegenWriter;
import software.amazon.smithy.codegen.core.Symbol;
import software.amazon.smithy.model.FromSourceLocation;
import software.amazon.smithy.model.SourceLocation;
import software.amazon.smithy.model.traits.StringListTrait;

public final class StringListTraitGenerator extends TraitGenerator {
    @Override
    protected void writeConstructors(TraitCodegenWriter writer, GenerateTraitDirective directive) {
        writeConstructor(writer, directive.traitSymbol(), directive.baseSymbol());
        writeConstructorWithSourceLocation(writer, directive.traitSymbol(), directive.baseSymbol());
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
    protected Class<?> getTraitClass() {
        return StringListTrait.class;
    }
}
