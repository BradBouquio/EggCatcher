package com.gmail.fortyeffsmc.eggcatcher;

import me.shansen.EggCatcher.EggCatcher;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEggThrowEvent;



public class EggThrowEvent implements Listener {

    @EventHandler(ignoreCancelled=false, priority= EventPriority.HIGH)
    public void onThrowEgg(PlayerEggThrowEvent throwEvent){
        if(isEggexEgg(throwEvent)) {
            throwEvent.setHatching(false);
        }
    }

    private boolean isEggexEgg(PlayerEggThrowEvent throwEvent) {
        String eggName = throwEvent.getEgg().getItem().getItemMeta().getDisplayName();

        if(!EggCatcher.eggNames.contains(ChatColor.stripColor(eggName))) return false;


        boolean hasEnchant = throwEvent.getEgg().getItem().containsEnchantment(Enchantment.ARROW_FIRE);
        if(hasEnchant && throwEvent.getEgg().getItem().getEnchantmentLevel(Enchantment.ARROW_FIRE) == 10) return true;
        else return false;
    }

}
