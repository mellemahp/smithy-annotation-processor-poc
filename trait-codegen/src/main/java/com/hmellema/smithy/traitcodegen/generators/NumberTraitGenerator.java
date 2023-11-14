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
        writer.write("private final $B value;", directive.symbol()).writeInline("\n");
    }

    @Override
    protected void writeGetters(TraitCodegenWriter writer, GenerateTraitDirective directive) {
        writer.addImport(directive.symbol());
        writer.openBlock("public $B getValue() {", "}", directive.symbol(),
                () -> writer.write("return value;")).writeInline("\n");
    }

    @Override
    protected void writeConstructors(TraitCodegenWriter writer, GenerateTraitDirective directive) {
        writeConstructor(writer, directive.symbol());
        writeConstructorWithSourceLocation(writer, directive.symbol());
    }

    private void writeConstructorWithSourceLocation(TraitCodegenWriter writer, Symbol symbol) {
        writer.addImport(FromSourceLocation.class);
        writer.openBlock("public $T($B value, FromSourceLocation sourceLocation) {", "}", symbol, symbol, () -> {
            writer.write("super(ID, sourceLocation);");
            writer.write("this.value = value;");
        }).writeInline("\n");
    }

    private void writeConstructor(TraitCodegenWriter writer, Symbol symbol) {
        writer.addImport(SourceLocation.class);
        writer.openBlock("public $T($B value) {", "}", symbol, symbol, () -> {
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
