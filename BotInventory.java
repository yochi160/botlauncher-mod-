package com.botlauncher;

import java.util.ArrayList;
import java.util.List;

public class BotInventory {
    public float health = 20;
    public float food   = 20;
    public String task  = "";
    public List<ItemStack> items = new ArrayList<>();

    public static class ItemStack {
        public String name;
        public int    count;
        public ItemStack(String name, int count) {
            this.name  = name;
            this.count = count;
        }
        public String getDisplayName() {
            return name.replace("_", " ");
        }
        public String getIcon() {
            if (name.contains("sword"))    return "⚔";
            if (name.contains("pickaxe"))  return "⛏";
            if (name.contains("axe"))      return "🪓";
            if (name.contains("shovel"))   return "🪣";
            if (name.contains("helmet"))   return "⛑";
            if (name.contains("chestplate"))return "🥋";
            if (name.contains("leggings")) return "👖";
            if (name.contains("boots"))    return "👟";
            if (name.contains("coal"))     return "⬛";
            if (name.contains("iron"))     return "🔩";
            if (name.contains("gold"))     return "🟡";
            if (name.contains("diamond"))  return "💎";
            if (name.contains("log"))      return "🪵";
            if (name.contains("planks"))   return "🪵";
            if (name.contains("stone"))    return "🪨";
            if (name.contains("cobble"))   return "🪨";
            if (name.contains("bread"))    return "🍞";
            if (name.contains("beef") || name.contains("pork") || name.contains("chicken")) return "🥩";
            if (name.contains("arrow"))    return "🏹";
            if (name.contains("torch"))    return "🕯";
            if (name.contains("chest"))    return "📦";
            return "📦";
        }
    }
}
