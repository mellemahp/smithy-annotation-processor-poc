package com.hmellema.smithy.traitcodegen.integrations.core;

import com.hmellema.smithy.traitcodegen.SymbolProperties;
import com.hmellema.smithy.traitcodegen.utils.SymbolUtil;
import com.hmellema.smithy.traitcodegen.writer.TraitCodegenWriter;
import com.hmellema.smithy.traitcodegen.writer.sections.FromNodeSection;
import software.amazon.smithy.codegen.core.Symbol;
import software.amazon.smithy.model.node.Node;
import software.amazon.smithy.model.shapes.*;
import software.amazon.smithy.utils.CodeInterceptor;
import software.amazon.smithy.utils.StringUtils;

import java.util.Iterator;

public class FromNodeSectionInjector implements CodeInterceptor<FromNodeSection, TraitCodegenWriter> {
    private static final String FROM_NODE_METHOD_TEMPLATE = "public static $T fromNode(Node node) {";
    @Override
    public Class<FromNodeSection> sectionType() {
        return FromNodeSection.class;
    }

    @Override
    public void write(TraitCodegenWriter writer, String previousText, FromNodeSection section) {
        writer.openDocstring();
        writer.writeDocStringContents("Creates a {@link $T} from a {@link Node}.", section.symbol());
        writer.writeDocStringContents("");
        writer.writeDocStringContents("@param node Node to create the $T from.", section.symbol());
        writer.writeDocStringContents("@return Returns the created $T.", section.symbol());
        writer.writeDocStringContents("@throws ExpectationNotMetException if the given Node is invalid.");
        writer.closeDocstring();

        writer.addImport(Node.class);
        writer.openBlock(FROM_NODE_METHOD_TEMPLATE, "}", section.symbol(),
                () -> section.shape().accept(new FromNodeBodyGenerator(writer, section)));
        writer.newLine();
    }

    private static final class FromNodeBodyGenerator extends ShapeVisitor.Default<Void> {
        private static final String BUILDER_INITIALIZER = "Builder builder = builder();";
        private static final String BUILD_AND_RETURN = "return builder.build();";

        private final TraitCodegenWriter writer;
        private final FromNodeSection section;

        private FromNodeBodyGenerator(TraitCodegenWriter writer, FromNodeSection section) {
            this.writer = writer;
            this.section = section;
        }

        @Override
        protected Void getDefault(Shape shape) {
            return null;
        }

        @Override
        public Void listShape(ListShape shape) {
            Symbol memberSymbol = section.symbolProvider().toSymbol(shape.getMember());
            writer.write(BUILDER_INITIALIZER);
            writer.write("node.expectArrayNode()");
            writer.indent();
            writer.write(".getElements().stream()");
            writer.write(".map(n -> " + memberSymbol.expectProperty(SymbolProperties.FROM_NODE_MAPPER) + ")", "n");
            writer.write(".forEach(builder::addValuesItem);");
            writer.dedent();
            writer.write(BUILD_AND_RETURN);

            return null;
        }

        @Override
        public Void mapShape(MapShape shape) {
            writer.write(BUILDER_INITIALIZER);
            Symbol keySymbol = section.symbolProvider().toSymbol(shape.getKey());
            Symbol valueSymbol = section.symbolProvider().toSymbol(shape.getValue());
            writer.openBlock("node.expectObjectNode().getMembers().forEach((k, v) -> {", "});",
                    () -> writer.write("builder.putValues("
                                    + keySymbol.expectProperty(SymbolProperties.FROM_NODE_MAPPER, String.class) + ", "
                                    + valueSymbol.expectProperty(SymbolProperties.FROM_NODE_MAPPER, String.class) + ");",
                            "k", "v"));
            writer.write(BUILD_AND_RETURN);
            return null;
        }

        @Override
        public Void intEnumShape(IntEnumShape shape) {
            writer.write("return $T.valueOf(node.expectNumberNode().getValue().intValue());", section.symbol());
            return null;
        }

        @Override
        public Void enumShape(EnumShape shape) {
            writer.openBlock("return $T.valueOf(node.expectStringNode()", ");", section.symbol(), () -> {
                writer.putContext("enumVariants", shape.getEnumValues());
                writer.write(".expectOneOf(${#enumVariants}${key:S}${^key.last},${/key.last}${/enumVariants})");
            });
            return null;
        }

