package com.hmellema.smithy.traitcodegen;

import com.hmellema.smithy.traitcodegen.directives.GenerateTraitDirective;
import com.hmellema.smithy.traitcodegen.generators.traits.AnnotationTraitGenerator;
import com.hmellema.smithy.traitcodegen.generators.traits.NumberTraitGenerator;
import com.hmellema.smithy.traitcodegen.generators.traits.StringListTraitGenerator;
import com.hmellema.smithy.traitcodegen.generators.traits.StringTraitGenerator;
import software.amazon.smithy.codegen.core.Symbol;
import software.amazon.smithy.model.shapes.*;
import software.amazon.smithy.model.traits.TraitDefinition;

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
    public Void integerShape(IntegerShape shape) {
        new NumberTraitGenerator().accept(getDirective(shape));
        return null;
    }

    @Override
    public Void stringShape(StringShape shape) {
        new StringTraitGenerator().accept(getDirective(shape));
        return null;
    }

    @Override
    public Void listShape(ListShape shape) {
        if (!shape.hasTrait(TraitDefinition.class))
        System.out.println("MEMBER " + shape.getMember());
        Symbol memberType = context.symbolProvider().toSymbol(shape.getMember());
        if (memberType.equals(SymbolUtil.fromClass(String.class))) {
            new StringListTraitGenerator().accept(getDirective(shape));
        }
        return null;
    }


    @Override
    public Void structureShape(StructureShape shape) {
        GenerateTraitDirective directive = getDirective(shape);
        if (shape.getAllMembers().isEmpty()) {
           new AnnotationTraitGenerator().accept(directive);
        }
        return null;
    }

    private GenerateTraitDirective getDirective(Shape shape) {
        return new GenerateTraitDirective(shape, context.symbolProvider().toSymbol(shape),
                context, context.settings(), context.model());
    }
}
