package com.hmellema.smithy.traitcodegen.writer;

import com.hmellema.smithy.traitcodegen.TraitCodegenContext;
import software.amazon.smithy.codegen.core.Symbol;

public interface SpiWriterUtils {
    String TRAIT_SERVICE_PROVIDER_FILE = "META-INF/services/software.amazon.smithy.model.traits.TraitService";

    /**
     * Write provider method to Java SPI to service file for {@link software.amazon.smithy.model.traits.TraitService}.
     *
     * @param context Codegen context
     * @param symbol Symbol for trait class
     */
    static void addSpiTraitProvider(TraitCodegenContext context, Symbol symbol) {
        context.writerDelegator().useFileWriter(TRAIT_SERVICE_PROVIDER_FILE,
                writer -> writer.writeInline("$L$$Provider", symbol.getFullName()));
    }
}
