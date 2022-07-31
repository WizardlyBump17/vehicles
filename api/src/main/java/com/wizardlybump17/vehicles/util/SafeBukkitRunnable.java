package com.wizardlybump17.vehicles.util;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public abstract class SafeBukkitRunnable extends BukkitRunnable {

    public boolean isScheduled() {
        try {
            Field field = getClass().getSuperclass().getSuperclass().getDeclaredField("task");
            field.setAccessible(true);
            return field.get(this) != null;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public synchronized void cancel() throws IllegalStateException {
        if (isScheduled())
            super.cancel();
    }

    @NotNull
    @Override
    public synchronized BukkitTask runTask(@NotNull Plugin plugin) throws IllegalArgumentException, IllegalStateException {
        cancel();
        return setupTask(Bukkit.getScheduler().runTask(plugin, (Runnable) this));
    }

    @NotNull
    @Override
    public synchronized BukkitTask runTaskAsynchronously(@NotNull Plugin plugin) throws IllegalArgumentException, IllegalStateException {
        cancel();
        return setupTask(Bukkit.getScheduler().runTaskAsynchronously(plugin, (Runnable) this));
    }

    @NotNull
    @Override
    public synchronized BukkitTask runTaskLater(@NotNull Plugin plugin, long delay) throws IllegalArgumentException, IllegalStateException {
        cancel();
        return setupTask(Bukkit.getScheduler().runTaskLater(plugin, (Runnable) this, delay));
    }

    @NotNull
    @Override
    public synchronized BukkitTask runTaskLaterAsynchronously(@NotNull Plugin plugin, long delay) throws IllegalArgumentException, IllegalStateException {
        cancel();
        return setupTask(Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, (Runnable) this, delay));
    }

    @NotNull
    @Override
    public synchronized BukkitTask runTaskTimer(@NotNull Plugin plugin, long delay, long period) throws IllegalArgumentException, IllegalStateException {
        cancel();
        return setupTask(Bukkit.getScheduler().runTaskTimer(plugin, (Runnable) this, delay, period));
    }

    @NotNull
    @Override
    public synchronized BukkitTask runTaskTimerAsynchronously(@NotNull Plugin plugin, long delay, long period) throws IllegalArgumentException, IllegalStateException {
        cancel();
        return setupTask(Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, (Runnable) this, delay, period));
    }

    private BukkitTask setupTask(BukkitTask task) {
        try {
            Method method = getClass().getSuperclass().getSuperclass().getDeclaredMethod("setupTask", BukkitTask.class);
            method.setAccessible(true);
            return (BukkitTask) method.invoke(this, task);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException(e);
        }
    }
}
