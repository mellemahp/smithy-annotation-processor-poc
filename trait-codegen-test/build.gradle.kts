description = "Test of Trait code generation"

plugins {
    id("smithy.annotation.processor.java-library-conventions")
    id("software.amazon.smithy.gradle.smithy-jar") version "0.9.0"
}

sourceSets {
    main {
        java {
            setSrcDirs(listOf("$buildDir/smithyprojections/trait-codegen-test/source/trait-codegen"))
        }
        resources {
            srcDir("$buildDir/smithyprojections/trait-codegen-test/source/trait-codegen")
            include("META-INF/**")
        }
    }

    test {
        resources {
            srcDirs += main.get().resources.srcDirs
        }
    }
}

dependencies {
    smithyBuild(project(":trait-codegen"))
    implementation("software.amazon.smithy:smithy-codegen-core:1.40.0")
}

tasks["compileJava"].dependsOn("smithyBuild")
tasks["processResources"].dependsOn("smithyBuild")
