package com.hmellema.smithy.traitcodegen;

import com.hmellema.smithy.traitcodegen.utils.SymbolUtil;
import software.amazon.smithy.codegen.core.Symbol;
import software.amazon.smithy.codegen.core.SymbolProvider;
import software.amazon.smithy.codegen.core.directed.CreateSymbolProviderDirective;
import software.amazon.smithy.model.Model;
import software.amazon.smithy.model.node.Node;
import software.amazon.smithy.model.shapes.*;
import software.amazon.smithy.model.traits.UniqueItemsTrait;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.Set;

final class BaseJavaSymbolProvider extends ShapeVisitor.Default<Symbol> implements SymbolProvider {
    private static final String NODE_FROM = "Node.from($L)";
    private static final String TO_NODE = "$L.toNode()";
    private static final String LIST_INITIALIZER = "forList()";
    private static final String SET_INITIALIZER = "forOrderedSet()";
    private static final String MAP_INITIALIZER = "forOrderedMap()";

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

    public static Symbol simpleShapeSymbolFrom(Class<?> clazz) {
        return SymbolUtil.fromClass(clazz).toBuilder()
                .putProperty(SymbolProperties.TO_NODE_MAPPER, NODE_FROM)
                .putProperty(SymbolProperties.NODE_MAPPING_IMPORTS, SymbolUtil.fromClass(Node.class))
                .build();
    }

    // TODO: ToNode?
    @Override
    public Symbol blobShape(BlobShape shape) {
        return SymbolUtil.fromClass(ByteBuffer.class);
    }

    @Override
    public Symbol booleanShape(BooleanShape shape) {
        return simpleShapeSymbolFrom(Boolean.class);
    }

    // TODO: ToNode?
    @Override
    public Symbol byteShape(ByteShape shape) {
        return SymbolUtil.fromClass(Byte.class);
    }

    @Override
    public Symbol shortShape(ShortShape shape) {
        return simpleShapeSymbolFrom(Short.class);
    }

    @Override
    public Symbol integerShape(IntegerShape shape) {
        return simpleShapeSymbolFrom(Integer.class);
    }

    @Override
    public Symbol intEnumShape(IntEnumShape shape) {
        return Symbol.builder()
                .name(SymbolUtil.getDefaultName(shape))
                .putProperty(SymbolProperties.ENUM_VALUE_TYPE, SymbolUtil.fromClass(int.class))
                .putProperty(SymbolProperties.TO_NODE_MAPPER, NODE_FROM)
                .putProperty(SymbolProperties.FROM_NODE_MAPPER, SymbolUtil.getDefaultName(shape) + ".fromNode($L)")
                .namespace(packageName, ".")
                .declarationFile(packagePath + "/" + SymbolUtil.getDefaultName(shape) + ".java")
                .build();
    }

    @Override
    public Symbol longShape(LongShape shape) {
        return simpleShapeSymbolFrom(Long.class);
    }

    @Override
    public Symbol floatShape(FloatShape shape) {
        return simpleShapeSymbolFrom(Float.class);
    }

    @Override
    public Symbol doubleShape(DoubleShape shape) {
        return simpleShapeSymbolFrom(Double.class);
    }

    @Override
    public Symbol bigIntegerShape(BigIntegerShape shape) {
        return simpleShapeSymbolFrom(BigInteger.class);
    }

    @Override
    public Symbol bigDecimalShape(BigDecimalShape shape) {
        return simpleShapeSymbolFrom(BigDecimal.class);
    }

    @Override
    public Symbol listShape(ListShape shape) {
        Class<?> shapeClass = shape.hasTrait(UniqueItemsTrait.class) ?  Set.class : List.class;
        return SymbolUtil.fromClass(shapeClass).toBuilder()
                .addReference(toSymbol(shape.getMember()))
                .putProperty(SymbolProperties.BUILDER_REF_INITIALIZER,
                        shape.hasTrait(UniqueItemsTrait.class) ? SET_INITIALIZER : LIST_INITIALIZER)
                .build();
    }

    @Override
    public Symbol mapShape(MapShape shape) {
        return SymbolUtil.fromClass(Map.class).toBuilder()
                .addReference(shape.getKey().accept(this))
                .addReference(shape.getValue().accept(this))
                .putProperty(SymbolProperties.BUILDER_REF_INITIALIZER, MAP_INITIALIZER)
                .build();
    }

    @Override
    public Symbol stringShape(StringShape shape) {
        return simpleShapeSymbolFrom(String.class).toBuilder()
                .putProperty(SymbolProperties.FROM_NODE_MAPPER, "$L.expectStringNode().getValue()")
                .build();
    }

    @Override
    public Symbol enumShape(EnumShape shape) {
        return Symbol.builder()
                .name(SymbolUtil.getDefaultName(shape))
                .putProperty(SymbolProperties.ENUM_VALUE_TYPE, SymbolUtil.fromClass(String.class))
                .putProperty(SymbolProperties.TO_NODE_MAPPER, "Node.from($L.getValue())")
                .putProperty(SymbolProperties.FROM_NODE_MAPPER, SymbolUtil.getDefaultName(shape) + ".fromNode($L)")
                .namespace(packageName, ".")
                .declarationFile(packagePath + "/" + SymbolUtil.getDefaultName(shape) + ".java")
                .build();
    }

    @Override
    public Symbol structureShape(StructureShape shape) {
        return Symbol.builder()
                .name(SymbolUtil.getDefaultName(shape))
                .namespace(packageName, ".")
                .putProperty(SymbolProperties.TO_NODE_MAPPER, TO_NODE)
                .putProperty(SymbolProperties.FROM_NODE_MAPPER, SymbolUtil.getDefaultName(shape) + ".fromNode($L)")
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
