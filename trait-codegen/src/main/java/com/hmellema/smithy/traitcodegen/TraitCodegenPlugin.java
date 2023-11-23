package com.hmellema.smithy.traitcodegen;

import com.google.auto.service.AutoService;
import com.hmellema.smithy.traitcodegen.integrations.TraitCodegenIntegration;
import com.hmellema.smithy.traitcodegen.writer.TraitCodegenWriter;
import software.amazon.smithy.build.PluginContext;
import software.amazon.smithy.build.SmithyBuildPlugin;
import software.amazon.smithy.codegen.core.directed.CodegenDirector;
import software.amazon.smithy.model.Model;

import java.util.logging.Logger;

@AutoService(SmithyBuildPlugin.class)
public class TraitCodegenPlugin implements SmithyBuildPlugin {
    private static final Logger LOGGER = Logger.getLogger(TraitCodegenPlugin.class.getName());
    private static final String NAME = "trait-codegen";
    private final CodegenDirector<TraitCodegenWriter, TraitCodegenIntegration, TraitCodegenContext, TraitCodegenSettings> runner =
            new CodegenDirector<>();

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void execute(PluginContext context) {
        runner.directedCodegen(new TraitCodegenDirectedCodegen());
        runner.integrationClass(TraitCodegenIntegration.class);
        runner.fileManifest(context.getFileManifest());
        Model transformed = SyntheticTraitServiceTransformer.transform(context.getModel());
        runner.model(transformed);
        runner.settings(TraitCodegenSettings.from(context.getSettings()));
        runner.service(SyntheticTraitServiceTransformer.SYNTHETIC_SERVICE_ID);
        runner.performDefaultCodegenTransforms();
        LOGGER.info("Plugin Initialized. Executing Trait Codegen Plugin.");
        runner.run();
        LOGGER.info("Trait Codegen plugin executed successfully.");
    }
}
