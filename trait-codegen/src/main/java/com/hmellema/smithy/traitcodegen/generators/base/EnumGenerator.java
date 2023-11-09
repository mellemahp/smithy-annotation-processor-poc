package com.hmellema.smithy.traitcodegen.generators.base;

import com.hmellema.smithy.traitcodegen.SymbolUtil;
import com.hmellema.smithy.traitcodegen.TraitCodegenContext;
import com.hmellema.smithy.traitcodegen.TraitCodegenSettings;
import software.amazon.smithy.codegen.core.Symbol;
import software.amazon.smithy.codegen.core.directed.GenerateEnumDirective;
import software.amazon.smithy.model.shapes.MemberShape;
import software.amazon.smithy.model.traits.EnumValueTrait;

import java.util.Locale;

public class EnumGenerator extends AbstractEnumGenerator<GenerateEnumDirective<TraitCodegenContext, TraitCodegenSettings>> {
    private static final String VARIANT_TEMPLATE = "$L($S)";
    private static final Symbol VALUE_TYPE = SymbolUtil.fromClass(String.class);
    @Override
    public void accept(GenerateEnumDirective<TraitCodegenContext, TraitCodegenSettings> directive) {
        directive.context().writerDelegator().useShapeWriter(directive.shape(),
                writer -> writeEnum(directive.shape(), directive.symbolProvider(), writer));
    }

    @Override
    String getVariantTemplate() {
        return VARIANT_TEMPLATE;
    }

    @Override
    Symbol getValueType() {
        return VALUE_TYPE;
    }

    @Override
    Object getEnumValue(MemberShape member) {
        return member.getTrait(EnumValueTrait.class)
                .flatMap(EnumValueTrait::getStringValue)
                .orElseGet(() -> member.getMemberName().toUpperCase(Locale.ENGLISH));
    }
}
