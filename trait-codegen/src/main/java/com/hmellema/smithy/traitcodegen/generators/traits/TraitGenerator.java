package com.hmellema.smithy.traitcodegen.generators.traits;

import com.hmellema.smithy.traitcodegen.directives.GenerateTraitDirective;
import com.hmellema.smithy.traitcodegen.generators.common.PropertiesGenerator;
import com.hmellema.smithy.traitcodegen.writer.SpiWriterUtils;
import com.hmellema.smithy.traitcodegen.writer.TraitCodegenWriter;
import com.hmellema.smithy.traitcodegen.sections.ClassSection;
import com.hmellema.smithy.traitcodegen.sections.PropertiesSection;
import software.amazon.smithy.model.shapes.ShapeId;

import java.util.function.Consumer;

abstract class TraitGenerator implements Consumer<GenerateTraitDirective> {
    private static final String TRAIT_ID_TEMPLATE = "public static final ShapeId ID = ShapeId.from($S);";

    @Override
    public void accept(GenerateTraitDirective directive) {
        directive.context().writerDelegator().useShapeWriter(directive.shape(), writer -> {
            imports(writer);
            writer.addImport(ShapeId.class);
            writer.pushState(new ClassSection(directive.shape()));
            writer.openBlock(getClassDefinition(), "}", directive.traitSymbol(), () -> {
                writer.write(TRAIT_ID_TEMPLATE, directive.shape().getId());
                writer.newLine();
                new PropertiesGenerator(writer, directive.shape(), directive.symbolProvider()).run();
                writeTraitBody(writer, directive);
                new ProviderGenerator(writer, directive.shape(), directive.traitSymbol(), directive.symbolProvider()).run();
            });
            writer.popState();
        });
        SpiWriterUtils.addSpiTraitProvider(directive.context(), directive.traitSymbol());
    }

    protected abstract void imports(TraitCodegenWriter writer);
    protected abstract String getClassDefinition();
    protected abstract void writeTraitBody(TraitCodegenWriter writer, GenerateTraitDirective directive);
}
