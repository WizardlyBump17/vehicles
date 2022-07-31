package com.wizardlybump17.vehicles.api.config;

import com.wizardlybump17.vehicles.api.Vehicles;
import com.wizardlybump17.wlib.config.ConfigInfo;
import com.wizardlybump17.wlib.config.Path;
import lombok.experimental.UtilityClass;

@UtilityClass
@ConfigInfo(name = "config.yml", holderType = Vehicles.class)
public class Config {

    @Path("number.precision")
    public static int numberPrecision = 1;
}
