package com.hmellema.smithy.processor.traitprocessor.annotations;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PACKAGE)
public @interface GenerateSmithyTraits {
    String NAME = "com.hmellema.smithy.processor.traitprocessor.annotations.GenerateSmithyTraits";

    String packageName();

    String[] header();
}
