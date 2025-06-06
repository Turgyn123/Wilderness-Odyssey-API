package com.thunder.wildernessodysseyapi.Cloak;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

import static com.thunder.wildernessodysseyapi.WildernessOdysseyAPIMainModClass.MOD_ID;

@EventBusSubscriber(modid = MOD_ID)
public class CloakToggleHandler {

    private static boolean wasKeyPressed = false;

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        // The usual null check is fine:
        if (mc.player == null) return;

        Player player = mc.player;

        // Check if the off-hand swap key (default: F) is pressed
        boolean isKeyPressed = mc.options.keySwapOffhand.isDown();

        if (isKeyPressed && !wasKeyPressed) {
            ItemStack offHandItem = player.getOffhandItem();

            // Check if the player has an Amethyst Shard in the off-hand
            if (offHandItem.getItem() == Items.AMETHYST_SHARD) {
                player.getCapability(MOD_ID.CLOAK_CAPABILITY).ifPresent(cloak -> {
                    boolean newState = !cloak.isCloakEnabled();
                    cloak.setCloakEnabled(newState);

                    // Send feedback message
                    player.sendSystemMessage(Component.literal("Cloak " + (newState ? "enabled!" : "disabled!"))
                            .withStyle(style -> style.withColor(newState ?
                                    TextColor.fromRgb(0x55FFFF) : // AQUA
                                    TextColor.fromRgb(0xFF5555)   // RED
                            )));

                    // Play a sound effect
                    player.level().playSound(null, player.blockPosition(), SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 1.0F, 1.0F);

                    // Consume one Amethyst Shard (unless in Creative mode)
                    if (!player.isCreative()) {
                        offHandItem.shrink(1);
                    }
                });
            }
        }

        // Update key state tracking
        wasKeyPressed = isKeyPressed;
    }
}