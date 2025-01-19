package dev.siebrenvde.configlib;

import dev.siebrenvde.configlib.metadata.ConfigComment;
import dev.siebrenvde.configlib.metadata.NoOptionSpacing;
import dev.siebrenvde.configlib.serialisers.TomlSerialiser;
import org.quiltmc.config.api.ReflectiveConfig;
import org.quiltmc.config.impl.ConfigFieldAnnotationProcessors;
import org.quiltmc.config.implementor_api.ConfigEnvironment;
import org.quiltmc.config.implementor_api.ConfigFactory;

import java.nio.file.Path;

public class ConfigLib {

    static {
        ConfigFieldAnnotationProcessors.register(NoOptionSpacing.class, new NoOptionSpacing.Processor());
        ConfigFieldAnnotationProcessors.register(ConfigComment.ConfigComments.class, new ConfigComment.Processor());
    }

    public static <T extends ReflectiveConfig> T toml(Path directory, String name, Class<T> configClass) {
        return ConfigFactory.create(
            new ConfigEnvironment(directory, TomlSerialiser.INSTANCE),
            "",
            name,
            configClass
        );
    }

}
