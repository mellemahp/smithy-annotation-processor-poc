package com.hmellema.smithy.traitcodegen.generators.traits;

import com.hmellema.smithy.traitcodegen.GenerateTraitDirective;
import com.hmellema.smithy.traitcodegen.generators.common.GetterGenerator;
import com.hmellema.smithy.traitcodegen.writer.TraitCodegenWriter;
import software.amazon.smithy.codegen.core.Symbol;
import software.amazon.smithy.model.SourceLocation;
import software.amazon.smithy.model.node.Node;
import software.amazon.smithy.model.node.ObjectNode;
import software.amazon.smithy.model.traits.AnnotationTrait;
import software.amazon.smithy.utils.MapUtils;

final class AnnotationTraitGenerator extends TraitGenerator {
    private static final String CLASS_TEMPLATE = "public final class $T extends AnnotationTrait {";

    @Override
    protected void imports(TraitCodegenWriter writer) {
        writer.addImport(AnnotationTrait.class);
    }

    @Override
    protected String getClassDefinition() {
        return CLASS_TEMPLATE;
    }

    @Override
    protected void writeTraitBody(TraitCodegenWriter writer, GenerateTraitDirective directive) {
        writeConstructor(writer, directive.traitSymbol());
        writeEmptyConstructor(writer, directive.traitSymbol());
        writeConstructorWithSourceLocation(writer, directive.traitSymbol());
        new GetterGenerator(writer, directive.symbolProvider(), directive.shape(), directive.model()).run();
    }

    private void writeConstructor(TraitCodegenWriter writer, Symbol symbol) {
        writer.addImport(ObjectNode.class);
        writer.openBlock("public $T(ObjectNode node) {", "}", symbol,
                () -> writer.write("super(ID, node);"));
        writer.newLine();
    }

    private void writeEmptyConstructor(TraitCodegenWriter writer, Symbol symbol) {
        writer.addImport(Node.class);
        writer.openBlock("public $T() {", "}", symbol,
                () -> writer.write("super(ID, Node.objectNode());"));
        writer.newLine();
    }

    private void writeConstructorWithSourceLocation(TraitCodegenWriter writer, Symbol symbol) {
        writer.addImports(SourceLocation.class, ObjectNode.class, MapUtils.class);
        writer.openBlock("public $T(SourceLocation sourceLocation) {", "}", symbol,
                () -> writer.write("this(new ObjectNode(MapUtils.of(), sourceLocation));"));
        writer.newLine();
    }
}
