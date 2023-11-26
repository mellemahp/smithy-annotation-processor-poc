package com.hmellema.smithy.traitcodegen.integrations.core;

import com.hmellema.smithy.traitcodegen.SymbolProperties;
import com.hmellema.smithy.traitcodegen.utils.SymbolUtil;
import com.hmellema.smithy.traitcodegen.writer.TraitCodegenWriter;
import com.hmellema.smithy.traitcodegen.writer.sections.ToNodeSection;
import software.amazon.smithy.codegen.core.Symbol;
import software.amazon.smithy.model.node.ArrayNode;
import software.amazon.smithy.model.node.Node;
import software.amazon.smithy.model.node.NumberNode;
import software.amazon.smithy.model.node.ObjectNode;
import software.amazon.smithy.model.shapes.*;
import software.amazon.smithy.model.traits.TraitDefinition;
import software.amazon.smithy.utils.CodeInterceptor;
import software.amazon.smithy.utils.StringUtils;

import java.util.AbstractMap;
import java.util.Map;

public class ToNodeSectionInjector  implements CodeInterceptor<ToNodeSection, TraitCodegenWriter> {
    private static final String CREATE_NODE_METHOD = "protected Node createNode() {";
    private static final String TO_NODE_METHOD = "public Node toNode() {";

    @Override
    public Class<ToNodeSection> sectionType() {
        return ToNodeSection.class;
    }

    @Override
    public void write(TraitCodegenWriter writer, String previousText, ToNodeSection section) {
        writer.addImport(Node.class);
        writer.write("@Override");
        if (SymbolUtil.isTrait(section.shape())) {
            writer.openBlock(CREATE_NODE_METHOD, "}",
                    () -> section.shape().accept(new CreateNodeBodyGenerator(writer, section)));
        } else {
            writer.openBlock(TO_NODE_METHOD, "}",
                    () -> section.shape().accept(new CreateNodeBodyGenerator(writer, section)));
        }
        writer.newLine();
    }

    private static final class CreateNodeBodyGenerator extends ShapeVisitor.Default<Void> {
        private final TraitCodegenWriter writer;
        private final ToNodeSection section;

        private CreateNodeBodyGenerator(TraitCodegenWriter writer, ToNodeSection section) {
            this.writer = writer;
            this.section = section;
        }

        @Override
        protected Void getDefault(Shape shape) {
            // Do nothing by default
            return null;
        }

        @Override
        public Void listShape(ListShape shape) {
            Symbol symbol = section.symbolProvider().toSymbol(shape.getMember());
            symbol.getProperty(SymbolProperties.NODE_MAPPING_IMPORTS, Symbol.class).ifPresent(writer::addImport);
            writer.addImport(ArrayNode.class);
            writer.write("return values.stream()")
                    .indent()
                    .write(".map(s -> " + symbol.expectProperty(SymbolProperties.TO_NODE_MAPPER, String.class) + ")", "s")
                    .write(".collect(ArrayNode.collect(getSourceLocation()));")
                    .dedent();
            return null;
        }

        @Override
        public Void shortShape(ShortShape shape) {
            generateNumberTraitCreator();
            return null;
        }

        @Override
        public Void integerShape(IntegerShape shape) {
            generateNumberTraitCreator();
            return null;
        }

        @Override
        public Void intEnumShape(IntEnumShape shape) {
            generateNumberTraitCreator();
            return null;
        }

        @Override
        public Void longShape(LongShape shape) {
            generateNumberTraitCreator();
            return null;
        }

        @Override
        public Void floatShape(FloatShape shape) {
            generateNumberTraitCreator();
            return null;
        }

        @Override
        public Void doubleShape(DoubleShape shape) {
            generateNumberTraitCreator();
            return null;
        }

        @Override
        public Void bigIntegerShape(BigIntegerShape shape) {
            generateNumberTraitCreator();
            return null;
        }

        @Override
        public Void bigDecimalShape(BigDecimalShape shape) {
            generateNumberTraitCreator();
            return null;
        }

