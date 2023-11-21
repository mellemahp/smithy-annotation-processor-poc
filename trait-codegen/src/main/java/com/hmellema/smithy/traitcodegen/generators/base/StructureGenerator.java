package com.hmellema.smithy.traitcodegen.generators.base;

import com.hmellema.smithy.traitcodegen.TraitCodegenContext;
import com.hmellema.smithy.traitcodegen.TraitCodegenSettings;
import com.hmellema.smithy.traitcodegen.generators.common.builder.BuilderConstructorGenerator;
import com.hmellema.smithy.traitcodegen.generators.common.builder.BuilderGenerator;
import com.hmellema.smithy.traitcodegen.generators.common.builder.ToBuilderGenerator;
import com.hmellema.smithy.traitcodegen.generators.common.CreateNodeGenerator;
import com.hmellema.smithy.traitcodegen.generators.common.GetterGenerator;
import com.hmellema.smithy.traitcodegen.writer.TraitCodegenWriter;
import com.hmellema.smithy.traitcodegen.writer.sections.ClassSection;
import software.amazon.smithy.codegen.core.SymbolProvider;
import software.amazon.smithy.codegen.core.directed.GenerateStructureDirective;
import software.amazon.smithy.model.node.ToNode;
import software.amazon.smithy.model.shapes.*;
import software.amazon.smithy.utils.StringUtils;

import java.util.function.Consumer;

public class StructureGenerator implements Consumer<GenerateStructureDirective<TraitCodegenContext, TraitCodegenSettings>> {
    private static final String BASE_CLASS_TEMPLATE_STRING = "public final class $1T implements ToNode, ToSmithyBuilder<$1T> {";
    private static final String PROPERTY_TEMPLATE = "private final $T $L;";

    @Override
    public void accept(GenerateStructureDirective<TraitCodegenContext, TraitCodegenSettings> directive) {
        directive.context().writerDelegator().useShapeWriter(directive.shape(), writer -> {
            writer.addImport(ToNode.class);
            writer.pushState(new ClassSection(directive.shape()))
                .openBlock(BASE_CLASS_TEMPLATE_STRING, "}", directive.symbol(), () -> {
                    writeProperties(directive.shape(), directive.symbolProvider(), writer);

                    // Create constructor from builder
                    new BuilderConstructorGenerator(writer, directive.symbol(), directive.shape(), directive.symbolProvider(), directive.model()).run();
                    writer.write("");

                    // Creates all builder-associated methods
                    new ToBuilderGenerator(writer, directive.symbol(), directive.shape(), directive.symbolProvider(), directive.model(), false).run();
                    writer.write("");

                    // Creates toNode method
                    CreateNodeGenerator toNodeGenerator = new CreateNodeGenerator(writer, directive.symbolProvider(), directive.model());
                    toNodeGenerator.writeToNodeMethod(directive.shape());

                    // TODO
                    // Creates fromNode method

                    directive.shape().accept(new GetterGenerator(writer, directive.symbolProvider(), directive.model()));

                    // TODO: Should these trait sub-structures have setters by default?
                    //writeSetters(directive.shape(), directive.symbolProvider(), writer);
                    BuilderGenerator builderGenerator = new BuilderGenerator(directive.shape(), directive.model(), directive.symbol(), directive.symbolProvider(), writer);
                    builderGenerator.run();
                })
                .popState();
        });
    }

    private void writeProperties(StructureShape shape, SymbolProvider provider, TraitCodegenWriter writer) {
        for (MemberShape member: shape.members()) {
            writer.write(PROPERTY_TEMPLATE, provider.toSymbol(member), member.getMemberName());
        }
        writer.write("");
    }

    private void writeSetters(StructureShape shape, SymbolProvider symbolProvider, TraitCodegenWriter writer) {
        for (MemberShape member : shape.members()) {
            writer.openBlock("public void set$L($T $L) {", "}",
                        StringUtils.capitalize(symbolProvider.toMemberName(member)),
                        symbolProvider.toSymbol(member),
                        symbolProvider.toMemberName(member),
                    () -> writer.write("this.$1L = $1L;", symbolProvider.toMemberName(member)));
            writer.write("");
        }
    }
}
