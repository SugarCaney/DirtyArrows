package nl.SugCube.DirtyArrows;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Error {
	
	public static void noMulti(Player player, String string) {
		if (string.equalsIgnoreCase("permissions")) {
			player.sendMessage(ChatColor.RED + "[!!] You don't have permission to use this bow");
		} else {
			player.sendMessage(ChatColor.RED + "[!!] You don't have enough resources to use this bow. Needed:");
			player.sendMessage(ChatColor.RED + "[!!] Arrow (8x)");
		}
	} 
	
	public static void noWoodsman(Player player) {
		player.sendMessage(ChatColor.RED + "[!!] You don't have permission to use this bow");
	} 
	
	public static void noLevel(Player player) {
		player.sendMessage(ChatColor.RED + "[!!] You don't have permission to use this bow");
	} 
	
	public static void noSlow(Player player) {
		player.sendMessage(ChatColor.RED + "[!!] You don't have permission to use this bow");
	} 
	
	public static void noDisarm(Player player) {
		player.sendMessage(ChatColor.RED + "[!!] You don't have permission to use this bow");
	} 
	
	public static void noDraining(Player player) {
		player.sendMessage(ChatColor.RED + "[!!] You don't have permission to use this bow");
	} 
	
	public static void noSwap(Player player) {
		player.sendMessage(ChatColor.RED + "[!!] You don't have permission to use this bow");
	} 
	
	public static void noDisorienting(Player player) {
		player.sendMessage(ChatColor.RED + "[!!] You don't have permission to use this bow");
	} 
	
	public static void noMachine(Player player) {
		player.sendMessage(ChatColor.RED + "[!!] You don't have permission to use this bow");
	}
	
	public static void noRanged(Player player) {
		player.sendMessage(ChatColor.RED + "[!!] You don't have permission to use this bow");
	} 
	
	public static void noUndead(Player player, String reason) {
		if (reason == "permissions") {
			player.sendMessage(ChatColor.RED + "[!!] You don't have permission to use this bow");
		} else if (reason == "item") {
			player.sendMessage(ChatColor.RED + "[!!] You don't have enough resources to use this bow. Needed:");
			player.sendMessage(ChatColor.RED + "[!!] Rotten Flesh (64x)");
		}
	}
	
	public static void noFirey(Player player, String reason) {
		if (reason == "permissions") {
			player.sendMessage(ChatColor.RED + "[!!] You don't have permission to use this bow");
		} else if (reason == "item") {
			player.sendMessage(ChatColor.RED + "[!!] You don't have enough resources to use this bow. Needed:");
			player.sendMessage(ChatColor.RED + "[!!] Fire Charge (1x)");
		}
	}
	
	public static void noWither(Player player, String reason) {
		if (reason == "permissions") {
			player.sendMessage(ChatColor.RED + "[!!] You don't have permission to use this bow");
		} else if (reason == "item") {
			player.sendMessage(ChatColor.RED + "[!!] You don't have enough resources to use this bow. Needed:");
			player.sendMessage(ChatColor.RED + "[!!] Soul Sand (1x)");
		}
	}
	
	public static void noFlintAnd(Player player, String reason) {
		if (reason == "permissions") {
			player.sendMessage(ChatColor.RED + "[!!] You don't have permission to use this bow");
		} else if (reason == "item") {
			player.sendMessage(ChatColor.RED + "[!!] You don't have enough resources to use this bow. Needed:");
			player.sendMessage(ChatColor.RED + "[!!] Flint And Steel");
		}
	}
	
	public static void noPoisonous(Player player, String reason) {
		if (reason == "permissions") {
			player.sendMessage(ChatColor.RED + "[!!] You don't have permission to use this bow");
		} else if (reason == "item") {
			player.sendMessage(ChatColor.RED + "[!!] You don't have enough resources to use this bow. Needed:");
			player.sendMessage(ChatColor.RED + "[!!] Spider Eye (x1)");
		}
	}
	
	public static void noEnlightened(Player player, String reason) {
		if (reason == "permissions") {
			player.sendMessage(ChatColor.RED + "[!!] You don't have permission to use this bow");
		} else if (reason == "item") {
			player.sendMessage(ChatColor.RED + "[!!] You don't have enough resources to use this bow. Needed:");
			player.sendMessage(ChatColor.RED + "[!!] Torch (x1)");
		}
	}
	
	public static void noNuclear(Player player, String reason) {
		if (reason == "permissions") {
			player.sendMessage(ChatColor.RED + "[!!] You don't have permission to use this bow");
		} else if (reason == "item") {
			player.sendMessage(ChatColor.RED + "[!!] You don't have enough resources to use this bow. Needed:");
			player.sendMessage(ChatColor.RED + "[!!] TNT (x64)");
		}
	}
	
	public static void noBatty(Player player, String reason) {
		if (reason == "permissions") {
			player.sendMessage(ChatColor.RED + "[!!] You don't have permission to use this bow");
		} else if (reason == "item") {
			player.sendMessage(ChatColor.RED + "[!!] You don't have enough resources to use this bow. Needed:");
			player.sendMessage(ChatColor.RED + "[!!] Rotten Flesh (x3)");
		}
	}
	
	public static void noJungle(Player player, String reason) {
		if (reason == "permissions") {
			player.sendMessage(ChatColor.RED + "[!!] You don't have permission to use this bow");
		} else if (reason == "item") {
			player.sendMessage(ChatColor.RED + "[!!] You don't have enough resources to use this bow. Needed:");
			player.sendMessage(ChatColor.RED + "[!!] Jungle Sapling (x1)");
			player.sendMessage(ChatColor.RED + "[!!] Bone Meal (x1)");
		}
	}
	
	public static void noSpruce(Player player, String reason) {
		if (reason == "permissions") {
			player.sendMessage(ChatColor.RED + "[!!] You don't have permission to use this bow");
		} else if (reason == "item") {
			player.sendMessage(ChatColor.RED + "[!!] You don't have enough resources to use this bow. Needed:");
			player.sendMessage(ChatColor.RED + "[!!] Spruce Sapling (x1)");
			player.sendMessage(ChatColor.RED + "[!!] Bone Meal (x1)");
		}
	}
	
	public static void noBirch(Player player, String reason) {
		if (reason == "permissions") {
			player.sendMessage(ChatColor.RED + "[!!] You don't have permission to use this bow");
		} else if (reason == "item") {
			player.sendMessage(ChatColor.RED + "[!!] You don't have enough resources to use this bow. Needed:");
			player.sendMessage(ChatColor.RED + "[!!] Birch Sapling (x1)");
			player.sendMessage(ChatColor.RED + "[!!] Bone Meal (x1)");
		}
	}
	
	public static void noOak(Player player, String reason) {
		if (reason == "permissions") {
			player.sendMessage(ChatColor.RED + "[!!] You don't have permission to use this bow");
		} else if (reason == "item") {
			player.sendMessage(ChatColor.RED + "[!!] You don't have enough resources to use this bow. Needed:");
			player.sendMessage(ChatColor.RED + "[!!] Oak Sapling (x1)");
			player.sendMessage(ChatColor.RED + "[!!] Bone Meal (x1)");
		}
	}
	
	public static void noEnder(Player player, String reason) {
		if (reason == "permissions") {
			player.sendMessage(ChatColor.RED + "[!!] You don't have permission to use this bow");
		} else if (reason == "item") {
			player.sendMessage(ChatColor.RED + "[!!] You don't have enough resources to use this bow. Needed:");
			player.sendMessage(ChatColor.RED + "[!!] Ender Pearl (x1)");
		}
	}
	
	public static void noClucky(Player player, String reason) {
		if (reason == "permissions") {
			player.sendMessage(ChatColor.RED + "[!!] You don't have permission to use this bow");
		} else if (reason == "item") {
			player.sendMessage(ChatColor.RED + "[!!] You don't have enough resources to use this bow. Needed:");
			player.sendMessage(ChatColor.RED + "[!!] Egg (x1)");
		}
	}
	
	public static void noLightning(Player player, String reason) {
		if (reason == "permissions") {
			player.sendMessage(ChatColor.RED + "[!!] You don't have permission to use this bow");
		} else if (reason == "item") {
			player.sendMessage(ChatColor.RED + "[!!] You don't have enough resources to use this bow. Needed:");
			player.sendMessage(ChatColor.RED + "[!!] Glowstone Dust (x1)");
		}
	}
	
	public static void noExploding(Player player, String reason) {
		if (reason == "permissions") {
			player.sendMessage(ChatColor.RED + "[!!] You don't have permission to use this bow");
		} else if (reason == "item") {
			player.sendMessage(ChatColor.RED + "[!!] You don't have enough resources to use this bow. Needed:");
			player.sendMessage(ChatColor.RED + "[!!] TNT (x1)");
		}
	}
	
}