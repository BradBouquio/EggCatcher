package me.shansen.EggCatcher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.shansen.EggCatcher.listeners.EggCatcherEntityListener;
import me.shansen.EggCatcher.listeners.EggCatcherPlayerListener;
import net.milkbowl.vault.economy.Economy;
import com.gmail.fortyeffsmc.eggcatcher.CommandCompleter;
import com.gmail.fortyeffsmc.eggcatcher.CommandHandler;

import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Egg;
import org.bukkit.entity.EntityType;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.java.JavaPlugin;

public class EggCatcher
extends JavaPlugin {
    public static List<Egg> eggs = new ArrayList<Egg>();
    public static Economy economy = null;
    public static EggCatcher plugin;
    public Map<String,String> mobMap = new HashMap<>();
    
    public void onDisable() {
    }


    
    public void onEnable() {
        RegisteredServiceProvider economyProvider;
        plugin = this;
        this.CheckConfigurationFile();
        PluginManager pm = this.getServer().getPluginManager();
        EggCatcherPlayerListener playerListener = new EggCatcherPlayerListener();
        EggCatcherEntityListener entityListener = new EggCatcherEntityListener(this);
		this.getCommand("eggex").setTabCompleter(new CommandCompleter());
		this.getCommand("eggex").setExecutor(new CommandHandler());
        pm.registerEvents((Listener)playerListener, (Plugin)this);
        pm.registerEvents((Listener)entityListener, (Plugin)this);
        for(EntityType eType : EntityType.values()) {
        	if(eType.isAlive()) {
        		if(eType.toString().equals("PIG_ZOMBIE")) {
        			mobMap.put(eType.toString(), "ZOMBIE_PIGMAN_SPAWN_EGG");
        		} else if(eType.toString().equals("MUSHROOM_COW")) {
        			mobMap.put(eType.toString(), "MOOSHROOM_SPAWN_EGG");
        		} else mobMap.put(eType.toString(), eType.toString() + "_SPAWN_EGG");
        	}
        }
        
        if (this.getServer().getPluginManager().getPlugin("Vault") != null && (economyProvider = this.getServer().getServicesManager().getRegistration(Economy.class)) != null) {
            economy = (Economy)economyProvider.getProvider();
        }
    }

    public void CheckConfigurationFile() {
        double configVersion = this.getConfig().getDouble("ConfigVersion", 0.0);
        if (configVersion == 4.0) {
            this.saveConfig();
        } else {
            this.saveResource("config.yml", true);
            this.reloadConfig();
        }
    }
}