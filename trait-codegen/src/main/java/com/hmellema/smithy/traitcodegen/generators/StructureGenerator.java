package com.hmellema.smithy.traitcodegen.generators;

import com.hmellema.smithy.traitcodegen.TraitCodegenContext;
import com.hmellema.smithy.traitcodegen.TraitCodegenSettings;
import com.hmellema.smithy.traitcodegen.writer.TraitCodegenWriter;
import com.hmellema.smithy.traitcodegen.writer.sections.ClassSection;
import software.amazon.smithy.codegen.core.SymbolProvider;
import software.amazon.smithy.codegen.core.directed.GenerateStructureDirective;
import software.amazon.smithy.model.shapes.*;
import software.amazon.smithy.utils.StringUtils;

import java.util.function.Consumer;

public class StructureGenerator implements Consumer<GenerateStructureDirective<TraitCodegenContext, TraitCodegenSettings>> {
    private static final String BASE_CLASS_TEMPLATE_STRING = "public final class $1T implements ToNode, ToSmithyBuilder<$1T> {";
    private static final String PROPERTY_TEMPLATE = "private final $T $L;";

    @Override
    public void accept(GenerateStructureDirective<TraitCodegenContext, TraitCodegenSettings> directive) {
        directive.context().writerDelegator().useShapeWriter(directive.shape(), writer -> {
            writer.pushState(new ClassSection(directive.shape()))
                .openBlock(BASE_CLASS_TEMPLATE_STRING, "}", directive.symbol(), () -> {
                    writeProperties(directive.shape(), directive.symbolProvider(), writer);

                    // Creates all builder-associated methods
                    BuilderGenerator builderGenerator = new BuilderGenerator(directive.shape(), directive.model(), directive.symbol(), directive.symbolProvider(), writer, false);
                    builderGenerator.createConstructorWithBuilder();
                    builderGenerator.createToBuilderMethod();

                    // Creates toNode method
                    ToNodeGenerator toNodeGenerator = new ToNodeGenerator(directive.shape(), writer);
                    toNodeGenerator.run();

                    // TODO
                    // Creates fromNode method

                    new GetterGenerator(writer, directive.shape(), directive.symbolProvider(), directive.model()).run();

                    // TODO: Should these trait sub-structures have setters by default?
                    //writeSetters(directive.shape(), directive.symbolProvider(), writer);
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
