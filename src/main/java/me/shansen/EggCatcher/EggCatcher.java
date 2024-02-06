package me.shansen.EggCatcher;

import com.gmail.fortyeffsmc.eggcatcher.ColorMatcher;
import com.gmail.fortyeffsmc.eggcatcher.CommandHandler;
import com.gmail.fortyeffsmc.eggcatcher.EggThrowEvent;
import com.gmail.fortyeffsmc.eggcatcher.ProjectileEvent;
import me.shansen.EggCatcher.listeners.EggCatcherEntityListener;
import me.shansen.EggCatcher.listeners.EggCatcherPlayerListener;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.entity.Egg;
import org.bukkit.entity.EntityType;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EggCatcher
extends JavaPlugin {
    public static List<Egg> eggs = new ArrayList<Egg>();
    public static Economy economy = null;
    public static EggCatcher plugin;
    public Map<String,String> mobMap = new HashMap<>();
    public static List<String> eggTierNames = new ArrayList<>();
    public static List<String> eggNames = new ArrayList<>();

    public void onDisable() {
    }


    
    public void onEnable() {

        RegisteredServiceProvider economyProvider;
        plugin = this;
        plugin.getConfig().getConfigurationSection("SpecialEggs").getKeys(false).forEach(tier -> eggTierNames.add(tier));
        eggTierNames.forEach(tierName -> eggNames.add(ChatColor.stripColor(ColorMatcher.translate(EggCatcher.plugin.getConfig().getString("SpecialEggs." + tierName + ".Name")))));

        this.CheckConfigurationFile();
        PluginManager pm = this.getServer().getPluginManager();
        EggCatcherPlayerListener playerListener = new EggCatcherPlayerListener();
        EggCatcherEntityListener entityListener = new EggCatcherEntityListener(this);
        pm.registerEvents(new ProjectileEvent(), this);
        pm.registerEvents(new EggThrowEvent(), this);
		//this.getCommand("eggex").setTabCompleter(new CommandCompleter());
		this.getCommand("eggex").setExecutor(new CommandHandler());
        pm.registerEvents((Listener)playerListener, (Plugin)this);
        pm.registerEvents((Listener)entityListener, (Plugin)this);
        for(EntityType eType : EntityType.values()) {
        	if(eType.isAlive()) {
        		if(eType.toString().equals("MUSHROOM_COW")) {
        			mobMap.put(eType.toString(), "MOOSHROOM_SPAWN_EGG");
        		} else mobMap.put(eType.toString(), eType.toString() + "_SPAWN_EGG");
        	}
        }
        
        economyProvider = this.getServer().getServicesManager().getRegistration(Economy.class);
        //if (this.getServer().getPluginManager().getPlugin("Vault") != null && economyProvider != null) {
        //economy = (Economy)economyProvider.getProvider();
        setupEconomy();
        //}
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
    
    private boolean setupEconomy()
    {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }

        return (economy != null);
    }
}