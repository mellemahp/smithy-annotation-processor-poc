package com.hmellema.traitcodegen.test;

import com.example.generated.BasicAnnotationTraitTrait;
import com.example.generated.StringTraitTrait;
import org.junit.jupiter.api.Test;
import software.amazon.smithy.model.Model;
import software.amazon.smithy.model.shapes.Shape;
import software.amazon.smithy.model.shapes.ShapeId;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestStringTrait {
    @Test
    public void loadsFromModel() {
        Model result = Model.assembler()
                .discoverModels(getClass().getClassLoader())
                .addImport(Objects.requireNonNull(getClass().getResource("string-trait.smithy")))
                .assemble()
                .unwrap();
        Shape shape = result.expectShape(ShapeId.from("test.smithy.traitcodegen#myStruct$fieldA"));
        StringTraitTrait trait = shape.expectTrait(StringTraitTrait.class);
        assertEquals(trait.getValue(), "Testing String Trait");
    }
}
