package com.hmellema.smithy.processor;

import com.google.auto.service.AutoService;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import com.hmellema.smithy.processor.annotations.SmithyProcess;
import software.amazon.smithy.build.SmithyBuild;
import software.amazon.smithy.build.SmithyBuildResult;
import software.amazon.smithy.model.Model;
import software.amazon.smithy.model.loader.ModelAssembler;

@AutoService(Processor.class)
@SupportedAnnotationTypes(SmithyProcess.NAME)
@SupportedSourceVersion(SourceVersion.RELEASE_17)
public class SmithyProcessor extends AbstractProcessor {
    private Messager messager;
    private Filer filer;
    private Path root;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        messager = processingEnv.getMessager();
        filer = processingEnv.getFiler();
        root = findRootPath(filer);
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
        getFiles(annotation.models()).forEach(assembler::addImport);

        SmithyBuild smithyBuild = SmithyBuild.create(SmithyProcessor.class.getClassLoader());
        smithyBuild.model(assembler.assemble().unwrap());
        smithyBuild.config(root.resolve(annotation.smithyBuild()));

        return smithyBuild.build();
    }

    private static SmithyProcess getAnnotation(Set<? extends Element> elements) {
        return elements.stream()
                .findFirst()
                .map(element -> element.getAnnotation(SmithyProcess.class))
                .orElseThrow(() -> new IllegalStateException("No annotation found on element"));
    }

    private List<Path> getFiles(String[] modelLocations) {
        List<Path> modelFiles = new ArrayList<>();
        for (String location : modelLocations) {
            PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:" + location);
            try (Stream<Path> files = Files.walk(root)) {
                files.map(root::relativize).filter(matcher::matches).map(root::resolve).forEach(modelFiles::add);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return modelFiles;
    }

    // TODO: There should be a better way?
    private static Path findRootPath(Filer filer) {
        final FileObject temp;
        try {
            temp = filer.createResource(StandardLocation.CLASS_OUTPUT, "", "Dummy");
            Path tempPath = Paths.get(temp.toUri());
            temp.delete();
            return walkUp(tempPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Path walkUp(Path path) {
        while(!new File(path.toString(), "smithy-build.json").exists()) {
            path = path.getParent();
            if (path == null) {
                throw new RuntimeException("Could not find a root path in the project");
            }
        }
        return path;
    }
}
