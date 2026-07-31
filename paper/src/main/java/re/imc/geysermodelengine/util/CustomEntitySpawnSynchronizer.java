package re.imc.geysermodelengine.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import re.imc.geysermodelengine.GeyserModelEngine;
import re.imc.geysermodelengine.runnables.SendAndSpawnRunnable;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public final class CustomEntitySpawnSynchronizer {

    public static void sendAndSpawn(GeyserModelEngine plugin, Player player, int entityId, String identifier, int requestedDelay, Runnable spawnAction) {
        long syncDelay = Math.max(requestedDelay, plugin.getConfigManager().getConfig().getInt("models.custom-entity-sync-delay", 150));
        long resendInterval = Math.max(1L, plugin.getConfigManager().getConfig().getInt("models.custom-entity-sync-resend-interval", 40));
        int resendCount = Math.max(1, plugin.getConfigManager().getConfig().getInt("models.custom-entity-sync-resend-count", 3));

        Bukkit.getGlobalRegionScheduler().run(plugin, new SendAndSpawnRunnable(player, entityId, identifier));

        Set<Long> scheduled = new HashSet<>();
        for (int i = 1; i < resendCount; i++) {
            long resendDelay = Math.min(syncDelay - 1L, resendInterval * i);
            if (resendDelay <= 0L || !scheduled.add(resendDelay)) {
                continue;
            }

            plugin.getSchedulerPool().schedule(() -> Bukkit.getGlobalRegionScheduler().run(plugin, new SendAndSpawnRunnable(player, entityId, identifier)), resendDelay, TimeUnit.MILLISECONDS);
        }

        plugin.getSchedulerPool().schedule(() -> {
            if (player.isOnline()) {
                spawnAction.run();
            }
        }, syncDelay, TimeUnit.MILLISECONDS);
    }
}