        @Override
        public Void structureShape(StructureShape shape) {
            writer.write(BUILDER_INITIALIZER);
            writer.write("node.expectObjectNode().warnIfAdditionalProperties(PROPERTIES)");
            writer.indent();
            Iterator<MemberShape> memberIterator = shape.members().iterator();
            while (memberIterator.hasNext()) {
                MemberShape member = memberIterator.next();
                member.accept(new MemberGenerator(member, writer, section));
                if (memberIterator.hasNext()) {
                    writer.writeInline("\n");
                } else {
                    writer.writeInline(";\n");
                }
            }
            writer.dedent();
            writer.write(BUILD_AND_RETURN);

            return null;
        }
    }

    private static final class MemberGenerator extends ShapeVisitor.Default<Void> {
        private final MemberShape member;
        private final TraitCodegenWriter writer;
        private final FromNodeSection section;

        private MemberGenerator(MemberShape member, TraitCodegenWriter writer, FromNodeSection section) {
            this.member = member;
            this.writer = writer;
            this.section = section;
            this.writer.putContext("memberPrefix", member.isRequired() ? ".expect" : ".get");
        }


        @Override
        public Void memberShape(MemberShape shape) {
            return section.model().expectShape(shape.getTarget()).accept(this);
        }

        @Override
        protected Void getDefault(Shape shape) {
            return null;
        }

        @Override
        public Void booleanShape(BooleanShape shape) {
            writer.writeInline("$memberPrefix:LBooleanMember($1S, builder::$1L)", section.symbolProvider().toMemberName(member));
            return null;
        }

        @Override
        public Void listShape(ListShape shape) {
            writer.writeInline("$memberPrefix:LArrayMember($S, n -> "
                    + section.symbolProvider().toSymbol(shape.getMember()).expectProperty(SymbolProperties.FROM_NODE_MAPPER, String.class)
                    + ", builder::$L)", section.symbolProvider().toMemberName(member), "n", section.symbolProvider().toMemberName(member));
            return null;
        }

        @Override
        public Void shortShape(ShortShape shape) {
            generateNumberMember(shape);
            return null;
        }

        @Override
        public Void integerShape(IntegerShape shape) {
            generateNumberMember(shape);
            return null;
        }

        @Override
        public Void longShape(LongShape shape) {
            generateNumberMember(shape);
            return null;
        }

        @Override
        public Void floatShape(FloatShape shape) {
            generateNumberMember(shape);
            return null;
        }

        @Override
        public Void doubleShape(DoubleShape shape) {
            generateNumberMember(shape);
            return null;
        }

        @Override
        public Void bigIntegerShape(BigIntegerShape shape) {
            generateNumberMember(shape);
            return null;
        }

        @Override
        public Void bigDecimalShape(BigDecimalShape shape) {
            generateNumberMember(shape);
            return null;
        }

        @Override
        public Void mapShape(MapShape shape) {
            String keyMapper = section.symbolProvider().toSymbol(shape.getKey()).expectProperty(SymbolProperties.FROM_NODE_MAPPER, String.class);
            String valueMapper = section.symbolProvider().toSymbol(shape.getValue()).expectProperty(SymbolProperties.FROM_NODE_MAPPER, String.class);
            writer.disableNewlines();
            writer.openBlock("$memberPrefix:LObjectMember($S, o -> o.getMembers().forEach((k, v) -> {\n", "}))", section.symbolProvider().toMemberName(member),
                    () -> writer.write("builder.put$L(" + keyMapper + ", " + valueMapper + ");\n",
                            StringUtils.capitalize(section.symbolProvider().toMemberName(member)), "k", "v"));
            writer.enableNewlines();
            return null;
        }

        private void generateNumberMember(NumberShape shape) {
            writer.writeInline("$memberPrefix:LNumberMember($1S, n -> builder.$1L(n.$L))",
                    section.symbolProvider().toMemberName(member),
                    section.symbolProvider().toSymbol(shape).expectProperty(SymbolProperties.VALUE_GETTER));
        }

        @Override
        public Void stringShape(StringShape shape) {
            if (SymbolUtil.isJavaString(section.symbolProvider().toSymbol(shape))) {
                writer.writeInline("$memberPrefix:LStringMember($1S, builder::$1L)", section.symbolProvider().toMemberName(member));
            } else {
                generateGenericMember(shape);
            }
            return null;
        }

        @Override
        public Void structureShape(StructureShape shape) {
            generateGenericMember(shape);
            return null;
        }

        private void generateGenericMember(Shape shape) {
            writer.writeInline("$memberPrefix:LMember($S, n -> "
                    + section.symbolProvider().toSymbol(shape).expectProperty(SymbolProperties.FROM_NODE_MAPPER, String.class)
                    + ", builder::$L)", section.symbolProvider().toMemberName(member), "n", section.symbolProvider().toMemberName(member));
        }
    }
}
