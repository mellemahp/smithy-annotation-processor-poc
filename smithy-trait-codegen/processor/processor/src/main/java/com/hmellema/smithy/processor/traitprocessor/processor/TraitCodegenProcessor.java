package com.hmellema.smithy.processor.traitprocessor.processor;


import com.hmellema.smithy.processor.SmithyProcessor;
import com.hmellema.smithy.processor.traitprocessor.annotations.GenerateSmithyTraits;
import software.amazon.smithy.model.node.Node;
import software.amazon.smithy.model.node.ObjectNode;

import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;

@SupportedAnnotationTypes(GenerateSmithyTraits.NAME)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class TraitCodegenProcessor extends SmithyProcessor<GenerateSmithyTraits> {
    @Override
    protected String getPluginName() {
        return "trait-codegen";
    }

    @Override
    protected Class<GenerateSmithyTraits> getAnnotationClass() {
        return GenerateSmithyTraits.class;
    }

    @Override
    protected ObjectNode createPluginNode(GenerateSmithyTraits annotation) {
        return Node.objectNodeBuilder()
                .withMember("package", annotation.packageName())
                .withMember("header", Node.fromStrings(annotation.header()))
                .build();
    }
}
