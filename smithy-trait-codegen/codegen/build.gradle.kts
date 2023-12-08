description = "Smithy Build Plugin that auto-generates traits"

plugins {
    id("smithy.annotation.processor.java-library-conventions")
}

dependencies {
    implementation("software.amazon.smithy:smithy-codegen-core:1.42.0")
}
