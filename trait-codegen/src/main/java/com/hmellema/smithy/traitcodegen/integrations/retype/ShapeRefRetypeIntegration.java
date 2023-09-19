package com.hmellema.smithy.traitcodegen.integrations.retype;

import com.google.auto.service.AutoService;
import com.hmellema.smithy.traitcodegen.TraitCodegenSettings;
import com.hmellema.smithy.traitcodegen.integrations.TraitCodegenIntegration;
import software.amazon.smithy.codegen.core.SymbolProvider;
import software.amazon.smithy.model.Model;

@AutoService(TraitCodegenIntegration.class)
public class ShapeRefRetypeIntegration implements TraitCodegenIntegration {
    @Override
    public String name() {
        return "retype-shaperef";
    }

    @Override
    public SymbolProvider decorateSymbolProvider(Model model, TraitCodegenSettings settings, SymbolProvider symbolProvider) {
        return TraitCodegenIntegration.super.decorateSymbolProvider(model, settings, symbolProvider);
    }
}
