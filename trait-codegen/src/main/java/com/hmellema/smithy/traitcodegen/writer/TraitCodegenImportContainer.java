package com.hmellema.smithy.traitcodegen.writer;

import software.amazon.smithy.codegen.core.ImportContainer;
import software.amazon.smithy.codegen.core.Symbol;

import java.util.HashSet;
import java.util.Set;

public class TraitCodegenImportContainer implements ImportContainer {
    private static final String JAVA_NAMESPACE_PREFIX = "java.lang";
    Set<Symbol> imports = new HashSet<>();

    @Override
    public void importSymbol(Symbol symbol, String alias) {
        if (!symbol.getNamespace().startsWith(JAVA_NAMESPACE_PREFIX)) {
            imports.add(symbol);
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (Symbol importSymbol: imports) {
            builder.append("import ");
            builder.append(importSymbol.getFullName());
            builder.append(";");
            builder.append(System.lineSeparator());
        }
        return builder.toString();
    }
}
