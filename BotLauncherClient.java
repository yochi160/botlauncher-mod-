package com.botlauncher;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;

public class BotLauncherClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        // Vérifie la touche à chaque tick
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (BotLauncherMod.openLauncherKey.wasPressed()) {
                // Ouvre l'écran du launcher
                client.setScreen(new BotLauncherScreen(client.currentScreen));
            }
        });

        BotLauncherMod.LOGGER.info("Client Bot Launcher initialisé !");
    }
}
