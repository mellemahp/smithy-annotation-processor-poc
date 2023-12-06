/**
 * Generates traits for tests.
 */
@GenerateSmithyTraits(
        packageName = "com.example.traits",
        header = {
                "Header line One",
                "Header line Two"
        })
package com.hmellema.traitcodegen;

import com.hmellema.smithy.processor.traitprocessor.annotations.GenerateSmithyTraits;
