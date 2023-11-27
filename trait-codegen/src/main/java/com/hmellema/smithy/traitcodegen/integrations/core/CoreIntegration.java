package com.hmellema.smithy.traitcodegen.integrations.core;

import com.google.auto.service.AutoService;
import com.hmellema.smithy.traitcodegen.TraitCodegenContext;
import com.hmellema.smithy.traitcodegen.integrations.TraitCodegenIntegration;
import com.hmellema.smithy.traitcodegen.writer.TraitCodegenWriter;
import software.amazon.smithy.model.shapes.ShapeId;
import software.amazon.smithy.utils.CodeInterceptor;
import software.amazon.smithy.utils.CodeSection;

import java.util.List;

@AutoService(TraitCodegenIntegration.class)
public final class CoreIntegration implements TraitCodegenIntegration {
    
    @Override
    public String name() {
        return "core";
    }

    @Override
    public List<? extends CodeInterceptor<? extends CodeSection, TraitCodegenWriter>> interceptors(TraitCodegenContext codegenContext) {
        return List.of(
                new GeneratedAnnotationInterceptor(),
                new PropertiesGeneratorInterceptor(),
                new ConstructorWithBuilderInjector(),
                new GetterSectionInterceptor(),
                new ToNodeSectionInjector(),
                new FromNodeSectionInjector(),
                new ToBuilderMethodInterceptor(),
                new BuilderMethodInterceptor(),
                new BuilderSectionInterceptor(),
                new ProviderSectionInjector()
        );
    }
}
