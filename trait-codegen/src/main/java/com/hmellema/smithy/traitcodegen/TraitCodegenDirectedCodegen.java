package com.hmellema.smithy.traitcodegen;

import com.hmellema.smithy.traitcodegen.integrations.TraitCodegenIntegration;
import software.amazon.smithy.codegen.core.SymbolProvider;
import software.amazon.smithy.codegen.core.directed.*;

public class TraitCodegenDirectedCodegen
        implements DirectedCodegen<TraitCodegenContext, TraitCodegenSettings, TraitCodegenIntegration> {

    @Override
    public SymbolProvider createSymbolProvider(CreateSymbolProviderDirective<TraitCodegenSettings> directive) {
        return TraitCodegenSymbolProvider.fromDirective(directive);
    }

    @Override
    public TraitCodegenContext createContext(CreateContextDirective<TraitCodegenSettings, TraitCodegenIntegration> directive) {
        return TraitCodegenContext.fromDirective(directive);
    }

    @Override
    public void generateService(GenerateServiceDirective<TraitCodegenContext, TraitCodegenSettings> directive) {
        // Do nothing on service generation
    }

    @Override
    public void generateError(GenerateErrorDirective<TraitCodegenContext, TraitCodegenSettings> directive) {
        // Do nothing on error generation
    }

    @Override
    public void generateStructure(GenerateStructureDirective<TraitCodegenContext, TraitCodegenSettings> directive) {

    }

    @Override
    public void generateUnion(GenerateUnionDirective<TraitCodegenContext, TraitCodegenSettings> directive) {

    }

    @Override
    public void generateEnumShape(GenerateEnumDirective<TraitCodegenContext, TraitCodegenSettings> directive) {

    }

    @Override
    public void generateIntEnumShape(GenerateIntEnumDirective<TraitCodegenContext, TraitCodegenSettings> directive) {

    }

    @Override
    public void customizeBeforeShapeGeneration(CustomizeDirective<TraitCodegenContext, TraitCodegenSettings> directive) {
        new TraitCodegenGenerator(directive).generate();
    }
}
