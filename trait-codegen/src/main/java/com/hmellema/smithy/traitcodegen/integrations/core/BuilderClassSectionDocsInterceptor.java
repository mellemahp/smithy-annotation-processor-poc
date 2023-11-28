package com.hmellema.smithy.traitcodegen.integrations.core;

import com.hmellema.smithy.traitcodegen.sections.BuilderClassSection;
import com.hmellema.smithy.traitcodegen.writer.TraitCodegenWriter;
import software.amazon.smithy.utils.CodeInterceptor;

public class BuilderClassSectionDocsInterceptor implements CodeInterceptor.Prepender<BuilderClassSection, TraitCodegenWriter> {
    @Override
    public void prepend(TraitCodegenWriter writer, BuilderClassSection section) {
        writer.openDocstring();
        writer.writeDocStringContents("Builder for {@link $T}.", section.symbol());
        writer.closeDocstring();
    }

    @Override
    public Class<BuilderClassSection> sectionType() {
        return BuilderClassSection.class;
    }
}
