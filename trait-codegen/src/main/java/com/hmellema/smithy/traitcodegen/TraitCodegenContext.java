package com.hmellema.smithy.traitcodegen;

import com.hmellema.smithy.traitcodegen.integrations.TraitCodegenIntegration;
import com.hmellema.smithy.traitcodegen.writer.TraitCodegenWriter;
import software.amazon.smithy.build.FileManifest;
import software.amazon.smithy.codegen.core.CodegenContext;
import software.amazon.smithy.codegen.core.SymbolProvider;
import software.amazon.smithy.codegen.core.WriterDelegator;
import software.amazon.smithy.codegen.core.directed.CodegenDirector;
import software.amazon.smithy.model.Model;
import software.amazon.smithy.utils.CodeInterceptor;
import software.amazon.smithy.utils.CodeSection;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

public class TraitCodegenContext implements CodegenContext<TraitCodegenSettings, TraitCodegenWriter, TraitCodegenIntegration> {
    private final Model model;
    private final TraitCodegenSettings settings;
    private final SymbolProvider symbolProvider;
    private final FileManifest fileManifest;
    private final List<TraitCodegenIntegration> integrations = new ArrayList<>();
    private final WriterDelegator<TraitCodegenWriter> writerDelegator;

    public TraitCodegenContext(Model model,
                               TraitCodegenSettings settings,
                               SymbolProvider symbolProvider,
                               FileManifest fileManifest
    ) {
        this.model = model;
        this.settings = settings;
        this.fileManifest = fileManifest;
        this.writerDelegator = new WriterDelegator<>(fileManifest, symbolProvider, new TraitCodegenWriter.Factory());
        this.integrations.addAll(ServiceLoader.load(TraitCodegenIntegration.class, this.getClass().getClassLoader())
                .stream().map(ServiceLoader.Provider::get).peek(System.out::println).toList());
        registerInterceptors();
        this.symbolProvider = createSymbolProvider(symbolProvider);
    }

    private SymbolProvider createSymbolProvider(SymbolProvider provider) {
        for (TraitCodegenIntegration integration : integrations) {
            provider = integration.decorateSymbolProvider(model, settings, provider);
        }
        return SymbolProvider.cache(provider);
    }

    private void registerInterceptors() {
        List<CodeInterceptor<? extends CodeSection, TraitCodegenWriter>> interceptors = new ArrayList<>();
        for (TraitCodegenIntegration integration : integrations) {
            interceptors.addAll(integration.interceptors(this));
        }
        writerDelegator.setInterceptors(interceptors);
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
