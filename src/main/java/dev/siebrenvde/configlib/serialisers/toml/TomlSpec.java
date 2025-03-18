package dev.siebrenvde.configlib.serialisers.toml;

import org.jspecify.annotations.NullMarked;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

@NullMarked
public class TomlSpec {

    public static final Charset CHARSET = StandardCharsets.UTF_8;

    /* Whitespace */
    public static final char TAB = '\t';
    public static final char SPACE = ' ';

    /* Newlines */
    public static final char LINE_FEED = '\n';
    public static final char CARRIAGE_RETURN = '\r';

    /* Comments */
    public static final char COMMENT_DELIMITER = '#';

    /* Keys */
    public static final Pattern BARE_KEY_PATTERN = Pattern.compile("^[A-Za-z0-9_-]+$");
    public static final char DOTTED_KEY_SEPARATOR = '.';

    /* Tables */
    public static final char TABLE_KEY_OPEN_DELIMITER = '[';
    public static final char TABLE_KEY_CLOSE_DELIMITER = ']';

    /* Key/Value Pairs */
    public static final char KEY_VALUE_SEPARATOR = '=';

    /* Arrays */
    public static final char ARRAY_OPEN_DELIMITER = '[';
    public static final char ARRAY_CLOSE_DELIMITER = ']';
    public static final char ARRAY_VALUE_SEPARATOR = ',';

    /* Strings */
    public static final char BASIC_STRING_DELIMITER = '"';
    public static final char LITERAL_STRING_DELIMITER = '\'';

    public static boolean isAllowedInString(int c) {
        return (c > 0x1f || c == 0x09) && c != 0x7f;
    }

}
