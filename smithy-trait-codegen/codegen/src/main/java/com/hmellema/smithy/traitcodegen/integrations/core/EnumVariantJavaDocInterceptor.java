package com.hmellema.smithy.traitcodegen.integrations.core;

import com.hmellema.smithy.traitcodegen.sections.EnumVariantSection;
import com.hmellema.smithy.traitcodegen.sections.JavaDocSection;
import com.hmellema.smithy.traitcodegen.writer.TraitCodegenWriter;
import software.amazon.smithy.model.traits.DocumentationTrait;
import software.amazon.smithy.utils.CodeInterceptor;

final class EnumVariantJavaDocInterceptor implements CodeInterceptor.Prepender<EnumVariantSection,
        TraitCodegenWriter> {
    @Override
    public void prepend(TraitCodegenWriter writer, EnumVariantSection section) {
        DocumentationTrait trait = section.memberShape().expectTrait(DocumentationTrait.class);
        writer.newLine();
        writer.openDocstring();
        writer.pushState(new JavaDocSection(section.memberShape()));
        writer.writeDocStringContents(trait.getValue());
        writer.popState();
        writer.closeDocstring();
    }

    @Override
    public Class<EnumVariantSection> sectionType() {
        return EnumVariantSection.class;
    }

    @Override
    public boolean isIntercepted(EnumVariantSection section) {
        return section.memberShape().hasTrait(DocumentationTrait.class);
    }
}
