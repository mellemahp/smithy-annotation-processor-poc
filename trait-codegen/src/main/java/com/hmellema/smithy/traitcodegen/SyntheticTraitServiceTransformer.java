package com.hmellema.smithy.traitcodegen;

import software.amazon.smithy.model.Model;
import software.amazon.smithy.model.loader.Prelude;
import software.amazon.smithy.model.shapes.OperationShape;
import software.amazon.smithy.model.shapes.ServiceShape;
import software.amazon.smithy.model.shapes.Shape;
import software.amazon.smithy.model.shapes.ShapeId;
import software.amazon.smithy.model.traits.PrivateTrait;
import software.amazon.smithy.model.traits.TraitDefinition;
import software.amazon.smithy.model.transform.ModelTransformer;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

final class SyntheticTraitServiceTransformer {
    public static final ShapeId SYNTHETIC_SERVICE_ID = ShapeId.from("smithy.synthetic#TraitService");

    static Model transform(Model model) {
        // Find all trait definition shapes excluding private traits and traits in the prelude.
        Set<Shape> toGenerate =  model.getShapesWithTrait(TraitDefinition.class).stream()
                .filter(SyntheticTraitServiceTransformer::isTraitDefinitionToGenerate)
                .collect(Collectors.toSet());

        Set<Shape> shapesToAdd = new HashSet<>();

        // Create a synthetic service builder to add operations to
        ServiceShape.Builder serviceBuilder = ServiceShape.builder().id(SYNTHETIC_SERVICE_ID);

        // Create a synthetic operation for each trait and add to the synthetic service
        for (Shape traitShape : toGenerate) {
            OperationShape op = OperationShape.builder()
                    .id(traitShape.getId().toString() + "SyntheticOperation")
                    .input(traitShape.toShapeId())
                    .build();
            shapesToAdd.add(op);
            serviceBuilder.addOperation(op.toShapeId());
        }
        shapesToAdd.add(serviceBuilder.build());

        return ModelTransformer.create().replaceShapes(model, shapesToAdd);
    }

    private static boolean isTraitDefinitionToGenerate(Shape shape) {
        return !Prelude.isPreludeShape(shape) && !shape.hasTrait(PrivateTrait.class);
    }
}
