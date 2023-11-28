package com.hmellema.smithy.traitcodegen.generators.base;

import com.hmellema.smithy.traitcodegen.TraitCodegenContext;
import com.hmellema.smithy.traitcodegen.TraitCodegenSettings;
import com.hmellema.smithy.traitcodegen.generators.common.*;
import com.hmellema.smithy.traitcodegen.sections.*;
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
                        new PropertiesGenerator(writer, directive.shape(), directive.symbolProvider()).run();
                        new ConstructorWithBuilderGenerator(writer, directive.symbol(), directive.shape(), directive.symbolProvider(), directive.model()).run();
                        new ToNodeGenerator(writer, directive.shape(), directive.symbolProvider(), directive.model()).run();
                        new FromNodeGenerator(writer, directive.symbol(), directive.shape(), directive.symbolProvider(), directive.model()).run();
                        new GetterGenerator(writer, directive.symbolProvider(), directive.shape(), directive.model()).run();
                        new BuilderGenerator(writer, directive.symbol(), directive.symbolProvider(), directive.shape(), directive.model()).run();
                    })
                    .popState();
            writer.newLine();
        });
    }
}
