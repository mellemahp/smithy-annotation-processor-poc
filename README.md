# Trait Codegen Annotation Processor Proof of Concept 
This repository demonstrates a possible approach to using a Smithy code gen plugin in 
conjuntion with an annotation processor to generate the java classes associated with traits. 

## Using
To try out the proof of concept, clone this repo and run the following from the root of the cloned repository:
```
./gradlew clean build
```

This will build all the packages. The `trait-processor-test` package shows the full usage of this POC. 
The JAR generated by the `trait-processor-test` package contains both the Smithy model from the /models folder 
and the Java definition of the trait. 

