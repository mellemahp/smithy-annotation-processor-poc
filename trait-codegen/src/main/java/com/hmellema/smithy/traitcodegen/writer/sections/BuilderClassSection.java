package com.hmellema.smithy.traitcodegen.writer.sections;

import software.amazon.smithy.codegen.core.Symbol;
import software.amazon.smithy.utils.CodeSection;

public record BuilderClassSection(Symbol symbol) implements CodeSection {}
