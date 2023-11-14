package com.hmellema.smithy.traitcodegen.generators;

import com.hmellema.smithy.traitcodegen.writer.TraitCodegenWriter;
import software.amazon.smithy.codegen.core.SymbolProvider;
import software.amazon.smithy.model.Model;
import software.amazon.smithy.model.shapes.MemberShape;
import software.amazon.smithy.model.shapes.ShapeType;
import software.amazon.smithy.model.shapes.StructureShape;
import software.amazon.smithy.utils.StringUtils;

import java.util.Optional;

public class GetterGenerator implements Runnable {
    private final TraitCodegenWriter writer;
    private final StructureShape shape;
    private final SymbolProvider symbolProvider;
    private final Model model;

    public GetterGenerator(TraitCodegenWriter writer, StructureShape shape, SymbolProvider symbolProvider, Model model) {
        this.writer = writer;
        this.shape = shape;
        this.symbolProvider = symbolProvider;
        this.model = model;
    }

    @Override
    public void run() {
        for (MemberShape member : shape.members()) {
            if (member.isRequired() || ShapeType.Category.AGGREGATE == model.expectShape(member.getTarget()).getType().getCategory()) {
                writer.openBlock("public $T get$L() {", "}",
                        symbolProvider.toSymbol(member), StringUtils.capitalize(symbolProvider.toMemberName(member)),
                        () -> writer.write("return $L;", symbolProvider.toMemberName(member)));
            } else {
                writer.addImport(Optional.class);
                writer.openBlock("public Optional<$T> get$L() {", "}",
                        symbolProvider.toSymbol(member), StringUtils.capitalize(symbolProvider.toMemberName(member)),
                        () -> writer.write("return Optional.ofNullable($L);", symbolProvider.toMemberName(member)));
            }
            writer.write("");
        }
    }
}
