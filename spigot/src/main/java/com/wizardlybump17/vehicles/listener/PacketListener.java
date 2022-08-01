package com.wizardlybump17.vehicles.listener;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.wizardlybump17.vehicles.Vehicles;
import com.wizardlybump17.vehicles.api.cache.VehicleCache;
import com.wizardlybump17.vehicles.api.listener.KeyListener;
import com.wizardlybump17.vehicles.api.vehicle.Vehicle;
import net.minecraft.network.protocol.game.PacketPlayInSteerVehicle;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Optional;

public class PacketListener extends PacketAdapter {

    private final VehicleCache cache;

    public PacketListener(Vehicles plugin) {
        super(plugin, PacketType.fromClass(PacketPlayInSteerVehicle.class));
        this.cache = plugin.getVehicleCache();
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
        Player player = event.getPlayer();
        Optional<Vehicle<?>> optional = cache.get(player, true);
        if (optional.isEmpty())
            return;

        Vehicle<?> vehicle = optional.get();
        PacketPlayInSteerVehicle packet = (PacketPlayInSteerVehicle) event.getPacket().getHandle();

        Bukkit.getScheduler().runTask(plugin, () -> { //we receive the packets async
            for (KeyListener listener : vehicle.getKeyListeners())
                listener.handle(player, packet.b(), packet.c(), packet.d(), packet.e());

            vehicle.move(player, packet.b(), packet.c());
            vehicle.rotate(player, packet.b(), packet.c());
            if (packet.d())
                vehicle.jump(player, packet.b(), packet.c());
            if (packet.e())
                vehicle.shift(player, packet.b(), packet.c());
        });
    }
}
