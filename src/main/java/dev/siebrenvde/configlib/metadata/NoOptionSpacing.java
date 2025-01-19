package dev.siebrenvde.configlib.metadata;

import org.quiltmc.config.api.annotations.ConfigFieldAnnotationProcessor;
import org.quiltmc.config.api.metadata.MetadataContainerBuilder;
import org.quiltmc.config.api.metadata.MetadataType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
public @interface NoOptionSpacing {

    MetadataType<OptionSpacing, Builder> TYPE = MetadataType.create(Builder::new);

    class OptionSpacing {}

    final class Builder implements MetadataType.Builder<OptionSpacing> {
        public OptionSpacing build() { return new OptionSpacing(); }
    }

    final class Processor implements ConfigFieldAnnotationProcessor<NoOptionSpacing> {
        @SuppressWarnings("ResultOfMethodCallIgnored")
        @Override
        public void process(NoOptionSpacing annotation, MetadataContainerBuilder<?> builder) {
            builder.metadata(TYPE, Builder::build);
        }
    }

}
