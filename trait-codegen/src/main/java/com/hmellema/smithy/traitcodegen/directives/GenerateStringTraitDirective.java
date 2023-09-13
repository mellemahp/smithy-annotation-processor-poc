package com.hmellema.smithy.traitcodegen.directives;

import com.hmellema.smithy.traitcodegen.TraitCodegenContext;
import com.hmellema.smithy.traitcodegen.TraitCodegenSettings;
import software.amazon.smithy.codegen.core.Symbol;
import software.amazon.smithy.model.Model;
import software.amazon.smithy.model.shapes.StringShape;

public record GenerateStringTraitDirective(StringShape shape,
                                           Symbol symbol,
                                           TraitCodegenContext context,
                                           TraitCodegenSettings settings,
                                           Model model) {}
