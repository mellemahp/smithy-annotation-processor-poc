package com.hmellema.smithy.traitcodegen.sections;

import software.amazon.smithy.codegen.core.Symbol;
import software.amazon.smithy.model.shapes.Shape;
import software.amazon.smithy.utils.CodeSection;

import java.util.Objects;

public final class FromNodeSection implements CodeSection {
    private final Shape shape;
    private final Symbol symbol;

    public FromNodeSection(Shape shape, Symbol symbol) {
        this.shape = shape;
        this.symbol = symbol;
    }

    public Shape shape() {
        return shape;
    }

    public Symbol symbol() {
        return symbol;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        FromNodeSection that = (FromNodeSection) obj;
        return Objects.equals(this.shape, that.shape) &&
                Objects.equals(this.symbol, that.symbol);
    }

    @Override
    public int hashCode() {
        return Objects.hash(shape, symbol);
    }

    @Override
    public String toString() {
        return "FromNodeSection[" +
                "shape=" + shape + ", " +
                "symbol=" + symbol + ']';
    }
}
