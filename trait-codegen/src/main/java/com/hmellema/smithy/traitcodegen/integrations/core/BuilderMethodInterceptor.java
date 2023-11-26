package com.hmellema.smithy.traitcodegen.integrations.core;

import com.hmellema.smithy.traitcodegen.writer.TraitCodegenWriter;
import com.hmellema.smithy.traitcodegen.writer.sections.BuilderClassSection;
import software.amazon.smithy.utils.CodeInterceptor;

public final class BuilderMethodInterceptor implements CodeInterceptor.Prepender<BuilderClassSection, TraitCodegenWriter> {
    private static final String BUILDER_METHOD_TEMPLATE = "public static final Builder builder() {";

    @Override
    public void prepend(TraitCodegenWriter writer, BuilderClassSection section) {
        writer.openBlock(BUILDER_METHOD_TEMPLATE, "}", () -> writer.write("return new Builder();"));
        writer.newLine();
    }

    @Override
    public Class<BuilderClassSection> sectionType() {
        return BuilderClassSection.class;
    }
}
