package fr.skytasul.music.utils;

import io.papermc.paper.threadedregions.scheduler.AsyncScheduler;
import io.papermc.paper.threadedregions.scheduler.EntityScheduler;
import io.papermc.paper.threadedregions.scheduler.GlobalRegionScheduler;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.concurrent.TimeUnit;

public final class FoliaCompat {

    private static final boolean FOLIA;

    static {
        boolean folia = false;
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            folia = true;
        } catch (ClassNotFoundException e) {
        }
        FOLIA = folia;
    }

    private FoliaCompat() {}

    public static boolean isFolia() {
        return FOLIA;
    }

    public static void runAtLocation(Player player, Plugin plugin, Runnable task) {
        if (FOLIA) {
            player.getScheduler().run(plugin, scheduledTask -> task.run(), null);
        } else {
            Bukkit.getScheduler().runTask(plugin, task);
        }
    }

    public static void runAtLocationDelayed(Player player, Plugin plugin, Runnable task, long delayTicks) {
        if (FOLIA) {
            player.getScheduler().runDelayed(plugin, scheduledTask -> task.run(), null, delayTicks);
        } else {
            Bukkit.getScheduler().runTaskLater(plugin, task, delayTicks);
        }
    }

    public static void runAsync(Plugin plugin, Runnable task) {
        if (FOLIA) {
            Bukkit.getServer().getAsyncScheduler().runNow(plugin, scheduledTask -> task.run());
        } else {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, task);
        }
    }

    public static Object runAsyncRepeating(Plugin plugin, Runnable task, long initialDelayTicks, long periodTicks) {
        if (FOLIA) {
            long initialMillis = initialDelayTicks * 50;
            long periodMillis = periodTicks * 50;
            return Bukkit.getServer().getAsyncScheduler().runAtFixedRate(plugin,
                    scheduledTask -> task.run(), initialMillis, periodMillis, TimeUnit.MILLISECONDS);
        } else {
            return Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, task, initialDelayTicks, periodTicks);
        }
    }

    public static void runGlobal(Plugin plugin, Runnable task) {
        if (FOLIA) {
            Bukkit.getServer().getGlobalRegionScheduler().run(plugin, scheduledTask -> task.run());
        } else {
            Bukkit.getScheduler().runTask(plugin, task);
        }
    }

    public static Object runGlobalRepeating(Plugin plugin, Runnable task, long initialDelayTicks, long periodTicks) {
        if (FOLIA) {
            return Bukkit.getServer().getGlobalRegionScheduler().runAtFixedRate(plugin,
                    scheduledTask -> task.run(), initialDelayTicks, periodTicks);
        } else {
            return Bukkit.getScheduler().runTaskTimer(plugin, task, initialDelayTicks, periodTicks);
        }
    }

    public static void cancelTask(Object taskHandle) {
        if (taskHandle == null) return;
        if (taskHandle instanceof org.bukkit.scheduler.BukkitTask) {
            ((org.bukkit.scheduler.BukkitTask) taskHandle).cancel();
        } else if (FOLIA && taskHandle instanceof ScheduledTask) {
            ((ScheduledTask) taskHandle).cancel();
        }
    }

    public static void cancelAsyncTask(Object taskHandle) {
        cancelTask(taskHandle);
    }
}
