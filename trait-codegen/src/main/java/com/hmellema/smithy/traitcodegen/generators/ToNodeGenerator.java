package com.hmellema.smithy.traitcodegen.generators;

import com.hmellema.smithy.traitcodegen.writer.TraitCodegenWriter;
import software.amazon.smithy.codegen.core.Symbol;
import software.amazon.smithy.codegen.core.SymbolProvider;
import software.amazon.smithy.model.Model;
import software.amazon.smithy.model.node.Node;
import software.amazon.smithy.model.node.ToNode;
import software.amazon.smithy.model.shapes.Shape;

final class ToNodeGenerator implements Runnable {
    private final Shape shape;
    private final TraitCodegenWriter writer;

    ToNodeGenerator(Shape shape, TraitCodegenWriter writer) {
        this.shape = shape;
        this.writer = writer;
    }

    @Override
    public void run() {
        writer.addImport(ToNode.class);
        writer.addImport(Node.class);
        writer.write("@Override");
        writer.openBlock("public Node toNode() {", "}", () -> {
            writer.write("return null;");
        });
        writer.write("");
    }
}
