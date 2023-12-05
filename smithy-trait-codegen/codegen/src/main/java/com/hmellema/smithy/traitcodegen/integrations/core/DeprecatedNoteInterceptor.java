package com.hmellema.smithy.traitcodegen.integrations.core;

import com.hmellema.smithy.traitcodegen.writer.TraitCodegenWriter;
import com.hmellema.smithy.traitcodegen.sections.JavaDocSection;
import software.amazon.smithy.model.traits.DeprecatedTrait;
import software.amazon.smithy.utils.CodeInterceptor;

public class DeprecatedNoteInterceptor implements CodeInterceptor.Appender<JavaDocSection, TraitCodegenWriter> {
    @Override
    public void append(TraitCodegenWriter writer, JavaDocSection section) {
        if (section.shape().hasTrait(DeprecatedTrait.class)) {
            DeprecatedTrait trait = section.shape().expectTrait(DeprecatedTrait.class);
            writer.putContext("since", trait.getSince());
            // Add spacing
            writer.writeDocStringContents("");
            writer.writeDocStringContents("@deprecated ${?since}As of ${since:L}. ${/since}$L", trait.getMessage());
        }
    }

    @Override
    public Class<JavaDocSection> sectionType() {
        return JavaDocSection.class;
    }
}
