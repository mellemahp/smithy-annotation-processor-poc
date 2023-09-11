package com.hmellema.smithy.traitcodegen;

import software.amazon.smithy.codegen.core.CodegenContext;
import software.amazon.smithy.codegen.core.SmithyIntegration;
import software.amazon.smithy.codegen.core.SymbolProvider;
import software.amazon.smithy.codegen.core.directed.*;

public interface TraitCodegen<C extends CodegenContext<S, ?, I>, S, I extends SmithyIntegration<S, ?, C>> {
    /**
     * Create the {@link SymbolProvider} used to map shapes to code symbols.
     *
     * @param directive Directive context data.
     * @return Returns the created SymbolProvider.
     */
    SymbolProvider createSymbolProvider(CreateSymbolProviderDirective<S> directive);

    /**
     * Creates the codegen context object.
     *
     * @param directive Directive context data.
     * @return Returns the created context object used by the rest of the directed generation.
     */
    C createContext(CreateContextDirective<S, I> directive);

    /**
     * Generates the code needed for a service shape.
     *
     * @param directive Directive to perform.
     */
    void generateTraits(CustomizeDirective<C, S> directive);
}
