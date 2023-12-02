package com.hmellema.traitcodegen.test;

import com.example.generated.BasicAnnotationTraitTrait;
import org.junit.jupiter.api.Test;
import software.amazon.smithy.model.Model;
import software.amazon.smithy.model.node.NodeMapper;
import software.amazon.smithy.model.shapes.*;

import java.util.Objects;

public class TestAnnotationTrait {
    @Test
    public void loadsFromModel() {
        Model result = Model.assembler()
                .discoverModels(getClass().getClassLoader())
                .addImport(Objects.requireNonNull(getClass().getResource("annotation-trait.smithy")))
                .assemble()
                .unwrap();
        Shape shape = result.expectShape(ShapeId.from("test.smithy.traitcodegen#myStruct$fieldA"));
        shape.expectTrait(BasicAnnotationTraitTrait.class);
    }
}
