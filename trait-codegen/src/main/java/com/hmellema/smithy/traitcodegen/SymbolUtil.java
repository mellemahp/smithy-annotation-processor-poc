package com.hmellema.smithy.traitcodegen;

import software.amazon.smithy.codegen.core.Symbol;
import software.amazon.smithy.codegen.core.SymbolProvider;
import software.amazon.smithy.model.shapes.Shape;
import software.amazon.smithy.model.traits.TraitDefinition;
import software.amazon.smithy.utils.StringUtils;

public interface SymbolUtil {
    Symbol JAVA_STRING = SymbolUtil.fromClass(String.class);

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

    static boolean isJavaString(Symbol symbol) {
        return JAVA_STRING.getName().equals(symbol.getName()) && JAVA_STRING.getNamespace().equals(symbol.getNamespace());
    }
}
