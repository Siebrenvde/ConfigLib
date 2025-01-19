package dev.siebrenvde.configlib;

import org.quiltmc.config.api.annotations.DisplayName;
import org.quiltmc.config.api.annotations.DisplayNameConvention;
import org.quiltmc.config.api.values.ValueTreeNode;
import org.quiltmc.config.impl.util.SerializerUtils;

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
