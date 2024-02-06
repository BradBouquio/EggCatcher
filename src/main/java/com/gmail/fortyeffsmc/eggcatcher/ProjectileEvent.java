package com.gmail.fortyeffsmc.eggcatcher;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;


public class ProjectileEvent implements Listener {

    @EventHandler(ignoreCancelled=false, priority=EventPriority.HIGH)
    public void onThrowEgg(ProjectileLaunchEvent throwEvent){
        Player player = null;
        if(throwEvent.getEntity().getShooter() instanceof Player){
            player = (Player) throwEvent.getEntity().getShooter();
        } else return;
        if(CatchChance.isEggexEgg(player)){
            CatchChance.playerThrowMap.put(player.getDisplayName(),CatchChance.calculateModifierChanceModifier(((Player) throwEvent.getEntity().getShooter()).getInventory().getItemInMainHand()));
        } else CatchChance.playerThrowMap.remove(player.getDisplayName());
    }
}
