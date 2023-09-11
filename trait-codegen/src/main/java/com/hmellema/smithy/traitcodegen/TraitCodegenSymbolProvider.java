package com.hmellema.smithy.traitcodegen;

import software.amazon.smithy.codegen.core.Symbol;
import software.amazon.smithy.codegen.core.SymbolProvider;
import software.amazon.smithy.codegen.core.directed.CreateSymbolProviderDirective;
import software.amazon.smithy.model.Model;
import software.amazon.smithy.model.shapes.*;

public class TraitCodegenSymbolProvider implements SymbolProvider, ShapeVisitor<Symbol> {
    private final TraitCodegenSettings settings;
    private final Model model;

    TraitCodegenSymbolProvider(Model model, TraitCodegenSettings settings) {
        this.settings = settings;
        this.model = model;
    }

    @Override
    public Symbol toSymbol(Shape shape) {
        return null;
    }

    @Override
    public Symbol blobShape(BlobShape shape) {
        return null;
    }

    @Override
    public Symbol booleanShape(BooleanShape shape) {
        return null;
    }

    @Override
    public Symbol listShape(ListShape shape) {
        return null;
    }

    @Override
    public Symbol mapShape(MapShape shape) {
        return null;
    }

    @Override
    public Symbol byteShape(ByteShape shape) {
        return null;
    }

    @Override
    public Symbol shortShape(ShortShape shape) {
        return null;
    }

    @Override
    public Symbol integerShape(IntegerShape shape) {
        return null;
    }

    @Override
    public Symbol longShape(LongShape shape) {
        return null;
    }

    @Override
    public Symbol floatShape(FloatShape shape) {
        return null;
    }

    @Override
    public Symbol documentShape(DocumentShape shape) {
        return null;
    }

    @Override
    public Symbol doubleShape(DoubleShape shape) {
        return null;
    }

    @Override
    public Symbol bigIntegerShape(BigIntegerShape shape) {
        return null;
    }

    @Override
    public Symbol bigDecimalShape(BigDecimalShape shape) {
        return null;
    }

    @Override
    public Symbol operationShape(OperationShape shape) {
        return null;
    }

    @Override
    public Symbol resourceShape(ResourceShape shape) {
        return null;
    }

    @Override
    public Symbol serviceShape(ServiceShape shape) {
        return null;
    }

    @Override
    public Symbol stringShape(StringShape shape) {
        return null;
    }

    @Override
    public Symbol structureShape(StructureShape shape) {
        return null;
    }

    @Override
    public Symbol unionShape(UnionShape shape) {
        return null;
    }

    @Override
    public Symbol memberShape(MemberShape shape) {
        return null;
    }

    @Override
    public Symbol timestampShape(TimestampShape shape) {
        return null;
    }
}
