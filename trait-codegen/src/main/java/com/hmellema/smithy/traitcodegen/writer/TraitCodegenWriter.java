package com.hmellema.smithy.traitcodegen.writer;

import com.hmellema.smithy.traitcodegen.SymbolUtil;
import software.amazon.smithy.codegen.core.Symbol;
import software.amazon.smithy.codegen.core.SymbolReference;
import software.amazon.smithy.codegen.core.SymbolWriter;
import software.amazon.smithy.utils.StringUtils;

import java.util.Iterator;
import java.util.function.BiFunction;

public class TraitCodegenWriter extends SymbolWriter<TraitCodegenWriter, TraitCodegenImportContainer> {
    private static final int MAX_LINE_LENGTH = 120;
    private static final String PACKAGE_HEADER_TEMPLATE = "package %s;%n";

    private final String packageName;
    private final String fileName;

    public TraitCodegenWriter(String fileName, String packageName) {
        super(new TraitCodegenImportContainer());
        this.packageName = packageName;
        this.fileName = fileName;
        putFormatter('T', new JavaTypeFormatter());
        putFormatter('B', new BaseTypeFormatter());
    }

    public void addImport(Symbol symbol) {
        addImport(symbol, symbol.getName());
    }

    public void addImport(Class<?> clazz) {
        addImport(SymbolUtil.fromClass(clazz));
    }

    public void writeDocString(String contents) {
        pushState().write("/**")
                .writeInline(" * ")
                .write(StringUtils.wrap(contents.replace("\n", "\n * "), MAX_LINE_LENGTH - 8,
                        System.lineSeparator() + " * ", false))
                .write(" */")
                .popState();
    }

    public void writeComment(String contents) {
        pushState().write("//")
                .writeInline(" * ")
                .write(StringUtils.wrap(contents.replace("\n", "\n// "), MAX_LINE_LENGTH - 8,
                        System.lineSeparator() + "// ", false))
                .write(" */")
                .popState();
    }

    public static final class Factory implements SymbolWriter.Factory<TraitCodegenWriter> {
        @Override
        public TraitCodegenWriter apply(String filename, String namespace) {
            return new TraitCodegenWriter(filename, namespace);
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (!fileName.startsWith("META-INF/services")) {
            builder.append(getAttribution()).append(System.lineSeparator());
            builder.append(getPackageHeader()).append(System.lineSeparator());
            builder.append(getImportContainer().toString()).append(System.lineSeparator());
        }
        builder.append(super.toString());
        return builder.toString();
    }

    public String getPackageHeader() {
        return String.format(PACKAGE_HEADER_TEMPLATE, packageName);
    }

    public String getAttribution() {
        return """
                /*
                 * Copyright My.company
                 */
                """;
    }

    /**
     * Implements a formatter for {@code $T} that formats Java types.
     */
    private final class JavaTypeFormatter implements BiFunction<Object, String, String> {
        @Override
        public String apply(Object type, String indent) {
            if (!(type instanceof Symbol typeSymbol)) {
                throw new RuntimeException("Invalid type provided for $T. Expected a Symbol but found: `" + type + "`.");
            }

            addImport(typeSymbol);
            if (typeSymbol.getReferences().isEmpty()) {
                return typeSymbol.getName();
            }
            StringBuilder builder = new StringBuilder();
            builder.append(typeSymbol.getName());
            builder.append("<");
            Iterator<SymbolReference> iterator = typeSymbol.getReferences().iterator();
            while (iterator.hasNext()) {
                Symbol refSymbol = iterator.next().getSymbol();
                addImport(refSymbol);
                builder.append(refSymbol.getName());
                if (iterator.hasNext()) {
                    builder.append(",");
                }
            }
            builder.append(">");
            return builder.toString();
        }
    }

    /**
     * Implements a formatter for {@code $B} that writes the base type for a class.
     */
    private final class BaseTypeFormatter implements BiFunction<Object, String, String> {
        private final JavaTypeFormatter javaTypeFormatter = new JavaTypeFormatter();
        @Override
        public String apply(Object type, String indent) {
            if (!(type instanceof Symbol typeSymbol)) {
                throw new RuntimeException("Invalid type provided for $B. Expected a Symbol but found: `" + type + "`.");
            }
            Symbol baseSymbol = typeSymbol.expectProperty("baseType", Symbol.class);
            return javaTypeFormatter.apply(baseSymbol, indent);
        }
    }
}
