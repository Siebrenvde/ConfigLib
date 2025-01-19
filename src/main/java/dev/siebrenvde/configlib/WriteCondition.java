package dev.siebrenvde.configlib;

import org.quiltmc.config.api.metadata.MetadataType;
import org.quiltmc.config.api.values.TrackedValue;

import java.util.Optional;

public class WriteCondition {

    static MetadataType<WriteCondition, Builder> TYPE = MetadataType.create(Optional::empty, Builder::new);

    public static void dontWrite(TrackedValue.Builder<?> builder) {
        builder.metadata(TYPE, Builder::build);
    }

    static final class Builder implements MetadataType.Builder<WriteCondition> {
        public WriteCondition build() { return new WriteCondition(); }
    }

}
