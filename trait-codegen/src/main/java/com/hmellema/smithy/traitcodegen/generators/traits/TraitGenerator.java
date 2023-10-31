package com.hmellema.smithy.traitcodegen.generators.traits;

import com.hmellema.smithy.traitcodegen.SymbolUtil;
import com.hmellema.smithy.traitcodegen.directives.GenerateTraitDirective;
import com.hmellema.smithy.traitcodegen.writer.SpiWriterUtils;
import com.hmellema.smithy.traitcodegen.writer.TraitCodegenWriter;
import com.hmellema.smithy.traitcodegen.writer.sections.ClassSection;
import software.amazon.smithy.codegen.core.Symbol;
import software.amazon.smithy.model.shapes.ShapeId;

import java.util.function.Consumer;

abstract class TraitGenerator implements Consumer<GenerateTraitDirective> {
    private static final Symbol SHAPE_ID_SYMBOL = SymbolUtil.fromClass(ShapeId.class);
    protected static final String CLASS_DEF_TEMPLATE = "public final class $L extends $L {";

    @Override
    public void accept(GenerateTraitDirective directive) {
        directive.context().writerDelegator().useShapeWriter(directive.shape(), writer -> {
            addTraitClassImport(writer);
            writer.pushState(new ClassSection(directive.shape()));
            writer.openBlock(CLASS_DEF_TEMPLATE,"}", directive.symbol().getName(), getTraitClass().getSimpleName(), () -> {
                writeIdProperty(writer, directive.shape().getId());
                writeAdditionalProperties(writer, directive);
                writeConstructors(writer, directive);
                writeGetters(writer, directive);
                writeAdditionalMethods(writer, directive);
                writeCreateNodeMethod(writer, directive);
                writeProviderClass(writer, directive);
            }).popState();
        });

        SpiWriterUtils.addSpiTraitProvider(directive.context(), directive.symbol());
    }

    protected void writeAdditionalMethods(TraitCodegenWriter writer, GenerateTraitDirective directive) {
        // defaults to empty
    }

    /**
     * Adds the ID static property to the generated trait
     *
     * @param writer {@code TraitCodegenWriter} to use for writing property
     * @param shapeId ShapeId to use for the ID property
     */
    protected void writeIdProperty(TraitCodegenWriter writer, ShapeId shapeId) {
        writer.addImport(SHAPE_ID_SYMBOL);
        writer.write("public static final ShapeId ID = ShapeId.from($S);", shapeId).writeInline("\n");
    }

    /**
     * Adds provider class to use as the {@link software.amazon.smithy.model.traits.TraitService} implementation for this trait
     * @param writer {@code TraitCodegenWriter} to use for writing property
     * @param directive Codegen directive
     */
    protected void writeProviderClass(TraitCodegenWriter writer, GenerateTraitDirective directive) {
        writer.openBlock("public static final class Provider extends $L.Provider<$T> {", "}",
                getTraitClass().getSimpleName(), directive.symbol(), () -> {
            addProviderConstructor(writer, directive);
            addCreateTraitMethod(writer, directive);
        });
    }

    protected void addProviderConstructor(TraitCodegenWriter writer, GenerateTraitDirective directive) {
        writer.openBlock("public Provider() {", "}",
                () -> writer.write("super(ID, $T::new);", directive.symbol()));
    }

    protected void addTraitClassImport(TraitCodegenWriter writer) {
        writer.addImport(SymbolUtil.fromClass(getTraitClass()));
    }

    protected void addCreateTraitMethod(TraitCodegenWriter writer, GenerateTraitDirective directive) {
        // Do nothing by default
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
