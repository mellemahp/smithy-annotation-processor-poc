description = "Annotation processor that executes Smithy build plugins"

plugins {
    id("smithy.annotation.processor.java-library-conventions")
    id("software.amazon.smithy.gradle.smithy-jar") version "0.8.0"
}

dependencies {
    compileOnly(project(":annotations"))
    annotationProcessor(project(":processor"))
    annotationProcessor("software.amazon.smithy:smithy-aws-traits:1.37.0")
    annotationProcessor(files("models", "smithy-build.json"))

    implementation("software.amazon.smithy:smithy-aws-traits:1.37.0")
}

tasks.withType<JavaCompile>().configureEach {
    options.sourcepath = files(sourceSets["main"].java.srcDirs) + sourceSets["main"].resources.sourceDirectories
}

tasks["compileJava"].dependsOn("processResources")