description = "Annotation processor that executes Smithy Trait Codegen Plugin"

plugins {
    id("smithy.annotation.processor.java-library-conventions")
}

dependencies {
    annotationProcessor("com.google.auto.service:auto-service:1.1.1")
    compileOnly("com.google.auto.service:auto-service-annotations:1.1.1")
    implementation(project(":smithy-annotation-processor"))
    implementation(project(":smithy-trait-codegen:processor:annotation"))
    implementation(project(":smithy-trait-codegen:codegen"))
}

