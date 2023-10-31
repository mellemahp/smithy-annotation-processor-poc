package com.hmellema.smithy.traitcodegen;

import com.google.auto.service.AutoService;
import software.amazon.smithy.build.PluginContext;
import software.amazon.smithy.build.SmithyBuildPlugin;
import software.amazon.smithy.codegen.core.SymbolProvider;
import software.amazon.smithy.codegen.core.directed.CodegenDirector;
import software.amazon.smithy.model.Model;
import software.amazon.smithy.model.neighbor.Walker;
import software.amazon.smithy.model.shapes.ServiceShape;
import software.amazon.smithy.model.shapes.Shape;
import software.amazon.smithy.model.shapes.ShapeVisitor;
import software.amazon.smithy.model.traits.Trait;
import software.amazon.smithy.model.traits.TraitDefinition;
import software.amazon.smithy.model.transform.ModelTransformer;

import java.util.HashSet;
import java.util.Set;
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
        TraitCodegenSettings settings = TraitCodegenSettings.from(context.getSettings());
        Model model = context.getModel();

        // Execute base transforms
        model = transformer.flattenAndRemoveMixins(model);
        model = transformer.changeStringEnumsToEnumShapes(model);

        // Create context to use for the rest of the steps
        SymbolProvider traitSymbolProvider = new TraitCodegenSymbolProvider(settings, model);
        TraitCodegenContext codegenContext = new TraitCodegenContext(model, settings, traitSymbolProvider, context.getFileManifest());

        // Set up generator visitor
        ShapeVisitor<Void> generator = new TraitCodegenGenerator(codegenContext);

        // Generate all shapes with the Trait smithy trait and all shapes within their closure
        Set<Shape> traitShapes = model.getShapesWithTrait(TraitDefinition.class);
        Set<Shape> shapeClosure = new HashSet<>(traitShapes);
        Walker walker = new Walker(model);
        traitShapes.forEach(traitShape -> shapeClosure.addAll(walker.walkShapes(traitShape)));

        // TESTING TRANFORMER
        Model transformed = SyntheticTraitServiceTransformer.transform(model);
        ServiceShape serviceShape = transformed.expectShape(SyntheticTraitServiceTransformer.SYNTHETIC_SERVICE_ID, ServiceShape.class);
        Set<Shape> shapesToWalk = new Walker(transformed).walkShapes(serviceShape);
        for (Shape shape : shapesToWalk) {
            if (shape.hasTrait(TraitDefinition.class)) {
                shape.accept(generator);
            } else {
                // Do nothing for now
               // shape.accept(generator);
            }
        }

        // Write all to files
        if (!codegenContext.writerDelegator().getWriters().isEmpty()) {
            codegenContext.writerDelegator().flushWriters();
        }

        LOGGER.info("Trait Codegen plugin executed successfully.");
    }
}
