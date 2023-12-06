package com.hmellema.smithy.traitcodegen.sections;

import software.amazon.smithy.model.shapes.MemberShape;
import software.amazon.smithy.utils.CodeSection;

public final class EnumVariantSection implements CodeSection {
    private final MemberShape memberShape;

    public EnumVariantSection(MemberShape memberShape) {
        this.memberShape = memberShape;
    }

    public MemberShape memberShape() {
        return memberShape;
    }
}
