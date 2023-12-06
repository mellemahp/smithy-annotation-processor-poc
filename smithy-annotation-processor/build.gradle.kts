description = "Annotation processor that executes Smithy build plugins"

plugins {
    id("smithy.annotation.processor.java-library-conventions")
}

dependencies {
    implementation("software.amazon.smithy:smithy-build:1.40.0")
    api("software.amazon.smithy:smithy-model:1.40.0")
}
