package com.hmellema.smithy.traitcodegen;

import software.amazon.smithy.codegen.core.Symbol;
import software.amazon.smithy.model.shapes.Shape;
import software.amazon.smithy.utils.StringUtils;

public interface SymbolUtil {
    static Symbol fromClass(Class<?> clazz) {
        return Symbol.builder()
                .name(clazz.getSimpleName())
                .namespace(clazz.getPackageName(), ".")
                .build();
    }

    static String getDefaultName(Shape shape) {
        return StringUtils.capitalize(shape.getId().getName());
    }

    static String getDefaultTraitName(Shape shape) {
        return getDefaultName(shape) + "Trait";
    }
}
