package com.hmellema.smithy.traitcodegen.integrations.core;

import com.hmellema.smithy.traitcodegen.SymbolProperties;
import com.hmellema.smithy.traitcodegen.utils.SymbolUtil;
import com.hmellema.smithy.traitcodegen.writer.TraitCodegenWriter;
import com.hmellema.smithy.traitcodegen.writer.sections.ProviderSection;
import software.amazon.smithy.model.node.Node;
import software.amazon.smithy.model.node.NodeMapper;
import software.amazon.smithy.model.shapes.*;
import software.amazon.smithy.model.traits.AnnotationTrait;
import software.amazon.smithy.model.traits.StringListTrait;
import software.amazon.smithy.model.traits.StringTrait;
import software.amazon.smithy.model.traits.Trait;
import software.amazon.smithy.utils.CodeInterceptor;


/**
 * Adds provider class to use as the {@link software.amazon.smithy.model.traits.TraitService} implementation for this trait
 */
public final class ProviderSectionInjector implements CodeInterceptor<ProviderSection, TraitCodegenWriter> {
    private static final String PROVIDER_METHOD = "public Provider() {";

    @Override
    public Class<ProviderSection> sectionType() {
        return ProviderSection.class;
    }

    @Override
    public void write(TraitCodegenWriter writer, String previousText, ProviderSection section) {
        section.shape().accept(new ProviderMethodVisitor(writer, section));
    }

    private static final class ProviderMethodVisitor extends ShapeVisitor.Default<Void> {
        private final TraitCodegenWriter writer;
        private final ProviderSection section;

        private ProviderMethodVisitor(TraitCodegenWriter writer, ProviderSection section) {
            this.writer = writer;
            this.section = section;
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
                        () -> writer.write("return new $T(value.expectNumberNode().getValue().$L, value.getSourceLocation());",
                                section.traitSymbol(), section.traitSymbol().expectProperty(SymbolProperties.VALUE_GETTER)));
            });
            writer.newLine();
        }


        @Override
        public Void listShape(ListShape shape) {
            if (SymbolUtil.isJavaString(section.symbolProvider().toSymbol(shape.getMember()))) {
                generateSimpleProvider(StringListTrait.class);
                return null;
            }
            generateAbstractTraitProvider();
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
                writer.write("@Override");
                writer.openBlock("public Trait createTrait(ShapeId target, Node value) {", "}", () -> {
                    writer.write("$1T result = new NodeMapper().deserialize(value, $1T.class);", section.traitSymbol());
                    writer.write("result.setNodeCache(value);");
                    writer.write("return result;");
                });
            });
            writer.newLine();
        }

        private void generateSimpleProvider(Class<?> traitClass) {
            writer.addImport(traitClass);
            writer.openBlock("public static final class Provider extends $L.Provider<$T> {", "}",
                    traitClass.getSimpleName(), section.traitSymbol(), () -> writer.openBlock(PROVIDER_METHOD, "}",
                            () -> writer.write("super(ID, $T::new);", section.traitSymbol())));
            writer.newLine();
        }
    }
}
