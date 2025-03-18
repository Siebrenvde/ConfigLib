package dev.siebrenvde.configlib.serialisers.json;

import dev.siebrenvde.configlib.metadata.ConfigComment;
import dev.siebrenvde.configlib.metadata.SkipWrite;
import org.jspecify.annotations.NullMarked;
import org.quiltmc.config.api.Config;
import org.quiltmc.config.api.Constraint;
import org.quiltmc.config.api.annotations.Comment;
import org.quiltmc.config.api.values.ConfigSerializableObject;
import org.quiltmc.config.api.values.TrackedValue;
import org.quiltmc.config.api.values.ValueList;
import org.quiltmc.config.api.values.ValueMap;
import org.quiltmc.config.api.values.ValueTreeNode;
import org.quiltmc.config.impl.util.SerializerUtils;
import org.quiltmc.parsers.json.JsonFormat;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Map;

@NullMarked
class JsonWriter {

    private final Config config;
    private final OutputStream to;
    private final org.quiltmc.parsers.json.JsonWriter writer;

    public JsonWriter(Config config, OutputStream to, JsonFormat format) {
        this.config = config;
        this.to = to;
        this.writer = org.quiltmc.parsers.json.JsonWriter.create(
            new OutputStreamWriter(to),
            format
        );
    }

    public void write() throws IOException {
        if (config.hasMetadata(ConfigComment.TYPE)) {
            for (String comment : config.metadata(ConfigComment.TYPE)) {
                writer.comment(comment);
            }
        }

        writer.beginObject();
        for (ValueTreeNode node : config.nodes()) {
            writeNode(node);
        }
        writer.endObject();

        // Ensure final newline
        writer.flush();
        to.write('\n');

        writer.close();
    }

    private void writeNode(ValueTreeNode node) throws IOException {
        if (node.hasMetadata(SkipWrite.TYPE)) return;

        if (node.hasMetadata(Comment.TYPE)) {
            for (String comment : node.metadata(Comment.TYPE)) {
                writer.comment(comment);
            }
        }

        String name = SerializerUtils.getSerializedName(node);

        if (node instanceof TrackedValue<?> value) {
            for (Constraint<?> constraint : value.constraints()) {
                writer.comment(constraint.getRepresentation());
            }

            writer.name(name);
            writeValue(value.getRealValue());
        } else {
            writer.name(name);
            writer.beginObject();
            for (ValueTreeNode sectionNode : (ValueTreeNode.Section) node) {
                writeNode(sectionNode);
            }
            writer.endObject();
        }
    }

    private void writeValue(Object value) throws IOException {
        if (value instanceof ValueMap<?> map) {
            writer.beginObject();
            for (Map.Entry<String, ?> entry : map) {
                writer.name(entry.getKey());
                writeValue(entry.getValue());
            }
            writer.endObject();
        } else if (value instanceof ValueList<?> list) {
            writer.beginArray();
            for (Object item : list) {
                writeValue(item);
            }
            writer.endArray();
        } else if (value instanceof ConfigSerializableObject<?> obj) {
            writeValue(obj.getRepresentation());
        } else if (value instanceof String string) {
            writer.value(string);
        } else if (value instanceof Enum<?> enumValue) {
            writer.value(enumValue.name());
        } else if (value instanceof Number number) {
            writer.value(number);
        } else if (value instanceof Boolean bool) {
            writer.value(bool);
        } else {
            throw new IllegalArgumentException("Unsupported value type: " + value.getClass().getName());
        }
    }

}
