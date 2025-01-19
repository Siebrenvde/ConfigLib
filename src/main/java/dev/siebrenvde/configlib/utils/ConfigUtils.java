package dev.siebrenvde.configlib.utils;

import org.quiltmc.config.api.annotations.DisplayName;
import org.quiltmc.config.api.annotations.DisplayNameConvention;
import org.quiltmc.config.api.values.ValueTreeNode;
import org.quiltmc.config.impl.util.SerializerUtils;

@SuppressWarnings("unused")
public class ConfigUtils {

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
