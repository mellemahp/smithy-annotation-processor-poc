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
    errorprone("com.google.errorprone:error_prone_core:2.23.0")
   // errorproneJavac("com.google.errorprone:javac:9+181-r4173-1")
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
