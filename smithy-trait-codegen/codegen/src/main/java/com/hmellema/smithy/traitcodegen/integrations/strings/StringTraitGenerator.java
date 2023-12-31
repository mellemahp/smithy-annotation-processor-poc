package com.hmellema.smithy.traitcodegen.integrations.strings;

import com.hmellema.smithy.traitcodegen.GenerateTraitDirective;
import com.hmellema.smithy.traitcodegen.generators.traits.TraitGenerator;
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
    protected void writeTraitBody(TraitCodegenWriter writer, GenerateTraitDirective directive) {
        writeConstructor(writer, directive.traitSymbol());
        writeConstructorWithSourceLocation(writer, directive.traitSymbol());
    }

    @Override
    protected void writeProvider(TraitCodegenWriter writer, GenerateTraitDirective directive) {
        writer.addImport(StringTrait.class);
        writer.openBlock("public static final class Provider extends StringTrait.Provider<$T> {", "}",
                directive.traitSymbol(), () -> writer.openBlock("public Provider() {", "}",
                        () -> writer.write("super(ID, $T::new);", directive.traitSymbol())));
    }

    private void writeConstructorWithSourceLocation(TraitCodegenWriter writer, Symbol symbol) {
        writer.addImport(FromSourceLocation.class);
        writer.openBlock("public $T(String name, FromSourceLocation sourceLocation) {", "}", symbol,
                () -> writer.write("super(ID, name, sourceLocation);"));
        writer.newLine();
    }

    private void writeConstructor(TraitCodegenWriter writer, Symbol symbol) {
        writer.addImport(SourceLocation.class);
        writer.openBlock("public $T(String name) {", "}", symbol,
                () -> writer.write("super(ID, name, SourceLocation.NONE);"));
        writer.newLine();
    }
}
