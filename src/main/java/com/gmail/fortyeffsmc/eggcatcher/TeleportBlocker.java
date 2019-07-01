package com.gmail.fortyeffsmc.eggcatcher;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import me.shansen.EggCatcher.EggCatcher;
import me.shansen.EggCatcher.listeners.EggCatcherEntityListener;

public class TeleportBlocker implements Listener {

    @EventHandler(ignoreCancelled=true, priority=EventPriority.LOWEST)
    public void onEntityHitByEgg(EntityTeleportEvent event) {
    	
    	/*
    	if (event.getEntity() instanceof Enderman) {
    		event.setCancelled(true);
    		event.getEntity().remove();
    	}
    	
    	
    	try {
        	if (event.getEntity().getLastDamageCause().getEntity() instanceof Egg) {
        		event.getEntity().remove();
        		for(Player player : Bukkit.getOnlinePlayers()) {
        			player.sendMessage("Get here");
        		}
        		event.setCancelled(true);
        	}
    	} catch(Exception e) {
    		
    	}
*/
    }
}
