package com.hmellema.smithy.processor;

import javax.annotation.processing.*;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.*;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.annotation.Annotation;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import software.amazon.smithy.build.SmithyBuild;
import software.amazon.smithy.build.SmithyBuildResult;
import software.amazon.smithy.build.model.SmithyBuildConfig;
import software.amazon.smithy.model.Model;
import software.amazon.smithy.model.loader.ModelAssembler;

public abstract class SmithyProcessor<A extends Annotation> extends AbstractProcessor {
    private static final String MANIFEST_PATH = "META-INF/smithy/manifest";
    private static final String SMITHY_PREFIX = "META-INF/smithy/";
    private Messager messager;
    private Filer filer;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        messager = processingEnv.getMessager();
        filer = processingEnv.getFiler();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        var elements = roundEnv.getElementsAnnotatedWith(getAnnotationClass());
        if (elements.size() != 1) {
            if (elements.size() > 1) {
                messager.printMessage(Diagnostic.Kind.ERROR,
                        "Only one package can have the " + getAnnotationClass() +  " annotation.");
            }
        } else {
            messager.printMessage(Diagnostic.Kind.NOTE,
                    "Executing processor: " + this.getClass().getSimpleName() + "...");
            A annotation = getAnnotation(elements);
            SmithyBuildConfig config = createBuildConfig(annotation);
            SmithyBuildResult buildResult = executeSmithyBuild(config);

            // TODO: Need to make sure artifacts are save to the right place?

            messager.printMessage(Diagnostic.Kind.NOTE, "ARTIFACTS: " + buildResult.allArtifacts().toList());
            messager.printMessage(Diagnostic.Kind.NOTE,
                    "Annotation processor " + this.getClass().getSimpleName() + " finished processing.");
        }

        // Always return false to ensure the annotation processor does not claim the annotation.
        return false;
    }

    /**
     * Annotation class for the processor.
     * <p>
     * Each implementation of {@code SmithyProcessor} should have a specific package-scoped annotation used for
     * configuration.
     *
     * @return class of the annotation used by this processor
     */
    protected abstract Class<A> getAnnotationClass();

    /**
     * Creates a Smithy Build Config based on the annotation data
     *
     * @param annotation instance of generator annotation to use to create the build config.
     * @return instantiation of the build config.
     */
    protected abstract SmithyBuildConfig createBuildConfig(A annotation);

    private SmithyBuildResult executeSmithyBuild(SmithyBuildConfig config) {
        ModelAssembler assembler = Model.assembler();
        // Discover any models on the annotation processor classpath
        assembler.discoverModels(SmithyProcessor.class.getClassLoader());

        // Load specified model files from the annotation
        getFiles().forEach(assembler::addImport);

        SmithyBuild smithyBuild = SmithyBuild.create(SmithyProcessor.class.getClassLoader());
        smithyBuild.model(assembler.assemble().unwrap());
        smithyBuild.config(config);

        return smithyBuild.build();
    }


    private A getAnnotation(Set<? extends Element> elements) {
        return elements.stream()
                .findFirst()
                .map(element -> element.getAnnotation(getAnnotationClass()))
                .orElseThrow(() -> new IllegalStateException("No annotation of type " + getAnnotationClass() + " found on element"));
    }

    private List<Path> getFiles() {
        List<Path> modelFiles = new ArrayList<>();
        try {
            // First, get the root manifest file. Then we will use that to discover the other smithy files in the
            // META-INF directory
            FileObject manifest = filer.getResource(StandardLocation.SOURCE_PATH, "", MANIFEST_PATH);
            // read each manifest entry and add to the list
            for (String line : Files.readAllLines(Paths.get(manifest.toUri()))) {
                FileObject resource = filer.getResource(StandardLocation.SOURCE_PATH, "",
                        SMITHY_PREFIX + line.trim());
                modelFiles.add(Paths.get(resource.toUri()));
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return modelFiles;
    }
}
