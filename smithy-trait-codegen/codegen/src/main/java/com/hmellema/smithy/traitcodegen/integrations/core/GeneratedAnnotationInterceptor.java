package com.hmellema.smithy.traitcodegen.integrations.core;

import com.hmellema.smithy.traitcodegen.sections.ClassSection;
import com.hmellema.smithy.traitcodegen.writer.TraitCodegenWriter;
import software.amazon.smithy.utils.CodeInterceptor;
import software.amazon.smithy.utils.SmithyGenerated;

final class GeneratedAnnotationInterceptor implements CodeInterceptor.Prepender<ClassSection,
        TraitCodegenWriter> {
    @Override
    public void prepend(TraitCodegenWriter writer, ClassSection section) {
        writer.addImport(SmithyGenerated.class);
        writer.write("@SmithyGenerated");
    }

    @Override
    public Class<ClassSection> sectionType() {
        return ClassSection.class;
    }
}
