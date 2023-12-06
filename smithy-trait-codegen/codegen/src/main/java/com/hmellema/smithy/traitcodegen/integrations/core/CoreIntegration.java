package com.hmellema.smithy.traitcodegen.integrations.core;

import com.hmellema.smithy.traitcodegen.TraitCodegenContext;
import com.hmellema.smithy.traitcodegen.integrations.TraitCodegenIntegration;
import com.hmellema.smithy.traitcodegen.writer.TraitCodegenWriter;
import software.amazon.smithy.utils.CodeInterceptor;
import software.amazon.smithy.utils.CodeSection;
import software.amazon.smithy.utils.ListUtils;

import java.util.List;

public final class CoreIntegration implements TraitCodegenIntegration {

    @Override
    public String name() {
        return "core";
    }

    @Override
    public List<? extends CodeInterceptor<? extends CodeSection, TraitCodegenWriter>> interceptors(TraitCodegenContext codegenContext) {
        return ListUtils.of(
                new DeprecatedAnnotationClassInterceptor(),
                new DeprecatedNoteInterceptor(),
                new ClassJavaDocInterceptor(),
                new ExternalDocsInterceptor(),
                new FromNodeDocsInterceptor(),
                new BuilderMethodDocsInterceptor(),
                new BuilderClassSectionDocsInterceptor(),
                new GetterJavaDocInterceptor(),
                new EnumVariantJavaDocInterceptor()
        );
    }
}
