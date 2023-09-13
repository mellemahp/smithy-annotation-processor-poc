description = "Test of Annotation processor that executes Trait code generation"

plugins {
    id("java-library")
    id("software.amazon.smithy.gradle.smithy-base") version "0.8.0"
}

dependencies {
    smithyBuild(project(":trait-codegen"))
}

repositories {
    mavenLocal()
    mavenCentral()
}
