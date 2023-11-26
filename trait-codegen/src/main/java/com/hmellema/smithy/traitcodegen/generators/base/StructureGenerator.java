package com.hmellema.smithy.traitcodegen.generators.base;

import com.hmellema.smithy.traitcodegen.TraitCodegenContext;
import com.hmellema.smithy.traitcodegen.TraitCodegenSettings;
import com.hmellema.smithy.traitcodegen.generators.common.BuilderConstructorGenerator;
import com.hmellema.smithy.traitcodegen.writer.sections.*;
import software.amazon.smithy.codegen.core.directed.GenerateStructureDirective;
import software.amazon.smithy.model.node.ToNode;

import java.util.function.Consumer;

public class StructureGenerator implements Consumer<GenerateStructureDirective<TraitCodegenContext, TraitCodegenSettings>> {
    private static final String BASE_CLASS_TEMPLATE_STRING = "public final class $1T implements ToNode, ToSmithyBuilder<$1T> {";

    @Override
    public void accept(GenerateStructureDirective<TraitCodegenContext, TraitCodegenSettings> directive) {
        directive.context().writerDelegator().useShapeWriter(directive.shape(), writer -> {
            writer.addImport(ToNode.class);
            writer.pushState(new ClassSection(directive.shape()))
                    .openBlock(BASE_CLASS_TEMPLATE_STRING, "}", directive.symbol(), () -> {
                        writer.injectSection(new PropertiesSection(directive.shape(), directive.symbolProvider()));

                        // Create constructor from builder
                        new BuilderConstructorGenerator(writer, directive.symbol(), directive.shape(), directive.symbolProvider(), directive.model()).run();
                        writer.newLine();

                        // Creates toNode method
                        writer.injectSection(new ToNodeSection(directive.shape(), directive.symbol(), directive.symbolProvider(), directive.model()));
                        writer.injectSection(new FromNodeSection(directive.shape(), directive.symbol(), directive.symbolProvider(), directive.model()));
                        writer.injectSection(new GetterSection(directive.shape(), directive.symbolProvider(), directive.model()));
                        writer.injectSection(new BuilderSection(directive.shape(), directive.symbol(), directive.symbolProvider(), directive.model()));
                    })
                    .popState();
        });
    }
}
