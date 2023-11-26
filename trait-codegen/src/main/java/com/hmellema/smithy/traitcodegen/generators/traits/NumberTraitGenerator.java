package com.hmellema.smithy.traitcodegen.generators.traits;

import com.hmellema.smithy.traitcodegen.directives.GenerateTraitDirective;
import com.hmellema.smithy.traitcodegen.writer.TraitCodegenWriter;
import com.hmellema.smithy.traitcodegen.writer.sections.GetterSection;
import com.hmellema.smithy.traitcodegen.writer.sections.ProviderSection;
import com.hmellema.smithy.traitcodegen.writer.sections.ToNodeSection;
import software.amazon.smithy.codegen.core.Symbol;
import software.amazon.smithy.model.FromSourceLocation;
import software.amazon.smithy.model.SourceLocation;
import software.amazon.smithy.model.traits.AbstractTrait;
import software.amazon.smithy.utils.CodeSection;
import software.amazon.smithy.utils.ListUtils;

import java.util.List;

public class NumberTraitGenerator extends TraitGenerator {
    private static final String CLASS_TEMPLATE = "public final class $T extends AbstractTrait {";

    @Override
    protected void imports(TraitCodegenWriter writer) {
        writer.addImport(AbstractTrait.class);
    }

    @Override
    protected String getClassDefinition() {
        return CLASS_TEMPLATE;
    }

    @Override
    protected void writeConstructors(TraitCodegenWriter writer, GenerateTraitDirective directive) {
        writeConstructor(writer, directive.traitSymbol(), directive.baseSymbol());
        writeConstructorWithSourceLocation(writer, directive.traitSymbol(), directive.baseSymbol());
    }

    @Override
    protected List<CodeSection> additionalSections(GenerateTraitDirective directive) {
        return ListUtils.of(
                new ToNodeSection(directive.shape(), directive.traitSymbol(), directive.symbolProvider(), directive.model()),
                new GetterSection(directive.shape(), directive.symbolProvider(), directive.model()),
                new ProviderSection(directive.shape(), directive.traitSymbol(), directive.symbolProvider())
        );
    }

    private void writeConstructorWithSourceLocation(TraitCodegenWriter writer, Symbol traitSymbol, Symbol baseSymbol) {
        writer.addImport(FromSourceLocation.class);
        writer.openBlock("public $T($T value, FromSourceLocation sourceLocation) {", "}",
                traitSymbol, baseSymbol, () -> {
                    writer.write("super(ID, sourceLocation);");
                    writer.write("this.value = value;");
                });
        writer.newLine();
    }

    private void writeConstructor(TraitCodegenWriter writer, Symbol traitSymbol, Symbol baseSymbol) {
        writer.addImport(SourceLocation.class);
        writer.openBlock("public $T($T value) {", "}",
                traitSymbol, baseSymbol, () -> {
                    writer.write("super(ID, SourceLocation.NONE);");
                    writer.write("this.value = value;");
                });
        writer.newLine();
    }
}
