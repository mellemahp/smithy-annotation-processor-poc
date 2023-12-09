package com.hmellema.smithy.traitcodegen;

import com.hmellema.smithy.traitcodegen.generators.traits.AnnotationTraitGenerator;
import com.hmellema.smithy.traitcodegen.generators.traits.CollectionTraitGenerator;
import com.hmellema.smithy.traitcodegen.generators.traits.DocumentTraitGenerator;
import com.hmellema.smithy.traitcodegen.generators.traits.EnumTraitGenerator;
import com.hmellema.smithy.traitcodegen.generators.traits.IntEnumTraitGenerator;
import com.hmellema.smithy.traitcodegen.generators.traits.NumberTraitGenerator;
import com.hmellema.smithy.traitcodegen.generators.traits.StringTraitGenerator;
import com.hmellema.smithy.traitcodegen.generators.traits.StructureTraitGenerator;
import com.hmellema.smithy.traitcodegen.generators.traits.TraitGenerator;
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

public interface
TraitGeneratorProvider {
    TraitGeneratorProvider DEFAULT = new DefaultProvider();

    TraitGenerator getGenerator(Shape shape);

    final class DefaultProvider extends ShapeVisitor.Default<TraitGenerator> implements TraitGeneratorProvider {
        @Override
        public TraitGenerator getGenerator(Shape shape) {
            return shape.accept(this);
        }

        @Override
        protected TraitGenerator getDefault(Shape shape) {
            throw new UnsupportedOperationException("Trait code generation does not support shapes of type: "
                    + shape.getType());
        }

        @Override
        public TraitGenerator booleanShape(BooleanShape shape) {
            throw new UnsupportedOperationException("Boolean shapes not supported for trait code generation. "
                    + "Consider using an Annotation trait instead");
        }

        @Override
        public TraitGenerator intEnumShape(IntEnumShape shape) {
            return new IntEnumTraitGenerator();
        }

        @Override
        public TraitGenerator stringShape(StringShape shape) {
            return new StringTraitGenerator();
        }

        @Override
        public TraitGenerator enumShape(EnumShape shape) {
            return new EnumTraitGenerator();
        }

        @Override
        public TraitGenerator listShape(ListShape shape) {
            return new CollectionTraitGenerator();
        }

        @Override
        public TraitGenerator byteShape(ByteShape shape) {
            return new NumberTraitGenerator();
        }

        @Override
        public TraitGenerator shortShape(ShortShape shape) {
            return new NumberTraitGenerator();
        }

        @Override
        public TraitGenerator integerShape(IntegerShape shape) {
            return new NumberTraitGenerator();
        }

        @Override
        public TraitGenerator longShape(LongShape shape) {
            return new NumberTraitGenerator();
        }

        @Override
        public TraitGenerator floatShape(FloatShape shape) {
            return new NumberTraitGenerator();
        }

        @Override
        public TraitGenerator documentShape(DocumentShape shape) {
            return new DocumentTraitGenerator();
        }

        @Override
        public TraitGenerator doubleShape(DoubleShape shape) {
            return new NumberTraitGenerator();
        }

        @Override
        public TraitGenerator bigDecimalShape(BigDecimalShape shape) {
            return new NumberTraitGenerator();
        }

        @Override
        public TraitGenerator mapShape(MapShape shape) {
            return new CollectionTraitGenerator();
        }

        @Override
        public TraitGenerator structureShape(StructureShape shape) {
            if (shape.getAllMembers().isEmpty()) {
                return new AnnotationTraitGenerator();
            }
            return new StructureTraitGenerator();
        }
    }
}
