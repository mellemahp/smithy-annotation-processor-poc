package com.hmellema.smithy.traitcodegen;

import software.amazon.smithy.codegen.core.Symbol;
import software.amazon.smithy.codegen.core.SymbolProvider;
import software.amazon.smithy.model.Model;
import software.amazon.smithy.model.shapes.*;
import software.amazon.smithy.model.traits.Trait;
import software.amazon.smithy.model.traits.TraitDefinition;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;

public class TraitCodegenSymbolProvider extends ShapeVisitor.Default<Symbol> implements SymbolProvider {
    private static final String VALUE_GETTER_PROPERTY = "value-getter";
    private final BaseJavaSymbolVisitor baseVisitor;
    private final String packageName;
    private final String packagePath;
    private final Model model;

    TraitCodegenSymbolProvider(TraitCodegenSettings settings, Model model) {
        this.packageName = settings.packageName();
        this.packagePath = "./" + packageName.replace(".", "/");
        this.baseVisitor = new BaseJavaSymbolVisitor();
        this.model = model;
    }

    @Override
    public Symbol toSymbol(Shape shape) {
        if (shape.hasTrait(TraitDefinition.class)) {
            return shape.accept(this).toBuilder()
                    .putProperty("baseType", shape.accept(baseVisitor))
                    .build();
        } else {
            return shape.accept(baseVisitor);
        }
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
                .putProperty(VALUE_GETTER_PROPERTY, "shortValue()")
                .build();
    }

    @Override
    public Symbol integerShape(IntegerShape shape) {
        return getSymbolBuilder(shape)
                .putProperty(VALUE_GETTER_PROPERTY, "intValue()")
                .build();
    }

    @Override
    public Symbol floatShape(FloatShape shape) {
        return getSymbolBuilder(shape)
                .putProperty(VALUE_GETTER_PROPERTY, "floatValue()")
                .build();
    }

    @Override
    public Symbol doubleShape(DoubleShape shape) {
        return getSymbolBuilder(shape)
                .putProperty(VALUE_GETTER_PROPERTY, "doubleValue()")
                .build();
    }

    @Override
    public Symbol longShape(LongShape shape) {
        return getSymbolBuilder(shape)
                .putProperty(VALUE_GETTER_PROPERTY, "longValue()")
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

    private Symbol.Builder getSymbolBuilder(Shape shape) {
        return Symbol.builder()
                .name(SymbolUtil.getDefaultName(shape))
                .namespace(packageName,".")
                .declarationFile(packagePath + "/" + SymbolUtil.getDefaultName(shape) + ".java");
    }

    private final class BaseJavaSymbolVisitor extends ShapeVisitor.Default<Symbol> {
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
                    .addReference(shape.getMember().accept(this))
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
            return model.expectShape(shape.getTarget()).accept(this);
        }

        @Override
        protected Symbol getDefault(Shape shape) {
            return null;
        }
    }
}
