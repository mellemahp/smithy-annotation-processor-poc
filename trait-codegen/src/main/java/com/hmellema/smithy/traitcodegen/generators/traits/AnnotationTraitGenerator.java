package com.hmellema.smithy.traitcodegen.generators.traits;

import com.hmellema.smithy.traitcodegen.directives.GenerateTraitDirective;
import com.hmellema.smithy.traitcodegen.writer.TraitCodegenWriter;
import software.amazon.smithy.codegen.core.Symbol;
import software.amazon.smithy.model.SourceLocation;
import software.amazon.smithy.model.node.Node;
import software.amazon.smithy.model.node.ObjectNode;
import software.amazon.smithy.model.traits.AnnotationTrait;
import software.amazon.smithy.utils.MapUtils;

public class AnnotationTraitGenerator extends TraitGenerator {
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
    protected void writeConstructors(TraitCodegenWriter writer, GenerateTraitDirective directive) {
        writeConstructor(writer, directive.traitSymbol());
        writeEmptyConstructor(writer, directive.traitSymbol());
        writeConstructorWithSourceLocation(writer, directive.traitSymbol());
    }

    private void writeConstructor(TraitCodegenWriter writer, Symbol symbol) {
        writer.addImport(ObjectNode.class);
        writer.openBlock("public $L(ObjectNode node) {", "}", symbol.getName(),
                () -> writer.write("super(ID, node);")).writeInline("\n");
    }

    private void writeEmptyConstructor(TraitCodegenWriter writer, Symbol symbol) {
        writer.addImport(Node.class);
        writer.openBlock("public $L() {", "}", symbol.getName(),
                () -> writer.write("super(ID, Node.objectNode());")).writeInline("\n");
    }

    private void writeConstructorWithSourceLocation(TraitCodegenWriter writer, Symbol symbol) {
        writer.addImports(SourceLocation.class, ObjectNode.class, MapUtils.class);
        writer.openBlock("public $L(SourceLocation sourceLocation) {", "}", symbol.getName(),
                () -> writer.write("this(new ObjectNode(MapUtils.of(), sourceLocation));")).writeInline("\n");
    }

}
