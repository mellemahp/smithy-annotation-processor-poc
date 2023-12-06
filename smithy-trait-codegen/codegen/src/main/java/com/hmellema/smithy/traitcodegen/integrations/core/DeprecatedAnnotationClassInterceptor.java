package com.hmellema.smithy.traitcodegen.integrations.core;

import com.hmellema.smithy.traitcodegen.writer.TraitCodegenWriter;
import com.hmellema.smithy.traitcodegen.sections.ClassSection;
import software.amazon.smithy.model.traits.DeprecatedTrait;
import software.amazon.smithy.utils.CodeInterceptor;

public class DeprecatedAnnotationClassInterceptor implements CodeInterceptor.Prepender<ClassSection, TraitCodegenWriter> {
    @Override
    public void prepend(TraitCodegenWriter writer, ClassSection section) {
        DeprecatedTrait trait = section.shape().expectTrait(DeprecatedTrait.class);
        writer.putContext("since", trait.getSince());
        writer.write("@Deprecated${?since}(since = ${since:S})${/since}");
    }

    @Override
    public Class<ClassSection> sectionType() {
        return ClassSection.class;
    }

    @Override
    public boolean isIntercepted(ClassSection section) {
        return section.shape().hasTrait(DeprecatedTrait.class);
    }
}
