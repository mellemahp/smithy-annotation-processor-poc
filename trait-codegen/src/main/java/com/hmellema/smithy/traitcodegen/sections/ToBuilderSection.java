package com.hmellema.smithy.traitcodegen.sections;

import software.amazon.smithy.codegen.core.Symbol;
import software.amazon.smithy.model.shapes.Shape;
import software.amazon.smithy.utils.CodeSection;

public record ToBuilderSection(Shape shape, Symbol symbol) implements CodeSection {
}
