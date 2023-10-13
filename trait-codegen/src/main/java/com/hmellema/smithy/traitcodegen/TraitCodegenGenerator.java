package com.hmellema.smithy.traitcodegen;

import com.hmellema.smithy.traitcodegen.directives.GenerateTraitDirective;
import com.hmellema.smithy.traitcodegen.generators.traits.*;
import software.amazon.smithy.codegen.core.Symbol;
import software.amazon.smithy.model.shapes.*;

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
    public Void intEnumShape(IntEnumShape shape) {
        new IntEnumTraitGenerator().accept(getDirective(shape));
        return null;
    }

    @Override
    public Void stringShape(StringShape shape) {
        new StringTraitGenerator().accept(getDirective(shape));
        return null;
    }

    @Override
    public Void enumShape(EnumShape shape) {
        new EnumTraitGenerator().accept(getDirective(shape));
        return null;
    }

    @Override
    public Void listShape(ListShape shape) {
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
