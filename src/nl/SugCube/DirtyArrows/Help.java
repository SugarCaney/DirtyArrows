package nl.SugCube.DirtyArrows;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Help {
	
	public static void showMainHelpPage3(Player player) {
		player.sendMessage(">>" + ChatColor.GREEN + "----" + ChatColor.WHITE + "> " + ChatColor.YELLOW + "DirtyArrows v1" + 
				ChatColor.RED + " MrSugarCaney" + ChatColor.WHITE + " <" + ChatColor.GREEN + "----" + ChatColor.WHITE + "<<");
		player.sendMessage(ChatColor.YELLOW + "Use /da to " + ChatColor.GREEN + "enable" + ChatColor.YELLOW + " or " +
				ChatColor.RED + "disable " + ChatColor.YELLOW + "the plugin.");
		player.sendMessage(ChatColor.YELLOW + "Rename your bow in an anvil to unlock their powers:");
		player.sendMessage(ChatColor.GOLD + "13 Machine Bastard " + ChatColor.WHITE + "Machine Gun!");
		player.sendMessage(ChatColor.GOLD + "14 Poisonous Bastard " + ChatColor.WHITE + "Poisons Target " +
				ChatColor.AQUA + "SpiderEye(1)");
		player.sendMessage(ChatColor.GOLD + "15 Disorienting Bastard " + ChatColor.WHITE + "Disorients Target");
		player.sendMessage(ChatColor.GOLD + "16 Swap Bastard " + ChatColor.WHITE + "Swaps yourself with the target");
		player.sendMessage(ChatColor.GOLD + "17 Draining Bastard " + ChatColor.WHITE + "Heal for 1/3 of your dealt damage");
		player.sendMessage(ChatColor.GOLD + "18 Flint and Bastard " + ChatColor.WHITE + "Set stuff on fire " +
				ChatColor.AQUA + "FlintAndSteel(dura)");
		player.sendMessage(">>" + ChatColor.GREEN + "----" + ChatColor.WHITE + "> " + ChatColor.YELLOW + "Page 3/3" + 
				ChatColor.RED + " /da help #" + ChatColor.WHITE + " <" + ChatColor.GREEN + "----" + ChatColor.WHITE + "<<");
	}
	
	public static void showMainHelpPage2(Player player) {
		player.sendMessage(">>" + ChatColor.GREEN + "----" + ChatColor.WHITE + "> " + ChatColor.YELLOW + "DirtyArrows v1" + 
				ChatColor.RED + " MrSugarCaney" + ChatColor.WHITE + " <" + ChatColor.GREEN + "----" + ChatColor.WHITE + "<<");
		player.sendMessage(ChatColor.YELLOW + "Use /da to " + ChatColor.GREEN + "enable" + ChatColor.YELLOW + " or " +
				ChatColor.RED + "disable " + ChatColor.YELLOW + "the plugin.");
		player.sendMessage(ChatColor.YELLOW + "Rename your bow in an anvil to unlock their powers:");
		player.sendMessage(ChatColor.GOLD + "07 Spruce Bastard " + ChatColor.WHITE + "Spawns a Pine " +
				ChatColor.AQUA + "SpruceSapl.(1) BoneMeal(1)");
		player.sendMessage(ChatColor.GOLD + "08 Jungle Bastard " + ChatColor.WHITE + "Spawn JungleTree " +
				ChatColor.AQUA + "JungleSapl.(1) BoneM.(1)");
		player.sendMessage(ChatColor.GOLD + "09 Batty Bastard " + ChatColor.WHITE + "Spawns 10 Bats " +
				ChatColor.AQUA + "RottenFlesh(3)");
		player.sendMessage(ChatColor.GOLD + "10 Nuclear Bastard " + ChatColor.WHITE + "Nuclear Arrows " +
				ChatColor.AQUA + "TNT(64)");
		player.sendMessage(ChatColor.GOLD + "11 Enligtened Bastard " + ChatColor.WHITE + "Places Torches " +
				ChatColor.AQUA + "Torch(1)");
		player.sendMessage(ChatColor.GOLD + "12 Ranged Bastard " + ChatColor.WHITE + "Long ranged power shots");
		player.sendMessage(">>" + ChatColor.GREEN + "----" + ChatColor.WHITE + "> " + ChatColor.YELLOW + "Page 2/3" + 
				ChatColor.RED + " /da help #" + ChatColor.WHITE + " <" + ChatColor.GREEN + "----" + ChatColor.WHITE + "<<");
	}

	public static void showMainHelpPage1(Player player) {	
		player.sendMessage(">>" + ChatColor.GREEN + "----" + ChatColor.WHITE + "> " + ChatColor.YELLOW + "DirtyArrows v1" + 
					ChatColor.RED + " MrSugarCaney" + ChatColor.WHITE + " <" + ChatColor.GREEN + "----" + ChatColor.WHITE + "<<");
		player.sendMessage(ChatColor.YELLOW + "Use /da to " + ChatColor.GREEN + "enable" + ChatColor.YELLOW + " or " +
					ChatColor.RED + "disable " + ChatColor.YELLOW + "the plugin.");
		player.sendMessage(ChatColor.YELLOW + "Rename your bow in an anvil to unlock their powers:");
		player.sendMessage(ChatColor.GOLD + "01 Exploding Bastard " + ChatColor.WHITE + "Exploding Arrows " +
					ChatColor.AQUA + "TNT(1)");
		player.sendMessage(ChatColor.GOLD + "02 Lightning Bastard " + ChatColor.WHITE + "Creates Lightning " +
					ChatColor.AQUA + "GlowstoneDust(1)");
		player.sendMessage(ChatColor.GOLD + "03 Clucky Bastard " + ChatColor.WHITE + "Spawns Chickens " +
					ChatColor.AQUA + "Egg(1)");
		player.sendMessage(ChatColor.GOLD + "04 Ender Bastard " + ChatColor.WHITE + "Teleports " +
					ChatColor.AQUA + "EnderPearl(1)");
		player.sendMessage(ChatColor.GOLD + "05 Oak Bastard " + ChatColor.WHITE + "Spawns an Oak " +
					ChatColor.AQUA + "OakSapling(1) BoneMeal(1)");
		player.sendMessage(ChatColor.GOLD + "06 Birch Bastard " + ChatColor.WHITE + "Spawns a Birch " +
					ChatColor.AQUA + "BirchSapling(1) BoneMeal(1)");
		player.sendMessage(">>" + ChatColor.GREEN + "----" + ChatColor.WHITE + "> " + ChatColor.YELLOW + "Page 1/3" + 
					ChatColor.RED + " /da help #" + ChatColor.WHITE + " <" + ChatColor.GREEN + "----" + ChatColor.WHITE + "<<");
	}
	
}
