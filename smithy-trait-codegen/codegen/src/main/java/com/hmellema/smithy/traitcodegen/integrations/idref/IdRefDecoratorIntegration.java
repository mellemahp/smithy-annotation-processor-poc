package com.hmellema.smithy.traitcodegen.integrations.idref;

import com.hmellema.smithy.traitcodegen.SymbolProperties;
import com.hmellema.smithy.traitcodegen.TraitCodegenSettings;
import com.hmellema.smithy.traitcodegen.integrations.TraitCodegenIntegration;
import com.hmellema.smithy.traitcodegen.utils.SymbolUtil;
import software.amazon.smithy.codegen.core.Symbol;
import software.amazon.smithy.codegen.core.SymbolProvider;
import software.amazon.smithy.codegen.core.SymbolReference;
import software.amazon.smithy.model.Model;
import software.amazon.smithy.model.shapes.MapShape;
import software.amazon.smithy.model.shapes.MemberShape;
import software.amazon.smithy.model.shapes.Shape;
import software.amazon.smithy.model.shapes.ShapeId;
import software.amazon.smithy.model.traits.IdRefTrait;
import software.amazon.smithy.utils.ListUtils;

public class IdRefDecoratorIntegration implements TraitCodegenIntegration {
    private static final String INTEGRATION_NAME = "id-ref-integration";
    private static final Symbol SHAPE_ID_SYMBOL = SymbolUtil.fromClass(ShapeId.class).toBuilder()
            .putProperty(SymbolProperties.TO_NODE_MAPPER, "Node.from($L.toString())")
            .putProperty(SymbolProperties.FROM_NODE_MAPPER, "ShapeId.fromNode($L)")
            .putProperty(SymbolProperties.VALUE_GETTER, "ShapeId.from($L)")
            .build();

    @Override
    public String name() {
        return INTEGRATION_NAME;
    }

    @Override
    public byte priority() {
        // Make sure this runs before all other integration
        return 127;
    }

    @Override
    public SymbolProvider decorateSymbolProvider(Model model, TraitCodegenSettings settings,
                                                 SymbolProvider symbolProvider) {
        return shape -> provideSymbol(shape, symbolProvider, model);
    }

    private Symbol provideSymbol(Shape shape, SymbolProvider symbolProvider, Model model) {
        if (shape.hasTrait(IdRefTrait.class)) {
            return SHAPE_ID_SYMBOL;
        } else if (shape.isMemberShape()) {
            Shape target = model.expectShape(shape.asMemberShape().orElseThrow(RuntimeException::new).getTarget());
            return provideSymbol(target, symbolProvider, model);
        } else if (shape.isListShape()) {
            // Replace any members reference by a list shape as the decorator does wrap the internal call from the
            // toSymbol(member)
            MemberShape member = shape.asListShape().orElseThrow(RuntimeException::new).getMember();
            return symbolProvider.toSymbol(shape).toBuilder()
                    .references(ListUtils.of(new SymbolReference(provideSymbol(member, symbolProvider, model))))
                    .build();
        } else if (shape.isMapShape()) {
            // Same as list replacement but for map shapes
            MapShape mapShape = shape.asMapShape().orElseThrow(RuntimeException::new);
            return symbolProvider.toSymbol(shape)
                    .toBuilder()
                    .references(ListUtils.of(
                            new SymbolReference(provideSymbol(mapShape.getKey(), symbolProvider, model)),
                            new SymbolReference(provideSymbol(mapShape.getValue(), symbolProvider, model))
                    ))
                    .build();
        }
        return symbolProvider.toSymbol(shape);
    }
}
