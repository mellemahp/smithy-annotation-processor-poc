package com.hmellema.smithy.traitcodegen.integrations.core;

import com.google.auto.service.AutoService;
import com.hmellema.smithy.traitcodegen.SymbolProperties;
import com.hmellema.smithy.traitcodegen.SymbolUtil;
import com.hmellema.smithy.traitcodegen.TraitCodegenSettings;
import com.hmellema.smithy.traitcodegen.integrations.TraitCodegenIntegration;
import software.amazon.smithy.codegen.core.Symbol;
import software.amazon.smithy.codegen.core.SymbolProvider;
import software.amazon.smithy.codegen.core.SymbolReference;
import software.amazon.smithy.model.Model;
import software.amazon.smithy.model.shapes.*;
import software.amazon.smithy.model.traits.IdRefTrait;

import java.util.List;

@AutoService(TraitCodegenIntegration.class)
public class IdRefDecoratorIntegration implements TraitCodegenIntegration {
    private static final String INTEGRATION_NAME = "id-ref-integration-core";
    private static final Symbol SHAPE_ID_SYMBOL = SymbolUtil.fromClass(ShapeId.class).toBuilder()
            .putProperty(SymbolProperties.NODE_MAPPER, "Node.from($L.toString())")
            .build();

    @Override
    public String name() {
        return INTEGRATION_NAME;
    }

    @Override
    public SymbolProvider decorateSymbolProvider(Model model, TraitCodegenSettings settings, SymbolProvider symbolProvider) {
        return shape -> provideSymbol(shape, symbolProvider, model);
    }

    private Symbol provideSymbol(Shape shape, SymbolProvider symbolProvider, Model model) {
        if (shape.hasTrait(IdRefTrait.class)) {
            return SHAPE_ID_SYMBOL;
        } else if (shape.isMemberShape()) {
            Shape target = model.expectShape(shape.asMemberShape().get().getTarget());
            return provideSymbol(target, symbolProvider, model);
        } else if (shape.isListShape()) {
            // Replace any members reference by a list shape as the decorator does wrap the internal call from the
            // toSymbol(member)
            MemberShape member = shape.asListShape().get().getMember();
            return symbolProvider.toSymbol(shape).toBuilder()
                    .references(List.of(new SymbolReference(provideSymbol(member, symbolProvider, model))))
                    .build();
        } else if (shape.isMapShape()) {
            // Same as list replacement but for map shapes
            MapShape mapShape = shape.asMapShape().get();
            return symbolProvider.toSymbol(shape)
                    .toBuilder()
                    .references(List.of(
                        new SymbolReference(provideSymbol(mapShape.getKey(), symbolProvider, model)),
                        new SymbolReference(provideSymbol(mapShape.getValue(), symbolProvider, model))
                    ))
                    .build();
        }
        return symbolProvider.toSymbol(shape);
    }
}
