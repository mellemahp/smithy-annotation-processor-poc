package com.hmellema.smithy.traitcodegen;

import com.hmellema.smithy.traitcodegen.directives.GenerateStringTraitDirective;
import com.hmellema.smithy.traitcodegen.generators.StringTraitGenerator;
import software.amazon.smithy.model.shapes.*;

import java.io.File;
import java.util.Optional;

public class TraitCodegenGenerator extends ShapeVisitor.Default<Void> {
    private final TraitCodegenContext context;

    public TraitCodegenGenerator(TraitCodegenContext context) {
        this.context = context;
    }

    @Override
    protected Void getDefault(Shape shape) {
        return null;
    }

    @Override
    public Void stringShape(StringShape shape) {
        GenerateStringTraitDirective directive = new GenerateStringTraitDirective(
                shape, context.symbolProvider().toSymbol(shape), context, context.settings(), context.model()
        );
        new StringTraitGenerator().accept(directive);
        return null;
    }
}
