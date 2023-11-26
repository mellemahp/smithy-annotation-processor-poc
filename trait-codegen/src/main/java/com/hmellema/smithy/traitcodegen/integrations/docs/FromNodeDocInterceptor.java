package com.hmellema.smithy.traitcodegen.integrations.docs;

import com.hmellema.smithy.traitcodegen.writer.TraitCodegenWriter;
import com.hmellema.smithy.traitcodegen.writer.sections.FromNodeSection;
import software.amazon.smithy.utils.CodeInterceptor;

public class FromNodeDocInterceptor implements CodeInterceptor.Prepender<FromNodeSection, TraitCodegenWriter> {
    @Override
    public void prepend(TraitCodegenWriter writer, FromNodeSection section) {
        writer.openDocstring();
        writer.writeDocStringContents("Creates a {@link $T} from a {@link Node}.", section.symbol());
        writer.writeDocStringContents("");
        writer.writeDocStringContents("@param node Node to create the $T from.", section.symbol());
        writer.writeDocStringContents("@return Returns the created $T.", section.symbol());
        writer.writeDocStringContents("@throws ExpectationNotMetException if the given Node is invalid.");
        writer.closeDocstring();
    }

    @Override
    public Class<FromNodeSection> sectionType() {
        return FromNodeSection.class;
    }
}
