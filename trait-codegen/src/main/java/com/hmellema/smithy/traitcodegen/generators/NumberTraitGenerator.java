package com.hmellema.smithy.traitcodegen.generators;

import com.hmellema.smithy.traitcodegen.directives.GenerateTraitDirective;
import com.hmellema.smithy.traitcodegen.writer.TraitCodegenWriter;
import software.amazon.smithy.codegen.core.Symbol;
import software.amazon.smithy.model.FromSourceLocation;
import software.amazon.smithy.model.SourceLocation;
import software.amazon.smithy.model.node.Node;
import software.amazon.smithy.model.node.NumberNode;
import software.amazon.smithy.model.traits.AbstractTrait;

public class NumberTraitGenerator extends TraitGenerator {
    @Override
    protected void writeAdditionalProperties(TraitCodegenWriter writer, GenerateTraitDirective directive) {
        writer.write("private final $T value;", directive.baseSymbol()).writeInline("\n");
    }

    @Override
    protected void writeGetters(TraitCodegenWriter writer, GenerateTraitDirective directive) {
        writer.openBlock("public $T getValue() {", "}", directive.baseSymbol(),
                () -> writer.write("return value;")).writeInline("\n");
    }

    @Override
    protected void writeConstructors(TraitCodegenWriter writer, GenerateTraitDirective directive) {
        writeConstructor(writer, directive.traitSymbol(), directive.baseSymbol());
        writeConstructorWithSourceLocation(writer, directive.traitSymbol(), directive.baseSymbol());
    }

    private void writeConstructorWithSourceLocation(TraitCodegenWriter writer, Symbol traitSymbol, Symbol baseSymbol) {
        writer.addImport(FromSourceLocation.class);
        writer.openBlock("public $T($T value, FromSourceLocation sourceLocation) {", "}",
                traitSymbol, baseSymbol, () -> {
            writer.write("super(ID, sourceLocation);");
            writer.write("this.value = value;");
        }).writeInline("\n");
    }

    private void writeConstructor(TraitCodegenWriter writer, Symbol traitSymbol, Symbol baseSymbol) {
        writer.addImport(SourceLocation.class);
        writer.openBlock("public $T($T value) {", "}",
                traitSymbol, baseSymbol, () -> {
            writer.write("super(ID, SourceLocation.NONE);");
            writer.write("this.value = value;");
        }).writeInline("\n");
    }

    @Override
    protected void writeAdditionalMethods(TraitCodegenWriter writer, GenerateTraitDirective directive) {
        writer.addImport(Node.class);
        writer.addImport(NumberNode.class);
        writer.write("@Override");
        writer.openBlock("protected final Node createNode() {", "}",
                () -> writer.write("return new NumberNode(value, getSourceLocation());"));
        writer.write("");
    }

    @Override
    protected Class<?> getTraitClass() {
        return AbstractTrait.class;
    }
}
