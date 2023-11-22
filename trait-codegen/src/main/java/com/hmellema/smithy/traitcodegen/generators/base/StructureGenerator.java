package com.hmellema.smithy.traitcodegen.generators.base;

import com.hmellema.smithy.traitcodegen.TraitCodegenContext;
import com.hmellema.smithy.traitcodegen.TraitCodegenSettings;
import com.hmellema.smithy.traitcodegen.generators.common.node.FromNodeGenerator;
import com.hmellema.smithy.traitcodegen.generators.common.PropertyGenerator;
import com.hmellema.smithy.traitcodegen.generators.common.builder.BuilderConstructorGenerator;
import com.hmellema.smithy.traitcodegen.generators.common.builder.BuilderGenerator;
import com.hmellema.smithy.traitcodegen.generators.common.builder.ToBuilderGenerator;
import com.hmellema.smithy.traitcodegen.generators.common.node.CreateNodeGenerator;
import com.hmellema.smithy.traitcodegen.generators.common.GetterGenerator;
import com.hmellema.smithy.traitcodegen.writer.sections.ClassSection;
import software.amazon.smithy.codegen.core.directed.GenerateStructureDirective;
import software.amazon.smithy.model.node.ToNode;

import java.util.function.Consumer;

public class StructureGenerator implements Consumer<GenerateStructureDirective<TraitCodegenContext, TraitCodegenSettings>> {
    private static final String BASE_CLASS_TEMPLATE_STRING = "public final class $1T implements ToNode, ToSmithyBuilder<$1T> {";
    private static final String PROPERTY_TEMPLATE = "private final $T $L;";

    @Override
    public void accept(GenerateStructureDirective<TraitCodegenContext, TraitCodegenSettings> directive) {
        directive.context().writerDelegator().useShapeWriter(directive.shape(), writer -> {
            writer.addImport(ToNode.class);
            writer.pushState(new ClassSection(directive.shape()))
                    .openBlock(BASE_CLASS_TEMPLATE_STRING, "}", directive.symbol(), () -> {
                        directive.shape().accept(new PropertyGenerator(writer, directive.symbolProvider()));

                        // Create constructor from builder
                        new BuilderConstructorGenerator(writer, directive.symbol(), directive.shape(), directive.symbolProvider(), directive.model()).run();
                        writer.write("");

                        // Creates all builder-associated methods
                        new ToBuilderGenerator(writer, directive.symbol(), directive.shape(), directive.symbolProvider(), directive.model()).run();
                        writer.write("");
                        new FromNodeGenerator(writer, directive.symbol(), directive.symbolProvider(), directive.shape(), directive.model()).run();
                        writer.write("");

                        // Creates toNode method
                        CreateNodeGenerator toNodeGenerator = new CreateNodeGenerator(writer, directive.symbolProvider(), directive.model());
                        toNodeGenerator.writeToNodeMethod(directive.shape());

                        // TODO
                        // Creates fromNode method

                        directive.shape().accept(new GetterGenerator(writer, directive.symbolProvider(), directive.model()));

                        // TODO: Should these trait sub-structures have setters by default?
                        //writeSetters(directive.shape(), directive.symbolProvider(), writer);
                        BuilderGenerator builderGenerator = new BuilderGenerator(directive.shape(), directive.model(), directive.symbol(), directive.symbolProvider(), writer);
                        builderGenerator.run();
                    })
                    .popState();
        });
    }
}
