package com.hmellema.smithy.processor;

import com.google.auto.service.AutoService;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.*;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.hmellema.smithy.processor.annotations.SmithyProcess;
import software.amazon.smithy.build.SmithyBuild;
import software.amazon.smithy.build.SmithyBuildResult;
import software.amazon.smithy.build.model.SmithyBuildConfig;
import software.amazon.smithy.model.Model;
import software.amazon.smithy.model.loader.ModelAssembler;

@AutoService(Processor.class)
@SupportedAnnotationTypes(SmithyProcess.NAME)
@SupportedSourceVersion(SourceVersion.RELEASE_17)
public class SmithyProcessor extends AbstractProcessor {
    private static final String MANIFEST_PATH = "META-INF/smithy/manifest";
    private static final String SMITHY_PREFIX = "META-INF/smithy/";
    private Messager messager;
    private Filer filer;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        messager = processingEnv.getMessager();
        filer = processingEnv.getFiler();
        messager.printMessage(Diagnostic.Kind.NOTE,
                "initialized processor: " + this.getClass().getSimpleName() + "...");
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        var elements = roundEnv.getElementsAnnotatedWith(SmithyProcess.class);
        if (elements.size() != 1) {
            if (elements.size() > 1) {
                messager.printMessage(Diagnostic.Kind.ERROR, "Only one package can have generator annotation.");
            }
            return false;
        }

        SmithyProcess annotation = getAnnotation(elements);
        SmithyBuildResult buildResult = executeSmithyBuild(annotation);

        messager.printMessage(Diagnostic.Kind.NOTE, "ARTIFACTS: " + buildResult.allArtifacts().toList());
        messager.printMessage(Diagnostic.Kind.NOTE,
                "Annotation processor " + this.getClass().getSimpleName() + " finished processing.");
        return false;
    }

    private SmithyBuildResult executeSmithyBuild(SmithyProcess annotation) {
        ModelAssembler assembler = Model.assembler();
        // Discover any models on the annotation processor classpath
        assembler.discoverModels(SmithyProcessor.class.getClassLoader());

        // Load specified model files from the annotation
        getFiles().forEach(assembler::addImport);

        SmithyBuild smithyBuild = SmithyBuild.create(SmithyProcessor.class.getClassLoader());
        smithyBuild.model(assembler.assemble().unwrap());
        // Create a temp build config with the desired plugins
        SmithyBuildConfig buildConfig = SmithyBuildConfig.builder().version("1.0").build();
        smithyBuild.config(buildConfig);

        return smithyBuild.build();
    }

    private static SmithyProcess getAnnotation(Set<? extends Element> elements) {
        return elements.stream()
                .findFirst()
                .map(element -> element.getAnnotation(SmithyProcess.class))
                .orElseThrow(() -> new IllegalStateException("No annotation found on element"));
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
            throw new RuntimeException(e);
        }
        return modelFiles;
    }
}
