package com.hmellema.smithy.traitcodegen.generators;

import com.hmellema.smithy.traitcodegen.SymbolUtil;
import com.hmellema.smithy.traitcodegen.directives.GenerateTraitDirective;
import com.hmellema.smithy.traitcodegen.writer.SpiWriterUtils;
import com.hmellema.smithy.traitcodegen.writer.TraitCodegenWriter;
import com.hmellema.smithy.traitcodegen.writer.sections.ClassSection;
import com.hmellema.smithy.traitcodegen.writer.sections.ProviderSection;
import software.amazon.smithy.codegen.core.Symbol;
import software.amazon.smithy.model.shapes.ShapeId;

import java.util.function.Consumer;

abstract class TraitGenerator implements Consumer<GenerateTraitDirective> {
    private static final Symbol SHAPE_ID_SYMBOL = SymbolUtil.fromClass(ShapeId.class);
    protected static final String CLASS_DEF_TEMPLATE = "public final class $T extends $T {";

    @Override
    public void accept(GenerateTraitDirective directive) {
        directive.context().writerDelegator().useShapeWriter(directive.shape(), writer -> {
            addTraitClassImport(writer);
            writer.pushState(new ClassSection(directive.shape()));
            writer.openBlock(CLASS_DEF_TEMPLATE,"}", directive.traitSymbol(), SymbolUtil.fromClass(getTraitClass()), () -> {
                writeIdProperty(writer, directive.shape().getId());
                writeAdditionalProperties(writer, directive);
                writeConstructors(writer, directive);
                writeGetters(writer, directive);
                writeAdditionalMethods(writer, directive);
                writeCreateNodeMethod(writer, directive);

                // Write trait provider class
                writer.injectSection(new ProviderSection(directive.shape(), directive.context().symbolProvider(), directive.model()));
            }).popState();
        });

        SpiWriterUtils.addSpiTraitProvider(directive.context(), directive.traitSymbol());
    }

    private void writeIdProperty(TraitCodegenWriter writer, ShapeId shapeId) {
        writer.addImport(SHAPE_ID_SYMBOL);
        writer.write("public static final ShapeId ID = ShapeId.from($S);", shapeId).writeInline("\n");
    }

    protected void writeAdditionalMethods(TraitCodegenWriter writer, GenerateTraitDirective directive) {
        // defaults to empty
    }

    protected void addTraitClassImport(TraitCodegenWriter writer) {
        writer.addImport(SymbolUtil.fromClass(getTraitClass()));
    }

    protected void writeAdditionalProperties(TraitCodegenWriter writer, GenerateTraitDirective directive) {
        // Do nothing by default
    }

    protected void writeGetters(TraitCodegenWriter writer, GenerateTraitDirective directive) {
        // Do nothing by default
    }

    private void writeCreateNodeMethod(TraitCodegenWriter writer, GenerateTraitDirective directive) {
        // Do nothing by default
    }

    protected abstract void writeConstructors(TraitCodegenWriter writer, GenerateTraitDirective directive);

    protected abstract Class<?> getTraitClass();
}
