package com.hmellema.smithy.processor;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.StandardLocation;
import software.amazon.smithy.build.SmithyBuild;
import software.amazon.smithy.build.SmithyBuildResult;
import software.amazon.smithy.build.model.SmithyBuildConfig;
import software.amazon.smithy.model.Model;
import software.amazon.smithy.model.loader.ModelAssembler;
import software.amazon.smithy.model.loader.ModelDiscovery;
import software.amazon.smithy.model.node.ObjectNode;
import software.amazon.smithy.utils.IoUtils;


public abstract class SmithyProcessor<A extends Annotation> extends AbstractProcessor {
    private static final String MANIFEST_PATH = "META-INF/smithy/manifest";
    private static final String SOURCE_PROJECTION_PATH = "build/smithy/source/";
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
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(getAnnotationClass());
        if (elements.size() != 1) {
            if (elements.size() > 1) {
                messager.printMessage(Diagnostic.Kind.ERROR,
                        "Only one package can have the " + getAnnotationClass() + " annotation.");
            }
        } else {
            messager.printMessage(Diagnostic.Kind.NOTE,
                    "Executing processor: " + this.getClass().getSimpleName() + "...");
            SmithyBuildConfig config = createBuildConfig(getAnnotation(elements));
            executeSmithyBuild(config).allArtifacts()
                    .filter(path -> path.toString().contains(SOURCE_PROJECTION_PATH + getPluginName()))
                    .forEach(this::writeArtifact);
        }

        // Always return false to ensure the annotation processor does not claim the annotation.
        return false;
    }

    /**
     * Name of the Smithy build plugin to execute with this annotation processor.
     *
     * @return name of plugin to run.
     */
    protected abstract String getPluginName();

    /**
     * Maps annotation data to a plugin configuration node.
     *
     * @param annotation instance of generator annotation to use to create the build config.
     * @return ObjectNode to use as plugin configuration node.
     */
    protected abstract ObjectNode createPluginNode(A annotation);


    /**
     * Annotation class for the processor.
     * <p>
     * Each implementation of {@code SmithyProcessor} should have a specific package-scoped annotation used for
     * configuration.
     *
     * @return class of the annotation used by this processor
     */
    protected abstract Class<A> getAnnotationClass();

    private SmithyBuildConfig createBuildConfig(A annotation) {
        Map<String, ObjectNode> pluginMap = new HashMap<>();
        pluginMap.put(getPluginName(), createPluginNode(annotation));
        return SmithyBuildConfig.builder().version("1.0").plugins(pluginMap).build();
    }

    private SmithyBuildResult executeSmithyBuild(SmithyBuildConfig config) {
        ModelAssembler assembler = Model.assembler();

        // Discover any models on the annotation processor classpath
        assembler.discoverModels(SmithyProcessor.class.getClassLoader());

        // Load specified model files from the annotation
        ModelDiscovery.findModels(getManifestUrl()).forEach(assembler::addImport);

        SmithyBuild smithyBuild = SmithyBuild.create(SmithyProcessor.class.getClassLoader());
        smithyBuild.model(assembler.assemble().unwrap());
        smithyBuild.config(config);

        return smithyBuild.build();
    }

    private void writeArtifact(Path path) {
        String pathStr = path.toString();
        String outputPath = pathStr.substring(pathStr.lastIndexOf(getPluginName()) + getPluginName().length() + 1);
        try {
            // Resources are written to the class output
            if (outputPath.startsWith("META-INF")) {
                try (Writer writer = filer.createResource(StandardLocation.CLASS_OUTPUT, "", outputPath).openWriter()) {
                    writer.write(IoUtils.readUtf8File(path));
                }
                // All other files are written to the source output
            } else {
                outputPath = outputPath.replace("/", ".").substring(0, outputPath.lastIndexOf(".java"));
                try (Writer writer = filer.createSourceFile(outputPath).openWriter()) {
                    writer.write(IoUtils.readUtf8File(path));
                }
            }
        } catch (IOException exc) {
            throw new UncheckedIOException(exc);
        }
    }

    private A getAnnotation(Set<? extends Element> elements) {
        return elements.stream()
                .findFirst()
                .map(element -> element.getAnnotation(getAnnotationClass()))
                .orElseThrow(() -> new IllegalStateException("No annotation of type "
                        + getAnnotationClass() + " found on element"));
    }

    private URL getManifestUrl() {
        try {
            return filer.getResource(StandardLocation.SOURCE_PATH, "", MANIFEST_PATH).toUri().toURL();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
