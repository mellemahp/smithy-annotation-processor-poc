package com.hmellema.smithy.traitcodegen.integrations.core;

import com.hmellema.smithy.traitcodegen.sections.GetterSection;
import com.hmellema.smithy.traitcodegen.sections.JavaDocSection;
import com.hmellema.smithy.traitcodegen.writer.TraitCodegenWriter;
import software.amazon.smithy.model.traits.DocumentationTrait;
import software.amazon.smithy.utils.CodeInterceptor;

public class GetterJavaDocInterceptor implements CodeInterceptor.Prepender<GetterSection, TraitCodegenWriter> {
    @Override
    public void prepend(TraitCodegenWriter writer, GetterSection section) {
        DocumentationTrait trait = section.shape().expectTrait(DocumentationTrait.class);
        writer.newLine();
        writer.openDocstring();
        writer.pushState(new JavaDocSection(section.shape()));
        writer.writeDocStringContents(trait.getValue());
        writer.popState();
        writer.closeDocstring();
    }

    @Override
    public Class<GetterSection> sectionType() {
        return GetterSection.class;
    }

    @Override
    public boolean isIntercepted(GetterSection section) {
        return section.shape().hasTrait(DocumentationTrait.class)
                && section.shape().isMemberShape();
    }
}
