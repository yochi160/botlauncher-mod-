package com.botlauncher;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

import java.util.List;

public class BotLauncherScreen extends Screen {

    private final Screen parent;
    private int currentTab = 0; // 0=Serveur, 1=Compte, 2=Contrôle, 3=Inventaire, 4=Logs

    // Onglet Serveur
    private TextFieldWidget ipField;
    private TextFieldWidget portField;
    private TextFieldWidget versionField;

    // Onglet Compte
    private TextFieldWidget playerField;
    private TextFieldWidget emailField;
    private TextFieldWidget passwordField;

    // Onglet Contrôle - message custom
    private TextFieldWidget customCmdField;

    // Bouton principal
    private ButtonWidget launchBtn;

    // Scroll logs
    private int logScroll = 0;

    private static final int[] TAB_COLORS = {
        0xFF2a6496, // Serveur - bleu
        0xFF5c3a7a, // Compte - violet
        0xFF3a7a3a, // Contrôle - vert
        0xFF7a5a2a, // Inventaire - orange
        0xFF2a2a2a, // Logs - gris
    };

    public BotLauncherScreen(Screen parent) {
        super(Text.literal("🤖 Minecraft AI Bot Launcher"));
        this.parent = parent;
        BotManager.loadConfig();
    }

    @Override
    protected void init() {
        int W = this.width;
        int H = this.height;

        // ── Onglets ──
        String[] tabs = {"Serveur", "Compte", "Contrôle", "Inventaire", "Logs"};
        int tabW = (W - 20) / tabs.length;
        for (int i = 0; i < tabs.length; i++) {
            final int idx = i;
            this.addDrawableChild(ButtonWidget.builder(
                Text.literal(tabs[i]),
                btn -> { currentTab = idx; rebuildWidgets(); }
            ).dimensions(10 + i * tabW, 30, tabW - 2, 20).build());
        }

        // ── Bouton Lancer/Arrêter ──
        launchBtn = ButtonWidget.builder(
            Text.literal(BotManager.isRunning() ? "⏹ ARRÊTER" : "▶ LANCER"),
            btn -> {
                if (BotManager.isRunning()) {
                    BotManager.stopBot();
                } else {
                    BotManager.saveConfig();
                    BotManager.startBot();
                }
                rebuildWidgets();
            }
        ).dimensions(W/2 - 80, H - 30, 160, 20).build();
        this.addDrawableChild(launchBtn);

        // ── Contenu selon l'onglet actif ──
        buildTabContent();
    }

    private void rebuildWidgets() {
        this.clearChildren();
        this.init();
    }

    private void buildTabContent() {
        int W = this.width;
        int startY = 58;

        switch (currentTab) {
            case 0 -> buildServerTab(startY, W);
            case 1 -> buildAccountTab(startY, W);
            case 2 -> buildControlTab(startY, W);
            case 3 -> {} // Inventaire - dessiné dans render()
            case 4 -> {} // Logs - dessiné dans render()
        }
    }

    private void buildServerTab(int y, int W) {
        ipField = new TextFieldWidget(this.textRenderer, W/2 - 120, y + 20, 240, 16, Text.literal("IP"));
        ipField.setText(BotManager.serverIp);
        ipField.setMaxLength(100);
        this.addDrawableChild(ipField);

        portField = new TextFieldWidget(this.textRenderer, W/2 - 120, y + 55, 100, 16, Text.literal("Port"));
        portField.setText(BotManager.serverPort);
        this.addDrawableChild(portField);

        versionField = new TextFieldWidget(this.textRenderer, W/2 - 120, y + 90, 240, 16, Text.literal("Version"));
        versionField.setText(BotManager.mcVersion);
        this.addDrawableChild(versionField);

        this.addDrawableChild(ButtonWidget.builder(Text.literal("💾 Sauvegarder"), btn -> {
            BotManager.serverIp   = ipField.getText();
            BotManager.serverPort = portField.getText();
            BotManager.mcVersion  = versionField.getText();
            BotManager.saveConfig();
        }).dimensions(W/2 - 60, y + 120, 120, 20).build());
    }

