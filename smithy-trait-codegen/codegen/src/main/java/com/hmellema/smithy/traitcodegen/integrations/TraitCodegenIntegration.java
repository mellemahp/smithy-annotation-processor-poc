package com.hmellema.smithy.traitcodegen.integrations;

import com.hmellema.smithy.traitcodegen.TraitCodegenContext;
import com.hmellema.smithy.traitcodegen.TraitCodegenSettings;
import com.hmellema.smithy.traitcodegen.writer.TraitCodegenWriter;
import software.amazon.smithy.codegen.core.SmithyIntegration;

public interface TraitCodegenIntegration extends SmithyIntegration<TraitCodegenSettings, TraitCodegenWriter,
        TraitCodegenContext> {
}
