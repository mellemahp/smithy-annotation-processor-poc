package com.hmellema.smithy.traitcodegen.generators;

import com.hmellema.smithy.traitcodegen.directives.GenerateTraitDirective;
import com.hmellema.smithy.traitcodegen.writer.TraitCodegenWriter;
import software.amazon.smithy.codegen.core.Symbol;
import software.amazon.smithy.model.SourceLocation;
import software.amazon.smithy.model.node.Node;
import software.amazon.smithy.model.node.ObjectNode;
import software.amazon.smithy.model.traits.AnnotationTrait;
import software.amazon.smithy.utils.MapUtils;

public class AnnotationTraitGenerator extends TraitGenerator {
    @Override
    protected void writeConstructors(TraitCodegenWriter writer, GenerateTraitDirective directive) {
        writeConstructor(writer, directive.traitSymbol());
        writeEmptyConstructor(writer, directive.traitSymbol());
        writeConstructorWithSourceLocation(writer, directive.traitSymbol());
    }

    @Override
    protected Class<?> getTraitClass() {
        return AnnotationTrait.class;
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
        writer.addImport(SourceLocation.class);
        writer.addImport(ObjectNode.class);
        writer.addImport(MapUtils.class);
        writer.openBlock("public $L(SourceLocation sourceLocation) {", "}", symbol.getName(),
                () -> writer.write("this(new ObjectNode(MapUtils.of(), sourceLocation));")).writeInline("\n");
    }
}
