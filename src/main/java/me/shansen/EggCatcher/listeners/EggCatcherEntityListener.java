package me.shansen.EggCatcher.listeners;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.shansen.EggCatcher.EggCatcher;
import me.shansen.EggCatcher.EggCatcherLogger;
import me.shansen.EggCatcher.events.EggCaptureEvent;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.Villager;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.projectiles.ProjectileSource;

public class EggCatcherEntityListener
implements Listener {
    private boolean usePermissions;
    private boolean useCatchChance;
    private boolean useHealthPercentage;
    private boolean looseEggOnFail;
    private boolean useVaultCost;
    private boolean useItemCost;
    private boolean explosionEffect;
    private boolean smokeEffect;
    private boolean nonPlayerCatching;
    private boolean preventCatchingBabyAnimals;
    private boolean preventCatchingTamedAnimals;
    private boolean preventCatchingShearedSheeps;
    private String catchChanceSuccessMessage;
    private String catchChanceFailMessage;
    private String healthPercentageFailMessage;
    private String vaultTargetBankAccount;
    private boolean spawnChickenOnFail;
    private boolean spawnChickenOnSuccess;
    private boolean deleteVillagerInventoryOnCatch;
    private boolean logCaptures;
    private Entity entity;
    EntityDamageByEntityEvent damageEvent;
    Player player;
    Egg egg;
    
    public boolean catchByPunch = false;
    public static EggCatcherEntityListener catchListener;
    FileConfiguration config;
    JavaPlugin plugin;
    private File captureLogFile;
    private EggCatcherLogger captureLogger;
    

    public EggCatcherEntityListener(JavaPlugin plugin) {
    	catchListener = this;
        this.plugin = plugin;
        reloadConfig();
        this.logCaptures = this.config.getBoolean("LogEggCaptures", false);
        this.captureLogFile = new File(plugin.getDataFolder(), "captures.txt");
        this.captureLogger = new EggCatcherLogger(this.captureLogFile);
    }
    
    public void reloadConfig() {
        this.config = plugin.getConfig();
        this.usePermissions = this.config.getBoolean("UsePermissions", true);
        this.useCatchChance = this.config.getBoolean("UseCatchChance", true);
        this.useHealthPercentage = this.config.getBoolean("UseHealthPercentage", false);
        this.looseEggOnFail = this.config.getBoolean("LooseEggOnFail", true);
        this.useVaultCost = this.config.getBoolean("UseVaultCost", false);
        this.useItemCost = this.config.getBoolean("UseItemCost", false);
        this.explosionEffect = this.config.getBoolean("ExplosionEffect", true);
        this.smokeEffect = this.config.getBoolean("SmokeEffect", false);
        this.nonPlayerCatching = this.config.getBoolean("NonPlayerCatching", true);
        this.catchChanceSuccessMessage = this.config.getString("Messages.CatchChanceSuccess");
        this.catchChanceFailMessage = this.config.getString("Messages.CatchChanceFail");
        this.healthPercentageFailMessage = this.config.getString("Messages.HealthPercentageFail");
        this.preventCatchingBabyAnimals = this.config.getBoolean("PreventCatchingBabyAnimals", true);
        this.preventCatchingTamedAnimals = this.config.getBoolean("PreventCatchingTamedAnimals", true);
        this.preventCatchingShearedSheeps = this.config.getBoolean("PreventCatchingShearedSheeps", true);
        this.spawnChickenOnFail = this.config.getBoolean("SpawnChickenOnFail", true);
        this.spawnChickenOnSuccess = this.config.getBoolean("SpawnChickenOnSuccess", false);
        this.vaultTargetBankAccount = this.config.getString("VaultTargetBankAccount", "");
        this.deleteVillagerInventoryOnCatch = this.config.getBoolean("DeleteVillagerInventoryOnCatch", false);
    }

    /*
     * Enabled aggressive block sorting
     * Lifted jumps to return sites
     */
    @EventHandler(ignoreCancelled=true, priority=EventPriority.HIGH)
    public void onEntityHitByEgg(EntityDamageEvent event) {
    	entity = event.getEntity();
    	if(!checkToProceed(event)) return;
    	
        double vaultCost = 0.0;
        String entityFriendlyName = entity.getType().toString();
        
        catchByPunch = isCatchByPunch();
        if(!catchByPunch && !(damageEvent.getDamager() instanceof Egg)) {
        	return;
        }
        
        if (!this.spawnChickenOnFail && !catchByPunch) {
            EggCatcher.eggs.add(egg);
        }
        
        
        EggCaptureEvent eggCaptureEvent = new EggCaptureEvent(entity, egg);
        this.plugin.getServer().getPluginManager().callEvent((Event)eggCaptureEvent);
        if (eggCaptureEvent.isCancelled()) {
            return;
        }
       
        if (catchByPunch || (egg.getShooter() instanceof Player)) {
            double healthPercentage;
            double currentHealth = ((LivingEntity)entity).getHealth();
            
            if (this.usePermissions && !player.hasPermission("eggcatcher.catch." + entityFriendlyName.toLowerCase().replace("_", ""))) {
                player.sendMessage(this.config.getString("Messages.PermissionFail"));
                if (this.looseEggOnFail) {
                	if(catchByPunch) player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount() - 1);
                	return;
                } else if (catchByPunch) return;
                player.getInventory().addItem(new ItemStack[]{new ItemStack(Material.EGG, 1)});
                EggCatcher.eggs.add(egg);
                return;
            }
            
            
            if (this.useHealthPercentage && (healthPercentage = this.config.getDouble("HealthPercentage." + entityFriendlyName)) < (currentHealth * 100.0 / ((LivingEntity)entity).getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue())) {
                
            	if (this.healthPercentageFailMessage.length() > 0) {
                    player.sendMessage(String.format(this.healthPercentageFailMessage, healthPercentage));
                }
            	
                if (this.looseEggOnFail) {
                	if(catchByPunch) player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount() - 1);
                	return;
                } else if (catchByPunch) return;
                player.getInventory().addItem(new ItemStack[]{new ItemStack(Material.EGG, 1)});
                EggCatcher.eggs.add(egg);
                return;
            }
            
            
            if (this.useCatchChance) {
                double catchChance = this.config.getDouble("CatchChance." + entityFriendlyName);
                
                if (Math.random() * 100.0 <= catchChance) {
                	
                    if (this.catchChanceSuccessMessage.length() > 0) {
                        player.sendMessage(this.catchChanceSuccessMessage);
                    }
                    
                } else {
                	
                    if (this.catchChanceFailMessage.length() > 0) {
                        player.sendMessage(this.catchChanceFailMessage);
                    }
                    
                    if (this.looseEggOnFail) {
                    	if(catchByPunch) player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount() - 1);
                    	return;
                    } else if (catchByPunch) return;
                    player.getInventory().addItem(new ItemStack[]{new ItemStack(Material.EGG, 1)});
                    EggCatcher.eggs.add(egg);
                    return;
                }
                
            }
            
            boolean freeCatch = player.hasPermission("eggcatcher.free");
            if (this.useVaultCost && !freeCatch) {
                vaultCost = this.config.getDouble("VaultCost." + entityFriendlyName);
                if (!EggCatcher.economy.has(player, vaultCost)) {
                    player.sendMessage(String.format(this.config.getString("Messages.VaultFail"), vaultCost));
                    if (this.looseEggOnFail) {
                    	if(catchByPunch) player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount() - 1);
                    	return;
                    } else if (catchByPunch) return;
                    player.getInventory().addItem(new ItemStack[]{new ItemStack(Material.EGG, 1)});
                    EggCatcher.eggs.add(egg);
                    return;
                }
                EggCatcher.economy.withdrawPlayer(player, vaultCost);
                if (!this.vaultTargetBankAccount.isEmpty()) {
                    EggCatcher.economy.bankDeposit(this.vaultTargetBankAccount, vaultCost);
                }
                player.sendMessage(String.format(this.config.getString("Messages.VaultSuccess"), vaultCost));
            }
            if (this.useItemCost && !freeCatch) {
                String itemName = this.config.getString("ItemCost.ItemName", "GOLD_NUGGET");
                int itemAmount = this.config.getInt("ItemCost.Amount." + entityFriendlyName, 0);
                ItemStack itemStack = new ItemStack(Material.valueOf((String)itemName), itemAmount);
                if (!player.getInventory().containsAtLeast(itemStack, itemStack.getAmount())) {
                    player.sendMessage(String.format(this.config.getString("Messages.ItemCostFail"), String.valueOf(itemAmount)));
                    if (this.looseEggOnFail) {
                    	if(catchByPunch) player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount() - 1);
                    	return;
                    } else if (catchByPunch) return;
                    player.getInventory().addItem(new ItemStack[]{new ItemStack(Material.EGG, 1)});
                    EggCatcher.eggs.add(egg);
                    return;
                }
                player.sendMessage(String.format(this.config.getString("Messages.ItemCostSuccess"), String.valueOf(itemAmount)));
                player.getInventory().removeItem(new ItemStack[]{itemStack});
            }
        } else {
            if (!this.nonPlayerCatching) {
                return;
            }
            if (this.useCatchChance) {
                double catchChance = this.config.getDouble("CatchChance." + entityFriendlyName);
                if (Math.random() * 100.0 > catchChance) {
                    return;
                }
            }
        }
        
        entity.remove();
        if(catchByPunch) {
        	player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount() - 1);
        }
        
        if (this.explosionEffect) {
            entity.getWorld().createExplosion(entity.getLocation(), 0.0f);
        }
        
        
        if (this.smokeEffect) {
            entity.getWorld().playEffect(entity.getLocation(), Effect.SMOKE, 0);
        }
        
        
        
        ItemStack eggStack = new ItemStack(Material.matchMaterial(EggCatcher.plugin.mobMap.get(entity.getType().toString().toUpperCase())), 1);
        String customName = ((LivingEntity)entity).getCustomName();
        
        
        if (customName != null) {
            ItemMeta meta = eggStack.getItemMeta();
            meta.setDisplayName(customName);
            eggStack.setItemMeta(meta);
        }
        
        if (entity instanceof Pig && ((Pig)entity).hasSaddle()) {
            entity.getWorld().dropItem(entity.getLocation(), new ItemStack(Material.SADDLE, 1));
        }
        
        if (entity instanceof Horse && ((Horse)entity).isCarryingChest()) {
            entity.getWorld().dropItemNaturally(entity.getLocation(), new ItemStack(Material.CHEST));
        }
        
        if (entity instanceof Villager && !this.deleteVillagerInventoryOnCatch || !(entity instanceof Villager) && entity instanceof InventoryHolder) {
            ItemStack[] items;
            for (ItemStack itemStack : items = ((InventoryHolder)entity).getInventory().getContents()) {
                if (itemStack == null) continue;
                entity.getWorld().dropItemNaturally(entity.getLocation(), itemStack);
            }
        }
        
        entity.getWorld().dropItem(entity.getLocation(), eggStack);
        
        if (!this.spawnChickenOnSuccess && !EggCatcher.eggs.contains((Object)egg)) {
            EggCatcher.eggs.add(egg);
        }
        
        if (!this.logCaptures) return;
        
        this.captureLogger.logToFile("Player " + ((Player)egg.getShooter()).getName() + " caught " + (Object)entity.getType() + " at X" + Math.round(entity.getLocation().getX()) + ",Y" + Math.round(entity.getLocation().getY()) + ",Z" + Math.round(entity.getLocation().getZ()));
    }
    
    
    
    public boolean checkToProceed(EntityDamageEvent event) {
    	
        if (event instanceof EntityDamageByEntityEvent) {
        	damageEvent = (EntityDamageByEntityEvent)event;
        } else return false;
    	
        if (this.preventCatchingBabyAnimals && entity instanceof Ageable && !((Ageable)entity).isAdult()) {
        	return false;
        }
        
        if (this.preventCatchingTamedAnimals && entity instanceof Tameable && ((Tameable)entity).isTamed()) {
        	return false;
        }
        
        if (this.preventCatchingShearedSheeps && entity instanceof Sheep && ((Sheep)entity).isSheared()) {
        	return false;
        }
        
        return true;
    }
    
    
    public boolean isCatchByPunch() {
        if (damageEvent.getDamager().getType().toString().equals("EGG")) {
        	egg = (Egg)damageEvent.getDamager();
        	if(egg.getShooter() instanceof Player) {
        		player = (Player)egg.getShooter();
        	}
        	return false;
        } else {
        	if(damageEvent.getDamager().getType().toString().equals("PLAYER")) {
        		player = (Player) damageEvent.getDamager();
        		if(player.getInventory().getItemInMainHand().getType().toString().equals("EGG")) {
            		catchByPunch = true;
            		return true;
        		}
        	}
        }
        return false;
        
    }
    
    public void broadcastMessage(String string) {
    	for(Player player : Bukkit.getOnlinePlayers()) {
    		player.sendMessage(string);
    	}
    }
}