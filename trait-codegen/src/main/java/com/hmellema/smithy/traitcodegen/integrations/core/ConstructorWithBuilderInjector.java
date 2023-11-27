package com.hmellema.smithy.traitcodegen.integrations.core;

import com.hmellema.smithy.traitcodegen.SymbolProperties;
import com.hmellema.smithy.traitcodegen.utils.SymbolUtil;
import com.hmellema.smithy.traitcodegen.writer.TraitCodegenWriter;
import com.hmellema.smithy.traitcodegen.writer.sections.ConstructorWithBuilderSection;
import software.amazon.smithy.model.shapes.*;
import software.amazon.smithy.utils.CodeInterceptor;
import software.amazon.smithy.utils.SmithyBuilder;

public final class ConstructorWithBuilderInjector implements CodeInterceptor<ConstructorWithBuilderSection, TraitCodegenWriter> {
    @Override
    public Class<ConstructorWithBuilderSection> sectionType() {
        return ConstructorWithBuilderSection.class;
    }

    @Override
    public void write(TraitCodegenWriter writer, String previousText, ConstructorWithBuilderSection section) {
        writer.openBlock("private $T(Builder builder) {", "}", section.symbol(), () -> {
            if (SymbolUtil.isTrait(section.shape())) {
                writer.write("super(ID, builder.getSourceLocation());");
            }
            section.shape().accept(new InitializerVisitor(writer, section));
        });
        writer.newLine();
    }

    private static final class InitializerVisitor extends ShapeVisitor.Default<Void> {
        private final TraitCodegenWriter writer;
        private final ConstructorWithBuilderSection section;

        private InitializerVisitor(TraitCodegenWriter writer, ConstructorWithBuilderSection section) {
            this.writer = writer;
            this.section = section;
        }

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
                    writer.write("this.$1L = SmithyBuilder.requiredState($1S, $2L);", SymbolUtil.toMemberNameOrValues(member, section.model(), section.symbolProvider()), getBuilderValue(member));
                } else {
                    writer.write("this.$L = $L;", SymbolUtil.toMemberNameOrValues(member, section.model(), section.symbolProvider()), getBuilderValue(member));
                }
            }
            return null;
        }

        private String getBuilderValue(MemberShape member) {
            if (section.symbolProvider().toSymbol(member).getProperty(SymbolProperties.BUILDER_REF_INITIALIZER).isPresent()) {
                return writer.format("builder.$L.copy()", SymbolUtil.toMemberNameOrValues(member, section.model(), section.symbolProvider()));
            } else {
                return writer.format("builder.$L", SymbolUtil.toMemberNameOrValues(member, section.model(), section.symbolProvider()));
            }
        }

        private void writeValuesInitializer() {
            writer.write("this.values = builder.values.copy();");
        }
    }
}
