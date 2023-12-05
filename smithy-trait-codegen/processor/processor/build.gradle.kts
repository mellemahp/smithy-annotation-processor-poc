description = "Annotation processor that executes Smithy Trait Codegen Plugin"

plugins {
    id("smithy.annotation.processor.java-library-conventions")
}

dependencies {
    implementation(project(":smithy-annotation-processor"))
    implementation(project(":smithy-trait-codegen:processor:annotation"))
    implementation(project(":smithy-trait-codegen:codegen"))
}

