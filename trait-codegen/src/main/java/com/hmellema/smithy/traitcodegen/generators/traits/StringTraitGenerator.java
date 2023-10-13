package com.hmellema.smithy.traitcodegen.generators.traits;

import com.hmellema.smithy.traitcodegen.directives.GenerateTraitDirective;
import com.hmellema.smithy.traitcodegen.writer.TraitCodegenWriter;
import software.amazon.smithy.codegen.core.Symbol;
import software.amazon.smithy.model.FromSourceLocation;
import software.amazon.smithy.model.SourceLocation;
import software.amazon.smithy.model.traits.StringTrait;

public class StringTraitGenerator extends SimpleTraitGenerator {
    @Override
    protected Class<?> getTraitClass() {
        return StringTrait.class;
    }

    @Override
    protected void writeConstructors(TraitCodegenWriter writer, GenerateTraitDirective directive) {
        writeConstructor(writer, directive.symbol());
        writeConstructorWithSourceLocation(writer, directive.symbol());
    }

    private void writeConstructorWithSourceLocation(TraitCodegenWriter writer, Symbol symbol) {
        writer.addImport(FromSourceLocation.class);
        writer.openBlock("public $T(String name, FromSourceLocation sourceLocation) {", "}", symbol,
                () -> writer.write("super(ID, name, sourceLocation);")).writeInline("\n");
    }

    private void writeConstructor(TraitCodegenWriter writer, Symbol symbol) {
        writer.addImport(SourceLocation.class);
        writer.openBlock("public $T(String name) {", "}", symbol,
                () -> writer.write("super(ID, name, SourceLocation.NONE);")).writeInline("\n");
    }
}
