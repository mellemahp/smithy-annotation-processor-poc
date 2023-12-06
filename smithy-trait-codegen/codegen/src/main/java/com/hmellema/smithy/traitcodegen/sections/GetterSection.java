package com.hmellema.smithy.traitcodegen.sections;

import software.amazon.smithy.model.shapes.Shape;
import software.amazon.smithy.utils.CodeSection;

public final class GetterSection implements CodeSection {
    private final Shape shape;

    public GetterSection(Shape shape) {
        this.shape = shape;
    }

    public Shape shape() {
        return shape;
    }
}
