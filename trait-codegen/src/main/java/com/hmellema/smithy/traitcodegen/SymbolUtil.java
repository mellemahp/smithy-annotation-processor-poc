package com.hmellema.smithy.traitcodegen;

import software.amazon.smithy.codegen.core.Symbol;

public interface SymbolUtil {
    static Symbol fromClass(Class<?> clazz) {
        return Symbol.builder()
                .name(clazz.getSimpleName())
                .namespace(clazz.getPackageName(), ".")
                .build();
    }
}
