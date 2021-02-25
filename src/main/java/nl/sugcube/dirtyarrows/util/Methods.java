package nl.sugcube.dirtyarrows.util;

import org.bukkit.ChatColor;

public class Methods {

	public static String setColours(String s) {
		return s.
			replace("&0", ChatColor.BLACK + "").
			replace("&1", ChatColor.DARK_BLUE + "").
			replace("&2", ChatColor.DARK_GREEN + "").
			replace("&3", ChatColor.DARK_AQUA + "").
			replace("&4", ChatColor.DARK_RED + "").
			replace("&5", ChatColor.DARK_PURPLE + "").
			replace("&6", ChatColor.GOLD + "").
			replace("&7", ChatColor.GRAY + "").
			replace("&8", ChatColor.DARK_GRAY + "").
			replace("&9", ChatColor.BLUE + "").
			replace("&a", ChatColor.GREEN + "").
			replace("&b", ChatColor.AQUA + "").
			replace("&c", ChatColor.RED + "").
			replace("&d", ChatColor.LIGHT_PURPLE + "").
			replace("&e", ChatColor.YELLOW + "").
			replace("&f", ChatColor.WHITE + "").
			replace("&k", ChatColor.MAGIC + "").
			replace("&l", ChatColor.BOLD + "").
			replace("&m", ChatColor.STRIKETHROUGH + "").
			replace("&n", ChatColor.UNDERLINE + "").
			replace("&o", ChatColor.ITALIC + "").
			replace("&r", ChatColor.RESET + "");
	}
	
}
