package com.hmellema.smithy.traitcodegen.generators.common;

import com.hmellema.smithy.traitcodegen.SymbolProperties;
import com.hmellema.smithy.traitcodegen.utils.SymbolUtil;
import com.hmellema.smithy.traitcodegen.writer.TraitCodegenWriter;
import software.amazon.smithy.codegen.core.Symbol;
import software.amazon.smithy.codegen.core.SymbolProvider;
import software.amazon.smithy.model.Model;
import software.amazon.smithy.model.shapes.*;
import software.amazon.smithy.model.traits.TraitDefinition;
import software.amazon.smithy.utils.SmithyBuilder;


public class BuilderConstructorGenerator implements Runnable {
    private final TraitCodegenWriter writer;
    private final Symbol symbol;
    private final Shape shape;
    private final SymbolProvider symbolProvider;
    private final Model model;
    private final boolean isTrait;

    public BuilderConstructorGenerator(TraitCodegenWriter writer, Symbol symbol, Shape shape, SymbolProvider symbolProvider, Model model) {
        this.writer = writer;
        this.symbol = symbol;
        this.shape = shape;
        this.symbolProvider = symbolProvider;
        this.model = model;
        this.isTrait = shape.hasTrait(TraitDefinition.class);
    }

    @Override
    public void run() {
        writer.openBlock("private $T(Builder builder) {", "}", symbol, () -> {
            if (isTrait) {
                writer.write("super(ID, builder.getSourceLocation());");
            }
            shape.accept(new InitializerVisitor());
        });
    }

    private final class InitializerVisitor extends ShapeVisitor.Default<Void> {
        @Override
        protected Void getDefault(Shape shape) {
            throw new UnsupportedOperationException("Does not support shape of type " + shape.getType());
        }

        @Override
        public Void listShape(ListShape shape) {
            writeValuesInitializer();
            return null;
        }

        @Override
        public Void mapShape(MapShape shape) {
            writeValuesInitializer();
            return null;
        }

        @Override
        public Void structureShape(StructureShape shape) {
            for (MemberShape member : shape.members()) {
                if (member.isRequired()) {
                    writer.addImport(SmithyBuilder.class);
                    writer.write("this.$1L = SmithyBuilder.requiredState($1S, $2L);", SymbolUtil.toMemberNameOrValues(member, model, symbolProvider), getBuilderValue(member));
                } else {
                    writer.write("this.$L = $L;", SymbolUtil.toMemberNameOrValues(member, model, symbolProvider), getBuilderValue(member));
                }
            }
            return null;
        }

        private String getBuilderValue(MemberShape member) {
            if (symbolProvider.toSymbol(member).getProperty(SymbolProperties.BUILDER_REF_INITIALIZER).isPresent()) {
                return writer.format("builder.$L.copy()", SymbolUtil.toMemberNameOrValues(member, model, symbolProvider));
            } else {
                return writer.format("builder.$L", SymbolUtil.toMemberNameOrValues(member, model, symbolProvider));
            }
        }

        private void writeValuesInitializer() {
            writer.write("this.values = builder.values.copy();");
        }
    }
}