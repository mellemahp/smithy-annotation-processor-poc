package com.hmellema.smithy.traitcodegen;

import com.hmellema.smithy.traitcodegen.generators.base.EnumGenerator;
import com.hmellema.smithy.traitcodegen.generators.base.IntEnumGenerator;
import com.hmellema.smithy.traitcodegen.generators.base.StructureGenerator;
import com.hmellema.smithy.traitcodegen.generators.traits.AnnotationTraitGenerator;
import com.hmellema.smithy.traitcodegen.generators.traits.CollectionTraitGenerator;
import com.hmellema.smithy.traitcodegen.generators.traits.DocumentTraitGenerator;
import com.hmellema.smithy.traitcodegen.generators.traits.EnumTraitGenerator;
import com.hmellema.smithy.traitcodegen.generators.traits.IntEnumTraitGenerator;
import com.hmellema.smithy.traitcodegen.generators.traits.NumberTraitGenerator;
import com.hmellema.smithy.traitcodegen.generators.traits.StringListTraitGenerator;
import com.hmellema.smithy.traitcodegen.generators.traits.StringTraitGenerator;
import com.hmellema.smithy.traitcodegen.generators.traits.StructureTraitGenerator;
import com.hmellema.smithy.traitcodegen.integrations.TraitCodegenIntegration;
import com.hmellema.smithy.traitcodegen.utils.ShapeUtils;
import com.hmellema.smithy.traitcodegen.utils.SymbolUtil;
import software.amazon.smithy.codegen.core.SymbolProvider;
import software.amazon.smithy.codegen.core.directed.CreateContextDirective;
import software.amazon.smithy.codegen.core.directed.CreateSymbolProviderDirective;
import software.amazon.smithy.codegen.core.directed.DirectedCodegen;
import software.amazon.smithy.codegen.core.directed.GenerateEnumDirective;
import software.amazon.smithy.codegen.core.directed.GenerateErrorDirective;
import software.amazon.smithy.codegen.core.directed.GenerateIntEnumDirective;
import software.amazon.smithy.codegen.core.directed.GenerateServiceDirective;
import software.amazon.smithy.codegen.core.directed.GenerateStructureDirective;
import software.amazon.smithy.codegen.core.directed.GenerateUnionDirective;
import software.amazon.smithy.model.shapes.BigDecimalShape;
import software.amazon.smithy.model.shapes.BooleanShape;
import software.amazon.smithy.model.shapes.ByteShape;
import software.amazon.smithy.model.shapes.DocumentShape;
import software.amazon.smithy.model.shapes.DoubleShape;
import software.amazon.smithy.model.shapes.EnumShape;
import software.amazon.smithy.model.shapes.FloatShape;
import software.amazon.smithy.model.shapes.IntEnumShape;
import software.amazon.smithy.model.shapes.IntegerShape;
import software.amazon.smithy.model.shapes.ListShape;
import software.amazon.smithy.model.shapes.LongShape;
import software.amazon.smithy.model.shapes.MapShape;
import software.amazon.smithy.model.shapes.Shape;
import software.amazon.smithy.model.shapes.ShapeVisitor;
import software.amazon.smithy.model.shapes.ShortShape;
import software.amazon.smithy.model.shapes.StringShape;
import software.amazon.smithy.model.shapes.StructureShape;

