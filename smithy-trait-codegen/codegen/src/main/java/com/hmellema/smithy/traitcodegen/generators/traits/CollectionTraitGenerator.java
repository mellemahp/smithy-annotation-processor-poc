package com.hmellema.smithy.traitcodegen.generators.traits;

import com.hmellema.smithy.traitcodegen.GenerateTraitDirective;
import com.hmellema.smithy.traitcodegen.generators.common.BuilderGenerator;
import com.hmellema.smithy.traitcodegen.generators.common.ConstructorWithBuilderGenerator;
import com.hmellema.smithy.traitcodegen.generators.common.FromNodeGenerator;
import com.hmellema.smithy.traitcodegen.generators.common.GetterGenerator;
import com.hmellema.smithy.traitcodegen.generators.common.PropertiesGenerator;
import com.hmellema.smithy.traitcodegen.generators.common.ToNodeGenerator;
import com.hmellema.smithy.traitcodegen.writer.TraitCodegenWriter;
import software.amazon.smithy.model.traits.AbstractTrait;
import software.amazon.smithy.utils.ToSmithyBuilder;

final class CollectionTraitGenerator extends TraitGenerator {
    private static final String CLASS_TEMPLATE = "public final class $1T extends AbstractTrait implements "
            + "ToSmithyBuilder<$1T> {";

    @Override
    protected void imports(TraitCodegenWriter writer) {
        writer.addImports(ToSmithyBuilder.class, AbstractTrait.class);
    }

    @Override
    protected String getClassDefinition() {
        return CLASS_TEMPLATE;
    }

    @Override
    protected void writeTraitBody(TraitCodegenWriter writer, GenerateTraitDirective directive) {
        new PropertiesGenerator(writer, directive.shape(), directive.symbolProvider()).run();
        new ConstructorWithBuilderGenerator(writer, directive.traitSymbol(), directive.shape(),
                directive.symbolProvider(), directive.model()).run();
        new ToNodeGenerator(writer, directive.shape(), directive.symbolProvider(), directive.model()).run();
        new FromNodeGenerator(writer, directive.traitSymbol(), directive.shape(), directive.symbolProvider(),
                directive.model()).run();
        new GetterGenerator(writer, directive.symbolProvider(), directive.shape(), directive.model()).run();
        new BuilderGenerator(writer, directive.traitSymbol(), directive.symbolProvider(), directive.shape(),
                directive.model()).run();
    }
}
