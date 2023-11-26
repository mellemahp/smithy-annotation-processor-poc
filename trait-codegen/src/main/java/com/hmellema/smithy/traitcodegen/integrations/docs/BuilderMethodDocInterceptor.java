package com.hmellema.smithy.traitcodegen.integrations.docs;

import com.hmellema.smithy.traitcodegen.writer.TraitCodegenWriter;
import com.hmellema.smithy.traitcodegen.writer.sections.BuilderMethodSection;
import software.amazon.smithy.utils.CodeInterceptor;

public class BuilderMethodDocInterceptor implements CodeInterceptor.Prepender<BuilderMethodSection, TraitCodegenWriter> {
    @Override
    public void prepend(TraitCodegenWriter writer, BuilderMethodSection section) {
        writer.openDocstring();
        writer.writeDocStringContents("Creates a builder used to build a {@link $T}.", section.symbol());
        writer.closeDocstring();
    }

    @Override
    public Class<BuilderMethodSection> sectionType() {
        return BuilderMethodSection.class;
    }
}
