package com.hmellema.smithy.traitcodegen.generators.traits;

import com.hmellema.smithy.traitcodegen.directives.GenerateTraitDirective;
import com.hmellema.smithy.traitcodegen.generators.common.node.FromNodeGenerator;
import com.hmellema.smithy.traitcodegen.generators.common.builder.BuilderConstructorGenerator;
import com.hmellema.smithy.traitcodegen.generators.common.builder.BuilderGenerator;
import com.hmellema.smithy.traitcodegen.generators.common.node.CreateNodeGenerator;
import com.hmellema.smithy.traitcodegen.generators.common.builder.ToBuilderGenerator;
import com.hmellema.smithy.traitcodegen.writer.TraitCodegenWriter;
import software.amazon.smithy.model.traits.AbstractTrait;
import software.amazon.smithy.utils.ToSmithyBuilder;

public class CollectionTraitGenerator extends TraitGenerator {
    private static final String CLASS_TEMPLATE = "public final class $1T extends AbstractTrait implements ToSmithyBuilder<$1T> {";

    @Override
    protected void imports(TraitCodegenWriter writer) {
        writer.addImport(ToSmithyBuilder.class);
        writer.addImport(AbstractTrait.class);
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
    protected void writeBuilder(TraitCodegenWriter writer, GenerateTraitDirective directive) {
        new BuilderGenerator(directive.shape(), directive.model(), directive.traitSymbol(), directive.symbolProvider(), writer).run();
    }

    protected void writeAdditionalMethods(TraitCodegenWriter writer, GenerateTraitDirective directive) {
        new CreateNodeGenerator(writer, directive.symbolProvider(), directive.model()).writeCreateNodeMethod(directive.shape());
        writer.write("");
        new ToBuilderGenerator(writer, directive.traitSymbol(), directive.shape(), directive.symbolProvider(), directive.model()).run();
        writer.write("");
        new FromNodeGenerator(writer, directive.traitSymbol(), directive.symbolProvider(), directive.shape(), directive.model()).run();
    }
}
