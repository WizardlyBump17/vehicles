package com.wizardlybump17.vehicles.api.config;

import com.wizardlybump17.vehicles.api.Vehicles;
import com.wizardlybump17.wlib.config.ConfigInfo;
import lombok.experimental.UtilityClass;

import java.text.DecimalFormat;

@UtilityClass
@ConfigInfo(name = "config.yml", holderType = Vehicles.class)
public class Config {

    public static final DecimalFormat FORMAT = new DecimalFormat("#.###");

    static {
        FORMAT.setMinimumFractionDigits(3);
    }
}
