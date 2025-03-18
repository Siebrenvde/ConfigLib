package dev.siebrenvde.configlib.serialisers.toml;

import org.jspecify.annotations.NullMarked;
import org.quiltmc.config.api.Config;
import org.quiltmc.config.api.Serializer;
import org.quiltmc.config.api.serializers.TomlSerializer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@NullMarked
public class TomlSerialiser implements Serializer {

    public static final TomlSerialiser INSTANCE = new TomlSerialiser();

    @Override
    public String getFileExtension() {
        return "toml";
    }

    @Override
    public void serialize(Config config, OutputStream to) throws IOException {
        new TomlWriter(config, to).write();
    }

    @Override
    public void deserialize(Config config, InputStream from) {
        TomlSerializer.INSTANCE.deserialize(config, from);
    }

}
