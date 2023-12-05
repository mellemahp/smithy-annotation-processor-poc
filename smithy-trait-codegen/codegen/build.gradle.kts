description = "Smithy Build Plugin that auto-generates traits"

plugins {
    id("smithy.annotation.processor.java-library-conventions")
}

dependencies {
    annotationProcessor("com.google.auto.service:auto-service:1.1.1")
    compileOnly("com.google.auto.service:auto-service-annotations:1.1.1")
    implementation("software.amazon.smithy:smithy-codegen-core:1.40.0")
}