        @Override
        public Void mapShape(MapShape shape) {
            writer.addImport(ObjectNode.class);
            // If it is a string, string map use the easier syntax
            if (SymbolUtil.isJavaString(section.symbolProvider().toSymbol(shape.getKey()))
                    && SymbolUtil.isJavaString(section.symbolProvider().toSymbol(shape.getValue()))
            ) {
                writer.write("return ObjectNode.fromStringMap(values).toBuilder()")
                        .write(".sourceLocation(getSourceLocation()).build();");
                return null;
            }
            Symbol keySymbol = section.symbolProvider().toSymbol(shape.getKey());
            Symbol valueSymbol = section.symbolProvider().toSymbol(shape.getValue());
            keySymbol.getProperty(SymbolProperties.NODE_MAPPING_IMPORTS, Symbol.class).ifPresent(writer::addImport);
            valueSymbol.getProperty(SymbolProperties.NODE_MAPPING_IMPORTS, Symbol.class).ifPresent(writer::addImport);
            String keyMapper = keySymbol.expectProperty(SymbolProperties.TO_NODE_MAPPER, String.class);
            String valueMapper = valueSymbol.expectProperty(SymbolProperties.TO_NODE_MAPPER, String.class);

            writer.addImports(AbstractMap.class, Map.class);
            writer.write("return values.entrySet().stream()")
                    .indent()
                    .write(".map(entry -> new AbstractMap.SimpleImmutableEntry<>(")
                    .indent()
                    .write(keyMapper + ", " + valueMapper + "))", "entry.getKey()", "entry.getValue()")
                    .dedent()
                    .write(".collect(ObjectNode.collect(Map.Entry::getKey, Map.Entry::getValue))")
                    .write(".toBuilder().sourceLocation(getSourceLocation()).build();")
                    .dedent();
            return null;
        }

        @Override
        public Void structureShape(StructureShape shape) {
            if (!shape.members().isEmpty()) {
                writer.addImport(Node.class);
                writer.write("return Node.objectNodeBuilder()").indent();
                if (shape.hasTrait(TraitDefinition.class)) {
                    writer.write(".sourceLocation(getSourceLocation())");
                }

                // Generate all members
                // TODO: This is all quite clunky. Fix
                for (MemberShape member : shape.members()) {
                    Symbol memberSymbol = section.symbolProvider().toSymbol(member);
                    memberSymbol.getProperty(SymbolProperties.NODE_MAPPING_IMPORTS, Symbol.class).ifPresent(writer::addImport);
                    if (member.isRequired()) {
                        writer.write(".withMember($S, " + memberSymbol.expectProperty(SymbolProperties.TO_NODE_MAPPER, String.class) + ")",
                                section.symbolProvider().toMemberName(member), section.symbolProvider().toMemberName(member));
                    } else if (section.model().expectShape(member.getTarget()).isListShape()) {
                        writer.addImport(ArrayNode.class);
                        Symbol listTargetSymbol = section.symbolProvider().toSymbol(section.model().expectShape(
                                section.model().expectShape(member.getTarget()).asListShape().orElseThrow().getMember().getTarget()));
                        writer.write(".withMember($S, get$L().stream().map(s -> " + listTargetSymbol.expectProperty(SymbolProperties.TO_NODE_MAPPER, String.class)
                                        + ").collect(ArrayNode.collect()))",
                                section.symbolProvider().toMemberName(member), StringUtils.capitalize(section.symbolProvider().toMemberName(member)), "s");
                    } else if (section.model().expectShape(member.getTarget()).isMapShape()) {
                        MapShape mapShape = section.model().expectShape(member.getTarget()).asMapShape().orElseThrow();
                        Symbol keySymbol = section.symbolProvider().toSymbol(mapShape.getKey());
                        Symbol valueSymbol = section.symbolProvider().toSymbol(mapShape.getValue());
                        keySymbol.getProperty(SymbolProperties.NODE_MAPPING_IMPORTS, Symbol.class).ifPresent(writer::addImport);
                        valueSymbol.getProperty(SymbolProperties.NODE_MAPPING_IMPORTS, Symbol.class).ifPresent(writer::addImport);
                        String keyMapper = keySymbol.expectProperty(SymbolProperties.TO_NODE_MAPPER, String.class);
                        String valueMapper = valueSymbol.expectProperty(SymbolProperties.TO_NODE_MAPPER, String.class);

                        writer.addImports(AbstractMap.class, Map.class, ObjectNode.class);
                        writer.openBlock(".withMember($1S, get$1L().entrySet().stream()", ")", StringUtils.capitalize(member.getMemberName()),
                                () -> writer.write(".map(entry -> new AbstractMap.SimpleImmutableEntry<>(")
                                        .indent()
                                        .write(keyMapper + ", " + valueMapper + "))", "entry.getKey()", "entry.getValue()")
                                        .dedent()
                                        .write(".collect(ObjectNode.collect(Map.Entry::getKey, Map.Entry::getValue))"));
                    } else {
                        writer.write(".withOptionalMember($S, get$L().map(m -> " + memberSymbol.expectProperty(SymbolProperties.TO_NODE_MAPPER, String.class) + "))",
                                section.symbolProvider().toMemberName(member), StringUtils.capitalize(section.symbolProvider().toMemberName(member)), "m");
                    }
                }
                writer.write(".build();");
                writer.dedent();
            }
            return null;
        }

        private void generateNumberTraitCreator() {
            writer.addImport(NumberNode.class);
            writer.write("return new NumberNode(value, getSourceLocation());");
        }
    }
}
