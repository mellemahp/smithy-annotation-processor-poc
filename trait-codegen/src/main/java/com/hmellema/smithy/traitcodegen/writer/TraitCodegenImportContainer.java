package com.hmellema.smithy.traitcodegen.writer;

import software.amazon.smithy.codegen.core.ImportContainer;
import software.amazon.smithy.codegen.core.Symbol;

import java.util.*;
import java.util.stream.Collectors;

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
        Set<String> sortedImports = imports.stream().map(Symbol::getFullName)
                .collect(Collectors.toCollection(TreeSet::new));
        StringBuilder builder = new StringBuilder();
        for (String importName: sortedImports) {
            builder.append("import ");
            builder.append(importName);
            builder.append(";");
            builder.append(System.lineSeparator());
        }
        return builder.toString();
    }
}
