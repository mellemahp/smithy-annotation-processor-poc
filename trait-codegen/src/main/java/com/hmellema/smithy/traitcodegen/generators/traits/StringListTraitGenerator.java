package com.hmellema.smithy.traitcodegen.generators.traits;

import com.hmellema.smithy.traitcodegen.directives.GenerateTraitDirective;
import com.hmellema.smithy.traitcodegen.writer.TraitCodegenWriter;
import com.hmellema.smithy.traitcodegen.writer.sections.BuilderSection;
import com.hmellema.smithy.traitcodegen.writer.sections.GetterSection;
import com.hmellema.smithy.traitcodegen.writer.sections.ProviderSection;
import software.amazon.smithy.codegen.core.Symbol;
import software.amazon.smithy.model.FromSourceLocation;
import software.amazon.smithy.model.SourceLocation;
import software.amazon.smithy.model.traits.StringListTrait;
import software.amazon.smithy.utils.CodeSection;
import software.amazon.smithy.utils.ListUtils;

import java.util.List;

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
                () -> writer.write("super(ID, values, sourceLocation);"));
        writer.newLine();
    }

    private void writeConstructor(TraitCodegenWriter writer, Symbol traitSymbol, Symbol baseSymbol) {
        writer.addImport(SourceLocation.class);
        writer.openBlock("public $T($T values) {", "}", traitSymbol, baseSymbol,
                () -> writer.write("super(ID, values, SourceLocation.NONE);"));
        writer.newLine();
    }

    @Override
    protected List<CodeSection> additionalSections(GenerateTraitDirective directive) {
        return ListUtils.of(
                new GetterSection(directive.shape(), directive.symbolProvider(), directive.model()),
                new BuilderSection(directive.shape(), directive.traitSymbol(), directive.symbolProvider(), directive.model()),
                new ProviderSection(directive.shape(), directive.traitSymbol(), directive.symbolProvider())
        );
    }
}
