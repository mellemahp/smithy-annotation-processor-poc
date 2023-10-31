description = "Annotation processor that executes Smithy build plugins"

plugins {
    id("smithy.annotation.processor.java-library-conventions")
}

dependencies {
    api("software.amazon.smithy:smithy-build:1.40.0")
    implementation("software.amazon.smithy:smithy-model:1.40.0")
}