    private void buildAccountTab(int y, int W) {
        playerField = new TextFieldWidget(this.textRenderer, W/2 - 120, y + 20, 240, 16, Text.literal("Pseudo"));
        playerField.setText(BotManager.playerName);
        this.addDrawableChild(playerField);

        emailField = new TextFieldWidget(this.textRenderer, W/2 - 120, y + 55, 240, 16, Text.literal("Email"));
        emailField.setText(BotManager.mcEmail);
        this.addDrawableChild(emailField);

        passwordField = new TextFieldWidget(this.textRenderer, W/2 - 120, y + 90, 240, 16, Text.literal("Mot de passe"));
        passwordField.setText(BotManager.mcPassword);
        passwordField.setRenderTextProvider((s, i) -> Text.literal("*".repeat(s.length())));
        this.addDrawableChild(passwordField);

        this.addDrawableChild(ButtonWidget.builder(Text.literal("💾 Sauvegarder"), btn -> {
            BotManager.playerName  = playerField.getText();
            BotManager.mcEmail     = emailField.getText();
            BotManager.mcPassword  = passwordField.getText();
            BotManager.saveConfig();
        }).dimensions(W/2 - 60, y + 120, 120, 20).build());
    }

    private void buildControlTab(int y, int W) {
        // Commandes rapides en grille
        String[][] cmds = {
            {"🔴 STOP",     "!stop"},
            {"👣 Suivre",   "!suis"},
            {"🤖 Auto",     "!seul"},
            {"📍 Viens",    "!viens"},
            {"🛡 Armure",   "!armure"},
            {"🪚 Craft",    "!craft"},
            {"📦 Coffre",   "!coffre"},
            {"🌲 Arbres",   "!arbres"},
            {"⛏ Miner",    "!mine"},
            {"🐄 Chasser",  "!chasse"},
            {"🏠 Maison",   "!maison"},
            {"📚 Apprend",  "!apprend"},
            {"✅ C'est bon","!c'est bon"},
            {"📊 Stats",    "!stats"},
        };

        int btnW = (W - 20) / 4;
        for (int i = 0; i < cmds.length; i++) {
            final String cmd = cmds[i][1];
            int col = i % 4;
            int row = i / 4;
            this.addDrawableChild(ButtonWidget.builder(
                Text.literal(cmds[i][0]),
                btn -> BotManager.sendCommand(cmd)
            ).dimensions(10 + col * btnW, y + row * 24, btnW - 4, 20).build());
        }

        // Champ message custom
        customCmdField = new TextFieldWidget(this.textRenderer, 10, y + 100, W - 100, 16, Text.literal("Message..."));
        this.addDrawableChild(customCmdField);

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Envoyer ↵"), btn -> {
            String msg = customCmdField.getText().trim();
            if (!msg.isEmpty()) {
                BotManager.sendCommand(msg);
                customCmdField.setText("");
            }
        }).dimensions(W - 86, y + 100, 76, 16).build());
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        // Fond semi-transparent
        ctx.fill(0, 0, this.width, this.height, 0xCC111122);

        // Titre
        ctx.drawCenteredTextWithShadow(this.textRenderer,
            Text.literal("⛏ MINECRAFT AI BOT LAUNCHER"), this.width / 2, 10, 0xFF4ecca3);

        // Statut
        String statusText = "● " + BotManager.getStatus();
        int statusColor = BotManager.isRunning() ? 0xFF4ecca3 : 0xFFe94560;
        ctx.drawTextWithShadow(this.textRenderer, Text.literal(statusText),
            this.width - 120, 12, statusColor);

        super.render(ctx, mouseX, mouseY, delta);

        // Contenu spécial des onglets (dessiné par-dessus les widgets)
        int startY = 58;
        switch (currentTab) {
            case 0 -> renderServerTab(ctx, startY);
            case 1 -> renderAccountTab(ctx, startY);
            case 3 -> renderInventoryTab(ctx, startY);
            case 4 -> renderLogsTab(ctx, startY);
        }
    }

    private void renderServerTab(DrawContext ctx, int y) {
        ctx.drawTextWithShadow(this.textRenderer, Text.literal("IP du serveur Aternos :"),
            this.width/2 - 120, y + 8, 0xFFaaaaaa);
        ctx.drawTextWithShadow(this.textRenderer, Text.literal("Port :"),
            this.width/2 - 120, y + 43, 0xFFaaaaaa);
        ctx.drawTextWithShadow(this.textRenderer, Text.literal("Version Minecraft :"),
            this.width/2 - 120, y + 78, 0xFFaaaaaa);
    }

    private void renderAccountTab(DrawContext ctx, int y) {
        ctx.drawTextWithShadow(this.textRenderer, Text.literal("Ton pseudo Minecraft :"),
            this.width/2 - 120, y + 8, 0xFFaaaaaa);
        ctx.drawTextWithShadow(this.textRenderer, Text.literal("Email Microsoft (2ème compte) :"),
            this.width/2 - 120, y + 43, 0xFFaaaaaa);
        ctx.drawTextWithShadow(this.textRenderer, Text.literal("Mot de passe :"),
            this.width/2 - 120, y + 78, 0xFFaaaaaa);
    }

    private void renderInventoryTab(DrawContext ctx, int y) {
        BotInventory inv = BotManager.getInventory();
        int W = this.width;

        // Stats
        ctx.drawTextWithShadow(this.textRenderer,
            Text.literal("❤ " + (int)inv.health + "/20   🍖 " + (int)inv.food + "/20"),
            10, y, 0xFFff6666);
        ctx.drawTextWithShadow(this.textRenderer,
            Text.literal("🔧 " + inv.task), 10, y + 12, 0xFFaaaaaa);

        // Séparateur
        ctx.fill(10, y + 25, W - 10, y + 26, 0xFF334455);

        ctx.drawTextWithShadow(this.textRenderer, Text.literal("🎒 Inventaire :"),
            10, y + 30, 0xFF4ecca3);

        // Items en 2 colonnes
        int col = 0, row = 0;
        int itemW = (W - 20) / 2;
        for (BotInventory.ItemStack item : inv.items) {
            int ix = 10 + col * itemW;
            int iy = y + 44 + row * 14;
            if (iy > this.height - 40) break;

            String line = item.getIcon() + " " + item.getDisplayName() + " x" + item.count;
            ctx.drawTextWithShadow(this.textRenderer, Text.literal(line), ix, iy, 0xFFdddddd);

            col++;
            if (col >= 2) { col = 0; row++; }
        }

        if (inv.items.isEmpty()) {
            ctx.drawCenteredTextWithShadow(this.textRenderer,
                Text.literal("Bot hors ligne ou inventaire vide"),
                W/2, y + 60, 0xFF888888);
        }
    }

    private void renderLogsTab(DrawContext ctx, int y) {
        int W = this.width;
        List<String> logs = BotManager.getLogs();
        int maxLines = (this.height - y - 50) / 10;
        int start = Math.max(0, logs.size() - maxLines - logScroll);
        int end   = Math.min(logs.size(), start + maxLines);

        ctx.fill(5, y, W - 5, this.height - 40, 0xFF0a0a15);

        for (int i = start; i < end; i++) {
            String line = logs.get(i);
            int color = 0xFFcccccc;
            if (line.contains("✅") || line.contains("Connecté")) color = 0xFF4ecca3;
            if (line.contains("❌") || line.contains("Erreur"))   color = 0xFFe94560;
            if (line.contains("→"))                               color = 0xFF89ddff;
            if (line.contains("[AUTO]"))                          color = 0xFFffcb6b;

            ctx.drawTextWithShadow(this.textRenderer,
                Text.literal(line.length() > 80 ? line.substring(0, 77) + "..." : line),
                10, y + 4 + (i - start) * 10, color);
        }

        // Scroll auto vers le bas
        if (logScroll == 0) {
            ctx.drawTextWithShadow(this.textRenderer,
                Text.literal("▼ Scroll: molette souris"), W - 120, this.height - 48, 0xFF555555);
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (currentTab == 4) {
            logScroll = Math.max(0, logScroll - (int)verticalAmount);
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // Ferme avec Echap ou B
        if (keyCode == 256 || keyCode == 66) { // ESCAPE ou B
            this.close();
            return true;
        }
        // Envoie avec Entrée dans le champ custom
        if (keyCode == 257 && currentTab == 2 && customCmdField != null && customCmdField.isFocused()) {
            String msg = customCmdField.getText().trim();
            if (!msg.isEmpty()) {
                BotManager.sendCommand(msg);
                customCmdField.setText("");
            }
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void close() {
        this.client.setScreen(parent);
    }

    @Override
    public boolean shouldPause() {
        return false; // Ne met pas le jeu en pause
    }
}
