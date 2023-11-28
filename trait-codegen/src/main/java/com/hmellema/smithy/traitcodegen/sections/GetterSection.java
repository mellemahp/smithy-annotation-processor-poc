package com.hmellema.smithy.traitcodegen.sections;

import software.amazon.smithy.codegen.core.SymbolProvider;
import software.amazon.smithy.model.Model;
import software.amazon.smithy.model.shapes.Shape;
import software.amazon.smithy.utils.CodeSection;

public record GetterSection(Shape shape, SymbolProvider symbolProvider, Model model) implements CodeSection {
}
