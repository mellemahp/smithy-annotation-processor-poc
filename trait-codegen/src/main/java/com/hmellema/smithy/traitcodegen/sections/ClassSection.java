package com.hmellema.smithy.traitcodegen.sections;

import software.amazon.smithy.model.shapes.Shape;
import software.amazon.smithy.utils.CodeSection;

import java.util.Objects;

public final class ClassSection implements CodeSection {
    private final Shape shape;

    public ClassSection(Shape shape) {
        this.shape = shape;
    }

    public Shape shape() {
        return shape;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        ClassSection that = (ClassSection) obj;
        return Objects.equals(this.shape, that.shape);
    }

    @Override
    public int hashCode() {
        return Objects.hash(shape);
    }

    @Override
    public String toString() {
        return "ClassSection[" +
                "shape=" + shape + ']';
    }

}
