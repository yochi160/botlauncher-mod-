package com.botlauncher;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import com.google.gson.*;

public class BotManager {

    private static Process botProcess = null;
    private static final List<String> logs = new ArrayList<>();
    private static String status = "Hors ligne";
    private static boolean running = false;
    private static BotInventory inventory = new BotInventory();
    private static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

    // Config sauvegardée
    public static String serverIp     = "";
    public static String serverPort   = "25565";
    public static String mcVersion    = "1.21.4";
    public static String playerName   = "";
    public static String mcEmail      = "";
    public static String mcPassword   = "";

    private static Path getBotDir() {
        // Cherche le dossier minecraft-bot dans les emplacements courants
        String[] candidates = {
            System.getProperty("user.home") + "/Desktop/minecraft-bot",
            System.getProperty("user.home") + "/minecraft-bot",
            System.getProperty("user.dir") + "/minecraft-bot",
        };
        for (String c : candidates) {
            if (Files.exists(Paths.get(c, "bot.js"))) {
                return Paths.get(c);
            }
        }
        return Paths.get(System.getProperty("user.home"), "Desktop", "minecraft-bot");
    }

    public static void startBot() {
        if (running) return;

        Path botDir = getBotDir();
        if (!Files.exists(botDir.resolve("bot.js"))) {
            addLog("❌ Dossier minecraft-bot introuvable !");
            addLog("📁 Cherché dans: " + botDir.toString());
            return;
        }

        try {
            // Construit les variables d'environnement
            ProcessBuilder pb = new ProcessBuilder("node", "bot.js");
            pb.directory(botDir.toFile());
            pb.redirectErrorStream(true);

            Map<String, String> env = pb.environment();
            env.put("BOT_SERVER",  serverIp);
            env.put("BOT_PORT",    serverPort);
            env.put("BOT_VERSION", mcVersion);
            env.put("BOT_FOLLOW",  playerName);
            env.put("MC_EMAIL",    mcEmail);
            env.put("MC_PASSWORD", mcPassword);

            botProcess = pb.start();
            running = true;
            status  = "En ligne";
            addLog("✅ Bot démarré !");
            addLog("🌐 Connexion à " + serverIp + ":" + serverPort);

            // Lit les logs du bot en arrière-plan
            scheduler.execute(() -> {
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(botProcess.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        addLog(line);
                    }
                } catch (Exception e) {
                    addLog("⚠️ Fin lecture logs: " + e.getMessage());
                }
                running = false;
                status  = "Déconnecté";
                addLog("❌ Bot arrêté.");
            });

            // Lit l'inventaire périodiquement
            scheduler.scheduleAtFixedRate(BotManager::readInventory, 1, 2, TimeUnit.SECONDS);

        } catch (Exception e) {
            addLog("❌ Erreur démarrage: " + e.getMessage());
            status = "Erreur";
        }
    }

    public static void stopBot() {
        if (botProcess != null) {
            botProcess.destroy();
            botProcess = null;
        }
        running = false;
        status  = "Hors ligne";
        addLog("🛑 Bot arrêté manuellement.");
    }

    public static void sendCommand(String command) {
        if (!running) {
            addLog("⚠️ Bot non connecté !");
            return;
        }
        Path botDir = getBotDir();
        try {
            JsonObject cmd = new JsonObject();
            cmd.addProperty("cmd", command);
            cmd.addProperty("time", System.currentTimeMillis() / 1000.0);
            Files.writeString(botDir.resolve("bot_command.json"), cmd.toString());
            addLog("→ " + command);
        } catch (Exception e) {
            addLog("❌ Erreur envoi commande: " + e.getMessage());
        }
    }

    private static void readInventory() {
        Path botDir = getBotDir();
        try {
            Path invFile = botDir.resolve("bot_inventory.json");
            if (Files.exists(invFile)) {
                String json = Files.readString(invFile);
                JsonObject obj = JsonParser.parseString(json).getAsJsonObject();
                inventory.health = obj.has("health") ? obj.get("health").getAsFloat() : 20;
                inventory.food   = obj.has("food")   ? obj.get("food").getAsFloat()   : 20;
                inventory.task   = obj.has("task")   ? obj.get("task").getAsString()  : "";
                inventory.items.clear();
                if (obj.has("items")) {
                    for (var el : obj.get("items").getAsJsonArray()) {
                        JsonObject item = el.getAsJsonObject();
                        inventory.items.add(new BotInventory.ItemStack(
                            item.get("name").getAsString(),
                            item.get("count").getAsInt()
                        ));
                    }
                }
                // Trie par quantité
                inventory.items.sort((a,b) -> b.count - a.count);
            }
        } catch (Exception ignored) {}
    }

    public static void saveConfig() {
        Path botDir = getBotDir();
        try {
            JsonObject cfg = new JsonObject();
            cfg.addProperty("server_ip",        serverIp);
            cfg.addProperty("server_port",       serverPort);
            cfg.addProperty("mc_version",        mcVersion);
            cfg.addProperty("player_to_follow",  playerName);
            cfg.addProperty("mc_email",          mcEmail);
            cfg.addProperty("mc_password",       mcPassword);
            Files.writeString(botDir.resolve("config.json"), cfg.toString());
        } catch (Exception ignored) {}
    }

    public static void loadConfig() {
        Path botDir = getBotDir();
        try {
            Path cfgFile = botDir.resolve("config.json");
            if (Files.exists(cfgFile)) {
                JsonObject cfg = JsonParser.parseString(Files.readString(cfgFile)).getAsJsonObject();
                if (cfg.has("server_ip"))       serverIp   = cfg.get("server_ip").getAsString();
                if (cfg.has("server_port"))     serverPort = cfg.get("server_port").getAsString();
                if (cfg.has("mc_version"))      mcVersion  = cfg.get("mc_version").getAsString();
                if (cfg.has("player_to_follow"))playerName = cfg.get("player_to_follow").getAsString();
                if (cfg.has("mc_email"))        mcEmail    = cfg.get("mc_email").getAsString();
                if (cfg.has("mc_password"))     mcPassword = cfg.get("mc_password").getAsString();
            }
        } catch (Exception ignored) {}
    }

    private static void addLog(String line) {
        String clean = line.replaceAll("\\[BOT\\]\\s*", "").trim();
        if (clean.isEmpty()) return;
        synchronized (logs) {
            logs.add(clean);
            if (logs.size() > 200) logs.remove(0);
        }
    }

    public static List<String> getLogs()      { return new ArrayList<>(logs); }
    public static String       getStatus()    { return status; }
    public static boolean      isRunning()    { return running; }
    public static BotInventory getInventory() { return inventory; }
}
