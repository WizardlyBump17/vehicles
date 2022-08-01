package com.wizardlybump17.vehicles;

import com.comphenix.protocol.ProtocolLibrary;
import com.wizardlybump17.vehicles.api.config.Messages;
import com.wizardlybump17.vehicles.api.model.VehicleModel;
import com.wizardlybump17.vehicles.api.model.airplane.AirplaneModel;
import com.wizardlybump17.vehicles.api.model.airplane.military.MilitaryAirplaneModel;
import com.wizardlybump17.vehicles.api.model.car.CarModel;
import com.wizardlybump17.vehicles.api.model.info.SpeedInfo;
import com.wizardlybump17.vehicles.api.model.info.TNTInfo;
import com.wizardlybump17.vehicles.api.model.info.airplane.FallSpeedInfo;
import com.wizardlybump17.vehicles.api.model.motorcycle.MotorcycleModel;
import com.wizardlybump17.vehicles.api.vehicle.Vehicle;
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
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;

public class Vehicles extends com.wizardlybump17.vehicles.api.Vehicles {

    private boolean dependenciesLoaded = true;

    @Override
    public void onEnable() {
        if (!checkDependencies())
            return;

        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketListener(this));

        initConfigSerializables();
        initConfigs();
        initListeners();

        ArgsReaderRegistry.INSTANCE.add(new VehicleModelArgsReader(getVehicleModelCache()));
        new CommandManager(new BukkitCommandHolder(this)).registerCommands(new VehicleCommand(this));

        reloadModels();
        initVehicles();

        getCheckVehiclesTask().runTaskTimerAsynchronously(this, 1, 1);
    }

    @Override
    public void onDisable() {
        getCheckVehiclesTask().cancel();
        if (!dependenciesLoaded)
            getLogger().severe("§c§lDependencies were not found.");
    }

    private boolean checkDependencies() {
        if (!checkPlugin("WLib") || !checkPlugin("ProtocolLib") || !checkPlugin("ModelEngine")) {
            dependenciesLoaded = false;
            Bukkit.getPluginManager().disablePlugin(this);
            return false;
        }
        return true;
    }

    private boolean checkPlugin(String name) {
        Plugin plugin = Bukkit.getPluginManager().getPlugin(name);
        if (!Bukkit.getPluginManager().isPluginEnabled(plugin)) {
            getLogger().severe("§c§l" + name + "§c is not installed or not enabled!");
            return false;
        }
        return true;
    }

    private void initConfigs() {
        ConfigHolderFactoryRegistry.getInstance().put(com.wizardlybump17.vehicles.api.Vehicles.class, new BukkitConfigHolderFactory(this));

        ConfigHandlerRegistry.getInstance().register(Messages.class);
        ConfigHandlerRegistry.getInstance().register(com.wizardlybump17.vehicles.api.config.Config.class);
    }

    private void initVehicles() {
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                Vehicle<?> vehicle = Vehicle.createVehicle(entity, getVehicleModelCache());
                if (vehicle != null)
                    getVehicleCache().add(vehicle);
            }
        }
    }

    private void initListeners() {
        Bukkit.getPluginManager().registerEvents(new PlayerListener(this), this);
        Bukkit.getPluginManager().registerEvents(new VehicleListener(getVehicleCache(), getVehicleModelCache()), this);
    }

    private void initConfigSerializables() {
        ConfigurationSerialization.registerClass(CarModel.class);
        ConfigurationSerialization.registerClass(MotorcycleModel.class);
        ConfigurationSerialization.registerClass(AirplaneModel.class);
        ConfigurationSerialization.registerClass(MilitaryAirplaneModel.class);
        ConfigurationSerialization.registerClass(TNTInfo.class);
        ConfigurationSerialization.registerClass(FallSpeedInfo.class);
        ConfigurationSerialization.registerClass(SpeedInfo.class);
    }

    public void reloadModels() {
        getVehicleModelCache().clear();

        for (Object o : Config.load("models.yml", this).getList("models", new ArrayList<>()))
            getVehicleModelCache().add((VehicleModel<?>) o);
    }
}
