description = "Annotation processor that executes Smithy Trait Codegen Plugin"

plugins {
    id("smithy.annotation.processor.java-library-conventions")
}

dependencies {
    annotationProcessor("com.google.auto.service:auto-service:1.1.1")
    compileOnly("com.google.auto.service:auto-service-annotations:1.1.1")
    implementation(project(":processor"))
    implementation(project(":trait-processor:annotation"))
    implementation(project(":trait-codegen"))
}

