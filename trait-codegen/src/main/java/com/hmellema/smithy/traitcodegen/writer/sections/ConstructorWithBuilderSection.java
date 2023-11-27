package com.hmellema.smithy.traitcodegen.writer.sections;

import software.amazon.smithy.codegen.core.Symbol;
import software.amazon.smithy.codegen.core.SymbolProvider;
import software.amazon.smithy.model.Model;
import software.amazon.smithy.model.shapes.Shape;
import software.amazon.smithy.utils.CodeSection;

public record ConstructorWithBuilderSection(Shape shape, Symbol symbol, SymbolProvider symbolProvider, Model model) implements CodeSection {
}
