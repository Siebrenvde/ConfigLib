package dev.siebrenvde.configlib.metadata;

import org.jspecify.annotations.NullMarked;
import org.quiltmc.config.api.annotations.ConfigFieldAnnotationProcessor;
import org.quiltmc.config.api.metadata.MetadataContainerBuilder;
import org.quiltmc.config.api.metadata.MetadataType;
import org.quiltmc.config.impl.StringIterator;

import java.lang.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Adds a comment to the top of a config file
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Repeatable(ConfigComment.ConfigComments.class)
@NullMarked
public @interface ConfigComment {

    MetadataType<ConfigCommentList, Builder> TYPE = MetadataType.create(Builder::new);

    String value();

    final class ConfigCommentList extends StringIterator {
        public ConfigCommentList(List<String> strings) {
            super(strings);
        }
    }

    final class Builder implements MetadataType.Builder<ConfigCommentList> {
        private final List<String> comments = new ArrayList<>();

        public void addComment(String comment) {
            comments.add(comment);
        }

        @Override
        public ConfigCommentList build() {
            return new ConfigCommentList(comments);
        }
    }

    final class Processor implements ConfigFieldAnnotationProcessor<ConfigComment> {
        @Override
        public void process(ConfigComment comment, MetadataContainerBuilder<?> builder) {
            builder.metadata(TYPE, b -> b.addComment(comment.value()));
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @interface ConfigComments {
        ConfigComment[] value();

        final class Processor implements ConfigFieldAnnotationProcessor<ConfigComments> {
            @Override
            public void process(ConfigComments comments, MetadataContainerBuilder<?> builder) {
                for (ConfigComment comment : comments.value()) {
                    builder.metadata(TYPE, b -> b.addComment(comment.value()));
                }
            }
        }
    }

}
