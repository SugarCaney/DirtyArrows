package nl.sugcube.dirtyarrows.util;

import nl.sugcube.dirtyarrows.DirtyArrows;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Error {
	
	public static void noFrozen(Player player, String string) {
		if (DirtyArrows.MINIGAME_VERSION) {
			return;
		}
		
		if (string.equalsIgnoreCase("permissions")) {
			player.sendMessage(ChatColor.RED + "[!!] You don't have permission to use this bow");
		} else {
			player.sendMessage(ChatColor.RED + "[!!] You don't have enough resources to use this bow. Needed:");
			player.sendMessage(ChatColor.RED + "[!!] Snowball (x1)");
		}
	} 
	
	public static void noCurse(Player player, String string) {
		if (DirtyArrows.MINIGAME_VERSION) {
			return;
		}
		
		if (string.equalsIgnoreCase("permissions")) {
			player.sendMessage(ChatColor.RED + "[!!] You don't have permission to use this bow");
		} else {
			player.sendMessage(ChatColor.RED + "[!!] You don't have enough resources to use this bow. Needed:");
			player.sendMessage(ChatColor.RED + "[!!] Fermented Spider Eye (x1)");
		}
	} 
	
	public static void noIron(Player player, String string) {
		if (DirtyArrows.MINIGAME_VERSION) {
			return;
		}
		
		if (string.equalsIgnoreCase("permissions")) {
			player.sendMessage(ChatColor.RED + "[!!] You don't have permission to use this bow");
		} else {
			player.sendMessage(ChatColor.RED + "[!!] You don't have enough resources to use this bow. Needed:");
			player.sendMessage(ChatColor.RED + "[!!] Anvil (x1)");
		}
	} 
	
	public static void noAirship(Player player, String string) {
		if (DirtyArrows.MINIGAME_VERSION) {
			return;
		}
		
		if (string.equalsIgnoreCase("permissions")) {
			player.sendMessage(ChatColor.RED + "[!!] You don't have permission to use this bow");
		} else {
			player.sendMessage(ChatColor.RED + "[!!] You don't have enough resources to use this bow. Needed:");
			player.sendMessage(ChatColor.RED + "[!!] Feather (x2)");
		}
	} 
	
	public static void noSulphur(Player player, String string) {
		if (DirtyArrows.MINIGAME_VERSION) {
			return;
		}
		
		if (string.equalsIgnoreCase("permissions")) {
			player.sendMessage(ChatColor.RED + "[!!] You don't have permission to use this bow");
		} else {
			player.sendMessage(ChatColor.RED + "[!!] You don't have enough resources to use this bow. Needed:");
			player.sendMessage(ChatColor.RED + "[!!] TNT (x5)");
		}
	} 
	
	public static void noDarkOak(Player player, String string) {
		if (DirtyArrows.MINIGAME_VERSION) {
			return;
		}
		
		if (string.equalsIgnoreCase("permissions")) {
			player.sendMessage(ChatColor.RED + "[!!] You don't have permission to use this bow");
		} else {
			player.sendMessage(ChatColor.RED + "[!!] You don't have enough resources to use this bow. Needed:");
			player.sendMessage(ChatColor.RED + "[!!] Dark Oak Sapling (x4)");
			player.sendMessage(ChatColor.RED + "[!!] Bone Meal (x1)");
		}
	} 
	
	public static void noAcacia(Player player, String string) {
		if (DirtyArrows.MINIGAME_VERSION) {
			return;
		}
		
		if (string.equalsIgnoreCase("permissions")) {
			player.sendMessage(ChatColor.RED + "[!!] You don't have permission to use this bow");
		} else {
			player.sendMessage(ChatColor.RED + "[!!] You don't have enough resources to use this bow. Needed:");
			player.sendMessage(ChatColor.RED + "[!!] Acacia Sapling (x1)");
			player.sendMessage(ChatColor.RED + "[!!] Bone Meal (x1)");
		}
	} 
	
	public static void noParalyze(Player player, String string) {
		if (DirtyArrows.MINIGAME_VERSION) {
			return;
		}
		
		if (string.equalsIgnoreCase("permissions")) {
			player.sendMessage(ChatColor.RED + "[!!] You don't have permission to use this bow");
		} else {
			player.sendMessage(ChatColor.RED + "[!!] You don't have enough resources to use this bow. Needed:");
			player.sendMessage(ChatColor.RED + "[!!] Nether Wart (x1)");
		}
	} 
	
	public static void noAquatic(Player player, String string) {
		if (DirtyArrows.MINIGAME_VERSION) {
			return;
		}
		
		if (string.equalsIgnoreCase("permissions")) {
			player.sendMessage(ChatColor.RED + "[!!] You don't have permission to use this bow");
		} else {
			player.sendMessage(ChatColor.RED + "[!!] You don't have enough resources to use this bow. Needed:");
			player.sendMessage(ChatColor.RED + "[!!] Water Bucket (x1)");
		}
	} 
	
	public static void noMagmatic(Player player, String string) {
		if (DirtyArrows.MINIGAME_VERSION) {
			return;
		}
		
		if (string.equalsIgnoreCase("permissions")) {
			player.sendMessage(ChatColor.RED + "[!!] You don't have permission to use this bow");
		} else {
			player.sendMessage(ChatColor.RED + "[!!] You don't have enough resources to use this bow. Needed:");
			player.sendMessage(ChatColor.RED + "[!!] Lava Bucket (x1)");
		}
	} 
	
	public static void noAirstrike(Player player, String string) {
		if (DirtyArrows.MINIGAME_VERSION) {
			return;
		}
		
		if (string.equalsIgnoreCase("permissions")) {
			player.sendMessage(ChatColor.RED + "[!!] You don't have permission to use this bow");
		} else {
			player.sendMessage(ChatColor.RED + "[!!] You don't have enough resources to use this bow. Needed:");
			player.sendMessage(ChatColor.RED + "[!!] TNT");
		}
	} 
	
	public static void noMulti(Player player, String string) {
		if (DirtyArrows.MINIGAME_VERSION) {
			return;
		}
		
		if (string.equalsIgnoreCase("permissions")) {
			player.sendMessage(ChatColor.RED + "[!!] You don't have permission to use this bow");
		} else {
			player.sendMessage(ChatColor.RED + "[!!] You don't have enough resources to use this bow. Needed:");
			player.sendMessage(ChatColor.RED + "[!!] Arrow (x8)");
		}
	} 
	
	public static void noBomb(Player player, String string) {
		if (DirtyArrows.MINIGAME_VERSION) {
			return;
		}
		
		if (string.equalsIgnoreCase("permissions")) {
			player.sendMessage(ChatColor.RED + "[!!] You don't have permission to use this bow");
		} else {
			player.sendMessage(ChatColor.RED + "[!!] You don't have enough resources to use this bow. Needed:");
			player.sendMessage(ChatColor.RED + "[!!] TNT (x3)");
		}
	} 
	
	public static void noWoodsman(Player player) {
		if (DirtyArrows.MINIGAME_VERSION) {
			return;
		}
		
		player.sendMessage(ChatColor.RED + "[!!] You don't have permission to use this bow");
	} 
	
	public static void noLevel(Player player) {
		if (DirtyArrows.MINIGAME_VERSION) {
			return;
		}
		
		player.sendMessage(ChatColor.RED + "[!!] You don't have permission to use this bow");
	} 
	
	public static void noSlow(Player player) {
		if (DirtyArrows.MINIGAME_VERSION) {
			return;
		}
		
		player.sendMessage(ChatColor.RED + "[!!] You don't have permission to use this bow");
	} 
	
	public static void noDisarm(Player player) {
		if (DirtyArrows.MINIGAME_VERSION) {
			return;
		}
		
		player.sendMessage(ChatColor.RED + "[!!] You don't have permission to use this bow");
	} 
	
	public static void noPull(Player player) {
		if (DirtyArrows.MINIGAME_VERSION) {
			return;
		}
		
		player.sendMessage(ChatColor.RED + "[!!] You don't have permission to use this bow");
	} 
	
	public static void noDraining(Player player) {
		if (DirtyArrows.MINIGAME_VERSION) {
			return;
		}
		
		player.sendMessage(ChatColor.RED + "[!!] You don't have permission to use this bow");
	} 
	
	public static void noSwap(Player player) {
		if (DirtyArrows.MINIGAME_VERSION) {
			return;
		}
		
		player.sendMessage(ChatColor.RED + "[!!] You don't have permission to use this bow");
	} 
	
	public static void noDisorienting(Player player) {
		if (DirtyArrows.MINIGAME_VERSION) {
			return;
		}
		
		player.sendMessage(ChatColor.RED + "[!!] You don't have permission to use this bow");
	} 
	
	public static void noMachine(Player player) {
		if (DirtyArrows.MINIGAME_VERSION) {
			return;
		}
		
		player.sendMessage(ChatColor.RED + "[!!] You don't have permission to use this bow");
	}
	
	public static void noRanged(Player player) {
		if (DirtyArrows.MINIGAME_VERSION) {
			return;
		}
		
		player.sendMessage(ChatColor.RED + "[!!] You don't have permission to use this bow");
	} 
	
	public static void noUndead(Player player, String reason) {
		if (DirtyArrows.MINIGAME_VERSION) {
			return;
		}
		
		if (reason == "permissions") {
			player.sendMessage(ChatColor.RED + "[!!] You don't have permission to use this bow");
		} else if (reason == "item") {
			player.sendMessage(ChatColor.RED + "[!!] You don't have enough resources to use this bow. Needed:");
			player.sendMessage(ChatColor.RED + "[!!] Rotten Flesh (x64)");
		}
	}
	
	public static void noFirey(Player player, String reason) {
		if (DirtyArrows.MINIGAME_VERSION) {
			return;
		}
		
		if (reason == "permissions") {
			player.sendMessage(ChatColor.RED + "[!!] You don't have permission to use this bow");
		} else if (reason == "item") {
			player.sendMessage(ChatColor.RED + "[!!] You don't have enough resources to use this bow. Needed:");
			player.sendMessage(ChatColor.RED + "[!!] Fire Charge (x1)");
		}
	}
	
	public static void noWither(Player player, String reason) {
		if (DirtyArrows.MINIGAME_VERSION) {
			return;
		}
		
		if (reason == "permissions") {
			player.sendMessage(ChatColor.RED + "[!!] You don't have permission to use this bow");
		} else if (reason == "item") {
			player.sendMessage(ChatColor.RED + "[!!] You don't have enough resources to use this bow. Needed:");
			player.sendMessage(ChatColor.RED + "[!!] Soul Sand (x3)");
		}
	}
	
	public static void noFlintAnd(Player player, String reason) {
		if (DirtyArrows.MINIGAME_VERSION) {
			return;
		}
		
		if (reason == "permissions") {
			player.sendMessage(ChatColor.RED + "[!!] You don't have permission to use this bow");
		} else if (reason == "item") {
			player.sendMessage(ChatColor.RED + "[!!] You don't have enough resources to use this bow. Needed:");
			player.sendMessage(ChatColor.RED + "[!!] Flint And Steel");
		}
	}
	
	public static void noPoisonous(Player player, String reason) {
		if (DirtyArrows.MINIGAME_VERSION) {
			return;
		}
		
		if (reason == "permissions") {
			player.sendMessage(ChatColor.RED + "[!!] You don't have permission to use this bow");
		} else if (reason == "item") {
			player.sendMessage(ChatColor.RED + "[!!] You don't have enough resources to use this bow. Needed:");
			player.sendMessage(ChatColor.RED + "[!!] Spider Eye (x1)");
		}
	}
	
	public static void noEnlightened(Player player, String reason) {
		if (DirtyArrows.MINIGAME_VERSION) {
			return;
		}
		
		if (reason == "permissions") {
			player.sendMessage(ChatColor.RED + "[!!] You don't have permission to use this bow");
		} else if (reason == "item") {
			player.sendMessage(ChatColor.RED + "[!!] You don't have enough resources to use this bow. Needed:");
			player.sendMessage(ChatColor.RED + "[!!] Torch (x1)");
		}
	}
	
	public static void noNuclear(Player player, String reason) {
		if (DirtyArrows.MINIGAME_VERSION) {
			return;
		}
		
		if (reason == "permissions") {
			player.sendMessage(ChatColor.RED + "[!!] You don't have permission to use this bow");
		} else if (reason == "item") {
			player.sendMessage(ChatColor.RED + "[!!] You don't have enough resources to use this bow. Needed:");
			player.sendMessage(ChatColor.RED + "[!!] TNT (x64)");
		}
	}
	
	public static void noBatty(Player player, String reason) {
		if (DirtyArrows.MINIGAME_VERSION) {
			return;
		}
		
		if (reason == "permissions") {
			player.sendMessage(ChatColor.RED + "[!!] You don't have permission to use this bow");
		} else if (reason == "item") {
			player.sendMessage(ChatColor.RED + "[!!] You don't have enough resources to use this bow. Needed:");
			player.sendMessage(ChatColor.RED + "[!!] Rotten Flesh (x3)");
		}
	}
	
	public static void noJungle(Player player, String reason) {
		if (DirtyArrows.MINIGAME_VERSION) {
			return;
		}
		
		if (reason == "permissions") {
			player.sendMessage(ChatColor.RED + "[!!] You don't have permission to use this bow");
		} else if (reason == "item") {
			player.sendMessage(ChatColor.RED + "[!!] You don't have enough resources to use this bow. Needed:");
			player.sendMessage(ChatColor.RED + "[!!] Jungle Sapling (x1)");
			player.sendMessage(ChatColor.RED + "[!!] Bone Meal (x1)");
		}
	}
	
	public static void noSpruce(Player player, String reason) {
		if (DirtyArrows.MINIGAME_VERSION) {
			return;
		}
		
		if (reason == "permissions") {
			player.sendMessage(ChatColor.RED + "[!!] You don't have permission to use this bow");
		} else if (reason == "item") {
			player.sendMessage(ChatColor.RED + "[!!] You don't have enough resources to use this bow. Needed:");
			player.sendMessage(ChatColor.RED + "[!!] Spruce Sapling (x1)");
			player.sendMessage(ChatColor.RED + "[!!] Bone Meal (x1)");
		}
	}
	
	public static void noBirch(Player player, String reason) {
		if (DirtyArrows.MINIGAME_VERSION) {
			return;
		}
		
		if (reason == "permissions") {
			player.sendMessage(ChatColor.RED + "[!!] You don't have permission to use this bow");
		} else if (reason == "item") {
			player.sendMessage(ChatColor.RED + "[!!] You don't have enough resources to use this bow. Needed:");
			player.sendMessage(ChatColor.RED + "[!!] Birch Sapling (x1)");
			player.sendMessage(ChatColor.RED + "[!!] Bone Meal (x1)");
		}
	}
	
	public static void noOak(Player player, String reason) {
		if (DirtyArrows.MINIGAME_VERSION) {
			return;
		}
		
		if (reason == "permissions") {
			player.sendMessage(ChatColor.RED + "[!!] You don't have permission to use this bow");
		} else if (reason == "item") {
			player.sendMessage(ChatColor.RED + "[!!] You don't have enough resources to use this bow. Needed:");
			player.sendMessage(ChatColor.RED + "[!!] Oak Sapling (x1)");
			player.sendMessage(ChatColor.RED + "[!!] Bone Meal (x1)");
		}
	}
	
	public static void noEnder(Player player, String reason) {
		if (DirtyArrows.MINIGAME_VERSION) {
			return;
		}
		
		if (reason == "permissions") {
			player.sendMessage(ChatColor.RED + "[!!] You don't have permission to use this bow");
		} else if (reason == "item") {
			player.sendMessage(ChatColor.RED + "[!!] You don't have enough resources to use this bow. Needed:");
			player.sendMessage(ChatColor.RED + "[!!] Ender Pearl (x1)");
		}
	}
	
	public static void noClucky(Player player, String reason) {
		if (DirtyArrows.MINIGAME_VERSION) {
			return;
		}
		
		if (reason == "permissions") {
			player.sendMessage(ChatColor.RED + "[!!] You don't have permission to use this bow");
		} else if (reason == "item") {
			player.sendMessage(ChatColor.RED + "[!!] You don't have enough resources to use this bow. Needed:");
			player.sendMessage(ChatColor.RED + "[!!] Egg (x1)");
		}
	}
	
	public static void noLightning(Player player, String reason) {
		if (DirtyArrows.MINIGAME_VERSION) {
			return;
		}
		
		if (reason == "permissions") {
			player.sendMessage(ChatColor.RED + "[!!] You don't have permission to use this bow");
		} else if (reason == "item") {
			player.sendMessage(ChatColor.RED + "[!!] You don't have enough resources to use this bow. Needed:");
			player.sendMessage(ChatColor.RED + "[!!] Glowstone Dust (x1)");
		}
	}
	
	public static void noExploding(Player player, String reason) {
		if (DirtyArrows.MINIGAME_VERSION) {
			return;
		}
		
		if (reason == "permissions") {
			player.sendMessage(ChatColor.RED + "[!!] You don't have permission to use this bow");
		} else if (reason == "item") {
			player.sendMessage(ChatColor.RED + "[!!] You don't have enough resources to use this bow. Needed:");
			player.sendMessage(ChatColor.RED + "[!!] TNT (x1)");
		}
	}
	
}