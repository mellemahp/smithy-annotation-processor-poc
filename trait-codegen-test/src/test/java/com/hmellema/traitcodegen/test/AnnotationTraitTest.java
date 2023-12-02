package com.hmellema.traitcodegen.test;

import com.example.generated.BasicAnnotationTraitTrait;
import org.junit.jupiter.api.Test;
import software.amazon.smithy.model.Model;
import software.amazon.smithy.model.SourceLocation;
import software.amazon.smithy.model.node.Node;
import software.amazon.smithy.model.shapes.*;
import software.amazon.smithy.model.traits.Trait;
import software.amazon.smithy.model.traits.TraitFactory;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;


class AnnotationTraitTest {
    @Test
    void loadsFromModel() {
        Model result = Model.assembler()
                .discoverModels(getClass().getClassLoader())
                .addImport(Objects.requireNonNull(getClass().getResource("annotation-trait.smithy")))
                .assemble()
                .unwrap();
        Shape shape = result.expectShape(ShapeId.from("test.smithy.traitcodegen#myStruct$fieldA"));
        shape.expectTrait(BasicAnnotationTraitTrait.class);
    }

    @Test
    void createsTrait() {
        ShapeId id = ShapeId.from("ns.foo#foo");
        TraitFactory provider = TraitFactory.createServiceFactory();
        Trait trait = provider.createTrait(BasicAnnotationTraitTrait.ID, id, Node.objectNode()).orElseThrow();
        BasicAnnotationTraitTrait annotation = (BasicAnnotationTraitTrait) trait;
        assertEquals(SourceLocation.NONE, annotation.getSourceLocation());
        assertEquals(trait, provider.createTrait(BasicAnnotationTraitTrait.ID, id, trait.toNode()).orElseThrow());
    }
}
