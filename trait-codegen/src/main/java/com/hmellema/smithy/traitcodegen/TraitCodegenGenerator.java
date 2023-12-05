package com.hmellema.smithy.traitcodegen;

import com.hmellema.smithy.traitcodegen.directives.GenerateTraitDirective;
import com.hmellema.smithy.traitcodegen.generators.traits.*;
import com.hmellema.smithy.traitcodegen.utils.SymbolUtil;
import software.amazon.smithy.codegen.core.directed.CustomizeDirective;
import software.amazon.smithy.model.shapes.*;
import software.amazon.smithy.model.traits.TraitDefinition;

final class TraitCodegenGenerator extends ShapeVisitor.Default<Void> implements Runnable {
    private final CustomizeDirective<TraitCodegenContext, TraitCodegenSettings> directive;
    private final TraitSymbolProvider traitSymbolProvider;

    public TraitCodegenGenerator(CustomizeDirective<TraitCodegenContext, TraitCodegenSettings> directive) {
        this.directive = directive;
        this.traitSymbolProvider = new TraitSymbolProvider(directive.settings());
    }

    @Override
    protected Void getDefault(Shape shape) {
        throw new UnsupportedOperationException("Trait code generation does not support shapes of type: " + shape.getType());
    }

    @Override
    public Void booleanShape(BooleanShape shape) {
        throw new UnsupportedOperationException("Boolean shapes not supported for trait code generation. "
                + "Consider using an Annotation trait instead");
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
        if (SymbolUtil.isJavaString(directive.symbolProvider().toSymbol(shape.getMember()))) {
            new StringListTraitGenerator().accept(getDirective(shape));
        } else {
            new CollectionTraitGenerator().accept(getDirective(shape));
        }
        return null;
    }

    @Override
    public Void shortShape(ShortShape shape) {
        generateNumberTrait(shape);
        return null;
    }

    @Override
    public Void integerShape(IntegerShape shape) {
        generateNumberTrait(shape);
        return null;
    }

    @Override
    public Void longShape(LongShape shape) {
        generateNumberTrait(shape);
        return null;
    }

    @Override
    public Void floatShape(FloatShape shape) {
        generateNumberTrait(shape);
        return null;
    }

    @Override
    public Void doubleShape(DoubleShape shape) {
        generateNumberTrait(shape);
        return null;
    }

    @Override
    public Void bigIntegerShape(BigIntegerShape shape) {
        generateNumberTrait(shape);
        return null;
    }

    @Override
    public Void bigDecimalShape(BigDecimalShape shape) {
        generateNumberTrait(shape);
        return null;
    }

    @Override
    public Void mapShape(MapShape shape) {
        new CollectionTraitGenerator().accept(getDirective(shape));
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

    private void generateNumberTrait(Shape shape) {
        new NumberTraitGenerator().accept(getDirective(shape));
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
