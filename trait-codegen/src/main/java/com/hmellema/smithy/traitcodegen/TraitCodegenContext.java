package com.hmellema.smithy.traitcodegen;

import com.hmellema.smithy.traitcodegen.integrations.TraitCodegenIntegration;
import com.hmellema.smithy.traitcodegen.writer.TraitCodegenWriter;
import software.amazon.smithy.build.FileManifest;
import software.amazon.smithy.codegen.core.CodegenContext;
import software.amazon.smithy.codegen.core.SymbolProvider;
import software.amazon.smithy.codegen.core.WriterDelegator;
import software.amazon.smithy.codegen.core.directed.CreateContextDirective;
import software.amazon.smithy.model.Model;

import java.util.List;

public class TraitCodegenContext implements CodegenContext<TraitCodegenSettings, TraitCodegenWriter, TraitCodegenIntegration> {
    private final Model model;
    private final TraitCodegenSettings settings;
    private final SymbolProvider symbolProvider;
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
        this.symbolProvider = symbolProvider;
        this.fileManifest = fileManifest;
        this.integrations = integrations;
        this.writerDelegator = new WriterDelegator<>(fileManifest, symbolProvider, new TraitCodegenWriter.Factory());
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

    public Model model() {
        return model;
    }

    public TraitCodegenSettings settings() {
        return settings;
    }

    public SymbolProvider symbolProvider() {
        return symbolProvider;
    }

    public FileManifest fileManifest() {
        return fileManifest;
    }

    public WriterDelegator<TraitCodegenWriter> writerDelegator() {
        return writerDelegator;
    }

    @Override
    public List<TraitCodegenIntegration> integrations() {
        return integrations;
    }
}
