package com.hmellema.smithy.traitcodegen.integrations.core;

import com.hmellema.smithy.traitcodegen.utils.SymbolUtil;
import com.hmellema.smithy.traitcodegen.writer.TraitCodegenWriter;
import com.hmellema.smithy.traitcodegen.writer.sections.BuilderClassSection;
import software.amazon.smithy.model.shapes.MemberShape;
import software.amazon.smithy.utils.CodeInterceptor;
import software.amazon.smithy.utils.SmithyBuilder;
import software.amazon.smithy.utils.ToSmithyBuilder;

import java.util.Iterator;

public class ToBuilderMethodInterceptor implements CodeInterceptor.Prepender<BuilderClassSection, TraitCodegenWriter> {
    private static final String VALUES_FLUENT_SETTER = ".values(getValues());";

    @Override
    public void prepend(TraitCodegenWriter writer, BuilderClassSection section) {
        writer.openDocstring();
        writer.writeDocStringContents("Creates a builder used to build a {@link $T}.", section.symbol());
        writer.closeDocstring();

        writer.addImports(SmithyBuilder.class, ToSmithyBuilder.class);
        writer.override();
        writer.openBlock("public SmithyBuilder<$T> toBuilder() {", "}", section.symbol(), () -> {
            writer.writeInline("return builder()");
            writer.indent();
            if (SymbolUtil.isTrait(section.shape())) {
                writer.write(".sourceLocation(getSourceLocation())");
            }

            // TODO: lots of special casing for the string list traits. Probably a better approach
            if (SymbolUtil.isStringListTrait(section.shape(), section.symbolProvider())) {
                writeStringListBody(writer);
            } else {
                writeBasicBody(writer, section);
            }
            writer.dedent();
        });
        writer.newLine();
    }

    private static void writeBasicBody(TraitCodegenWriter writer, BuilderClassSection section) {
        Iterator<MemberShape> memberIterator = section.shape().members().iterator();
        while (memberIterator.hasNext()) {
            MemberShape member = memberIterator.next();
            writer.writeInline(".$1L($1L)", SymbolUtil.toMemberNameOrValues(member, section.model(), section.symbolProvider()));
            if (memberIterator.hasNext()) {
                writer.writeInline("\n");
            } else {
                writer.writeInline(";\n");
            }
        }
    }

    private static void writeStringListBody(TraitCodegenWriter writer) {
        writer.write(VALUES_FLUENT_SETTER);
    }

    @Override
    public Class<BuilderClassSection> sectionType() {
        return BuilderClassSection.class;
    }
}
