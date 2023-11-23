package com.hmellema.smithy.traitcodegen.generators.common;

import com.hmellema.smithy.traitcodegen.utils.SymbolUtil;
import com.hmellema.smithy.traitcodegen.writer.TraitCodegenWriter;
import software.amazon.smithy.codegen.core.SymbolProvider;
import software.amazon.smithy.model.Model;
import software.amazon.smithy.model.shapes.*;
import software.amazon.smithy.utils.StringUtils;

import java.util.EnumSet;
import java.util.Optional;

public final class GetterGenerator extends ShapeVisitor.Default<Void> {
    private static final EnumSet<ShapeType> NO_OPTIONAL_WRAPPING_TYPES = EnumSet.of(ShapeType.MAP, ShapeType.LIST);
    private final TraitCodegenWriter writer;
    private final SymbolProvider symbolProvider;
    private final Model model;

    public GetterGenerator(TraitCodegenWriter writer, SymbolProvider symbolProvider, Model model) {
        this.writer = writer;
        this.symbolProvider = symbolProvider;
        this.model = model;
    }

    @Override
    protected Void getDefault(Shape shape) {
        // Do not generate a getter by default
        return null;
    }

    @Override
    public Void shortShape(ShortShape shape) {
        generateValueGetter(shape);
        return null;
    }

    @Override
    public Void integerShape(IntegerShape shape) {
        generateValueGetter(shape);
        return null;
    }

    @Override
    public Void longShape(LongShape shape) {
        generateValueGetter(shape);
        return null;
    }

    @Override
    public Void floatShape(FloatShape shape) {
        generateValueGetter(shape);
        return null;
    }

    @Override
    public Void doubleShape(DoubleShape shape) {
        generateValueGetter(shape);
        return null;
    }

    @Override
    public Void bigIntegerShape(BigIntegerShape shape) {
        generateValueGetter(shape);
        return null;
    }

    @Override
    public Void bigDecimalShape(BigDecimalShape shape) {
        generateValueGetter(shape);
        return null;
    }

    @Override
    public Void listShape(ListShape shape) {
        // StringListTraits already have a getter.
        if (SymbolUtil.isJavaString(symbolProvider.toSymbol(shape.getMember()))) {
            return null;
        }

        generateValuesGetter(shape);
        return null;
    }

    @Override
    public Void mapShape(MapShape shape) {
        generateValuesGetter(shape);
        return null;
    }


    @Override
    public Void intEnumShape(IntEnumShape shape) {
        writer.addImport(Integer.class);
        writer.openBlock("public Integer getValue() {", "}",
                () -> writer.write("return value;"));
        return null;
    }

    @Override
    public Void structureShape(StructureShape shape) {
        for (MemberShape member : shape.members()) {
            if (member.isRequired() || NO_OPTIONAL_WRAPPING_TYPES.contains(model.expectShape(member.getTarget()).getType())) {
                generateNonOptionalGetter(member);
            } else {
                generateOptionalGetter(member);
            }
            writer.newLine();
        }
        return null;
    }

    private void generateNonOptionalGetter(MemberShape member) {
        writer.openBlock("public $T get$L() {", "}",
                symbolProvider.toSymbol(member), StringUtils.capitalize(symbolProvider.toMemberName(member)),
                () -> writer.write("return $L;", symbolProvider.toMemberName(member)));
    }

    private void generateOptionalGetter(MemberShape member) {
        writer.addImport(Optional.class);
        writer.openBlock("public Optional<$T> get$L() {", "}",
                symbolProvider.toSymbol(member), StringUtils.capitalize(symbolProvider.toMemberName(member)),
                () -> writer.write("return Optional.ofNullable($L);", symbolProvider.toMemberName(member)));
    }

    private void generateValuesGetter(Shape shape) {
        writer.openBlock("public $T getValues() {", "}",
                symbolProvider.toSymbol(shape), () -> writer.write("return values;"));
    }

    private void generateValueGetter(Shape shape) {
        writer.openBlock("public $T getValue() {", "}",
                symbolProvider.toSymbol(shape), () -> writer.write("return value;"));
    }
}
