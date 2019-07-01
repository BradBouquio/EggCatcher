package me.shansen.EggCatcher.listeners;

import java.util.List;
import me.shansen.EggCatcher.EggCatcher;
import org.bukkit.entity.Egg;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEggThrowEvent;

public class EggCatcherPlayerListener
implements Listener {
    @EventHandler
    public void onPlayerEggThrow(PlayerEggThrowEvent event) {
        if (EggCatcher.eggs.contains((Object)event.getEgg())) {
            event.setHatching(false);
            EggCatcher.eggs.remove((Object)event.getEgg());
        }
    }
}