package dev.siebrenvde.configlib.serialisers.toml;

import dev.siebrenvde.configlib.metadata.ConfigComment;
import dev.siebrenvde.configlib.metadata.NoOptionSpacing;
import dev.siebrenvde.configlib.metadata.SkipWrite;
import org.jspecify.annotations.NullMarked;
import org.quiltmc.config.api.Config;
import org.quiltmc.config.api.Constraint;
import org.quiltmc.config.api.annotations.Comment;
import org.quiltmc.config.api.values.ConfigSerializableObject;
import org.quiltmc.config.api.values.TrackedValue;
import org.quiltmc.config.api.values.ValueKey;
import org.quiltmc.config.api.values.ValueList;
import org.quiltmc.config.api.values.ValueMap;
import org.quiltmc.config.api.values.ValueTreeNode;
import org.quiltmc.config.impl.util.SerializerUtils;
import org.quiltmc.config.impl.values.ValueKeyImpl;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@NullMarked
class TomlWriter {

    private static final String INDENTATION = "  ";

    private final Config config;
    private final OutputStream to;

    private int indent = 0;

    public TomlWriter(Config config, OutputStream to) {
        this.config = config;
        this.to = to;
    }

    public void write() throws IOException {
        write(config.nodes(), true, true);
    }

    private void write(Iterable<ValueTreeNode> unparsedNodes, boolean firstValue, boolean isRoot) throws IOException {
        if (isRoot && config.hasMetadata(ConfigComment.TYPE)) {
            for (String comment : config.metadata(ConfigComment.TYPE)) {
                writeComment(comment);
            }
            writeNewline();
        }

        List<ValueTreeNode> nodes = new ArrayList<>();
        List<ValueTreeNode> maps = new ArrayList<>();
        List<ValueTreeNode> sections = new ArrayList<>();

        for (ValueTreeNode node : unparsedNodes) {
            if (node.hasMetadata(SkipWrite.TYPE)) continue;

            if (node instanceof TrackedValue<?> value) {
                if (value.getRealValue() instanceof ValueMap<?>) {
                    maps.add(value);
                } else {
                    nodes.add(value);
                }
            } else {
                sections.add(node);
            }
        }

        nodes.addAll(maps);
        nodes.addAll(sections);

        for (ValueTreeNode node : nodes) {
            if (!firstValue) {
                if(!(node instanceof TrackedValue<?>) || !config.hasMetadata(NoOptionSpacing.TYPE)) {
                    writeNewline();
                }
            } else {
                firstValue = false;
            }

            if (node.hasMetadata(Comment.TYPE)) {
                for (String comment : node.metadata(Comment.TYPE)) {
                    writeComment(comment);
                }
            }

            ValueKey key = SerializerUtils.getSerializedKey(config, node);

            if(node instanceof TrackedValue<?> value) {
                for (Constraint<?> constraint : value.constraints()) {
                    writeComment(constraint.getRepresentation());
                }

                if (value.getRealValue() instanceof ValueMap<?> map) {
                    writeTableKey(key);
                    indent++;
                    writeValue(map);
                    indent--;
                } else {
                    writeValueKey(key);
                    writeValue(value.getRealValue());
                }

                writeNewline();
            } else {
                writeTableKey(key);
                indent++;
                write((ValueTreeNode.Section) node, false, false);
                indent--;
            }

        }
    }

    private byte[] asBytes(String string) {
        return string.getBytes(TomlSpec.CHARSET);
    }

    private void writeIndent() throws IOException {
        to.write(asBytes(INDENTATION.repeat(indent)));
    }

    private void writeNewline() throws IOException {
        to.write(TomlSpec.LINE_FEED);
    }

    private void writeSpace() throws IOException {
        to.write(TomlSpec.SPACE);
    }

