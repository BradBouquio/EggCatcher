package com.gmail.fortyeffsmc.eggcatcher;

import me.shansen.EggCatcher.EggCatcher;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class CatchChance {

    public static double chanceModifier = EggCatcher.plugin.getConfig().getDouble("ChanceModifier");
    public static Map<String, Double> playerThrowMap = new HashMap<>();

    public static Double calculateModifierChanceModifier(ItemStack itemStack) {
        String loreString = ChatColor.stripColor(itemStack.getItemMeta().getLore().get(0));
        Double tier = Double.parseDouble(loreString.substring(loreString.length()-1));
        return Math.pow(chanceModifier, tier-1);
    }

    public static boolean isEggexEgg(Player player) {
        String eggName = "";
        if(player != null) eggName = player.getInventory().getItemInMainHand().getItemMeta().getDisplayName();
        if(!EggCatcher.eggNames.contains(ChatColor.stripColor(eggName))) return false;

        boolean hasEnchant = player.getInventory().getItemInMainHand().containsEnchantment(Enchantment.ARROW_FIRE);
        if(hasEnchant && player.getInventory().getItemInMainHand().getEnchantmentLevel(Enchantment.ARROW_FIRE) == 10) return true;
        else return false;
    }

    public static boolean playerThrewSpecial(String name){
        return playerThrowMap.containsKey(name);
    }
}
