package dev.siebrenvde.configlib;

import dev.siebrenvde.configlib.metadata.ConfigComment;
import dev.siebrenvde.configlib.metadata.NoOptionSpacing;
import dev.siebrenvde.configlib.serialisers.TomlSerialiser;
import org.jspecify.annotations.NullMarked;
import org.quiltmc.config.api.ReflectiveConfig;
import org.quiltmc.config.impl.ConfigFieldAnnotationProcessors;
import org.quiltmc.config.implementor_api.ConfigEnvironment;
import org.quiltmc.config.implementor_api.ConfigFactory;

import java.nio.file.Path;

@SuppressWarnings("unused")
@NullMarked
public class ConfigLib {

    static {
        ConfigFieldAnnotationProcessors.register(NoOptionSpacing.class, new NoOptionSpacing.Processor());
        ConfigFieldAnnotationProcessors.register(ConfigComment.ConfigComments.class, new ConfigComment.Processor());
    }

    /**
     * Creates a TOML config using the directory name as the family
     * @param directory the directory your config files are stored in
     * @param name the name of the file (without extension)
     * @param configClass the config class
     * @return an instance of the config class
     */
    public static <T extends ReflectiveConfig> T toml(Path directory, String name, Class<T> configClass) {
        return toml(
            directory.getParent(),
            directory.getFileName().toString(),
            name,
            configClass
        );
    }

    /**
     * Creates a new TOML config
     * @param directory the global config directory
     * @param family the directory your config files are stored in
     * @param name the name of the file (without extension)
     * @param configClass the config class
     * @return an instance of the config class
     */
    public static <T extends ReflectiveConfig> T toml(Path directory, String family, String name, Class<T> configClass) {
        return ConfigFactory.create(
            new ConfigEnvironment(directory, TomlSerialiser.INSTANCE),
            family,
            name,
            configClass
        );
    }

}
