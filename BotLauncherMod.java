package com.botlauncher;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BotLauncherMod implements ModInitializer {
    public static final String MOD_ID = "botlauncher";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    // Touche pour ouvrir le launcher (défaut: B)
    public static KeyBinding openLauncherKey;

    @Override
    public void onInitialize() {
        LOGGER.info("Bot Launcher Mod chargé !");

        // Enregistre la touche clavier
        openLauncherKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.botlauncher.open",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_B,
            "category.botlauncher"
        ));

        LOGGER.info("Touche enregistrée : B (peut être changée dans Contrôles)");
    }
}
