package com.hmellema.smithy.traitcodegen.sections;

import software.amazon.smithy.model.shapes.Shape;
import software.amazon.smithy.utils.CodeSection;

public final class PropertySection implements CodeSection {
    private final Shape shape;

    public PropertySection(Shape shape) {
        this.shape = shape;
    }

    public Shape shape() {
        return shape;
    }
}
