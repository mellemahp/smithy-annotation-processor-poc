package com.hmellema.smithy.traitcodegen;

public final class SymbolProperties {
    public static final String VALUE_GETTER = "value-getter";

    // Provides the lambda method call to map the shape to a node
    public static final String NODE_MAPPER = "node-mapper";

    // Other symbols to import when using the node mapper for a symbol
    public static final String NODE_MAPPING_IMPORTS = "node-mapper-imports";

    // Type of the value of each enum variant
    public static final String ENUM_VALUE_TYPE = "enum-value-type";

    // Provides an initializer for the builder ref
    public static final String BUILDER_REF_INITIALIZER = "builder-ref-initializer";

    private SymbolProperties() {
        // No constructor for constants class
    }
}
