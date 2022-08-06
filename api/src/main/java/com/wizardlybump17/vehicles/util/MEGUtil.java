package com.wizardlybump17.vehicles.util;

import com.ticxo.modelengine.api.generator.blueprint.BlueprintBone;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

@UtilityClass
public class MEGUtil {

    @Nullable
    public static BlueprintBone getBone(BlueprintBone parent, String name) {
        BlueprintBone bone = parent.getBone(name);
        if (bone != null)
            return bone;

        for (Map.Entry<String, BlueprintBone> entry : parent.getChildren().entrySet())
            if (entry.getKey().startsWith(name))
                return entry.getValue();

        return null;
    }
}
