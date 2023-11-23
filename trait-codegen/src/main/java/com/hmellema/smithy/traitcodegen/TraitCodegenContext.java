package com.hmellema.smithy.traitcodegen;

import com.hmellema.smithy.traitcodegen.integrations.TraitCodegenIntegration;
import com.hmellema.smithy.traitcodegen.writer.TraitCodegenWriter;
import software.amazon.smithy.build.FileManifest;
import software.amazon.smithy.codegen.core.CodegenContext;
import software.amazon.smithy.codegen.core.SymbolProvider;
import software.amazon.smithy.codegen.core.WriterDelegator;
import software.amazon.smithy.codegen.core.directed.CreateContextDirective;
import software.amazon.smithy.model.Model;
import software.amazon.smithy.model.traits.TraitDefinition;

import java.util.List;

public class TraitCodegenContext implements CodegenContext<TraitCodegenSettings, TraitCodegenWriter, TraitCodegenIntegration> {
    private final Model model;
    private final TraitCodegenSettings settings;
    private final SymbolProvider baseSymbolProvider;
    private final SymbolProvider traitSymbolProvider;
    private final FileManifest fileManifest;
    private final List<TraitCodegenIntegration> integrations;
    private final WriterDelegator<TraitCodegenWriter> writerDelegator;


    private TraitCodegenContext(Model model,
                                TraitCodegenSettings settings,
                                SymbolProvider symbolProvider,
                                FileManifest fileManifest,
                                List<TraitCodegenIntegration> integrations
    ) {
        this.model = model;
        this.settings = settings;
        this.baseSymbolProvider = symbolProvider;
        this.traitSymbolProvider = SymbolProvider.cache(new TraitSymbolProvider(settings));

        this.fileManifest = fileManifest;
        this.integrations = integrations;
        this.writerDelegator = new WriterDelegator<>(fileManifest, shape -> {
            if (shape.hasTrait(TraitDefinition.class)) {
                return traitSymbolProvider.toSymbol(shape);
            } else {
                return baseSymbolProvider.toSymbol(shape);
            }
        }, new TraitCodegenWriter.Factory());
    }

    public static TraitCodegenContext fromDirective(
            CreateContextDirective<TraitCodegenSettings, TraitCodegenIntegration> directive
    ) {
        return new TraitCodegenContext(
                directive.model(),
                directive.settings(),
                directive.symbolProvider(),
                directive.fileManifest(),
                directive.integrations()
        );
    }

    @Override
    public Model model() {
        return model;
    }

    @Override
    public TraitCodegenSettings settings() {
        return settings;
    }

    @Override
    public SymbolProvider symbolProvider() {
        return baseSymbolProvider;
    }

    @Override
    public FileManifest fileManifest() {
        return fileManifest;
    }

    @Override
    public WriterDelegator<TraitCodegenWriter> writerDelegator() {
        return writerDelegator;
    }

    @Override
    public List<TraitCodegenIntegration> integrations() {
        return integrations;
    }
}
