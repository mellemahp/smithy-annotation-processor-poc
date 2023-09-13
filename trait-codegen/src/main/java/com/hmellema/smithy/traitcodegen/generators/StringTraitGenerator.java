package com.hmellema.smithy.traitcodegen.generators;

import com.hmellema.smithy.traitcodegen.SymbolUtil;
import com.hmellema.smithy.traitcodegen.directives.GenerateStringTraitDirective;
import com.hmellema.smithy.traitcodegen.writer.TraitCodegenWriter;
import software.amazon.smithy.codegen.core.Symbol;
import software.amazon.smithy.model.FromSourceLocation;
import software.amazon.smithy.model.SourceLocation;
import software.amazon.smithy.model.shapes.ShapeId;
import software.amazon.smithy.model.traits.StringTrait;

import java.util.function.Consumer;

public class StringTraitGenerator implements Consumer<GenerateStringTraitDirective> {
    private static final Symbol STRING_TRAIT_SYMBOL = SymbolUtil.fromClass(StringTrait.class);
    private static final Symbol SHAPE_ID_SYMBOL = SymbolUtil.fromClass(ShapeId.class);
    private static final String CLASS_DEF_TEMPLATE = "public final class $L extends StringTrait {";

    @Override
    public void accept(GenerateStringTraitDirective directive) {
        directive.context().writerDelegator().useShapeWriter(directive.shape(), writer -> {
            writer.addImport(STRING_TRAIT_SYMBOL);
            // TODO Add section for class
            writer.pushState();
            writer.openBlock(CLASS_DEF_TEMPLATE,"}", directive.symbol().getName(), () -> {
                writeIdProperty(writer, directive.shape().getId());
                writeConstructor(writer, directive.symbol());
                writeConstructorWithSourceLocation(writer, directive.symbol());
                writeProviderMethod(writer, directive.symbol());
            }).popState();
        });

        // Write definition to service file
        directive.context().writerDelegator().useFileWriter("META-INF/services/software.amazon.smithy.model.traits.TraitService", writer -> {
            writer.writeInline("$L$$Provider", directive.symbol().getFullName());
        });
    }

    private void writeConstructorWithSourceLocation(TraitCodegenWriter writer, Symbol symbol) {
        writer.addImport(SymbolUtil.fromClass(FromSourceLocation.class));
        writer.openBlock("private $L(String name, FromSourceLocation sourceLocation) {", "}", symbol.getName(), () -> {
            writer.write("super(ID, name, sourceLocation);");
        }).writeInline("\n");
    }

    private void writeProviderMethod(TraitCodegenWriter writer, Symbol symbol) {
        writer.openBlock("public static final class Provider extends StringTrait.Provider<$L> {", "}", symbol.getName(), () -> {
            writer.openBlock("public Provider() {", "}", () -> {
                writer.write("super(ID, $L::new);", symbol.getName());
            });
        });
    }
    private void writeConstructor(TraitCodegenWriter writer, Symbol symbol) {
        writer.addImport(SymbolUtil.fromClass(SourceLocation.class));
        writer.openBlock("private $L(String name) {", "}", symbol.getName(), () -> {
            writer.write("super(ID, name, SourceLocation.NONE);");
        }).writeInline("\n");
    }

    private void writeIdProperty(TraitCodegenWriter writer, ShapeId shapeId) {
        writer.addImport(SHAPE_ID_SYMBOL);
        writer.write("public static final ShapeId ID = ShapeId.from($S);", shapeId).writeInline("\n");
    }
}
