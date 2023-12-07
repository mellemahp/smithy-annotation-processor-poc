package com.hmellema.smithy.traitcodegen.generators.traits;

import com.hmellema.smithy.traitcodegen.SymbolProperties;
import com.hmellema.smithy.traitcodegen.sections.ProviderSection;
import com.hmellema.smithy.traitcodegen.utils.SymbolUtil;
import com.hmellema.smithy.traitcodegen.writer.TraitCodegenWriter;
import software.amazon.smithy.codegen.core.Symbol;
import software.amazon.smithy.codegen.core.SymbolProvider;
import software.amazon.smithy.model.node.Node;
import software.amazon.smithy.model.node.NodeMapper;
import software.amazon.smithy.model.shapes.BigDecimalShape;
import software.amazon.smithy.model.shapes.ByteShape;
import software.amazon.smithy.model.shapes.DocumentShape;
import software.amazon.smithy.model.shapes.DoubleShape;
import software.amazon.smithy.model.shapes.EnumShape;
import software.amazon.smithy.model.shapes.FloatShape;
import software.amazon.smithy.model.shapes.IntEnumShape;
import software.amazon.smithy.model.shapes.IntegerShape;
import software.amazon.smithy.model.shapes.ListShape;
import software.amazon.smithy.model.shapes.LongShape;
import software.amazon.smithy.model.shapes.MapShape;
import software.amazon.smithy.model.shapes.Shape;
import software.amazon.smithy.model.shapes.ShapeId;
import software.amazon.smithy.model.shapes.ShapeVisitor;
import software.amazon.smithy.model.shapes.ShortShape;
import software.amazon.smithy.model.shapes.StringShape;
import software.amazon.smithy.model.shapes.StructureShape;
import software.amazon.smithy.model.traits.AnnotationTrait;
import software.amazon.smithy.model.traits.StringListTrait;
import software.amazon.smithy.model.traits.StringTrait;
import software.amazon.smithy.model.traits.Trait;


/**
 * Adds provider class to use as the {@link software.amazon.smithy.model.traits.TraitService} implementation for a
 * trait.
 */
final class ProviderGenerator implements Runnable {
    private static final String PROVIDER_METHOD = "public Provider() {";

    private final TraitCodegenWriter writer;
    private final Shape shape;
    private final Symbol traitSymbol;
    private final SymbolProvider symbolProvider;

    ProviderGenerator(TraitCodegenWriter writer, Shape shape, Symbol traitSymbol, SymbolProvider symbolProvider) {
        this.writer = writer;
        this.shape = shape;
        this.traitSymbol = traitSymbol;
        this.symbolProvider = symbolProvider;
    }

    @Override
    public void run() {
        writer.pushState(new ProviderSection(shape, traitSymbol));
        shape.accept(new ProviderMethodVisitor());
        writer.popState();
    }

    private final class ProviderMethodVisitor extends ShapeVisitor.Default<Void> {

        @Override
        public Void getDefault(Shape shape) {
            throw new UnsupportedOperationException("Provider generator does not support shape "
                    + shape + " of type " + shape.getType());
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
        public Void documentShape(DocumentShape shape) {
            writer.openBlock("public static final class Provider extends AbstractTrait.Provider {", "}", () -> {
                // Basic constructor
                writer.openBlock(PROVIDER_METHOD, "}", () -> writer.write("super(ID);"));
                writer.newLine();
                // Provider method
                writer.addImports(Trait.class, ShapeId.class, Node.class);
                writer.override();
                writer.openBlock("public Trait createTrait(ShapeId target, Node value) {", "}",
                        () -> writer.write("return new $T(value);", traitSymbol));
            });
            return null;
        }

        @Override
        public Void doubleShape(DoubleShape shape) {
            generateNumericTraitProvider();
            return null;
        }

        @Override
        public Void bigDecimalShape(BigDecimalShape shape) {
            writer.openBlock("public static final class Provider extends AbstractTrait.Provider {", "}", () -> {
                // Basic constructor
                writer.openBlock(PROVIDER_METHOD, "}", () -> writer.write("super(ID);"));
                writer.newLine();
                // Provider method
                writer.addImports(Trait.class, ShapeId.class, Node.class);
                writer.override();
                writer.openBlock("public Trait createTrait(ShapeId target, Node value) {", "}",
                        () -> writer.write("return new $T(value.expectNumberNode().asBigDecimal().get(), value"
                                        + ".getSourceLocation());", traitSymbol));
            });
            return null;
        }

        @Override
        public Void mapShape(MapShape shape) {
            generateAbstractTraitProvider();
            return null;
        }

        private void generateNumericTraitProvider() {
            writer.openBlock("public static final class Provider extends AbstractTrait.Provider {", "}", () -> {
                // Basic constructor
                writer.openBlock(PROVIDER_METHOD, "}", () -> writer.write("super(ID);"));
                writer.newLine();
                // Provider method
                writer.addImports(Trait.class, ShapeId.class, Node.class);
                writer.override();
                writer.openBlock("public Trait createTrait(ShapeId target, Node value) {", "}",
                        () -> writer.write("return new $T(value.expectNumberNode().getValue().$L, value"
                                        + ".getSourceLocation());",
                                traitSymbol, traitSymbol.expectProperty(SymbolProperties.VALUE_GETTER)));
            });
        }


        @Override
        public Void listShape(ListShape shape) {
            if (SymbolUtil.isJavaString(symbolProvider.toSymbol(shape.getMember()))) {
                generateSimpleProvider(StringListTrait.class);
                return null;
            }
            generateAbstractTraitProvider();
            return null;
        }

        @Override
        public Void byteShape(ByteShape shape) {
            generateNumericTraitProvider();
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
            writer.addImports(Trait.class, Node.class, NodeMapper.class);
            writer.openBlock("public static final class Provider extends AbstractTrait.Provider {", "}", () -> {
                writer.openBlock(PROVIDER_METHOD, "}", () -> writer.write("super(ID);"));
                writer.newLine();
                writer.override();
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
}
