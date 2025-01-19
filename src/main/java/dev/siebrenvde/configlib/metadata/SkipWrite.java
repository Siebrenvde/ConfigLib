package dev.siebrenvde.configlib.metadata;

import org.quiltmc.config.api.metadata.MetadataType;

/**
 * When added to a node's metadata, prevents the node
 * from being written to the config file
 * <p>
 * Useful for environment-specific options
 */
public class SkipWrite {

    public static MetadataType<SkipWrite, Builder> TYPE = MetadataType.create(Builder::new);

    public static final class Builder implements MetadataType.Builder<SkipWrite> {
        @SuppressWarnings("InstantiationOfUtilityClass")
        public SkipWrite build() { return new SkipWrite(); }
    }

}
