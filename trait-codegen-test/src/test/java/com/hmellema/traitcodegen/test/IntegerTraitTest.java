package com.hmellema.traitcodegen.test;

import com.example.generated.HttpCodeIntegerTrait;
import org.junit.jupiter.api.Test;
import software.amazon.smithy.model.Model;
import software.amazon.smithy.model.SourceLocation;
import software.amazon.smithy.model.node.Node;
import software.amazon.smithy.model.shapes.Shape;
import software.amazon.smithy.model.shapes.ShapeId;
import software.amazon.smithy.model.traits.Trait;
import software.amazon.smithy.model.traits.TraitFactory;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

class IntegerTraitTest {
    @Test
    void loadsFromModel() {
        Model result = Model.assembler()
                .discoverModels(getClass().getClassLoader())
                .addImport(Objects.requireNonNull(getClass().getResource("integer-trait.smithy")))
                .assemble()
                .unwrap();
        Shape shape = result.expectShape(ShapeId.from("test.smithy.traitcodegen#myStruct"));
        HttpCodeIntegerTrait trait = shape.expectTrait(HttpCodeIntegerTrait.class);
        assertEquals(1, trait.getValue());
    }

    @Test
    void createsTrait() {
        ShapeId id = ShapeId.from("ns.foo#foo");
        TraitFactory provider = TraitFactory.createServiceFactory();
        Trait trait = provider.createTrait(HttpCodeIntegerTrait.ID, id, Node.from(1)).orElseThrow();
        HttpCodeIntegerTrait annotation = (HttpCodeIntegerTrait) trait;
        assertEquals(SourceLocation.NONE, annotation.getSourceLocation());
        assertEquals(trait, provider.createTrait(HttpCodeIntegerTrait.ID, id, trait.toNode()).orElseThrow());
    }
}
