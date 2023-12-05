package com.hmellema.smithy.traitcodegen.integrations.core;

import com.hmellema.smithy.traitcodegen.sections.ToBuilderSection;
import com.hmellema.smithy.traitcodegen.writer.TraitCodegenWriter;
import software.amazon.smithy.utils.CodeInterceptor;

public class BuilderMethodDocsInterceptor implements CodeInterceptor.Prepender<ToBuilderSection, TraitCodegenWriter> {
    @Override
    public void prepend(TraitCodegenWriter writer, ToBuilderSection section) {
        writer.openDocstring();
        writer.writeDocStringContents("Creates a builder used to build a {@link $T}.", section.symbol());
        writer.closeDocstring();
    }

    @Override
    public Class<ToBuilderSection> sectionType() {
        return ToBuilderSection.class;
    }
}
