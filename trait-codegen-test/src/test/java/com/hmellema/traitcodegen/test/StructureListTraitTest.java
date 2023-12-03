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
        assertEquals(expected.size(), actual.size());
        assertEquals(2, actual.size());
        assertEquals(expected.get(0).getA(), actual.get(0).getA());
        assertEquals(expected.get(0).getB(), actual.get(0).getB());
        assertEquals(expected.get(0).getC(), actual.get(0).getC());

        assertEquals(expected.get(1).getA(), actual.get(1).getA());
        assertEquals(expected.get(1).getB(), actual.get(1).getB());
        assertEquals( expected.get(1).getC(), actual.get(1).getC());
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
