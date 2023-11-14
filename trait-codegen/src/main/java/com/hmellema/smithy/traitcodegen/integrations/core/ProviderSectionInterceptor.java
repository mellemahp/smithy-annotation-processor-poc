package com.hmellema.smithy.traitcodegen.integrations.core;

import com.hmellema.smithy.traitcodegen.TraitCodegenSettings;
import com.hmellema.smithy.traitcodegen.TraitSymbolProvider;
import com.hmellema.smithy.traitcodegen.writer.TraitCodegenWriter;
import com.hmellema.smithy.traitcodegen.writer.sections.ProviderSection;
import software.amazon.smithy.codegen.core.Symbol;
import software.amazon.smithy.codegen.core.SymbolProvider;
import software.amazon.smithy.model.Model;
import software.amazon.smithy.model.node.Node;
import software.amazon.smithy.model.node.NodeMapper;
import software.amazon.smithy.model.shapes.*;
import software.amazon.smithy.model.traits.AnnotationTrait;
import software.amazon.smithy.model.traits.StringListTrait;
import software.amazon.smithy.model.traits.StringTrait;
import software.amazon.smithy.model.traits.Trait;
import software.amazon.smithy.utils.CodeInterceptor;

public class ProviderSectionInterceptor implements CodeInterceptor<ProviderSection, TraitCodegenWriter> {
    private final TraitCodegenSettings settings;

    public ProviderSectionInterceptor(TraitCodegenSettings settings) {
        this.settings = settings;
    }

    @Override
    public Class<ProviderSection> sectionType() {
        return ProviderSection.class;
    }

    @Override
    public void write(TraitCodegenWriter writer, String previousText, ProviderSection section) {
        Generator generator = new Generator(settings, writer, section.model());
        section.shape().accept(generator);
    }

    /**
     * Adds provider class to use as the {@link software.amazon.smithy.model.traits.TraitService} implementation for this trait
     */
    private static final class Generator extends ShapeVisitor.Default<Void> {
        private static final String PROVIDER_METHOD = "public Provider() {";
        private final SymbolProvider symbolProvider;
        private final TraitCodegenWriter writer;
        private final Model model;

        public Generator(TraitCodegenSettings settings, TraitCodegenWriter writer, Model model) {
            this.symbolProvider = new TraitSymbolProvider(settings);
            this.writer = writer;
            this.model = model;
        }

        @Override
        protected Void getDefault(Shape shape) {
            return null;
        }

        @Override
        public Void shortShape(ShortShape shape) {
            generateNumericTraitProvider(shape);
            return null;
        }

        @Override
        public Void integerShape(IntegerShape shape) {
            generateNumericTraitProvider(shape);
            return null;
        }

        @Override
        public Void intEnumShape(IntEnumShape shape) {
            generateNumericTraitProvider(shape);
            return null;
        }

        @Override
        public Void longShape(LongShape shape) {
            generateNumericTraitProvider(shape);
            return null;
        }

        @Override
        public Void floatShape(FloatShape shape) {
            generateNumericTraitProvider(shape);
            return null;
        }

        @Override
        public Void doubleShape(DoubleShape shape) {
            generateNumericTraitProvider(shape);
            return null;
        }

        @Override
        public Void bigIntegerShape(BigIntegerShape shape) {
            generateNumericTraitProvider(shape);
            return null;
        }

        @Override
        public Void bigDecimalShape(BigDecimalShape shape) {
            generateNumericTraitProvider(shape);
            return null;
        }

        private void generateNumericTraitProvider(Shape shape) {
            writer.openBlock("public static final class Provider extends AbstractTrait.Provider {", "}", () -> {
                // Basic constructor
                writer.openBlock(PROVIDER_METHOD, "}", () -> writer.write("super(ID);"));

                // Provider method
                writer.addImport(Trait.class);
                writer.addImport(ShapeId.class);
                writer.addImport(Node.class);
                Symbol symbol = symbolProvider.toSymbol(shape);
                writer.write("@Override");
                writer.openBlock("public Trait createTrait(ShapeId target, Node value) {", "}",
                        () -> writer.write("return new $T(value.expectNumberNode().getValue().$L, value.getSourceLocation());",
                                symbol, symbol.expectProperty("value-getter")));
            });
        }


        @Override
        public Void listShape(ListShape shape) {
            if (model.expectShape(shape.getMember().getTarget()).isStringShape()) {
                generateSimpleProvider(shape, StringListTrait.class);
                return null;
            }
            return null;
        }

        @Override
        public Void stringShape(StringShape shape) {
            generateSimpleProvider(shape, StringTrait.class);
            return null;
        }

        @Override
        public Void enumShape(EnumShape shape) {
            generateSimpleProvider(shape, StringTrait.class);
            return null;
        }

        @Override
        public Void structureShape(StructureShape shape) {
            if (shape.members().isEmpty()) {
                generateSimpleProvider(shape, AnnotationTrait.class);
                return null;
            }
            generateAbstractTraitProvider(shape);
            return null;
        }

        private void generateAbstractTraitProvider(Shape shape) {
            writer.addImport(Trait.class);
            writer.addImport(Node.class);
            writer.addImport(NodeMapper.class);

            writer.openBlock("public static final class Provider extends AbstractTrait.Provider {", "}", () -> {
                writer.openBlock(PROVIDER_METHOD, "}",
                        () -> writer.write("super(ID);"));
                writer.write("");
                writer.write("@Override");
                writer.openBlock("public Trait createTrait(ShapeId target, Node value) {", "}", () -> {
                    writer.write("$1T result = new NodeMapper().deserialize(value, $1T.class);", symbolProvider.toSymbol(shape));
                    writer.write("result.setNodeCache(value);");
                    writer.write("return result;");
                });
            });
        }

        private void generateSimpleProvider(Shape shape, Class<?> traitClass) {
            writer.addImport(traitClass);
            Symbol symbol = symbolProvider.toSymbol(shape);
            writer.openBlock("public static final class Provider extends $L.Provider<$T> {", "}",
                    traitClass.getSimpleName(), symbol, () -> writer.openBlock(PROVIDER_METHOD, "}",
                            () -> writer.write("super(ID, $T::new);", symbol)));
        }
    }
}
