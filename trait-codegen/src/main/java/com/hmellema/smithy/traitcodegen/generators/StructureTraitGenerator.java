package com.hmellema.smithy.traitcodegen.generators;

import com.hmellema.smithy.traitcodegen.directives.GenerateTraitDirective;
import com.hmellema.smithy.traitcodegen.writer.TraitCodegenWriter;
import software.amazon.smithy.model.shapes.MemberShape;
import software.amazon.smithy.model.shapes.StructureShape;
import software.amazon.smithy.model.traits.AbstractTrait;

public final class StructureTraitGenerator extends TraitGenerator {
    @Override
    protected Class<?> getTraitClass() {
        return AbstractTrait.class;
    }

    @Override
    protected void writeAdditionalMethods(TraitCodegenWriter writer, GenerateTraitDirective directive) {
        StructureShape shape = directive.shape().asStructureShape().orElseThrow();
        new GetterGenerator(writer, shape, directive.symbolProvider(), directive.model()).run();
        getBuilderGenerator(writer, directive).run();
        new CreateNodeGenerator(writer).run();
    }

    @Override
    protected void writeAdditionalProperties(TraitCodegenWriter writer, GenerateTraitDirective directive) {
        for (MemberShape member: directive.shape().members()) {
            writer.write("private final $T $L;", directive.symbolProvider().toSymbol(member), member.getMemberName());
        }
        writer.write("");
    }

    @Override
    protected void writeConstructors(TraitCodegenWriter writer, GenerateTraitDirective directive) {
        getBuilderGenerator(writer, directive).createConstructorWithBuilder();
    }

    private BuilderGenerator getBuilderGenerator(TraitCodegenWriter writer, GenerateTraitDirective directive) {
        StructureShape structureShape = directive.shape().asStructureShape()
                .orElseThrow(() -> new RuntimeException("Expected structure shape"));
        return new BuilderGenerator(structureShape, directive.model(), directive.traitSymbol(), directive.symbolProvider(), writer, true);
    }
}
