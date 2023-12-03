package com.hmellema.traitcodegen.test;

import com.example.generated.DeprecatedStringTraitTrait;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DeprecatedStringTest {
    @Test
    void checkForDeprecatedAnnotation() {
        Deprecated deprecated = DeprecatedStringTraitTrait.class.getAnnotation(Deprecated.class);
        assertNotNull(deprecated);
        assertEquals("a long long time ago", deprecated.since());
    }
}
