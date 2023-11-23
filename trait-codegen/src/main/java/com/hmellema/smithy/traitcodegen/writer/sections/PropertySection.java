package com.hmellema.smithy.traitcodegen.writer.sections;

import software.amazon.smithy.model.shapes.MemberShape;
import software.amazon.smithy.utils.CodeSection;

public record PropertySection(MemberShape shape) implements CodeSection {
}
