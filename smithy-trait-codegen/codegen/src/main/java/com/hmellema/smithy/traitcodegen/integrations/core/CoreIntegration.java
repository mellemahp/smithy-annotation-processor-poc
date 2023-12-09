package com.hmellema.smithy.traitcodegen.integrations.core;

import com.hmellema.smithy.traitcodegen.TraitCodegenContext;
import com.hmellema.smithy.traitcodegen.TraitGeneratorProvider;
import com.hmellema.smithy.traitcodegen.generators.traits.StringListTraitGenerator;
import com.hmellema.smithy.traitcodegen.integrations.TraitCodegenIntegration;
import com.hmellema.smithy.traitcodegen.utils.SymbolUtil;
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

    @Override
    public TraitGeneratorProvider decorateGeneratorProvider(TraitCodegenContext context,
                                                            TraitGeneratorProvider provider) {
        return shape -> {
            // Handles special casing for StringListShapes
            if (shape.isListShape() && SymbolUtil.isJavaString(
                    context.symbolProvider().toSymbol(
                            shape.asListShape().orElseThrow(RuntimeException::new).getMember()))
            ) {
                return new StringListTraitGenerator();
            }
            return provider.getGenerator(shape);
        };
    }
}
