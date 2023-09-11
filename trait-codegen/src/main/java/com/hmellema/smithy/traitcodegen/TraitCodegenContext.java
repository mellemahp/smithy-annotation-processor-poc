package com.hmellema.smithy.traitcodegen;

import com.hmellema.smithy.traitcodegen.writer.TraitCodegenWriter;
import software.amazon.smithy.build.FileManifest;
import software.amazon.smithy.codegen.core.SymbolProvider;
import software.amazon.smithy.codegen.core.WriterDelegator;
import software.amazon.smithy.model.Model;

public class TraitCodegenContext {
    private final Model model;
    private final TraitCodegenSettings settings;
    private final SymbolProvider symbolProvider;
    private final FileManifest fileManifest;
    private final WriterDelegator<TraitCodegenWriter> writerDelegator;

    public TraitCodegenContext(Model model,
                               TraitCodegenSettings settings,
                               SymbolProvider symbolProvider,
                               FileManifest fileManifest
    ) {
        this.model = model;
        this.settings = settings;
        this.symbolProvider = symbolProvider;
        this.fileManifest = fileManifest;

        //TODO: use a separate symbol provider for the delegator here and for the actual symbol types
        this.writerDelegator = new WriterDelegator<>(fileManifest, symbolProvider, new TraitCodegenWriter.Factory());
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
}
