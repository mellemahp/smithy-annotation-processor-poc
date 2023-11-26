package com.hmellema.smithy.traitcodegen.generators.traits;

import com.hmellema.smithy.traitcodegen.directives.GenerateTraitDirective;
import com.hmellema.smithy.traitcodegen.generators.common.BuilderConstructorGenerator;
import com.hmellema.smithy.traitcodegen.generators.common.node.CreateNodeGenerator;
import com.hmellema.smithy.traitcodegen.generators.common.node.FromNodeGenerator;
import com.hmellema.smithy.traitcodegen.writer.TraitCodegenWriter;
import com.hmellema.smithy.traitcodegen.writer.sections.BuilderSection;
import software.amazon.smithy.model.traits.AbstractTrait;
import software.amazon.smithy.utils.ToSmithyBuilder;

public final class StructureTraitGenerator extends TraitGenerator {
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
        new BuilderConstructorGenerator(writer, directive.traitSymbol(), directive.shape(), directive.symbolProvider(), directive.model()).run();
    }


    @Override
    protected void writeAdditionalMethods(TraitCodegenWriter writer, GenerateTraitDirective directive) {
        new CreateNodeGenerator(writer, directive.symbolProvider(), directive.model()).writeCreateNodeMethod(directive.shape());
        writer.newLine();
        new FromNodeGenerator(writer, directive.traitSymbol(), directive.symbolProvider(), directive.shape(), directive.model()).run();
    }

    @Override
    protected void writeBuilder(TraitCodegenWriter writer, GenerateTraitDirective directive) {
        writer.injectSection(new BuilderSection(directive.shape(), directive.traitSymbol(), directive.symbolProvider(), directive.model()));
    }
}
