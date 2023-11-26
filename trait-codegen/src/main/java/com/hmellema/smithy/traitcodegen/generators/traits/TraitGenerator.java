package com.hmellema.smithy.traitcodegen.generators.traits;

import com.hmellema.smithy.traitcodegen.directives.GenerateTraitDirective;
import com.hmellema.smithy.traitcodegen.generators.common.GetterGenerator;
import com.hmellema.smithy.traitcodegen.writer.SpiWriterUtils;
import com.hmellema.smithy.traitcodegen.writer.TraitCodegenWriter;
import com.hmellema.smithy.traitcodegen.writer.sections.ClassSection;
import com.hmellema.smithy.traitcodegen.writer.sections.PropertiesSection;
import software.amazon.smithy.model.shapes.ShapeId;

import java.util.function.Consumer;

abstract class TraitGenerator implements Consumer<GenerateTraitDirective> {
    private static final String TRAIT_ID_TEMPLATE = "public static final ShapeId ID = ShapeId.from($S);";

    @Override
    public void accept(GenerateTraitDirective directive) {
        directive.context().writerDelegator().useShapeWriter(directive.shape(), writer -> {
            writer.pushState(new ClassSection(directive.shape()));
            writeTraitClass(writer, directive);
        });
        SpiWriterUtils.addSpiTraitProvider(directive.context(), directive.traitSymbol());
    }

    private void writeTraitClass(TraitCodegenWriter writer, GenerateTraitDirective directive) {
        imports(writer);
        writer.addImport(ShapeId.class);
        writer.openBlock(getClassDefinition(), "}", directive.traitSymbol(), () -> {
            writer.write(TRAIT_ID_TEMPLATE, directive.shape().getId());
            writer.injectSection(new PropertiesSection(directive.shape(), directive.symbolProvider()));
            writeConstructors(writer, directive);
            writeAdditionalMethods(writer, directive);
            writeBuilder(writer, directive);
            directive.shape().accept(new GetterGenerator(writer, directive.symbolProvider(), directive.model()));
            directive.shape().accept(new ProviderGenerator(directive.symbolProvider(), directive.traitSymbol(), writer));
        });
    }


    protected abstract void imports(TraitCodegenWriter writer);

    protected abstract String getClassDefinition();

    protected abstract void writeConstructors(TraitCodegenWriter writer, GenerateTraitDirective directive);

    protected void writeAdditionalMethods(TraitCodegenWriter writer, GenerateTraitDirective directive) {
        // No additional methods by default
    }

    protected void writeBuilder(TraitCodegenWriter writer, GenerateTraitDirective directive) {
        // Traits have no builder by default
    }
}
