package com.hmellema.smithy.traitcodegen.generators.traits;

import com.hmellema.smithy.traitcodegen.directives.GenerateTraitDirective;
import com.hmellema.smithy.traitcodegen.writer.SpiWriterUtils;
import com.hmellema.smithy.traitcodegen.writer.TraitCodegenWriter;
import com.hmellema.smithy.traitcodegen.writer.sections.ClassSection;
import com.hmellema.smithy.traitcodegen.writer.sections.PropertiesSection;
import software.amazon.smithy.model.shapes.ShapeId;
import software.amazon.smithy.utils.CodeSection;

import java.util.List;
import java.util.function.Consumer;

abstract class TraitGenerator implements Consumer<GenerateTraitDirective> {
    private static final String TRAIT_ID_TEMPLATE = "public static final ShapeId ID = ShapeId.from($S);";

    @Override
    public void accept(GenerateTraitDirective directive) {
        directive.context().writerDelegator().useShapeWriter(directive.shape(), writer -> writeTraitClass(writer, directive));
        SpiWriterUtils.addSpiTraitProvider(directive.context(), directive.traitSymbol());
    }

    private void writeTraitClass(TraitCodegenWriter writer, GenerateTraitDirective directive) {
        imports(writer);
        writer.addImport(ShapeId.class);
        writer.pushState(new ClassSection(directive.shape()));
        writer.openBlock(getClassDefinition(), "}", directive.traitSymbol(), () -> {
            writer.write(TRAIT_ID_TEMPLATE, directive.shape().getId());
            writer.newLine();
            writer.injectSection(new PropertiesSection(directive.shape(), directive.symbolProvider()));
            writeConstructors(writer, directive);
            writeAdditionalMethods(writer, directive);
            additionalSections(directive).forEach(writer::injectSection);
        });
        writer.popState();
    }

    protected abstract void imports(TraitCodegenWriter writer);
    protected abstract String getClassDefinition();
    protected abstract void writeConstructors(TraitCodegenWriter writer, GenerateTraitDirective directive);
    protected abstract List<CodeSection> additionalSections(GenerateTraitDirective directive);
    protected void writeAdditionalMethods(TraitCodegenWriter writer, GenerateTraitDirective directive) {
        // no additional methods by default
    }
}
