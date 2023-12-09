package com.hmellema.smithy.traitcodegen.integrations;

import com.hmellema.smithy.traitcodegen.TraitCodegenContext;
import com.hmellema.smithy.traitcodegen.TraitCodegenSettings;
import com.hmellema.smithy.traitcodegen.TraitGeneratorProvider;
import com.hmellema.smithy.traitcodegen.writer.TraitCodegenWriter;
import software.amazon.smithy.codegen.core.SmithyIntegration;

public interface TraitCodegenIntegration extends SmithyIntegration<TraitCodegenSettings, TraitCodegenWriter,
        TraitCodegenContext> {
    /**
     * Use this method to override the implementation of a trait generator based on the shape or
     * base on metatraits.
     */
    default TraitGeneratorProvider decorateGeneratorProvider(TraitCodegenContext context,
                                                             TraitGeneratorProvider provider) {
        return provider;
    }
}
