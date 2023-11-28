package com.hmellema.smithy.traitcodegen.integrations.core;

import com.hmellema.smithy.traitcodegen.writer.TraitCodegenWriter;
import com.hmellema.smithy.traitcodegen.sections.ClassSection;
import software.amazon.smithy.model.traits.DeprecatedTrait;
import software.amazon.smithy.utils.CodeInterceptor;

public class DeprecatedAnnotationClassInterceptor implements CodeInterceptor.Prepender<ClassSection, TraitCodegenWriter> {
    @Override
    public void prepend(TraitCodegenWriter writer, ClassSection section) {
        section.shape().getTrait(DeprecatedTrait.class).ifPresent(t ->  writer.write("@Deprecated"));
    }

    @Override
    public Class<ClassSection> sectionType() {
        return ClassSection.class;
    }
}
