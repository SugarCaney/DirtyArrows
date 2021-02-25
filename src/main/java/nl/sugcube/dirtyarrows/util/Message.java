package nl.sugcube.dirtyarrows.util;

import nl.sugcube.dirtyarrows.DirtyArrows;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Message {

	/**
	 * The tag to show up in MINIGAME_VERSION.
	 */
	public static final String MG_TAG = ChatColor.GRAY + "[" + ChatColor.RED + "->" + ChatColor.GRAY + "] ";
	
	/**
	 * PART 1: You don't have enough levels to make that bow!
	 */
	public static final String NOT_ENOUGH_LEVELS_1 = ChatColor.RED + "[!!] You don't have enough levels to make that bow!";
	
	/**
	 * PART 2 	<br>
	 * %l% : The amount of levels required.
	 */
	public static final String NOT_ENOUGH_LEVELS_2 = ChatColor.RED + "[!!] Levels required: " + ChatColor.YELLOW + "%l%";
	
	/**
	 * DirtyArrows have been disabled.
	 */
	public static final String DISABLED = ChatColor.YELLOW + "Dirty Arrows have been" + ChatColor.RED + " disabled!";
	
	/**
	 * DirtyArrows have been disabled.
	 */
	public static final String ENABLED = ChatColor.YELLOW + "Dirty Arrows have been " + ChatColor.GREEN + "enabled!";
	
	/**
	 * %p% : The name of the shooter.
	 */
	public static final String MG_HEADSHOT_BY = MG_TAG + "Headshot by " + ChatColor.RED + "%p%";
	
	/**
	 * %p% : The name of the target player.
	 */
	public static final String MG_HEADSHOT_ON = MG_TAG + "You made a headshot on " + ChatColor.RED + "%p%";
	
	/**
	 * %p% : The name of the shooter.
	 */
	public static final String HEADSHOT_BY = ChatColor.YELLOW + "Headshot by " + ChatColor.RED + "%p%";
	
	/**
	 * %p% : The name of the target.
	 */
	public static final String HEADSHOT_ON = ChatColor.YELLOW + "You made a headshot on " + ChatColor.GREEN +
			"%p%";
	
	/**
	 * Get the message that shows up when someone enables or disables DirtyArrows.
	 * @param enabled (boolean) <i>true</i> if enabled, <i>false</i> if disabled.
	 * @return (String) The message to show to the player.
	 */
	public static String getEnabled(boolean enabled) {
		if (DirtyArrows.MINIGAME_VERSION) {
			return "";
		}
		else {
			if (enabled) {
				return ENABLED;
			}
			else {
				return DISABLED;
			}
		}
	}
	
	/**
	 * Get the tag when MINIGAME_VERSION is true.
	 * @return MG_TAG in case that MINIGAME_VERSION is true, otherwise it returns an empty string.
	 */
	public static String getTag() {
		if (DirtyArrows.MINIGAME_VERSION) {
			return MG_TAG;
		}
		else {
			return "";
		}
	}
	
	/**
	 * Get the message to show when a player makes a headshot.
	 * @param p (Player) The player's name that should occur in the message.
	 * @param headshotType (Type) The type of headshot it is.
	 * @return (String) The message the be sent.
	 */
	public static String getHeadshot(Player p, Type headshotType) {
		if (DirtyArrows.MINIGAME_VERSION) {
			if (headshotType == Type.HEADSHOT_BY) {
				return MG_HEADSHOT_BY.replace("%p%", p.getName());
			}
			else if (headshotType == Type.HEADSHOT_ON) {
				return MG_HEADSHOT_ON.replace("%p%", p.getName());
			}
		}
		else {
			if (headshotType == Type.HEADSHOT_BY) {
				return HEADSHOT_BY.replace("%p%", p.getName());
			}
			else if (headshotType == Type.HEADSHOT_ON) {
				return HEADSHOT_ON.replace("%p%", p.getName());
			}
		}
		
		return "";
	}
	
	public enum Type {
		/**
		 * Headshot by 'player'
		 */
		HEADSHOT_BY,
		/**
		 * You made a headshot on 'player'
		 */
		HEADSHOT_ON
	}
	
}
