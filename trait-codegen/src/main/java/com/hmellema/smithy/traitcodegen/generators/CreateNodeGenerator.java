package com.hmellema.smithy.traitcodegen.generators;

import com.hmellema.smithy.traitcodegen.writer.TraitCodegenWriter;
import software.amazon.smithy.model.node.Node;

public class CreateNodeGenerator implements Runnable {
    private final TraitCodegenWriter writer;


    public CreateNodeGenerator(TraitCodegenWriter writer) {
        this.writer = writer;
    }

    @Override
    public void run() {
        writer.addImport(Node.class);
        writer.write("@Override");
        writer.openBlock("protected Node createNode() {", "}", () -> {
            writer.write("return null;");
        });
    }
}
