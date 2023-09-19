package com.hmellema.smithy.traitcodegen.integrations.core;

import com.hmellema.smithy.traitcodegen.writer.TraitCodegenWriter;
import com.hmellema.smithy.traitcodegen.writer.sections.PropertySection;
import software.amazon.smithy.model.traits.DocumentationTrait;
import software.amazon.smithy.utils.CodeInterceptor;

public class PropertyJavaDocInterceptor implements CodeInterceptor.Prepender<PropertySection, TraitCodegenWriter> {
    @Override
    public void prepend(TraitCodegenWriter writer, PropertySection section) {
        section.shape().getTrait(DocumentationTrait.class).ifPresent(
                documentationTrait -> writer.writeComment(documentationTrait.getValue()));
    }

    @Override
    public Class<PropertySection> sectionType() {
        return PropertySection.class;
    }
}
