package dev.siebrenvde.configlib.metadata;

import org.quiltmc.config.api.metadata.MetadataType;
import org.quiltmc.config.impl.ConfigFieldAnnotationProcessors;

import java.lang.annotation.Annotation;
import java.util.function.Supplier;

/**
 * When added to a node's metadata, prevents the node
 * from being written to the config file
 * <p>
 * Useful for environment-specific options
 */
public class SkipWrite {

    public static MetadataType<SkipWrite, Builder> TYPE = MetadataType.create(Builder::new);

    /**
     * Registers the provided annotation to <b><u>not</u></b> write its affected field when the condition is <code>true</code>
     * @param annotationClass the annotation to register
     * @param condition the condition
     */
    @SuppressWarnings("unused")
    public static void skipWhen(Class<? extends Annotation> annotationClass, boolean condition) {
        skipWhen(annotationClass, () -> condition);
    }

    /**
     * Registers the provided annotation to <b><u>not</u></b> write its affected field when the condition is <code>true</code>
     * @param annotationClass the annotation to register
     * @param condition the condition
     */
    @SuppressWarnings({"ResultOfMethodCallIgnored", "unused"})
    public static void skipWhen(Class<? extends Annotation> annotationClass, Supplier<Boolean> condition) {
        ConfigFieldAnnotationProcessors.register(
            annotationClass,
            (annotation, builder) -> {
                if (condition.get()) builder.metadata(SkipWrite.TYPE, Builder::build);
            }
        );
    }

    public static final class Builder implements MetadataType.Builder<SkipWrite> {
        @SuppressWarnings("InstantiationOfUtilityClass")
        public SkipWrite build() { return new SkipWrite(); }
    }

}
