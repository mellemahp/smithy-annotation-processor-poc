package com.hmellema.traitcodegen.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.traits.NestedA;
import com.example.traits.NestedB;
import com.example.traits.StructureTraitTrait;
import java.util.Objects;
import org.junit.jupiter.api.Test;
import software.amazon.smithy.model.Model;
import software.amazon.smithy.model.SourceLocation;
import software.amazon.smithy.model.shapes.Shape;
import software.amazon.smithy.model.shapes.ShapeId;
import software.amazon.smithy.model.traits.Trait;
import software.amazon.smithy.model.traits.TraitFactory;
import software.amazon.smithy.utils.ListUtils;
import software.amazon.smithy.utils.MapUtils;
import software.amazon.smithy.utils.SetUtils;

class StructureTraitTest {
    @Test
    void loadsFromModel() {
        Model result = Model.assembler()
                .discoverModels(getClass().getClassLoader())
                .addImport(Objects.requireNonNull(getClass().getResource("struct-trait.smithy")))
                .assemble()
                .unwrap();
        Shape shape = result.expectShape(ShapeId.from("test.smithy.traitcodegen#myStruct"));
        StructureTraitTrait trait = shape.expectTrait(StructureTraitTrait.class);
        assertEquals("first", trait.getFieldA());
        assertTrue(trait.getFieldB().isPresent());
        assertFalse(trait.getFieldB().get());
        assertTrue(trait.getFieldC().isPresent());
        NestedA nestedA = trait.getFieldC().get();
        assertEquals("nested", nestedA.getFieldN());
        assertTrue(nestedA.getFieldQ().orElseThrow(RuntimeException::new));
        assertEquals(NestedB.A, nestedA.getFieldZ().orElseThrow(RuntimeException::new));
        assertIterableEquals(ListUtils.of("a", "b", "c"), trait.getFieldD());
        assertIterableEquals(SetUtils.of("a", "b"), trait.getFieldE().keySet());
        assertIterableEquals(ListUtils.of("one", "two"), trait.getFieldE().values());
    }

    @Test
    void createsTrait() {
        ShapeId id = ShapeId.from("ns.foo#foo");
        TraitFactory provider = TraitFactory.createServiceFactory();
        StructureTraitTrait struct = StructureTraitTrait.builder()
                .fieldA("a")
                .fieldB(true)
                .fieldC(NestedA.builder()
                        .fieldN("nested")
                        .fieldQ(false)
                        .fieldZ(NestedB.B)
                        .build()
                )
                .fieldD(ListUtils.of("a", "b", "c"))
                .fieldE(MapUtils.of("a", "one", "b", "two"))
                .build();
        Trait trait =
                provider.createTrait(StructureTraitTrait.ID, id, struct.toNode()).orElseThrow(RuntimeException::new);
        StructureTraitTrait annotation = (StructureTraitTrait) trait;
        assertEquals(SourceLocation.NONE, annotation.getSourceLocation());
        assertEquals(trait,
                provider.createTrait(StructureTraitTrait.ID, id, trait.toNode()).orElseThrow(RuntimeException::new));
    }
}
