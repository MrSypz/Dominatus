package sypztep.dominatus.common.event;

import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import sypztep.dominatus.Dominatus;
import sypztep.dominatus.client.payload.SyncAttackDamagePayloadS2C;
import sypztep.dominatus.common.util.gemsystem.GemManagerHelper;

public class RestoreGemPowerEvent implements ServerPlayerEvents.AfterRespawn {

    @Override
    public void afterRespawn(ServerPlayerEntity oldPlayer, ServerPlayerEntity newPlayer, boolean alive) {
        Dominatus.LOGGER.info("Player respawned. Restoring gem powers for {}", newPlayer.getName().getString());
        GemManagerHelper.updateEntityStats(newPlayer);
        newPlayer.setHealth(newPlayer.getMaxHealth());
        SyncAttackDamagePayloadS2C.send(newPlayer);
    }
}
