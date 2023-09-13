package com.hmellema.smithy.traitcodegen;

import com.google.auto.service.AutoService;
import software.amazon.smithy.build.PluginContext;
import software.amazon.smithy.build.SmithyBuildPlugin;
import software.amazon.smithy.codegen.core.SymbolProvider;
import software.amazon.smithy.model.Model;
import software.amazon.smithy.model.shapes.Shape;
import software.amazon.smithy.model.shapes.ShapeVisitor;
import software.amazon.smithy.model.traits.TraitDefinition;
import software.amazon.smithy.model.transform.ModelTransformer;

import java.util.logging.Logger;

@AutoService(SmithyBuildPlugin.class)
public class TraitCodegenPlugin implements SmithyBuildPlugin {
    private static final Logger LOGGER = Logger.getLogger(TraitCodegenPlugin.class.getName());

    private final ModelTransformer transformer = ModelTransformer.create();

    @Override
    public String getName() {
        return "trait-codegen";
    }

    @Override
    public void execute(PluginContext context) {
        LOGGER.severe("Initializing Trait Codegen plugin...");
        TraitCodegenSettings settings = TraitCodegenSettings.from(context.getSettings());
        Model model = context.getModel();

        // Execute base transforms
        model = transformer.flattenAndRemoveMixins(model);
        model = transformer.changeStringEnumsToEnumShapes(model);

        // Create context to use for the rest of the steps
        SymbolProvider baseSymbolProvider = new TraitCodegenSymbolProvider(settings);
        TraitCodegenContext codegenContext = new TraitCodegenContext(model, settings, baseSymbolProvider, context.getFileManifest());

        // Set up generator visitor
        ShapeVisitor<Void>  generator = new TraitCodegenGenerator(codegenContext);

        // Generate all shapes with the Trait smithy trait
        for (Shape shape : model.getShapesWithTrait(TraitDefinition.class)) {
            shape.accept(generator);
        }

        // Write all to files
        if (!codegenContext.writerDelegator().getWriters().isEmpty()) {
            LOGGER.severe("Flushing remaining writers.");
            codegenContext.writerDelegator().flushWriters();
        }

        LOGGER.info("Trait Codegen plugin executed successfully.");
    }
}
