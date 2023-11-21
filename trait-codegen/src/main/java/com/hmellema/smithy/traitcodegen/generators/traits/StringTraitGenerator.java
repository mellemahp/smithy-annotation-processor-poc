package com.hmellema.smithy.traitcodegen.generators.traits;

import com.hmellema.smithy.traitcodegen.directives.GenerateTraitDirective;
import com.hmellema.smithy.traitcodegen.writer.TraitCodegenWriter;
import software.amazon.smithy.codegen.core.Symbol;
import software.amazon.smithy.model.FromSourceLocation;
import software.amazon.smithy.model.SourceLocation;
import software.amazon.smithy.model.traits.StringTrait;

public class StringTraitGenerator extends TraitGenerator {
    private static final String CLASS_TEMPLATE = "public final class $T extends StringTrait {";

    @Override
    protected void imports(TraitCodegenWriter writer) {
        writer.addImport(StringTrait.class);
    }

    @Override
    protected String getClassDefinition() {
        return CLASS_TEMPLATE;
    }

    @Override
    protected void writeConstructors(TraitCodegenWriter writer, GenerateTraitDirective directive) {
        writeConstructor(writer, directive.traitSymbol());
        writeConstructorWithSourceLocation(writer, directive.traitSymbol());
    }

    @Override
    protected void writeAdditionalMethods(TraitCodegenWriter writer, GenerateTraitDirective directive) {
        // String traits have no need for additional methods
    }

    @Override
    protected void writeBuilder(TraitCodegenWriter writer, GenerateTraitDirective directive) {
        // Does not use a builder
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
