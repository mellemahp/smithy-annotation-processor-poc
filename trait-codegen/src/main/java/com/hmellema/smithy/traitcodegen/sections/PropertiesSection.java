package com.hmellema.smithy.traitcodegen.sections;

import software.amazon.smithy.codegen.core.SymbolProvider;
import software.amazon.smithy.model.shapes.Shape;
import software.amazon.smithy.utils.CodeSection;

public record PropertiesSection(Shape shape, SymbolProvider symbolProvider) implements CodeSection {
}
