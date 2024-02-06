package me.shansen.EggCatcher.listeners;

import com.gmail.fortyeffsmc.eggcatcher.CatchChance;
import com.gmail.fortyeffsmc.eggcatcher.ColorMatcher;
import me.shansen.EggCatcher.EggCatcher;
import me.shansen.EggCatcher.EggCatcherLogger;
import me.shansen.EggCatcher.events.EggCaptureEvent;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

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
    private String specialEggName;
    private String successMessage;
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
        this.successMessage = this.plugin.getConfig().getString("Messages.Success");
        this.healthPercentageFailMessage = this.config.getString("Messages.HealthPercentageFail");
        this.preventCatchingBabyAnimals = this.config.getBoolean("PreventCatchingBabyAnimals", true);
        this.preventCatchingTamedAnimals = this.config.getBoolean("PreventCatchingTamedAnimals", true);
        this.preventCatchingShearedSheeps = this.config.getBoolean("PreventCatchingShearedSheeps", true);
        //this.specialEggName = this.config.getString("SpecialEgg.Name", "");
        this.spawnChickenOnFail = this.config.getBoolean("SpawnChickenOnFail", true);
        this.spawnChickenOnSuccess = this.config.getBoolean("SpawnChickenOnSuccess", false);
        this.vaultTargetBankAccount = this.config.getString("VaultTargetBankAccount", "");
        this.deleteVillagerInventoryOnCatch = this.config.getBoolean("DeleteVillagerInventoryOnCatch", false);
    }

    /*
     * Enabled aggressive block sorting
     * Lifted jumps to return sites
     */
    @EventHandler(ignoreCancelled=false, priority=EventPriority.HIGHEST)
    public void onEntityHitByEgg(EntityDamageByEntityEvent event) {
        damageEvent = event;
        entity = damageEvent.getEntity();
    	if(!checkToProceed(event)) return;

        double vaultCost = 0.0;
        String entityFriendlyName = entity.getType().toString();
        catchByPunch = isCatchByPunch();
        if(!catchByPunch && !(damageEvent.getDamager() instanceof Egg)) {
        	return;
        }
        if(!CatchChance.playerThrewSpecial(player.getDisplayName())) return;

        event.setCancelled(true);
        
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
                	if(catchByPunch && player.getGameMode() != GameMode.CREATIVE) player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount() - 1);
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
                	if(catchByPunch && player.getGameMode() != GameMode.CREATIVE) player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount() - 1);
                	return;
                } else if (catchByPunch) return;
                player.getInventory().addItem(new ItemStack[]{new ItemStack(Material.EGG, 1)});
                EggCatcher.eggs.add(egg);
                return;
            }
            
            
            if (this.useCatchChance) {
                double catchChance = this.config.getDouble("CatchChance." + entityFriendlyName) * CatchChance.playerThrowMap.get(player.getDisplayName());

                double roll = Math.random() * 100D;
                if (roll <= catchChance) {
                	
                    if (this.catchChanceSuccessMessage.length() > 0) {
                        player.sendMessage(ColorMatcher.translate(catchChanceSuccessMessage));
                    }
                    
                } else {
                	
                    if (this.catchChanceFailMessage.length() > 0) {
                        Player.Spigot spigotPlayer = player.spigot();
                        spigotPlayer.sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ColorMatcher.translate(catchChanceFailMessage)));
                        player.playSound(player.getLocation(), Sound.ENTITY_TURTLE_EGG_BREAK, 0.5f,2.0f);
                    }
                    
                    if (this.looseEggOnFail) {
                    	if(catchByPunch && player.getGameMode() != GameMode.CREATIVE) player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount() - 1);
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
                    	if(catchByPunch && player.getGameMode() != GameMode.CREATIVE) player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount() - 1);
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
                    	if(catchByPunch && player.getGameMode() != GameMode.CREATIVE) player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount() - 1);
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
        if(catchByPunch && player.getGameMode() != GameMode.CREATIVE) {
        	player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount() - 1);
        }
        
        if (this.explosionEffect) {
            entity.getWorld().createExplosion(entity.getLocation(), 0.0f);
        }
        
        
        if (this.smokeEffect) {
            entity.getWorld().playEffect(entity.getLocation(), Effect.SMOKE, 0);
        }
        
        
        
        ItemStack eggStack = new ItemStack(Material.matchMaterial(EggCatcher.plugin.mobMap.get(entity.getType().toString().toUpperCase())), 1);
        String customName = ((LivingEntity)entity).getName();
        
        
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
        player.playSound(player.getLocation(), Sound.ENTITY_WITHER_AMBIENT, 0.5f,2.0f);
        player.sendMessage(ColorMatcher.translate(String.format(successMessage,entityFriendlyName.toLowerCase().replace("_", " "))));
        CatchChance.playerThrowMap.remove(player.getDisplayName());
        if (!this.spawnChickenOnSuccess && !EggCatcher.eggs.contains((Object)egg)) {
            EggCatcher.eggs.add(egg);
        }
        
        if (!this.logCaptures) return;
        
        this.captureLogger.logToFile("Player " + ((Player)egg.getShooter()).getName() + " caught " + (Object)entity.getType() + " at X" + Math.round(entity.getLocation().getX()) + ",Y" + Math.round(entity.getLocation().getY()) + ",Z" + Math.round(entity.getLocation().getZ()));
    }
    
    
    
    public boolean checkToProceed(EntityDamageByEntityEvent event) {
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
            		if(CatchChance.isEggexEgg(player)) {
            		    CatchChance.playerThrowMap.put(player.getDisplayName(),CatchChance.calculateModifierChanceModifier((player.getInventory().getItemInMainHand())));
            		    return true;
                    }
            		else return false;
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
    
    private double round (double value, int precision) {
    	int scale = (int) Math.pow(10, precision);
    	return (double) Math.round(value * scale);
    }
}