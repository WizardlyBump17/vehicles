package com.wizardlybump17.vehicles.api.config;

import com.wizardlybump17.vehicles.api.Vehicles;
import com.wizardlybump17.wlib.config.ConfigInfo;
import com.wizardlybump17.wlib.config.Path;
import lombok.experimental.UtilityClass;

@UtilityClass
@ConfigInfo(name = "messages.yml", holderType = Vehicles.class, saveDefault = true)
public class Messages {

    @Path(value = "errors.invalid-player", options = "fancy")
    public static String invalidPlayer = "§cInvalid player.";
    @Path(value = "errors.invalid-model", options = "fancy")
    public static String invalidModel = "§cInvalid model.";
    @Path(value = "errors.not-in-vehicle", options = "fancy")
    public static String notInVehicle = "§cYou are not in a vehicle.";
    @Path(value = "errors.invalid.lock-type", options = "fancy")
    public static String invalidLockType = "§cInvalid lock type.";

    @Path(value = "command.vehicle.leave.left", options = "fancy")
    public static String leftVehicle = "§aYou left the vehicle.";
    @Path(value = "command.vehicle.lock.locked", options = "fancy")
    public static String lockedVehicle = "§aYou locked the {type}.";
}
