package com.hmellema.smithy.traitcodegen.integrations.core;

import com.hmellema.smithy.traitcodegen.TraitCodegenContext;
import com.hmellema.smithy.traitcodegen.integrations.TraitCodegenIntegration;
import com.hmellema.smithy.traitcodegen.writer.TraitCodegenWriter;
import software.amazon.smithy.utils.CodeInterceptor;
import software.amazon.smithy.utils.CodeSection;

import java.util.List;

public final class CoreIntegration implements TraitCodegenIntegration {

    @Override
    public String name() {
        return "core";
    }

    @Override
    public List<? extends CodeInterceptor<? extends CodeSection, TraitCodegenWriter>> interceptors(TraitCodegenContext codegenContext) {
        return List.of(
                new GeneratedAnnotationInterceptor(),
                new DeprecatedAnnotationClassInterceptor(),
                new DeprecatedNoteInterceptor(),
                new ClassJavaDocInterceptor(),
                new ExternalDocsInterceptor(),
                new PropertyJavaDocInterceptor(),
                new FromNodeDocsInterceptor(),
                new BuilderMethodDocsInterceptor(),
                new BuilderClassSectionDocsInterceptor()
        );
    }
}
