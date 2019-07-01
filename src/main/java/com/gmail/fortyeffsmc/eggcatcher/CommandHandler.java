package com.gmail.fortyeffsmc.eggcatcher;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.shansen.EggCatcher.EggCatcher;
import me.shansen.EggCatcher.listeners.EggCatcherEntityListener;


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
					EggCatcher.plugin.getCommand("eggex").setTabCompleter(new CommandCompleter());
					EggCatcher.plugin.getCommand("eggex").setExecutor(new CommandHandler());
					EggCatcherEntityListener.catchListener.reloadConfig();
					//EggCatcher.plugin.getServer().getPluginManager().registerEvents(new EggCatcherEntityListener(EggCatcher.plugin), EggCatcher.plugin);
					sender.sendMessage("Eggex config reloaded!");
					break;
				default: return false;
			}
		} catch ( IndexOutOfBoundsException e ) {
		    return false;
		}
		return true;
			
	}

}
