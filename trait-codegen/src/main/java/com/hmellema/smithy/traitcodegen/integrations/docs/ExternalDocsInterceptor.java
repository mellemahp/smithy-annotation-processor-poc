package com.hmellema.smithy.traitcodegen.integrations.docs;

import com.hmellema.smithy.traitcodegen.writer.TraitCodegenWriter;
import com.hmellema.smithy.traitcodegen.writer.sections.JavaDocSection;
import software.amazon.smithy.model.traits.ExternalDocumentationTrait;
import software.amazon.smithy.utils.CodeInterceptor;

import java.util.Map;

public class ExternalDocsInterceptor implements CodeInterceptor.Appender<JavaDocSection, TraitCodegenWriter> {

    @Override
    public void append(TraitCodegenWriter writer, JavaDocSection section) {
        if (section.shape().hasTrait(ExternalDocumentationTrait.class)) {
            ExternalDocumentationTrait trait = section.shape().expectTrait(ExternalDocumentationTrait.class);
            // Add a space to make it easier to read
            writer.writeDocStringContents("");
            for (Map.Entry<String, String> entry : trait.getUrls().entrySet()) {
                writer.writeDocStringContents("@see <a href=$S>$L</a>", entry.getKey(), entry.getValue());
            }
        }
    }

    @Override
    public Class<JavaDocSection> sectionType() {
        return JavaDocSection.class;
    }
}
