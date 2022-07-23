package com.wizardlybump17.vehicles.command.reader;

import com.wizardlybump17.vehicles.api.cache.VehicleModelCache;
import com.wizardlybump17.vehicles.api.model.VehicleModel;
import com.wizardlybump17.wlib.command.args.reader.ArgsReader;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class VehicleModelArgsReader extends ArgsReader<VehicleModel> {

    private final VehicleModelCache cache;

    @Override
    public Class<VehicleModel> getType() {
        return VehicleModel.class;
    }

    @Override
    public VehicleModel<?> read(String s) {
        return cache.get(s).orElse(null);
    }
}
