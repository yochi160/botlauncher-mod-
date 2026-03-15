# Bot Launcher Mod - Fabric 1.21.4

Mod Fabric qui intègre le launcher du bot directement dans Minecraft.

## Installation

### Prérequis
- Minecraft 1.21.4 avec Fabric Loader 0.16+
- Fabric API
- Java 21+
- Gradle 8+ (pour compiler)

### Compiler le mod

1. Ouvre un terminal dans ce dossier
2. Télécharge le wrapper Gradle :
   ```
   gradle wrapper --gradle-version 8.8
   ```
3. Compile :
   ```
   gradlew build
   ```
4. Le fichier `.jar` se trouve dans `build/libs/botlauncher-1.0.0.jar`

### Installer le mod

1. Copie `botlauncher-1.0.0.jar` dans ton dossier `.minecraft/mods/`
2. Lance Minecraft avec le profil Fabric 1.21.4

## Utilisation

- **Appuie sur `B`** en jeu → Le launcher s'ouvre
- La touche est configurable dans **Options → Contrôles → Bot Launcher**

## Onglets du launcher

### 🌐 Serveur
- IP du serveur Aternos
- Port (défaut 25565)
- Version Minecraft

### 🤖 Compte
- Ton pseudo Minecraft
- Email + Mot de passe du 2ème compte

### 🎮 Contrôle
Boutons rapides :
- 🔴 STOP, 👣 Suivre, 🤖 Auto, 📍 Viens
- 🛡 Armure, 🪚 Craft, 📦 Coffre
- 🌲 Couper bois, ⛏ Miner, 🐄 Chasser
- 🏠 Maison, 📚 Apprendre, ✅ C'est bon, 📊 Stats
- Champ message personnalisé

### 🎒 Inventaire
- Vie et faim en temps réel
- Tâche en cours
- Tous les items du bot

### 📋 Logs
- Journal en temps réel de tout ce que fait le bot
- Scroll avec la molette

## Structure dossier

Le mod cherche le dossier `minecraft-bot` dans :
1. `Bureau/minecraft-bot`
2. `Documents/minecraft-bot`
3. Dossier courant

## Notes

- Le mod ne met PAS le jeu en pause quand ouvert
- Fonctionne en multijoueur et solo
- Les paramètres sont sauvegardés dans `config.json` du dossier bot
