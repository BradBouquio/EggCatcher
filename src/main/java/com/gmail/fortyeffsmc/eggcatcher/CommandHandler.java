package com.gmail.fortyeffsmc.eggcatcher;

import me.shansen.EggCatcher.EggCatcher;
import me.shansen.EggCatcher.listeners.EggCatcherEntityListener;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class CommandHandler implements CommandExecutor {

	private Player player;
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		player = sender instanceof Player ? (Player)sender : null;
		
		try {
			switch(args[0].toLowerCase()) {
				case "reload":
					if(player != null && !player.hasPermission("eggcatcher.reload")) {
						player.sendMessage("You don't have permission to do that!");
						break;
					}
					EggCatcher.plugin.reloadConfig();
					EggCatcherEntityListener.catchListener.reloadConfig();
					//EggCatcher.plugin.getServer().getPluginManager().registerEvents(new EggCatcherEntityListener(EggCatcher.plugin), EggCatcher.plugin);
					sender.sendMessage("Eggex config reloaded!");
					break;
				case "give":
					if(player != null && !player.hasPermission("eggcatcher.giveegg") && !player.isOp()) {
						player.sendMessage("You don't have permission to do that!");
						break;
					}

					Player playerToGive = player;
					if(args.length > 1) {
						playerToGive = Bukkit.getPlayer(args[1]);
						if(playerToGive == null) {
							player.sendMessage("That is not a valid player!");
							return true;
						}
					}

					String eggTierName = "";
					if(args.length > 2){
						eggTierName = args[2];
						if(!EggCatcher.plugin.getConfig().getConfigurationSection("SpecialEggs").getKeys(false).contains(eggTierName)){
							player.sendMessage("That egg does not exist!");
							player.sendMessage("/eggex give [playerName] [eggName] [amount]");
							return true;
						}
					}

					int numItems = 1;
					try {
						if(args.length > 3) numItems = Integer.parseInt(args[3]);
					} catch (NumberFormatException e){}

					ItemStack eggItem = new ItemStack(Material.EGG, numItems);
					ItemMeta eggItemMeta = eggItem.getItemMeta();
					String eggName = ColorMatcher.translate(EggCatcher.plugin.getConfig().getString("SpecialEggs." + eggTierName + ".Name", "Magic Egg"));
					String eggLore = ColorMatcher.translate(EggCatcher.plugin.getConfig().getString("SpecialEggs." + eggTierName + ".Lore", "An egg for catching"));
					eggItemMeta.addEnchant(Enchantment.ARROW_FIRE, 10, true);
					eggItemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
					eggItemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
					eggItemMeta.setDisplayName(eggName);

					List<String> lore = new ArrayList<>();
					Collections.addAll(lore, eggLore.split("\\|"));
					eggItemMeta.setLore(lore);
					eggItem.setItemMeta(eggItemMeta);
					playerToGive.getInventory().addItem(eggItem);
					sender.sendMessage("Gave " + numItems + " egg items to " + playerToGive.getDisplayName());
					break;
				default: return false;
			}
		} catch ( IndexOutOfBoundsException e ) {
		    return false;
		}
		return true;
			
	}

}
