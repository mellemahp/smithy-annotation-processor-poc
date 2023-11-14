package com.hmellema.smithy.traitcodegen;

import software.amazon.smithy.codegen.core.Symbol;
import software.amazon.smithy.codegen.core.SymbolProvider;
import software.amazon.smithy.codegen.core.directed.CreateSymbolProviderDirective;
import software.amazon.smithy.model.Model;
import software.amazon.smithy.model.shapes.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;

final class BaseJavaSymbolProvider extends ShapeVisitor.Default<Symbol> implements SymbolProvider {
    private final String packageName;
    private final String packagePath;
    private final Model model;

    private BaseJavaSymbolProvider(TraitCodegenSettings settings, Model model) {
        this.packageName = settings.packageName();
        this.packagePath = "./" + packageName.replace(".", "/");
        this.model = model;
    }

    public static SymbolProvider fromDirective(CreateSymbolProviderDirective<TraitCodegenSettings> directive) {
        return new BaseJavaSymbolProvider(directive.settings(), directive.model());
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
    public Symbol intEnumShape(IntEnumShape shape) {
        return Symbol.builder()
                .name(SymbolUtil.getDefaultName(shape))
                .putProperty("enumValueType", SymbolUtil.fromClass(int.class))
                .namespace(packageName, ".")
                .declarationFile(packagePath + "/" + SymbolUtil.getDefaultName(shape) + ".java")
                .build();
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
    public Symbol listShape(ListShape shape) {
        return SymbolUtil.fromClass(List.class).toBuilder()
                .addReference(toSymbol(shape.getMember()))
                .build();
    }

    @Override
    public Symbol mapShape(MapShape shape) {
        return SymbolUtil.fromClass(Map.class).toBuilder()
                .addReference(shape.getKey().accept(this))
                .addReference(shape.getValue().accept(this))
                .build();
    }

    @Override
    public Symbol stringShape(StringShape shape) {
        return SymbolUtil.fromClass(String.class);
    }

    @Override
    public Symbol enumShape(EnumShape shape) {
        return Symbol.builder()
                .name(SymbolUtil.getDefaultName(shape))
                .putProperty("enumValueType", SymbolUtil.fromClass(String.class))
                .namespace(packageName, ".")
                .declarationFile(packagePath + "/" + SymbolUtil.getDefaultName(shape) + ".java")
                .build();
    }

    @Override
    public Symbol structureShape(StructureShape shape) {
        return Symbol.builder()
                .name(SymbolUtil.getDefaultName(shape))
                .namespace(packageName, ".")
                .declarationFile(packagePath + "/" + SymbolUtil.getDefaultName(shape) + ".java")
                .build();
    }

    @Override
    public Symbol memberShape(MemberShape shape) {
        return toSymbol(model.expectShape(shape.getTarget()));
    }

    @Override
    protected Symbol getDefault(Shape shape) {
        return null;
    }

    @Override
    public Symbol toSymbol(Shape shape) {
        return shape.accept(this);
    }
}
