package com.hmellema.smithy.traitcodegen.integrations.docs;

import com.hmellema.smithy.traitcodegen.writer.TraitCodegenWriter;
import com.hmellema.smithy.traitcodegen.writer.sections.JavaDocSection;
import com.hmellema.smithy.traitcodegen.writer.sections.PropertySection;
import software.amazon.smithy.model.traits.DocumentationTrait;
import software.amazon.smithy.utils.CodeInterceptor;

public class PropertyJavaDocInterceptor implements CodeInterceptor.Prepender<PropertySection, TraitCodegenWriter> {
    @Override
    public void prepend(TraitCodegenWriter writer, PropertySection section) {
        if (section.shape().hasTrait(DocumentationTrait.class)) {
            DocumentationTrait trait = section.shape().expectTrait(DocumentationTrait.class);
            writer.openDocstring();
            writer.pushState(new JavaDocSection(section.shape()));
            writer.writeDocStringContents(trait.getValue());
            writer.popState();
            writer.closeDocstring();
        }
    }

    @Override
    public Class<PropertySection> sectionType() {
        return PropertySection.class;
    }
}
