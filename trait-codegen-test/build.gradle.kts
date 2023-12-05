description = "Test of Trait code generation"

plugins {
    id("smithy.annotation.processor.java-library-conventions")
}

dependencies {
    compileOnly(project(":trait-processor:annotation"))
    annotationProcessor(project(":trait-processor:processor"))
    implementation("software.amazon.smithy:smithy-codegen-core:1.40.0")
}

tasks.withType<JavaCompile>().configureEach {
    options.sourcepath = files(sourceSets["main"].java.srcDirs) + sourceSets["main"].resources.sourceDirectories
}
//
//val test = tasks.withType<JavaCompile>().getByName("compileTestJava") {
//    dependsOn("jar")
//    doFirst {
//        classpath += tasks["jar"].outputs.files
//    }
//}
