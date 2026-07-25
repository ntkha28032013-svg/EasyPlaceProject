```java
package com.mod;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.play.*;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.item.BlockItem;

public class EasyPlace implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || client.world == null) return;
            
            // Tự động chuyển hotbar nếu tay không cầm block
            if (!(client.player.getMainHandStack().getItem() instanceof BlockItem)) {
                for (int i = 0; i < 9; i++) {
                    if (client.player.getInventory().getStack(i).getItem() instanceof BlockItem) {
                        client.player.getInventory().selectedSlot = i;
                        client.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(i));
                        break;
                    }
                }
            }

            // EasyPlace (Giới hạn 10 ticks để né Anticheat)
            if (client.crosshairTarget instanceof BlockHitResult && client.world.getTime() % 10 == 0) {
                client.getNetworkHandler().sendPacket(new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, (BlockHitResult) client.crosshairTarget, 0));
            }
        });
    }
}