package com.thunder.wildernessodysseyapi.WorldVersionChecker;

import com.thunder.wildernessodysseyapi.Core.ModConstants;
import com.thunder.wildernessodysseyapi.Core.WildernessOdysseyAPINetworkHandler;
import com.thunder.wildernessodysseyapi.WorldVersionChecker.Packet.SyncWorldVersionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import static com.thunder.wildernessodysseyapi.Core.ModConstants.MOD_ID;

@EventBusSubscriber(modid = MOD_ID)
public class SyncWorldVersionOnJoin {
    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        var level   = player.serverLevel();
        var storage = level.getDataStorage();
        var data    = storage.computeIfAbsent(
                WorldVersionData.factory(),
                WorldVersionData.FILE_NAME
        );

        int current  = ModConstants.CURRENT_WORLD_VERSION;
        int worldVer = data.getVersion();

        if (worldVer < current) {
            WildernessOdysseyAPINetworkHandler.sendTo(player, new SyncWorldVersionPacket(worldVer));
        }
    }

}
