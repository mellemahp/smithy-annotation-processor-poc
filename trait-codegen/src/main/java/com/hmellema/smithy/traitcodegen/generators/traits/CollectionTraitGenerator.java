package com.hmellema.smithy.traitcodegen.generators.traits;

import com.hmellema.smithy.traitcodegen.directives.GenerateTraitDirective;
import com.hmellema.smithy.traitcodegen.writer.TraitCodegenWriter;
import com.hmellema.smithy.traitcodegen.writer.sections.*;
import software.amazon.smithy.model.traits.AbstractTrait;
import software.amazon.smithy.utils.CodeSection;
import software.amazon.smithy.utils.ListUtils;
import software.amazon.smithy.utils.ToSmithyBuilder;

import java.util.List;

public final class CollectionTraitGenerator extends TraitGenerator {
    private static final String CLASS_TEMPLATE = "public final class $1T extends AbstractTrait implements ToSmithyBuilder<$1T> {";

    @Override
    protected void imports(TraitCodegenWriter writer) {
        writer.addImports(ToSmithyBuilder.class, AbstractTrait.class);
    }

    @Override
    protected String getClassDefinition() {
        return CLASS_TEMPLATE;
    }

    @Override
    protected void writeConstructors(TraitCodegenWriter writer, GenerateTraitDirective directive) {
        writer.injectSection(new ConstructorWithBuilderSection(directive.shape(), directive.traitSymbol(), directive.symbolProvider(), directive.model()));
    }

    @Override
    protected List<CodeSection> additionalSections(GenerateTraitDirective directive) {
        return ListUtils.of(
            new ToNodeSection(directive.shape(), directive.traitSymbol(), directive.symbolProvider(), directive.model()),
            new FromNodeSection(directive.shape(), directive.traitSymbol(), directive.symbolProvider(), directive.model()),
            new GetterSection(directive.shape(), directive.symbolProvider(), directive.model()),
            new BuilderSection(directive.shape(), directive.traitSymbol(), directive.symbolProvider(), directive.model()),
            new ProviderSection(directive.shape(), directive.traitSymbol(), directive.symbolProvider())
        );
    }
}
