package com.gmail.fortyeffsmc.eggcatcher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

public class CommandCompleter implements TabCompleter {

	private static final List<String> COMMANDS = new ArrayList<>();
	private static final List<String> COMMANDS2 = new ArrayList<>();
	
	public CommandCompleter() {
		COMMANDS.add("reload");
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length == 1) {
			final List<String> completions = new ArrayList<>();
			StringUtil.copyPartialMatches(args[0], COMMANDS, completions);
			Collections.sort(completions);
			return completions;
		}
		if(args.length == 2) {
			return null;
		}
		if(args[0].equalsIgnoreCase("give") && args.length == 3) {
			final List<String> completions = new ArrayList<>();
			StringUtil.copyPartialMatches(args[2], COMMANDS2, completions);
			Collections.sort(completions);
			return completions;
		}
		else return null;

	}

}