    private void writeSanitised(String string, boolean escape) throws IOException {
        StringBuilder sb = new StringBuilder();

        string.codePoints().forEach(codePoint -> {
            if (escape) {
                switch(codePoint) {
                    case '"' -> sb.append("\\\"");
                    case '\\' -> sb.append("\\\\");
                    case '\b' -> sb.append("\\b");
                    case '\n' -> sb.append("\\n");
                    case '\f' -> sb.append("\\f");
                    case '\r' -> sb.append("\\r");
                    default -> {
                        if (TomlSpec.isAllowedInString(codePoint)) {
                            sb.append(Character.toChars(codePoint));
                        }
                    }
                }
            } else {
                if (TomlSpec.isAllowedInString(codePoint)) {
                    sb.append(Character.toChars(codePoint));
                }
            }
        });

        to.write(asBytes(sb.toString()));
    }

    private void writeComment(String comment) throws IOException {
        writeIndent();
        to.write(TomlSpec.COMMENT_DELIMITER);
        writeSpace();
        writeSanitised(comment, false);
        writeNewline();
    }

    private void writeKey(ValueKey key, boolean full) throws IOException {
        String keyString = full ? key.toString() : key.getLastComponent();
        if (TomlSpec.BARE_KEY_PATTERN.matcher(keyString).matches()) {
            to.write(asBytes(keyString));
        } else {
            for (int codePoint : keyString.codePoints().toArray()) {
                if (!TomlSpec.isAllowedInString(codePoint)) {
                    throw new IllegalArgumentException(String.format("Illegal character '%s' in key \"%s\"", Character.toString(codePoint), key));
                }
            }
            if (full) {
                int i = 0;
                for (String component : key) {
                    if (i != 0) to.write(TomlSpec.DOTTED_KEY_SEPARATOR);
                    if (TomlSpec.BARE_KEY_PATTERN.matcher(component).matches()) {
                        to.write(asBytes(component));
                    } else {
                        writeString(component);
                    }
                    i++;
                }
            } else {
                writeString(keyString);
            }
        }
    }

    private void writeTableKey(ValueKey key) throws IOException {
        writeIndent();
        to.write(TomlSpec.TABLE_KEY_OPEN_DELIMITER);
        writeKey(key, true);
        to.write(TomlSpec.TABLE_KEY_CLOSE_DELIMITER);
        writeNewline();
    }

    private void writeValueKey(ValueKey key) throws IOException {
        writeIndent();
        writeKey(key, false);
        writeSpace();
        to.write(TomlSpec.KEY_VALUE_SEPARATOR);
        writeSpace();
    }

    private void writeValue(Object value) throws IOException {
        if(value instanceof ValueMap<?> map) {
            writeMap(map);
        } else if(value instanceof ValueList<?> list) {
            writeList(list);
        } else if(value instanceof ConfigSerializableObject<?> obj) {
            writeValue(obj.getRepresentation());
        } else if(value instanceof String string) {
            writeString(string);
        } else if(value instanceof Enum<?> enumValue) {
            writeString(enumValue.name());
        } else if(value instanceof Number || value instanceof Boolean) {
            to.write(asBytes(value.toString()));
        } else {
            throw new IllegalArgumentException("Unsupported value type: " + value.getClass().getName());
        }
    }

    private void writeString(String string) throws IOException {
        to.write(TomlSpec.BASIC_STRING_DELIMITER);
        writeSanitised(string, true);
        to.write(TomlSpec.BASIC_STRING_DELIMITER);
    }

    private void writeMap(ValueMap<?> map) throws IOException {
        int i = 0;
        for (Map.Entry<String, ?> entry : map) {
            writeValueKey(new ValueKeyImpl(entry.getKey()));
            writeValue(entry.getValue());
            if (i != map.size() - 1) writeNewline();
            i++;
        }
    }

    private void writeList(ValueList<?> list) throws IOException {
        boolean multiline = list.size() > 1;

        to.write(TomlSpec.ARRAY_OPEN_DELIMITER);

        if (multiline) {
            writeNewline();
            indent++;
        }

        int i = 0;
        for (Object obj : list) {
            if (multiline) writeIndent();

            writeValue(obj);
            if (i != list.size() - 1) to.write(TomlSpec.ARRAY_VALUE_SEPARATOR);

            if (multiline) writeNewline();
            i++;
        }

        if(multiline) {
            indent--;
            writeIndent();
        }

        to.write(TomlSpec.ARRAY_CLOSE_DELIMITER);
    }

}
