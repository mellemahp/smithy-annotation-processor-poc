package com.hmellema.traitcodegen.test;

import com.example.generated.HttpCodeFloatTrait;
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

class FloatTraitTest {
    @Test
    void loadsFromModel() {
        Model result = Model.assembler()
                .discoverModels(getClass().getClassLoader())
                .addImport(Objects.requireNonNull(getClass().getResource("float-trait.smithy")))
                .assemble()
                .unwrap();
        Shape shape = result.expectShape(ShapeId.from("test.smithy.traitcodegen#myStruct"));
        HttpCodeFloatTrait trait = shape.expectTrait(HttpCodeFloatTrait.class);
        assertEquals(1.1, trait.getValue(), 0.0001);
    }

    @Test
    void createsTrait() {
        ShapeId id = ShapeId.from("ns.foo#foo");
        TraitFactory provider = TraitFactory.createServiceFactory();
        Trait trait = provider.createTrait(HttpCodeFloatTrait.ID, id, Node.from(1.2)).orElseThrow();
        HttpCodeFloatTrait annotation = (HttpCodeFloatTrait) trait;
        assertEquals(SourceLocation.NONE, annotation.getSourceLocation());
        assertEquals(trait, provider.createTrait(HttpCodeFloatTrait.ID, id, trait.toNode()).orElseThrow());
    }
}
