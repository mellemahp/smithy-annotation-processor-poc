package com.hmellema.smithy.traitcodegen.writer.sections;

import software.amazon.smithy.model.shapes.Shape;
import software.amazon.smithy.utils.CodeSection;

public record JavaDocSection(Shape shape) implements CodeSection { }
