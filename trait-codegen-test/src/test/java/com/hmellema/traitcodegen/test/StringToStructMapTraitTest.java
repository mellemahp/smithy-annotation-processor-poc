package com.hmellema.traitcodegen.test;

import com.example.generated.MapValue;
import com.example.generated.StringStringMapTrait;
import com.example.generated.StringToStructMapTrait;
import org.junit.jupiter.api.Test;
import software.amazon.smithy.model.Model;
import software.amazon.smithy.model.SourceLocation;
import software.amazon.smithy.model.node.Node;
import software.amazon.smithy.model.shapes.Shape;
import software.amazon.smithy.model.shapes.ShapeId;
import software.amazon.smithy.model.traits.Trait;
import software.amazon.smithy.model.traits.TraitFactory;
import software.amazon.smithy.utils.BuilderRef;
import software.amazon.smithy.utils.MapUtils;

import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

class StringToStructMapTraitTest {
    @Test
    void loadsFromModel() {
        Model result = Model.assembler()
                .discoverModels(getClass().getClassLoader())
                .addImport(Objects.requireNonNull(getClass().getResource("string-struct-map-trait.smithy")))
                .assemble()
                .unwrap();
        Shape shape = result.expectShape(ShapeId.from("test.smithy.traitcodegen#myStruct"));
        StringToStructMapTrait trait = shape.expectTrait(StringToStructMapTrait.class);
        BuilderRef<Map<String, MapValue>> ref = BuilderRef.forOrderedMap();
        ref.get().put("one", MapValue.builder().a("foo").b(2).build());
        ref.get().put("two", MapValue.builder().a("bar").b(4).build());
        // TODO: Doesnt work right because of lack of struct equals impl
        //assertIterableEquals(ref.copy().entrySet(), trait.getValues().entrySet());
    }

    @Test
    void createsTrait() {
        ShapeId id = ShapeId.from("ns.foo#foo");
        TraitFactory provider = TraitFactory.createServiceFactory();
        Node node = StringToStructMapTrait.builder()
                .putValues("one", MapValue.builder().a("foo").b(2).build())
                .putValues("two", MapValue.builder().a("bar").b(4).build())
                .build().toNode();
        Trait trait = provider.createTrait(StringToStructMapTrait.ID, id, node).orElseThrow();
        StringToStructMapTrait annotation = (StringToStructMapTrait) trait;
        assertEquals(SourceLocation.NONE, annotation.getSourceLocation());
        assertEquals(trait, provider.createTrait(StringToStructMapTrait.ID, id, trait.toNode()).orElseThrow());
    }
}
