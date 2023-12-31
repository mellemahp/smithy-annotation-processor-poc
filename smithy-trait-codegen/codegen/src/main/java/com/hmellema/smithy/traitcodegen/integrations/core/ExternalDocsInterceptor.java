package com.hmellema.smithy.traitcodegen.integrations.core;

import com.hmellema.smithy.traitcodegen.sections.JavaDocSection;
import com.hmellema.smithy.traitcodegen.writer.TraitCodegenWriter;
import java.util.Map;
import software.amazon.smithy.model.traits.ExternalDocumentationTrait;
import software.amazon.smithy.utils.CodeInterceptor;

final class ExternalDocsInterceptor implements CodeInterceptor.Appender<JavaDocSection, TraitCodegenWriter> {
    @Override
    public void append(TraitCodegenWriter writer, JavaDocSection section) {
        ExternalDocumentationTrait trait = section.shape().expectTrait(ExternalDocumentationTrait.class);
        // Add a space to make it easier to read
        writer.writeDocStringContents("");
        for (Map.Entry<String, String> entry : trait.getUrls().entrySet()) {
            writer.writeDocStringContents("@see <a href=$S>$L</a>", entry.getKey(), entry.getValue());
        }
    }

    @Override
    public Class<JavaDocSection> sectionType() {
        return JavaDocSection.class;
    }

    @Override
    public boolean isIntercepted(JavaDocSection section) {
        return section.shape().hasTrait(ExternalDocumentationTrait.class);
    }
}
