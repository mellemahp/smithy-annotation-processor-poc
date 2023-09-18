package com.hmellema.smithy.traitcodegen.generators;

import com.hmellema.smithy.traitcodegen.SymbolUtil;
import com.hmellema.smithy.traitcodegen.TraitCodegenContext;
import com.hmellema.smithy.traitcodegen.directives.GenerateTraitDirective;
import com.hmellema.smithy.traitcodegen.writer.TraitCodegenWriter;
import software.amazon.smithy.codegen.core.Symbol;
import software.amazon.smithy.model.shapes.ShapeId;

import java.util.function.Consumer;

abstract class SimpleTraitGenerator implements Consumer<GenerateTraitDirective> {
    private static final String TRAIT_SERVICE_PROVIDER_FILE = "META-INF/services/software.amazon.smithy.model.traits.TraitService";
    private static final Symbol SHAPE_ID_SYMBOL = SymbolUtil.fromClass(ShapeId.class);
    protected static final String CLASS_DEF_TEMPLATE = "public final class $L extends $L {";

    @Override
    public void accept(GenerateTraitDirective directive) {
        directive.context().writerDelegator().useShapeWriter(directive.shape(), writer -> {
            addTraitClassImport(writer);
            // TODO Add section for class
            writer.pushState();
            writer.openBlock(CLASS_DEF_TEMPLATE,"}", directive.symbol().getName(), getTraitClass().getSimpleName(), () -> {
                writeIdProperty(writer, directive.shape().getId());
                writeConstructors(writer, directive);
                writeProviderClass(writer, directive.symbol());
            }).popState();
        });

        addProviderToServices(directive.context(), directive.symbol());
    }

    /**
     * Write provider method to Java SPI to service file for {@link software.amazon.smithy.model.traits.TraitService}.
     *
     * @param context Codegen context
     * @param symbol Symbol for trait class
     */
    protected void addProviderToServices(TraitCodegenContext context, Symbol symbol) {
        context.writerDelegator().useFileWriter(TRAIT_SERVICE_PROVIDER_FILE,
                writer -> writer.writeInline("$L$$Provider", symbol.getFullName()));
    }

    /**
     * Adds the ID static property to the generated trait
     *
     * @param writer {@code TraitCodegenWriter} to use for writing property
     * @param shapeId Shape Id to use for the ID property
     */
    protected void writeIdProperty(TraitCodegenWriter writer, ShapeId shapeId) {
        writer.addImport(SHAPE_ID_SYMBOL);
        writer.write("public static final ShapeId ID = ShapeId.from($S);", shapeId).writeInline("\n");
    }

    /**
     * Adds provider class to use as the {@link software.amazon.smithy.model.traits.TraitService} implementation for this trait
     * @param writer {@code TraitCodegenWriter} to use for writing property
     * @param symbol Symbol for trait shape
     */
    protected void writeProviderClass(TraitCodegenWriter writer, Symbol symbol) {
        writer.openBlock("public static final class Provider extends $L.Provider<$L> {", "}",
                getTraitClass().getSimpleName(), symbol.getName(), () -> {
            writer.openBlock("public Provider() {", "}", () -> {
                writer.write("super(ID, $L::new);", symbol.getName());
            });
        });
    }

    protected void addTraitClassImport(TraitCodegenWriter writer) {
        writer.addImport(SymbolUtil.fromClass(getTraitClass()));
    }

    protected abstract void writeConstructors(TraitCodegenWriter writer, GenerateTraitDirective directive);

    protected abstract Class<?> getTraitClass();
}
