package com.hmellema.smithy.traitcodegen.integrations.core;

import com.hmellema.smithy.traitcodegen.TraitCodegenContext;
import com.hmellema.smithy.traitcodegen.integrations.TraitCodegenIntegration;
import com.hmellema.smithy.traitcodegen.writer.TraitCodegenWriter;
import java.util.List;
import software.amazon.smithy.utils.CodeInterceptor;
import software.amazon.smithy.utils.CodeSection;
import software.amazon.smithy.utils.ListUtils;

public final class CoreIntegration implements TraitCodegenIntegration {

    @Override
    public String name() {
        return "core";
    }

    @Override
    public List<? extends CodeInterceptor<? extends CodeSection, TraitCodegenWriter>> interceptors(
            TraitCodegenContext codegenContext) {
        return ListUtils.of(
                new GeneratedAnnotationInterceptor(),
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
