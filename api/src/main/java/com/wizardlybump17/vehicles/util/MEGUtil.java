package com.wizardlybump17.vehicles.util;

import com.ticxo.modelengine.api.generator.blueprint.BlueprintBone;
import lombok.experimental.UtilityClass;
import org.bukkit.util.Vector;
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

    public static Vector getRotation(BlueprintBone bone) {
        return new Vector(Math.toDegrees(-bone.getLocalRotationX()), Math.toDegrees(-bone.getLocalRotationY()), Math.toDegrees(bone.getLocalRotationZ()));
    }

    public static Vector getPosition(BlueprintBone bone, BlueprintBone parent) {
        double x = -bone.getGlobalOffsetX() * 16;
        double y = bone.getGlobalOffsetY() * 16;
        double z = bone.getGlobalOffsetZ() * 16;

        if (parent != null) {
            x += -parent.getGlobalOffsetX() * 16;
            y += parent.getGlobalOffsetY() * 16;
            z += parent.getGlobalOffsetZ() * 16;
        }

        return new Vector(x, y, z);
    }
}
