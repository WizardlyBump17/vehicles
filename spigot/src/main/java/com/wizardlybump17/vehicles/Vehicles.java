package com.wizardlybump17.vehicles;

import com.comphenix.protocol.ProtocolLibrary;
import com.wizardlybump17.vehicles.api.config.Messages;
import com.wizardlybump17.vehicles.api.model.CarModel;
import com.wizardlybump17.vehicles.api.model.VehicleModel;
import com.wizardlybump17.vehicles.command.VehicleCommand;
import com.wizardlybump17.vehicles.command.reader.VehicleModelArgsReader;
import com.wizardlybump17.vehicles.listener.PacketListener;
import com.wizardlybump17.vehicles.listener.PlayerListener;
import com.wizardlybump17.vehicles.listener.VehicleListener;
import com.wizardlybump17.wlib.command.CommandManager;
import com.wizardlybump17.wlib.command.args.ArgsReaderRegistry;
import com.wizardlybump17.wlib.command.holder.BukkitCommandHolder;
import com.wizardlybump17.wlib.config.Config;
import com.wizardlybump17.wlib.config.holder.BukkitConfigHolderFactory;
import com.wizardlybump17.wlib.config.registry.ConfigHandlerRegistry;
import com.wizardlybump17.wlib.config.registry.ConfigHolderFactoryRegistry;
import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

import java.util.ArrayList;

public class Vehicles extends com.wizardlybump17.vehicles.api.Vehicles {

    @Override
    public void onEnable() {
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketListener(this));

        initConfigs();
        initVehicles();
        initListeners();
        initConfigSerializables();

        ArgsReaderRegistry.INSTANCE.add(new VehicleModelArgsReader(getVehicleModelCache()));
        new CommandManager(new BukkitCommandHolder(this)).registerCommands(new VehicleCommand(this));

        reloadModels();
    }

    private void initConfigs() {
        ConfigHolderFactoryRegistry.getInstance().put(com.wizardlybump17.vehicles.api.Vehicles.class, new BukkitConfigHolderFactory(this));

        ConfigHandlerRegistry.getInstance().register(Messages.class);
    }

    private void initVehicles() { //TODO load from database
    }

    private void initListeners() {
        Bukkit.getPluginManager().registerEvents(new PlayerListener(this), this);
        Bukkit.getPluginManager().registerEvents(new VehicleListener(getVehicleCache()), this);
    }

    private void initConfigSerializables() {
        ConfigurationSerialization.registerClass(CarModel.class);
    }

    public void reloadModels() {
        getVehicleModelCache().clear();

        for (Object o : Config.load("models.yml", this).getList("models", new ArrayList<>()))
            getVehicleModelCache().add((VehicleModel<?>) o);
    }
}