final class TraitCodegenDirectedCodegen
        implements DirectedCodegen<TraitCodegenContext, TraitCodegenSettings, TraitCodegenIntegration> {

    @Override
    public SymbolProvider createSymbolProvider(CreateSymbolProviderDirective<TraitCodegenSettings> directive) {
        return BaseJavaSymbolProvider.fromDirective(directive);
    }

    @Override
    public TraitCodegenContext createContext(
            CreateContextDirective<TraitCodegenSettings, TraitCodegenIntegration> directive
    ) {
        return TraitCodegenContext.fromDirective(directive);
    }

    @Override
    public void generateService(
            GenerateServiceDirective<TraitCodegenContext, TraitCodegenSettings> directive
    ) {
        final TraitCodegenVisitor visitor = new TraitCodegenVisitor(directive.context());
        directive.connectedShapes().values().stream()
                .filter(ShapeUtils::isTrait)
                .forEach(shape -> shape.accept(visitor));
    }

    @Override
    public void generateError(GenerateErrorDirective<TraitCodegenContext, TraitCodegenSettings> directive) {
        // Do nothing on error generation
    }

    @Override
    public void generateStructure(GenerateStructureDirective<TraitCodegenContext, TraitCodegenSettings> directive) {
        if (!ShapeUtils.isTrait(directive.shape())) {
            new StructureGenerator().accept(directive);
        }
    }

    @Override
    public void generateUnion(GenerateUnionDirective<TraitCodegenContext, TraitCodegenSettings> directive) {
        throw new UnsupportedOperationException("trait codegen does not support generation of union traits");
    }

    @Override
    public void generateEnumShape(GenerateEnumDirective<TraitCodegenContext, TraitCodegenSettings> directive) {
        if (!ShapeUtils.isTrait(directive.shape())) {
            new EnumGenerator().accept(directive);
        }
    }

    @Override
    public void generateIntEnumShape(GenerateIntEnumDirective<TraitCodegenContext, TraitCodegenSettings> directive) {
        if (!ShapeUtils.isTrait(directive.shape())) {
            new IntEnumGenerator().accept(directive);
        }
    }

    private static final class TraitCodegenVisitor extends ShapeVisitor.Default<Void> {
        private final TraitCodegenContext context;

        private TraitCodegenVisitor(TraitCodegenContext context) {
            this.context = context;
        }

        @Override
        protected Void getDefault(Shape shape) {
            throw new UnsupportedOperationException("Trait code generation does not support shapes of type: "
                    + shape.getType());
        }

        @Override
        public Void booleanShape(BooleanShape shape) {
            throw new UnsupportedOperationException("Boolean shapes not supported for trait code generation. "
                    + "Consider using an Annotation trait instead");
        }

        @Override
        public Void intEnumShape(IntEnumShape shape) {
            new IntEnumTraitGenerator().accept(new GenerateTraitDirective(context, shape));
            return null;
        }

        @Override
        public Void stringShape(StringShape shape) {
            new StringTraitGenerator().accept(new GenerateTraitDirective(context, shape));
            return null;
        }

        @Override
        public Void enumShape(EnumShape shape) {
            new EnumTraitGenerator().accept(new GenerateTraitDirective(context, shape));
            return null;
        }

        @Override
        public Void listShape(ListShape shape) {
            if (SymbolUtil.isJavaString(context.symbolProvider().toSymbol(shape.getMember()))) {
                new StringListTraitGenerator().accept(new GenerateTraitDirective(context, shape));
            } else {
                new CollectionTraitGenerator().accept(new GenerateTraitDirective(context, shape));
            }
            return null;
        }

        @Override
        public Void byteShape(ByteShape shape) {
            new NumberTraitGenerator().accept(new GenerateTraitDirective(context, shape));
            return null;
        }

        @Override
        public Void shortShape(ShortShape shape) {
            new NumberTraitGenerator().accept(new GenerateTraitDirective(context, shape));
            return null;
        }

        @Override
        public Void integerShape(IntegerShape shape) {
            new NumberTraitGenerator().accept(new GenerateTraitDirective(context, shape));
            return null;
        }

        @Override
        public Void longShape(LongShape shape) {
            new NumberTraitGenerator().accept(new GenerateTraitDirective(context, shape));
            return null;
        }

        @Override
        public Void floatShape(FloatShape shape) {
            new NumberTraitGenerator().accept(new GenerateTraitDirective(context, shape));
            return null;
        }

        @Override
        public Void documentShape(DocumentShape shape) {
            new DocumentTraitGenerator().accept(new GenerateTraitDirective(context, shape));
            return null;
        }

        @Override
        public Void doubleShape(DoubleShape shape) {
            new NumberTraitGenerator().accept(new GenerateTraitDirective(context, shape));
            return null;
        }

        @Override
        public Void bigDecimalShape(BigDecimalShape shape) {
            new NumberTraitGenerator().accept(new GenerateTraitDirective(context, shape));
            return null;
        }

        @Override
        public Void mapShape(MapShape shape) {
            new CollectionTraitGenerator().accept(new GenerateTraitDirective(context, shape));
            return null;
        }

        @Override
        public Void structureShape(StructureShape shape) {
            if (shape.getAllMembers().isEmpty()) {
                new AnnotationTraitGenerator().accept(new GenerateTraitDirective(context, shape));
            } else {
                new StructureTraitGenerator().accept(new GenerateTraitDirective(context, shape));
            }
            return null;
        }
    }
}
