package com.hmellema.smithy.traitcodegen.integrations.core;

import com.hmellema.smithy.traitcodegen.writer.TraitCodegenWriter;
import com.hmellema.smithy.traitcodegen.writer.sections.ClassSection;
import software.amazon.smithy.model.traits.DocumentationTrait;
import software.amazon.smithy.model.traits.ExternalDocumentationTrait;
import software.amazon.smithy.utils.CodeInterceptor;

import java.util.Map;

public class ClassJavaDocInterceptor implements CodeInterceptor.Prepender<ClassSection, TraitCodegenWriter> {
    @Override
    public void prepend(TraitCodegenWriter writer, ClassSection section) {
        if (section.shape().hasTrait(DocumentationTrait.class)) {
            writer.openDocstring();
            writer.writeDocStringContents(section.shape().expectTrait(DocumentationTrait.class).getValue());
            section.shape().getTrait(ExternalDocumentationTrait.class)
                    .ifPresent(trait -> writeExternalDocumentation(writer, trait));
            writer.closeDocstring();
        }
    }

    private static void writeExternalDocumentation(TraitCodegenWriter writer, ExternalDocumentationTrait trait) {
        for (Map.Entry<String, String> entry : trait.getUrls().entrySet()) {
            writer.writeDocStringContents("@see <a href=$S>$L</a>", entry.getKey(), entry.getValue());
        }
    }

    @Override
    public Class<ClassSection> sectionType() {
        return ClassSection.class;
    }
}
