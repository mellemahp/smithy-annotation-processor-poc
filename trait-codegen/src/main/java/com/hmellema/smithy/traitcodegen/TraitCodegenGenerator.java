package com.hmellema.smithy.traitcodegen;

import com.hmellema.smithy.traitcodegen.directives.GenerateTraitDirective;
import com.hmellema.smithy.traitcodegen.generators.traits.*;
import software.amazon.smithy.codegen.core.Symbol;
import software.amazon.smithy.codegen.core.directed.CustomizeDirective;
import software.amazon.smithy.model.shapes.*;
import software.amazon.smithy.model.traits.TraitDefinition;

public class TraitCodegenGenerator extends ShapeVisitor.Default<Void> {
    private final CustomizeDirective<TraitCodegenContext, TraitCodegenSettings> directive;

    public TraitCodegenGenerator(CustomizeDirective<TraitCodegenContext, TraitCodegenSettings> directive) {
        this.directive = directive;
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
        Symbol memberType = directive.symbolProvider().toSymbol(shape.getMember());
        if (memberType.equals(SymbolUtil.fromClass(String.class))) {
            new StringListTraitGenerator().accept(getDirective(shape));
        }
        return null;
    }


    @Override
    public Void structureShape(StructureShape shape) {
        if (shape.getAllMembers().isEmpty()) {
           new AnnotationTraitGenerator().accept(getDirective(shape));
        }
        return null;
    }

    private GenerateTraitDirective getDirective(Shape shape) {
        return new GenerateTraitDirective(shape, directive.symbolProvider().toSymbol(shape),
                directive.context(), directive.settings(), directive.model());
    }

    public void generate() {
        directive.connectedShapes().values().stream()
                .filter(shape -> shape.hasTrait(TraitDefinition.class))
                .forEach(shape -> shape.accept(this));
    }
}
