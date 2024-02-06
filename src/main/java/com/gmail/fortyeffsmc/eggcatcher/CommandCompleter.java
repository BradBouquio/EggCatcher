//package com.gmail.fortyeffsmc.eggcatcher;
//
//import org.bukkit.Bukkit;
//import org.bukkit.command.Command;
//import org.bukkit.command.CommandSender;
//import org.bukkit.command.TabCompleter;
//import org.bukkit.util.StringUtil;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//
//public class CommandCompleter implements TabCompleter {
//
//	private static final List<String> COMMANDS = new ArrayList<>();
//	private static final List<String> COMMANDS2 = new ArrayList<>();
//	private final List<String> ONLINEPLAYERS = new ArrayList<>();
//
//	public CommandCompleter() {
//		COMMANDS.add("reload");
//		COMMANDS.add("give-egg");
//		COMMANDS2.add("[num]");
//	}
//
//	@Override
//	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
//		ONLINEPLAYERS.clear();
//		Bukkit.getOnlinePlayers().forEach(player -> ONLINEPLAYERS.add(player.getDisplayName()));
//		if(args.length == 1) {
//			final List<String> completions = new ArrayList<>();
//			StringUtil.copyPartialMatches(args[0], COMMANDS, completions);
//			Collections.sort(completions);
//			return completions;
//		}
//		if(args.length == 2 && args[0].equalsIgnoreCase("give-egg")) {
//			final List<String> completions = new ArrayList<>();
//			StringUtil.copyPartialMatches(args[1], ONLINEPLAYERS, completions);
//			Collections.sort(completions);
//			return completions;
//		}
//		if(args.length == 2 && args[0].equalsIgnoreCase("give-egg")){
//			final List<String> completions = new ArrayList<>();
//			StringUtil.copyPartialMatches(args[2], COMMANDS2, completions);
//			Collections.sort(completions);
//			return completions;
//		}
//		else return null;
//
//	}
//
//}