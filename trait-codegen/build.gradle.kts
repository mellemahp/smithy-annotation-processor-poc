description = "Smithy Build Plugin that auto-generates traits"

plugins {
    id("java-library")
   //id("smithy.annotation.processor.java-library-conventions")
}

dependencies {
    annotationProcessor("com.google.auto.service:auto-service:1.1.1")
    compileOnly("com.google.auto.service:auto-service-annotations:1.1.1")
    implementation("software.amazon.smithy:smithy-codegen-core:1.40.0")
    implementation("software.amazon.smithy:smithy-model:1.40.0")
}

// TODO: Remove when the java convention is re-introduced
repositories {
    mavenLocal()
    // Use Maven Central for resolving dependencies.
    mavenCentral()
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
// TODO: END REMOVE