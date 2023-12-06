package com.hmellema.smithy.traitcodegen.sections;

import software.amazon.smithy.codegen.core.Symbol;
import software.amazon.smithy.model.shapes.Shape;
import software.amazon.smithy.utils.CodeSection;

public final class ProviderSection implements CodeSection {
    private final Shape shape;
    private final Symbol symbol;

    public ProviderSection(Shape shape, Symbol symbol) {
        this.shape = shape;
        this.symbol = symbol;
    }

    public Shape shape() {
        return shape;
    }

    public Symbol symbol() {
        return symbol;
    }
}
