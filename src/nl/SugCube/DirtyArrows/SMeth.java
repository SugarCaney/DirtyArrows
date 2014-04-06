package nl.SugCube.DirtyArrows;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;

public class SMeth {

	/**
	 * Converts a string into a location. Useful if you're using extern data storage.
	 * Seperator: %
	 * (world)%(double x)%(double y)%(double z) OR
	 * (world)%(double x)%(double y)%(double z)%(double pitch)%(double yaw)
	 * @param locationString (String) The string to convert
	 * @return (Location) The location the string represents
	 */
	public static Location toLocation(String locationString) {
		String[] s = locationString.split("%");
		
		Location loc = new Location(Bukkit.getWorld(s[0]), Double.parseDouble(s[1]), Double.parseDouble(s[2]),
				Double.parseDouble(s[3]));
		if (s.length > 4) {
			loc.setPitch(Float.parseFloat(s[4]));
			loc.setYaw(Float.parseFloat(s[5]));
		}
		
		return loc;
	}
	
	/**
	 * Converts a Location into a string. Useful if you're using extern data storage.
	 * Seperator: %
	 * (world)%(double x)%(double y)%(double z)
	 * Special use: signs
	 * @param loc (Location) The Location to convert
	 * @return (String) The string that represents the location.
	 */
	public static String toLocationStringSign(Location loc) {
		return loc.getWorld().getName() + "%" + loc.getX() + "%" + loc.getY() + "%" + loc.getZ();
	}
	
	/**
	 * Converts a Location into a string. Useful if you're using extern data storage.
	 * Seperator: %
	 * (world)%(double x)%(double y)%(double z)%(double pitch)%(double yaw)
	 * @param loc (Location) The Location to convert
	 * @return (String) The string that represents the location.
	 */
	public static String toLocationString(Location loc) {
		return loc.getWorld().getName() + "%" + loc.getX() + "%" + loc.getY() + "%" + loc.getZ() + "%" +
				loc.getPitch() + "%" + loc.getYaw();
	}
	
	/**
	 * Converts an amount of seconds to a nice string format (mm:ss) or (hh:mm:ss)
	 * @param seconds (int) The amount of seconds to format
	 * @return (String) Formatted time
	 */
	public static String toTime(int seconds) {
		int hours = 0;
		int minutes = 0;

		if (seconds / 3600 > 0) {
			hours = seconds / 3600;
			seconds -= hours * 3600;
		}
		
		if (seconds / 60 > 0) {
			minutes = seconds / 60;
			seconds -= minutes * 60;
		}
		
		if (hours > 0) {
			return hours + ":" + (minutes < 10 ? "0" + minutes : minutes) + ":" + (seconds < 10 ? "0" + seconds : seconds);
		} else {
			return minutes + ":" + (seconds < 10 ? "0" + seconds : seconds);
		}
	}
	
	/**
	 * Replaces all hexidecimal colour-codes with the representing ChatColors
	 * @param s (String) The string to replace the colours
	 * @return (String) Colourful text
	 */
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