package com.hmellema.smithy.traitcodegen.generators.base;

import com.hmellema.smithy.traitcodegen.writer.TraitCodegenWriter;
import com.hmellema.smithy.traitcodegen.writer.sections.ClassSection;
import com.hmellema.smithy.traitcodegen.writer.sections.FromNodeSection;
import software.amazon.smithy.codegen.core.Symbol;
import software.amazon.smithy.codegen.core.SymbolProvider;
import software.amazon.smithy.model.Model;
import software.amazon.smithy.model.shapes.MemberShape;
import software.amazon.smithy.model.shapes.Shape;

import java.util.Iterator;
import java.util.function.Consumer;

abstract class AbstractEnumGenerator<T> implements Consumer<T> {
    private static final String VALUE_FIELD_TEMPLATE = "private final $T value;";

    protected void writeEnum(Shape enumShape, SymbolProvider provider, TraitCodegenWriter writer, Model model) {
        var enumSymbol = provider.toSymbol(enumShape);
        writer.pushState(new ClassSection(enumShape))
                .openBlock("public enum $L {", "}", enumSymbol.getName(), () -> {
                    writeVariants(enumShape, provider, writer);
                    writer.newLine();

                    writeValueField(writer);
                    writer.newLine();

                    writeConstructor(enumSymbol, writer);

                    writeValueGetter(writer);
                    writer.newLine();
                    writer.injectSection(new FromNodeSection(enumShape, enumSymbol, provider, model));
                })
                .popState();
    }

    abstract String getVariantTemplate();

    abstract Symbol getValueType();

    abstract Object getEnumValue(MemberShape member);

    private void writeVariants(Shape enumShape, SymbolProvider provider, TraitCodegenWriter writer) {
        Iterator<MemberShape> memberIterator = enumShape.members().iterator();
        String template = getVariantTemplate();
        while (memberIterator.hasNext()) {
            MemberShape member = memberIterator.next();
            String name = provider.toMemberName(member);
            if (memberIterator.hasNext()) {
                writer.write(template + ",", name, getEnumValue(member));
            } else {
                writer.write(template + ";", name, getEnumValue(member));
            }
        }
    }

    private void writeValueField(TraitCodegenWriter writer) {
        writer.write(VALUE_FIELD_TEMPLATE, getValueType());
    }

    private void writeValueGetter(TraitCodegenWriter writer) {
        writer.openBlock("public $T getValue() {", "}", getValueType(),
                () -> writer.write("return value;"));
    }

    private void writeConstructor(Symbol enumSymbol, TraitCodegenWriter writer) {
        writer.openBlock("$L($T value) {", "}",
                enumSymbol.getName(), getValueType(), () -> writer.write("this.value = value;"));
        writer.newLine();
    }
}
