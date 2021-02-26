package nl.sugcube.dirtyarrows.util;

import nl.sugcube.dirtyarrows.DirtyArrows;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Message {

    /**
     * The tag to show up in front of messages.
     */
    public static final String ERROR_TAG = ChatColor.RED + "[!!]";

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
     * Gave bow to player(s).
     */
	public static final String GAVE_BOW = ChatColor.YELLOW + "Gave " + ChatColor.GREEN + "%s " + ChatColor.YELLOW + "to " + ChatColor.GREEN + "%s";

    /**
     * Region position set.
     */
	public static final String SET_POSITION = ChatColor.GREEN + "Position %d " + ChatColor.YELLOW + "has been set to " + ChatColor.GREEN + "%s";

    /**
     * List regions.
     */
	public static final String REGIONS = ChatColor.YELLOW + "Regions: " + ChatColor.GREEN + "%s";

    /**
     * You are in region %s.
     */
	public static final String IN_REGION = ChatColor.YELLOW + "You are in region: " + ChatColor.GREEN + "%s";

    /**
     * You are not in a region.
     */
	public static final String NOT_IN_REGION = ChatColor.YELLOW + "You are " + ChatColor.RED + "not " + ChatColor.YELLOW + " in a DirtyArrows region.";

    /**
     * Removed region %s
     */
	public static final String REGION_REMOVED = ChatColor.YELLOW + "Removed region " + ChatColor.GREEN + "%s";

    /**
     * Created region %s.
     */
	public static final String REGION_CREATED = ChatColor.YELLOW + "Created region " + ChatColor.GREEN + "%s";

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
	public static String getEnabled(boolean enabled, DirtyArrows plugin) {
		if (plugin.isMinigameVersion()) {
			return "";
		}

        if (enabled) {
            return ENABLED;
        }
        else {
            return DISABLED;
        }
    }
	
	/**
	 * Get the tag when MINIGAME_VERSION is true.
	 * @return MG_TAG in case that MINIGAME_VERSION is true, otherwise it returns an empty string.
	 */
	public static String getTag(DirtyArrows plugin) {
		if (plugin.isMinigameVersion()) {
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
	public static String getHeadshot(Player player, Type headshotType, DirtyArrows plugin) {
		if (plugin.isMinigameVersion()) {
			if (headshotType == Type.HEADSHOT_BY) {
				return MG_HEADSHOT_BY.replace("%p%", player.getName());
			}
			else if (headshotType == Type.HEADSHOT_ON) {
				return MG_HEADSHOT_ON.replace("%p%", player.getName());
			}
		}
		else {
			if (headshotType == Type.HEADSHOT_BY) {
				return HEADSHOT_BY.replace("%p%", player.getName());
			}
			else if (headshotType == Type.HEADSHOT_ON) {
				return HEADSHOT_ON.replace("%p%", player.getName());
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
