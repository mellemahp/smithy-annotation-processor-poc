description = "Test of Annotation processor that executes Trait code generation"

plugins {
    id("smithy.annotation.processor.java-library-conventions")
    id("software.amazon.smithy.gradle.smithy-jar") version "0.9.0"
}

dependencies {
    annotationProcessor(project(":trait-processor:processor"))
    compileOnly(project(":trait-processor:annotation"))
    implementation("software.amazon.smithy:smithy-model:1.40.0")
}

tasks.withType<JavaCompile>().configureEach {
    options.sourcepath = files(sourceSets["main"].java.srcDirs) + sourceSets["main"].resources.sourceDirectories
}
