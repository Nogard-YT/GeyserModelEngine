package re.imc.geysermodelengine.runnables;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import me.zimzaza4.geyserutils.spigot.api.EntityUtils;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

public class SendAndSpawnRunnable implements Consumer<ScheduledTask> {

    private final Player player;
    private final int entityID;
    private final String identifier;

    public SendAndSpawnRunnable(Player player, int entityID, String identifier) {
        this.player = player;
        this.entityID = entityID;
        this.identifier = identifier;
    }

    @Override
    public void accept(ScheduledTask scheduledTask) {
        if (player.isOnline()) {
            EntityUtils.setCustomEntity(player, entityID, identifier);
        }
    }
}
