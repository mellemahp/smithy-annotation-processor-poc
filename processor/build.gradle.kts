description = "Annotation processor that executes Smithy build plugins"

plugins {
    id("smithy.annotation.processor.java-library-conventions")
}

dependencies {
    annotationProcessor("com.google.auto.service:auto-service:1.1.1")
    implementation("com.google.auto.service:auto-service-annotations:1.1.1")
    implementation("software.amazon.smithy:smithy-build:1.37.0")
    implementation("software.amazon.smithy:smithy-model:1.37.0")
    implementation(project(":annotations"))
}

