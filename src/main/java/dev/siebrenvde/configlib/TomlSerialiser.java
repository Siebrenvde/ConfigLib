package dev.siebrenvde.configlib;

import org.quiltmc.config.api.Config;
import org.quiltmc.config.api.Constraint;
import org.quiltmc.config.api.Serializer;
import org.quiltmc.config.api.annotations.Comment;
import org.quiltmc.config.api.serializers.TomlSerializer;
import org.quiltmc.config.api.values.*;
import org.quiltmc.config.impl.util.SerializerUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

public class TomlSerialiser implements Serializer {
    public static final TomlSerialiser INSTANCE = new TomlSerialiser();
    private static final String INDENTATION = "  ";
    private static final Character NEWLINE = '\n';

    @Override
    public String getFileExtension() {
        return "toml";
    }

    @Override
    public void serialize(Config config, OutputStream to) throws IOException {
        write(config, to, config.nodes(), 0, true, true);
    }

    private void write(Config config, OutputStream to, Iterable<ValueTreeNode> nodes, int indent, boolean firstValue, boolean isRoot) throws IOException {
        if(config.hasMetadata(ConfigComment.TYPE) && isRoot) {
            isRoot = false;
            for(String comment : config.metadata(ConfigComment.TYPE)) {
                to.write(("# " + comment).getBytes());
                writeNewline(to);
            }
            writeNewline(to);
        }
        for (ValueTreeNode node : nodes) {
            if(node.hasMetadata(WriteCondition.TYPE)) {
                System.out.println(node.key());
                continue;
            }

            if(!firstValue) {
                if(!(node instanceof TrackedValue<?>) || !config.hasMetadata(NoOptionSpacing.TYPE)) {
                    writeNewline(to);
                }
            } else {
                firstValue = false;
            }

            if(node.hasMetadata(Comment.TYPE)) {
                for(String comment : node.metadata(Comment.TYPE)) {
                    writeIndent(to, indent);
                    to.write(("# " + comment).getBytes());
                    writeNewline(to);
                }
            }

            ValueKey key = SerializerUtils.getSerializedKey(config, node);

            if(node instanceof TrackedValue<?> value) {
                for (Constraint<?> constraint : value.constraints()) {
                    writeIndent(to, indent);
                    to.write(("# " + constraint.getRepresentation()).getBytes());
                    writeNewline(to);
                }

                if(value.getRealValue() instanceof ValueMap<?> map) {
                    writeTableKey(key, to, indent);
                    writeValue(map, to, indent + 1);
                } else {
                    writeIndent(to, indent);
                    to.write((key.getLastComponent() + " = ").getBytes());
                    writeValue(value.getRealValue(), to, indent);
                }

                writeNewline(to);
            } else {
                writeTableKey(key, to, indent);
                write(config, to, (ValueTreeNode.Section) node, indent + 1, firstValue, isRoot);
            }
        }
    }

    private static void writeIndent(OutputStream to, int indent) throws IOException {
        to.write(INDENTATION.repeat(indent).getBytes());
    }

    private static void writeNewline(OutputStream to) throws IOException {
        to.write(NEWLINE);
    }

    private static void writeTableKey(ValueKey key, OutputStream to, int indent) throws IOException {
        writeIndent(to, indent);
        to.write(("[" + key + "]").getBytes());
        writeNewline(to);
    }

    private static void writeValue(Object value, OutputStream to, int indent) throws IOException {
        if(value instanceof ValueMap<?> map) {
            writeMap(map, to, indent);
        } else if(value instanceof ValueList<?> list) {
            writeList(list, to, indent);
        } else if(value instanceof ConfigSerializableObject<?> obj) {
            writeValue(obj.getRepresentation(), to, indent);
        } else if(value instanceof String string) {
            writeString(string, to);
        } else if(value instanceof Enum<?> enumValue) {
            writeString(enumValue.name(), to);
        } else if(value instanceof Number || value instanceof Boolean) {
            to.write(value.toString().getBytes());
        } else {
            throw new IllegalArgumentException("Unsupported value type: " + value.getClass().getName());
        }
    }

    private static void writeString(String string, OutputStream to) throws IOException {
        to.write(("\"" + string + "\"").getBytes());
    }

    private static void writeMap(ValueMap<?> map, OutputStream to, int indent) throws IOException {
        int i = 0;
        for (Map.Entry<String, ?> entry : map) {
            writeIndent(to, indent);
            to.write((entry.getKey() + " = ").getBytes());
            writeValue(entry.getValue(), to, indent);
            if(i != map.size() - 1) writeNewline(to);
            i++;
        }
    }

    private static void writeList(ValueList<?> list, OutputStream to, int indent) throws IOException {
        boolean multiline = list.size() > 1;
        to.write('[');
        if(multiline) to.write('\n');
        int i = 0;
        for (Object obj : list) {
            if(multiline) writeIndent(to, indent + 1);
            writeValue(obj, to, indent + 1);
            if(i != list.size() - 1) to.write(',');
            if(multiline) writeNewline(to);
            i++;
        }
        if(multiline) writeIndent(to, indent);
        to.write(']');
    }

    @Override
    public void deserialize(Config config, InputStream from) {
        TomlSerializer.INSTANCE.deserialize(config, from);
    }

}
