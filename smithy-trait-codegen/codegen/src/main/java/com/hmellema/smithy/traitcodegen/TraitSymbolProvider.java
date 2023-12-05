package com.hmellema.smithy.traitcodegen;

import com.hmellema.smithy.traitcodegen.utils.SymbolUtil;
import software.amazon.smithy.codegen.core.Symbol;
import software.amazon.smithy.codegen.core.SymbolProvider;
import software.amazon.smithy.model.shapes.*;
import software.amazon.smithy.model.traits.TraitDefinition;


final class TraitSymbolProvider extends ShapeVisitor.Default<Symbol> implements SymbolProvider {
    private final String packageName;
    private final String packagePath;

    public TraitSymbolProvider(TraitCodegenSettings settings) {
        this.packageName = settings.packageName();
        this.packagePath = "./" + packageName.replace(".", "/");
    }

    @Override
    public Symbol toSymbol(Shape shape) {
        if (shape.hasTrait(TraitDefinition.class)) {
            return shape.accept(this);
        }
        throw new UnsupportedOperationException("Expected shape to have trait definition trait but was not found.");
    }


    @Override
    protected Symbol getDefault(Shape shape) {
        throw new UnsupportedOperationException("Shape type" + shape.getType() + " not supported by Trait Codegen");
    }

    @Override
    public Symbol listShape(ListShape shape) {
        return getSymbolBuilder(shape).build();
    }

    @Override
    public Symbol shortShape(ShortShape shape) {
        return getSymbolBuilder(shape)
                .putProperty(SymbolProperties.VALUE_GETTER, "shortValue()")
                .build();
    }

    @Override
    public Symbol integerShape(IntegerShape shape) {
        return getSymbolBuilder(shape)
                .putProperty(SymbolProperties.VALUE_GETTER, "intValue()")
                .build();
    }

    @Override
    public Symbol floatShape(FloatShape shape) {
        return getSymbolBuilder(shape)
                .putProperty(SymbolProperties.VALUE_GETTER, "floatValue()")
                .build();
    }

    @Override
    public Symbol doubleShape(DoubleShape shape) {
        return getSymbolBuilder(shape)
                .putProperty(SymbolProperties.VALUE_GETTER, "doubleValue()")
                .build();
    }

    @Override
    public Symbol mapShape(MapShape shape) {
        return getSymbolBuilder(shape).build();
    }

    @Override
    public Symbol longShape(LongShape shape) {
        return getSymbolBuilder(shape)
                .putProperty(SymbolProperties.VALUE_GETTER, "longValue()")
                .build();
    }

    @Override
    public Symbol stringShape(StringShape shape) {
        return getSymbolBuilder(shape).build();
    }

    @Override
    public Symbol structureShape(StructureShape shape) {
        return getSymbolBuilder(shape).build();
    }

    @Override
    public Symbol unionShape(UnionShape shape) {
        throw new UnsupportedOperationException("Cannot generate Union shape traits");
    }

    @Override
    public Symbol memberShape(MemberShape shape) {
        throw new UnsupportedOperationException("Cannot generate trait for member shape.");
    }

    @Override
    public Symbol timestampShape(TimestampShape shape) {
        throw new UnsupportedOperationException("Cannot generate trait for timestamp at this time");
    }

    private Symbol.Builder getSymbolBuilder(Shape shape) {
        return Symbol.builder()
                .name(SymbolUtil.getDefaultTraitName(shape))
                .namespace(packageName, ".")
                .declarationFile(packagePath + "/" + SymbolUtil.getDefaultTraitName(shape) + ".java");
    }
}
