description = "Test of Trait code generation"

plugins {
    id("smithy.annotation.processor.java-library-conventions")
}

dependencies {
    compileOnly(project(":smithy-trait-codegen:processor:annotation"))
    annotationProcessor(project(":smithy-trait-codegen:processor:processor"))
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
