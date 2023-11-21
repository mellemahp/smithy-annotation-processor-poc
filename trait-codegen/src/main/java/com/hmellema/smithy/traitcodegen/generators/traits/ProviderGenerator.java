package com.hmellema.smithy.traitcodegen.generators.traits;

import com.hmellema.smithy.traitcodegen.SymbolProperties;
import com.hmellema.smithy.traitcodegen.SymbolUtil;
import com.hmellema.smithy.traitcodegen.writer.TraitCodegenWriter;
import software.amazon.smithy.codegen.core.Symbol;
import software.amazon.smithy.codegen.core.SymbolProvider;
import software.amazon.smithy.model.node.Node;
import software.amazon.smithy.model.node.NodeMapper;
import software.amazon.smithy.model.shapes.*;
import software.amazon.smithy.model.traits.AnnotationTrait;
import software.amazon.smithy.model.traits.StringListTrait;
import software.amazon.smithy.model.traits.StringTrait;
import software.amazon.smithy.model.traits.Trait;


/**
 * Adds provider class to use as the {@link software.amazon.smithy.model.traits.TraitService} implementation for this trait
 */
final class ProviderGenerator extends ShapeVisitor.Default<Void> {
    private static final String PROVIDER_METHOD = "public Provider() {";
    private final SymbolProvider baseSymbolProvider;
    private final Symbol traitSymbol;
    private final TraitCodegenWriter writer;

    ProviderGenerator(SymbolProvider baseSymbolProvider, Symbol traitSymbol, TraitCodegenWriter writer) {
        this.baseSymbolProvider = baseSymbolProvider;
        this.traitSymbol = traitSymbol;
        this.writer = writer;
    }

    @Override
    public Void getDefault(Shape shape) {
        return null;
    }

    @Override
    public Void shortShape(ShortShape shape) {
        generateNumericTraitProvider();
        return null;
    }

    @Override
    public Void integerShape(IntegerShape shape) {
        generateNumericTraitProvider();
        return null;
    }

    @Override
    public Void intEnumShape(IntEnumShape shape) {
        generateNumericTraitProvider();
        return null;
    }

    @Override
    public Void longShape(LongShape shape) {
        generateNumericTraitProvider();
        return null;
    }

    @Override
    public Void floatShape(FloatShape shape) {
        generateNumericTraitProvider();
        return null;
    }

    @Override
    public Void doubleShape(DoubleShape shape) {
        generateNumericTraitProvider();
        return null;
    }

    @Override
    public Void bigIntegerShape(BigIntegerShape shape) {
        generateNumericTraitProvider();
        return null;
    }

    @Override
    public Void bigDecimalShape(BigDecimalShape shape) {
        generateNumericTraitProvider();
        return null;
    }

    private void generateNumericTraitProvider() {
        writer.openBlock("public static final class Provider extends AbstractTrait.Provider {", "}", () -> {
            // Basic constructor
            writer.openBlock(PROVIDER_METHOD, "}", () -> writer.write("super(ID);"));

            // Provider method
            writer.addImport(Trait.class);
            writer.addImport(ShapeId.class);
            writer.addImport(Node.class);
            writer.write("@Override");
            writer.openBlock("public Trait createTrait(ShapeId target, Node value) {", "}",
                    () -> writer.write("return new $T(value.expectNumberNode().getValue().$L, value.getSourceLocation());",
                            traitSymbol, traitSymbol.expectProperty(SymbolProperties.VALUE_GETTER)));
        });
    }


    @Override
    public Void listShape(ListShape shape) {
        if (SymbolUtil.isJavaString(baseSymbolProvider.toSymbol(shape.getMember()))) {
            generateSimpleProvider(StringListTrait.class);
            return null;
        }
        return null;
    }

    @Override
    public Void stringShape(StringShape shape) {
        generateSimpleProvider(StringTrait.class);
        return null;
    }

    @Override
    public Void enumShape(EnumShape shape) {
        generateSimpleProvider(StringTrait.class);
        return null;
    }

    @Override
    public Void structureShape(StructureShape shape) {
        if (shape.members().isEmpty()) {
            generateSimpleProvider(AnnotationTrait.class);
        } else {
            generateAbstractTraitProvider();
        }
        return null;
    }

    private void generateAbstractTraitProvider() {
        writer.addImport(Trait.class);
        writer.addImport(Node.class);
        writer.addImport(NodeMapper.class);

        writer.openBlock("public static final class Provider extends AbstractTrait.Provider {", "}", () -> {
            writer.openBlock(PROVIDER_METHOD, "}",
                    () -> writer.write("super(ID);"));
            writer.write("");
            writer.write("@Override");
            writer.openBlock("public Trait createTrait(ShapeId target, Node value) {", "}", () -> {
                writer.write("$1T result = new NodeMapper().deserialize(value, $1T.class);", traitSymbol);
                writer.write("result.setNodeCache(value);");
                writer.write("return result;");
            });
        });
    }

    private void generateSimpleProvider(Class<?> traitClass) {
        writer.addImport(traitClass);
        writer.openBlock("public static final class Provider extends $L.Provider<$T> {", "}",
                traitClass.getSimpleName(), traitSymbol, () -> writer.openBlock(PROVIDER_METHOD, "}",
                        () -> writer.write("super(ID, $T::new);", traitSymbol)));
    }
}
