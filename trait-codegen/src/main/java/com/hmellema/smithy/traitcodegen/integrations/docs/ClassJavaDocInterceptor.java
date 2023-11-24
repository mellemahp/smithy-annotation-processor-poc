package com.hmellema.smithy.traitcodegen.integrations.docs;

import com.hmellema.smithy.traitcodegen.writer.TraitCodegenWriter;
import com.hmellema.smithy.traitcodegen.writer.sections.ClassSection;
import com.hmellema.smithy.traitcodegen.writer.sections.JavaDocSection;
import software.amazon.smithy.model.traits.DocumentationTrait;
import software.amazon.smithy.utils.CodeInterceptor;

public class ClassJavaDocInterceptor implements CodeInterceptor.Prepender<ClassSection, TraitCodegenWriter> {
    @Override
    public void prepend(TraitCodegenWriter writer, ClassSection section) {
        if (section.shape().hasTrait(DocumentationTrait.class)) {
            writer.openDocstring();
            writer.pushState(new JavaDocSection(section.shape()));
            writer.writeDocStringContents(section.shape().expectTrait(DocumentationTrait.class).getValue());
            writer.popState();
            writer.closeDocstring();
        }
    }

    @Override
    public Class<ClassSection> sectionType() {
        return ClassSection.class;
    }
}
