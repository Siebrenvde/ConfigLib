package dev.siebrenvde.configlib.utils;

import org.jspecify.annotations.NullMarked;
import org.quiltmc.config.api.annotations.DisplayName;
import org.quiltmc.config.api.annotations.DisplayNameConvention;
import org.quiltmc.config.api.values.ValueTreeNode;
import org.quiltmc.config.impl.util.SerializerUtils;

@SuppressWarnings("unused")
@NullMarked
public class ConfigUtils {

    /**
     * Gets the display name for the provided node
     * <p></p>
     * When no display name is set, defaults to {@link SerializerUtils#getSerializedName(ValueTreeNode)}
     * @param value the node
     * @return the display name, if present
     */
    public static String getDisplayName(ValueTreeNode value) {
        if (value.hasMetadata(DisplayName.TYPE)) {
            return value.metadata(DisplayName.TYPE).getName();
        } else {
            return value.hasMetadata(DisplayNameConvention.TYPE)
                ? value.metadata(DisplayNameConvention.TYPE).coerce(value.key().getLastComponent())
                : SerializerUtils.getSerializedName(value);
        }
    }

}
