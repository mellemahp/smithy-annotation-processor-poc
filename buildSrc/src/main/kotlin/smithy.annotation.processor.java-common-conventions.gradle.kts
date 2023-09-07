import net.ltgt.gradle.errorprone.errorprone

plugins {
    // Apply the java Plugin to add support for Java.
    java
    id("net.ltgt.errorprone")
}

repositories {
    mavenLocal()
    // Use Maven Central for resolving dependencies.
    mavenCentral()
}

dependencies {
    annotationProcessor("com.uber.nullaway:nullaway:0.10.13")
    compileOnly("com.google.code.findbugs:jsr305:3.0.2")
    errorprone("com.google.errorprone:error_prone_core:2.21.1")
    errorproneJavac("com.google.errorprone:javac:9+181-r4173-1")
}


tasks.withType<JavaCompile>().configureEach {
    options.errorprone {
        if (!name.contains("test", true)) {
            error("NullAway")
            errorproneArgs.addAll(
                "-XepOpt:NullAway:AnnotatedPackages=com.hmellema",
            )
        }
    }
}

testing {
    suites {
        // Configure the built-in test suite
        getting(JvmTestSuite::class) {
            // Use JUnit Jupiter test framework
            useJUnitJupiter("5.8.2")
        }
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
