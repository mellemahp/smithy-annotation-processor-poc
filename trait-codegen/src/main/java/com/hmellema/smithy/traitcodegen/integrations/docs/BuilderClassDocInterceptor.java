package com.hmellema.smithy.traitcodegen.integrations.docs;

import com.hmellema.smithy.traitcodegen.writer.TraitCodegenWriter;
import com.hmellema.smithy.traitcodegen.writer.sections.BuilderClassSection;
import software.amazon.smithy.utils.CodeInterceptor;

public class BuilderClassDocInterceptor implements CodeInterceptor.Prepender<BuilderClassSection, TraitCodegenWriter> {
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
