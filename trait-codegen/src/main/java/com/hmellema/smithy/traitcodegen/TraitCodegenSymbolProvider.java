package com.hmellema.smithy.traitcodegen;

import software.amazon.smithy.codegen.core.Symbol;
import software.amazon.smithy.codegen.core.SymbolProvider;
import software.amazon.smithy.model.Model;
import software.amazon.smithy.model.shapes.*;
import software.amazon.smithy.utils.StringUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;

public class TraitCodegenSymbolProvider implements SymbolProvider, ShapeVisitor<Symbol> {
    private final String packageName;

    private final String packagePath;

    TraitCodegenSymbolProvider(TraitCodegenSettings settings) {
        this.packageName = settings.packageName();
        this.packagePath = "./" + packageName.replace(".", "/");
    }

    @Override
    public Symbol toSymbol(Shape shape) {
        return shape.accept(this);
    }

    @Override
    public Symbol blobShape(BlobShape shape) {
        return SymbolUtil.fromClass(ByteBuffer.class);
    }

    @Override
    public Symbol booleanShape(BooleanShape shape) {
        return SymbolUtil.fromClass(Boolean.class);
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
        return SymbolUtil.fromClass(Byte.class);
    }

    @Override
    public Symbol shortShape(ShortShape shape) {
        return SymbolUtil.fromClass(Short.class);
    }

    @Override
    public Symbol integerShape(IntegerShape shape) {
        return SymbolUtil.fromClass(Integer.class);
    }

    @Override
    public Symbol longShape(LongShape shape) {
        return SymbolUtil.fromClass(Long.class);
    }

    @Override
    public Symbol floatShape(FloatShape shape) {
        return SymbolUtil.fromClass(Float.class);
    }

    @Override
    public Symbol documentShape(DocumentShape shape) {
        return null;
    }

    @Override
    public Symbol doubleShape(DoubleShape shape) {
        return SymbolUtil.fromClass(Double.class);
    }

    @Override
    public Symbol bigIntegerShape(BigIntegerShape shape) {
        return SymbolUtil.fromClass(BigInteger.class);
    }

    @Override
    public Symbol bigDecimalShape(BigDecimalShape shape) {
        return SymbolUtil.fromClass(BigDecimal.class);
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
        return Symbol.builder()
                .name(StringUtils.capitalize(shape.getId().getName()))
                .namespace(packageName,".")
                .declarationFile(packagePath + "/" + StringUtils.capitalize(shape.getId().getName()) + ".java")
                .build();
    }

    @Override
    public Symbol structureShape(StructureShape shape) {
        return Symbol.builder()
                .name(StringUtils.capitalize(shape.getId().getName()))
                .namespace(packageName,".")
                .declarationFile(packagePath + "/" + StringUtils.capitalize(shape.getId().getName()) + ".java")
                .build();
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
