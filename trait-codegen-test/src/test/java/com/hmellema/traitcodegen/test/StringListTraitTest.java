package com.hmellema.traitcodegen.test;

import com.example.generated.StringListTraitTrait;
import com.example.generated.StringTraitTrait;
import org.junit.jupiter.api.Test;
import software.amazon.smithy.model.Model;
import software.amazon.smithy.model.SourceLocation;
import software.amazon.smithy.model.node.ArrayNode;
import software.amazon.smithy.model.shapes.Shape;
import software.amazon.smithy.model.shapes.ShapeId;
import software.amazon.smithy.model.traits.Trait;
import software.amazon.smithy.model.traits.TraitFactory;
import software.amazon.smithy.utils.ListUtils;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

class StringListTraitTest {
    @Test
    void loadsFromModel() {
        Model result = Model.assembler()
                .discoverModels(getClass().getClassLoader())
                .addImport(Objects.requireNonNull(getClass().getResource("strlist-trait.smithy")))
                .assemble()
                .unwrap();
        Shape shape = result.expectShape(ShapeId.from("test.smithy.traitcodegen#myStruct"));
        StringListTraitTrait trait = shape.expectTrait(StringListTraitTrait.class);
        assertIterableEquals(ListUtils.of("a", "b", "c", "d"), trait.getValues());
    }

    @Test
    void createsTrait() {
        ShapeId id = ShapeId.from("ns.foo#foo");
        TraitFactory provider = TraitFactory.createServiceFactory();
        ArrayNode input = ArrayNode.fromStrings("a", "b", "c");
        Trait trait = provider.createTrait(StringListTraitTrait.ID, id, input).orElseThrow();
        StringListTraitTrait annotation = (StringListTraitTrait) trait;
        assertEquals(SourceLocation.NONE, annotation.getSourceLocation());
        assertEquals(trait, provider.createTrait(StringListTraitTrait.ID, id, trait.toNode()).orElseThrow());
    }
}
