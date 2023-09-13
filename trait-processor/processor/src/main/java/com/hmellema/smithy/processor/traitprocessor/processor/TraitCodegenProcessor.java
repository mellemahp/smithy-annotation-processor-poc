package com.hmellema.smithy.processor.traitprocessor.processor;


import com.google.auto.service.AutoService;
import com.hmellema.smithy.processor.SmithyProcessor;
import com.hmellema.smithy.processor.traitprocessor.annotations.GenerateSmithyTraits;
import software.amazon.smithy.build.model.SmithyBuildConfig;
import software.amazon.smithy.model.node.Node;
import software.amazon.smithy.model.node.ObjectNode;

import javax.annotation.processing.Processor;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import java.util.HashMap;
import java.util.Map;

@AutoService(Processor.class)
@SupportedAnnotationTypes(GenerateSmithyTraits.NAME)
@SupportedSourceVersion(SourceVersion.RELEASE_17)
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
    protected SmithyBuildConfig createBuildConfig(GenerateSmithyTraits annotation) {
        Map<String, ObjectNode> pluginMap = new HashMap<>();
        pluginMap.put(getPluginName(), Node.objectNodeBuilder()
                .withMember("package", annotation.packageName())
                .build());

        return SmithyBuildConfig.builder().version("1.0").plugins(pluginMap).build();
    }
}
