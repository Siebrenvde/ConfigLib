package dev.siebrenvde.configlib.serialisers.json;

import org.jspecify.annotations.NullMarked;
import org.quiltmc.config.api.Config;
import org.quiltmc.config.api.Serializer;
import org.quiltmc.config.api.serializers.Json5Serializer;
import org.quiltmc.parsers.json.JsonFormat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@NullMarked
public class JsonSerialiser implements Serializer {

    public static final JsonSerialiser JSON = new JsonSerialiser(JsonFormat.JSON, "json");
    public static final JsonSerialiser JSON5 = new JsonSerialiser(JsonFormat.JSON5, "json5");

    private final JsonFormat format;
    private final String extension;

    private JsonSerialiser(JsonFormat format, String extension) {
        this.format = format;
        this.extension = extension;
    }

    @Override
    public String getFileExtension() {
        return extension;
    }

    @Override
    public void serialize(Config config, OutputStream to) throws IOException {
        new JsonWriter(config, to, format).write();
    }

    @Override
    public void deserialize(Config config, InputStream from) {
        Json5Serializer.INSTANCE.deserialize(config, from);
    }

}
