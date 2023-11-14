package com.hmellema.smithy.traitcodegen;

import com.hmellema.smithy.traitcodegen.directives.GenerateTraitDirective;
import com.hmellema.smithy.traitcodegen.generators.*;
import software.amazon.smithy.codegen.core.Symbol;
import software.amazon.smithy.codegen.core.directed.CustomizeDirective;
import software.amazon.smithy.model.shapes.*;
import software.amazon.smithy.model.traits.TraitDefinition;

final class TraitCodegenGenerator extends ShapeVisitor.Default<Void> implements Runnable{
    private final CustomizeDirective<TraitCodegenContext, TraitCodegenSettings> directive;
    private final TraitSymbolProvider traitSymbolProvider;

    public TraitCodegenGenerator(CustomizeDirective<TraitCodegenContext, TraitCodegenSettings> directive) {
        this.directive = directive;
        this.traitSymbolProvider = new TraitSymbolProvider(directive.settings());
    }

    @Override
    protected Void getDefault(Shape shape) {
        if (shape instanceof NumberShape) {
            generateNumberTrait(shape);
        }
        return null;
    }

    private void generateNumberTrait(Shape shape) {
        new NumberTraitGenerator().accept(getDirective(shape));
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
        } else {
            new StructureTraitGenerator().accept(getDirective(shape));
        }
        return null;
    }

    private GenerateTraitDirective getDirective(Shape shape) {
        return new GenerateTraitDirective(shape,
                traitSymbolProvider.toSymbol(shape),
                directive.symbolProvider().toSymbol(shape),
                directive.symbolProvider(),
                directive.context(),
                directive.settings(),
                directive.model());
    }

    @Override
    public void run() {
        directive.connectedShapes().values().stream()
                .filter(shape -> shape.hasTrait(TraitDefinition.class))
                .forEach(shape -> shape.accept(this));
    }
}
