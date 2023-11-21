package com.hmellema.smithy.traitcodegen.generators.common.builder;

import com.hmellema.smithy.traitcodegen.writer.TraitCodegenWriter;
import software.amazon.smithy.codegen.core.Symbol;
import software.amazon.smithy.codegen.core.SymbolProvider;
import software.amazon.smithy.model.Model;
import software.amazon.smithy.model.shapes.MemberShape;
import software.amazon.smithy.model.shapes.Shape;
import software.amazon.smithy.utils.SmithyBuilder;
import software.amazon.smithy.utils.ToSmithyBuilder;

import java.util.Iterator;

public class ToBuilderGenerator implements Runnable {
    private final TraitCodegenWriter writer;
    private final Symbol symbol;
    private final Shape shape;
    private final SymbolProvider symbolProvider;
    private final Model model;
    private final boolean isTrait;

    public ToBuilderGenerator(TraitCodegenWriter writer, Symbol symbol, Shape shape, SymbolProvider symbolProvider, Model model, boolean isTrait) {
        this.writer = writer;
        this.symbol = symbol;
        this.shape = shape;
        this.symbolProvider = symbolProvider;
        this.model = model;
        this.isTrait = isTrait;
    }

    @Override
    public void run() {
        writer.addImport(SmithyBuilder.class);
        writer.addImport(ToSmithyBuilder.class);
        writer.write("@Override");
        writer.openBlock("public SmithyBuilder<$T> toBuilder() {", "}", symbol, () -> {
            writer.writeInline("return builder()");
            writer.indent();
            if (isTrait) {
                writer.write(".sourceLocation(getSourceLocation())");
            }
            Iterator<MemberShape> memberIterator = shape.members().iterator();
            while (memberIterator.hasNext()) {
                MemberShape member = memberIterator.next();
                writer.writeInline(".$1L($1L)", toMemberName(member));
                if (memberIterator.hasNext()) {
                    writer.writeInline("\n");
                } else {
                    writer.writeInline(";\n");
                }
            }
            writer.dedent();
        });
        writer.write("");
    }

    // TODO: Figure out why this doesnt work correctly in the SymbolProvider?
    private String toMemberName(MemberShape member) {
        Shape containerShape = model.expectShape(member.getContainer());
        if (containerShape.isMapShape() || containerShape.isListShape()) {
            return "values";
        } else {
            return symbolProvider.toMemberName(member);
        }
    }
}
