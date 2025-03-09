package dev.siebrenvde.configlib.utils;

import org.jspecify.annotations.NullMarked;

@NullMarked
public class SerialiserUtils {

    public static String sanitiseString(String string) {
        StringBuilder sb = new StringBuilder();

        string.codePoints().forEach(c -> {
            switch(c) {
                case '"' -> sb.append("\\\"");
                case '\\' -> sb.append("\\\\");
                case '\b' -> sb.append("\\b");
                case '\n' -> sb.append("\\n");
                case '\f' -> sb.append("\\f");
                case '\r' -> sb.append("\\r");
                default -> {
                    if (!((c <= 0x1f && c != 0x09) || c == 0x7f)) {
                        sb.append(Character.toChars(c));
                    }
                }
            }
        });

        return sb.toString();
    }

}
