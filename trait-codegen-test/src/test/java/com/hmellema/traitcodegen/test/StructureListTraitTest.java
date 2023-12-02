package com.hmellema.traitcodegen.test;

import com.example.generated.ListMember;
import com.example.generated.StructureListTraitTrait;
import org.junit.jupiter.api.Test;
import software.amazon.smithy.model.Model;
import software.amazon.smithy.model.SourceLocation;
import software.amazon.smithy.model.node.ArrayNode;
import software.amazon.smithy.model.node.Node;
import software.amazon.smithy.model.shapes.Shape;
import software.amazon.smithy.model.shapes.ShapeId;
import software.amazon.smithy.model.traits.Trait;
import software.amazon.smithy.model.traits.TraitFactory;
import software.amazon.smithy.utils.ListUtils;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StructureListTraitTest {
    @Test
    void loadsFromModel() {
        Model result = Model.assembler()
                .discoverModels(getClass().getClassLoader())
                .addImport(Objects.requireNonNull(getClass().getResource("structure-list-trait.smithy")))
                .assemble()
                .unwrap();
        Shape shape = result.expectShape(ShapeId.from("test.smithy.traitcodegen#myStruct"));
        StructureListTraitTrait trait = shape.expectTrait(StructureListTraitTrait.class);
        List<ListMember> actual = trait.getValues();
        List<ListMember> expected = ListUtils.of(
            ListMember.builder().a("first").b(1).c("other").build(),
            ListMember.builder().a("second").b(2).c("more").build()
        );
        // TODO: Make this work better with custom equals impls
        assertEquals(actual.size(), expected.size());
        assertEquals(actual.size(), 2);
        assertEquals(actual.get(0).getA(), expected.get(0).getA());
        assertEquals(actual.get(0).getB(), expected.get(0).getB());
        assertEquals(actual.get(0).getC(), expected.get(0).getC());

        assertEquals(actual.get(1).getA(), expected.get(1).getA());
        assertEquals(actual.get(1).getB(), expected.get(1).getB());
        assertEquals(actual.get(1).getC(), expected.get(1).getC());
    }

    @Test
    void createsTrait() {
        ShapeId id = ShapeId.from("ns.foo#foo");
        TraitFactory provider = TraitFactory.createServiceFactory();
        Node input = ArrayNode.fromNodes(
                ListMember.builder().a("first").b(1).c("other").build().toNode(),
                ListMember.builder().a("second").b(2).c("more").build().toNode()
        );
        Trait trait = provider.createTrait(StructureListTraitTrait.ID, id, input).orElseThrow();
        StructureListTraitTrait annotation = (StructureListTraitTrait) trait;
        assertEquals(SourceLocation.NONE, annotation.getSourceLocation());
        assertEquals(trait, provider.createTrait(StructureListTraitTrait.ID, id, trait.toNode()).orElseThrow());
    }
}
