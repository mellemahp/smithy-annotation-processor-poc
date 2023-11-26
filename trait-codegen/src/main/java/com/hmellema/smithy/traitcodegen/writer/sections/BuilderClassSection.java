package com.hmellema.smithy.traitcodegen.writer.sections;

import software.amazon.smithy.codegen.core.Symbol;
import software.amazon.smithy.codegen.core.SymbolProvider;
import software.amazon.smithy.model.Model;
import software.amazon.smithy.model.shapes.Shape;
import software.amazon.smithy.utils.CodeSection;

public record BuilderClassSection(Shape shape, Symbol symbol, SymbolProvider symbolProvider, Model model) implements CodeSection {
    public static BuilderClassSection fromBuilderSection(BuilderSection section) {
        return new BuilderClassSection(
                section.shape(),
                section.symbol(),
                section.symbolProvider(),
                section.model()
        );
    }
}
