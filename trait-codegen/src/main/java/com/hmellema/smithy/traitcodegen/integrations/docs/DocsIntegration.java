package com.hmellema.smithy.traitcodegen.integrations.docs;

import com.google.auto.service.AutoService;
import com.hmellema.smithy.traitcodegen.TraitCodegenContext;
import com.hmellema.smithy.traitcodegen.integrations.TraitCodegenIntegration;
import com.hmellema.smithy.traitcodegen.writer.TraitCodegenWriter;
import software.amazon.smithy.utils.CodeInterceptor;
import software.amazon.smithy.utils.CodeSection;

import java.util.List;

@AutoService(TraitCodegenIntegration.class)
public class DocsIntegration implements TraitCodegenIntegration {
    @Override
    public String name() {
        return "docs";
    }

    @Override
    public List<? extends CodeInterceptor<? extends CodeSection, TraitCodegenWriter>> interceptors(TraitCodegenContext codegenContext) {
        return List.of(
                new DeprecatedAnnotationClassInterceptor(),
                new DeprecatedNoteInterceptor(),
                new ClassJavaDocInterceptor(),
                new FromNodeDocInterceptor(),
                new ExternalDocsInterceptor(),
                new PropertyJavaDocInterceptor()
        );
    }
}