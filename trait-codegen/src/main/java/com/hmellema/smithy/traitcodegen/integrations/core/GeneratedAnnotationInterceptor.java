package com.hmellema.smithy.traitcodegen.integrations.core;

import com.hmellema.smithy.traitcodegen.TraitCodegenPlugin;
import com.hmellema.smithy.traitcodegen.writer.TraitCodegenWriter;
import com.hmellema.smithy.traitcodegen.writer.sections.ClassSection;
import software.amazon.smithy.codegen.core.Symbol;
import software.amazon.smithy.utils.CodeInterceptor;

import javax.annotation.processing.Generated;

public class GeneratedAnnotationInterceptor implements CodeInterceptor.Prepender<ClassSection, TraitCodegenWriter> {
    private final Symbol generatedAnnotationSymbol = Symbol.builder()
            .name(Generated.class.getSimpleName())
            .namespace(Generated.class.getPackageName(), ".")
            .build();

    @Override
    public void prepend(TraitCodegenWriter writer, ClassSection section) {
        writer.addImport(generatedAnnotationSymbol, generatedAnnotationSymbol.getName());
        writer.write("@$T($S)", generatedAnnotationSymbol, TraitCodegenPlugin.class.getName());
    }

    @Override
    public Class<ClassSection> sectionType() {
        return ClassSection.class;
    }
}
