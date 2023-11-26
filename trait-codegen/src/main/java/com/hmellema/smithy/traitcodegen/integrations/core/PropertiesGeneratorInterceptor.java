package com.hmellema.smithy.traitcodegen.integrations.core;

import com.hmellema.smithy.traitcodegen.utils.SymbolUtil;
import com.hmellema.smithy.traitcodegen.writer.TraitCodegenWriter;
import com.hmellema.smithy.traitcodegen.writer.sections.PropertiesSection;
import com.hmellema.smithy.traitcodegen.writer.sections.PropertySection;
import software.amazon.smithy.codegen.core.SymbolProvider;
import software.amazon.smithy.model.shapes.*;
import software.amazon.smithy.model.traits.EnumValueTrait;
import software.amazon.smithy.utils.CodeInterceptor;
import software.amazon.smithy.utils.SetUtils;

import java.util.Map;
import java.util.Set;

public class PropertiesGeneratorInterceptor implements CodeInterceptor<PropertiesSection, TraitCodegenWriter> {
    @Override
    public Class<PropertiesSection> sectionType() {
        return PropertiesSection.class;
    }

    @Override
    public void write(TraitCodegenWriter writer, String previousText, PropertiesSection section) {
        section.shape().accept(new PropertyGenerator(writer, section.symbolProvider()));
    }

    private static final class PropertyGenerator extends ShapeVisitor.Default<Void> {
        private static final String PROPERTY_TEMPLATE = "private final $T $L;";

        private final TraitCodegenWriter writer;
        private final SymbolProvider symbolProvider;

        public PropertyGenerator(TraitCodegenWriter writer, SymbolProvider symbolProvider) {
            this.writer = writer;
            this.symbolProvider = symbolProvider;
        }

        @Override
        protected Void getDefault(Shape shape) {
            return null;
        }

        @Override
        public Void listShape(ListShape shape) {
            // Lists that contain only Strings members do not need additional properties
            if (!SymbolUtil.isJavaString(symbolProvider.toSymbol(shape.getMember()))) {
                createValuesProperty(shape);
            }
            return null;
        }

        @Override
        public Void shortShape(ShortShape shape) {
            createValueProperty(shape);
            return null;
        }

        @Override
        public Void integerShape(IntegerShape shape) {
            createValueProperty(shape);
            return null;
        }

        @Override
        public Void intEnumShape(IntEnumShape shape) {
            writer.addImport(Integer.class);
            writer.write("private final Integer value;");
            for (Map.Entry<String, MemberShape> memberEntry : shape.getAllMembers().entrySet()) {
                writer.write("public static final Integer $L = $L;", memberEntry.getKey(),
                        memberEntry.getValue().expectTrait(EnumValueTrait.class).expectIntValue());
            }

            return null;
        }

        @Override
        public Void longShape(LongShape shape) {
            createValueProperty(shape);
            return null;
        }

        @Override
        public Void floatShape(FloatShape shape) {
            createValueProperty(shape);
            return null;
        }

        @Override
        public Void doubleShape(DoubleShape shape) {
            createValueProperty(shape);
            return null;
        }

        @Override
        public Void bigIntegerShape(BigIntegerShape shape) {
            createValueProperty(shape);
            return null;
        }

        @Override
        public Void bigDecimalShape(BigDecimalShape shape) {
            createValueProperty(shape);
            return null;
        }

        @Override
        public Void mapShape(MapShape shape) {
            createValuesProperty(shape);
            return null;
        }

        @Override
        public Void stringShape(StringShape shape) {
            // String Traits do not need any additional properties.
            return null;
        }

        @Override
        public Void enumShape(EnumShape shape) {
            for (Map.Entry<String, MemberShape> memberEntry : shape.getAllMembers().entrySet()) {
                writer.write("public static final String $L = $S;", memberEntry.getKey(),
                        memberEntry.getValue().expectTrait(EnumValueTrait.class).expectStringValue());
            }
            return null;
        }

        @Override
        public Void structureShape(StructureShape shape) {
            if (!shape.members().isEmpty()) {
                writer.addImports(Set.class, SetUtils.class);
                writer.putContext("properties", shape.getAllMembers());
                writer.openBlock("private static final Set<String> PROPERTIES = SetUtils.of(", ");",
                        () -> writer.write("${#properties}${key:S}${^key.last}, ${/key.last}${/properties}"));
            }

            for (MemberShape member : shape.members()) {
                writer.pushState(new PropertySection(shape));
                writer.write(PROPERTY_TEMPLATE, symbolProvider.toSymbol(member), symbolProvider.toMemberName(member));
                writer.popState();
            }

            return null;
        }

        private void createValueProperty(Shape shape) {
            writer.write("private final $T value;", symbolProvider.toSymbol(shape));
        }

        private void createValuesProperty(Shape shape) {
            writer.write("private final $T values;", symbolProvider.toSymbol(shape));
        }
    }
}
