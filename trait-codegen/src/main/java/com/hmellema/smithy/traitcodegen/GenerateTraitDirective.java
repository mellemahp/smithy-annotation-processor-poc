package com.hmellema.smithy.traitcodegen;

import software.amazon.smithy.codegen.core.Symbol;
import software.amazon.smithy.codegen.core.SymbolProvider;
import software.amazon.smithy.model.Model;
import software.amazon.smithy.model.shapes.Shape;

public final class GenerateTraitDirective {
    private final Shape shape;
    private final Symbol traitSymbol;
    private final Symbol baseSymbol;
    private final SymbolProvider symbolProvider;
    private final TraitCodegenContext context;
    private final TraitCodegenSettings settings;
    private final Model model;

    GenerateTraitDirective(Shape shape,
                           Symbol traitSymbol,
                           Symbol baseSymbol,
                           SymbolProvider symbolProvider,
                           TraitCodegenContext context,
                           TraitCodegenSettings settings,
                           Model model) {
        this.shape = shape;
        this.traitSymbol = traitSymbol;
        this.baseSymbol = baseSymbol;
        this.symbolProvider = symbolProvider;
        this.context = context;
        this.settings = settings;
        this.model = model;
    }

    public Shape shape() {
        return shape;
    }

    public Symbol traitSymbol() {
        return traitSymbol;
    }

    public Symbol baseSymbol() {
        return baseSymbol;
    }

    public SymbolProvider symbolProvider() {
        return symbolProvider;
    }

    public TraitCodegenContext context() {
        return context;
    }

    public TraitCodegenSettings settings() {
        return settings;
    }

    public Model model() {
        return model;
    }
}
