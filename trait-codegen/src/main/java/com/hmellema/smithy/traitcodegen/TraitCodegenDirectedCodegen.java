package com.hmellema.smithy.traitcodegen;

import com.hmellema.smithy.traitcodegen.generators.base.EnumGenerator;
import com.hmellema.smithy.traitcodegen.generators.base.IntEnumGenerator;
import com.hmellema.smithy.traitcodegen.generators.base.StructureGenerator;
import com.hmellema.smithy.traitcodegen.integrations.TraitCodegenIntegration;
import com.hmellema.smithy.traitcodegen.utils.SymbolUtil;
import software.amazon.smithy.codegen.core.SymbolProvider;
import software.amazon.smithy.codegen.core.directed.*;
import software.amazon.smithy.model.traits.TraitDefinition;

final class TraitCodegenDirectedCodegen
        implements DirectedCodegen<TraitCodegenContext, TraitCodegenSettings, TraitCodegenIntegration> {

    @Override
    public SymbolProvider createSymbolProvider(CreateSymbolProviderDirective<TraitCodegenSettings> directive) {
        return BaseJavaSymbolProvider.fromDirective(directive);
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
    public void generateOperation(GenerateOperationDirective<TraitCodegenContext, TraitCodegenSettings> directive) {
        // Do nothing for operation generation
    }

    @Override
    public void generateError(GenerateErrorDirective<TraitCodegenContext, TraitCodegenSettings> directive) {
        // Do nothing on error generation
    }

    @Override
    public void generateStructure(GenerateStructureDirective<TraitCodegenContext, TraitCodegenSettings> directive) {
        if (!SymbolUtil.isTrait(directive.shape())) {
            new StructureGenerator().accept(directive);
        }
    }

    @Override
    public void generateUnion(GenerateUnionDirective<TraitCodegenContext, TraitCodegenSettings> directive) {
        // TODO: Implement
    }

    @Override
    public void generateEnumShape(GenerateEnumDirective<TraitCodegenContext, TraitCodegenSettings> directive) {
        if (!SymbolUtil.isTrait(directive.shape())) {
            new EnumGenerator().accept(directive);
        }
    }

    @Override
    public void generateIntEnumShape(GenerateIntEnumDirective<TraitCodegenContext, TraitCodegenSettings> directive) {
        if (!SymbolUtil.isTrait(directive.shape())) {
            new IntEnumGenerator().accept(directive);
        }
    }

    @Override
    public void customizeBeforeShapeGeneration(CustomizeDirective<TraitCodegenContext, TraitCodegenSettings> directive) {
        new TraitCodegenGenerator(directive).run();
    }
}
