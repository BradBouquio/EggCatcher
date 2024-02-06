package com.gmail.fortyeffsmc.eggcatcher;

import org.bukkit.Bukkit;
import net.md_5.bungee.api.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColorMatcher {

    private static Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");

    public static String translate(String text){
        if(Bukkit.getVersion().contains("1.20")){
            Matcher match = pattern.matcher(text);
            while(match.find()){
                String color = text.substring(match.start(), match.end());
                text = text.replace(color, ChatColor.of(color) + "");
                match = pattern.matcher(text);
            }
        }
        return ChatColor.translateAlternateColorCodes('&',text);
    }
}
