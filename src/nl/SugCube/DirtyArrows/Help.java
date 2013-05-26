package nl.SugCube.DirtyArrows;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Help {
	
	public static DirtyArrows plugin;
	
	public Help(DirtyArrows instance) {
		plugin = instance;
	}
	
	public static void showMainHelpPage4(Player player) {
		player.sendMessage(">>" + ChatColor.GREEN + "----" + ChatColor.WHITE + "> " + ChatColor.YELLOW + "DirtyArrows v1" + 
				ChatColor.RED + " MrSugarCaney" + ChatColor.WHITE + " <" + ChatColor.GREEN + "----" + ChatColor.WHITE + "<<");
		player.sendMessage(ChatColor.YELLOW + "Use /da to " + ChatColor.GREEN + "enable" + ChatColor.YELLOW + " or " +
				ChatColor.RED + "disable " + ChatColor.YELLOW + "the plugin.");
		player.sendMessage(ChatColor.YELLOW + "Rename your bow in an anvil to unlock their powers:");
		player.sendMessage(ChatColor.GOLD + "19 " + plugin.getConfig().getString("disarming.name") + ChatColor.WHITE + " Disarm your target!");
		player.sendMessage(ChatColor.GOLD + "20 " + plugin.getConfig().getString("wither.name") + ChatColor.WHITE + " Shoot wither skulls " +
				ChatColor.AQUA + "SoulSand(3)");
		player.sendMessage(ChatColor.GOLD + "21 " + plugin.getConfig().getString("firey.name") + ChatColor.WHITE + " Shoot fireballs " +
				ChatColor.AQUA + "FireCharge(1)");
		player.sendMessage(ChatColor.GOLD + "22 " + plugin.getConfig().getString("slow.name") + ChatColor.WHITE + " Slow, but powerful shot!");
		player.sendMessage(ChatColor.GOLD + "23 " + plugin.getConfig().getString("level.name") + ChatColor.WHITE + " Steals a level from a player");
		player.sendMessage(ChatColor.GOLD + "24 " + plugin.getConfig().getString("undead.name") + ChatColor.WHITE + " Summons the undead " +
				ChatColor.AQUA + "RottenFlesh(64)");
		player.sendMessage(">>" + ChatColor.GREEN + "----" + ChatColor.WHITE + "> " + ChatColor.YELLOW + "Page 4/4" + 
				ChatColor.RED + " /da help #" + ChatColor.WHITE + " <" + ChatColor.GREEN + "----" + ChatColor.WHITE + "<<");
	}
	
	public static void showMainHelpPage3(Player player) {
		player.sendMessage(">>" + ChatColor.GREEN + "----" + ChatColor.WHITE + "> " + ChatColor.YELLOW + "DirtyArrows v1" + 
				ChatColor.RED + " MrSugarCaney" + ChatColor.WHITE + " <" + ChatColor.GREEN + "----" + ChatColor.WHITE + "<<");
		player.sendMessage(ChatColor.YELLOW + "Use /da to " + ChatColor.GREEN + "enable" + ChatColor.YELLOW + " or " +
				ChatColor.RED + "disable " + ChatColor.YELLOW + "the plugin.");
		player.sendMessage(ChatColor.YELLOW + "Rename your bow in an anvil to unlock their powers:");
		player.sendMessage(ChatColor.GOLD + "13 " + plugin.getConfig().getString("machine.name") + ChatColor.WHITE + " Machine Gun!");
		player.sendMessage(ChatColor.GOLD + "14 " + plugin.getConfig().getString("poisonous.name") + ChatColor.WHITE + " Poisons Target " +
				ChatColor.AQUA + "SpiderEye(1)");
		player.sendMessage(ChatColor.GOLD + "15 " + plugin.getConfig().getString("disorienting.name") + ChatColor.WHITE + " Disorients Target");
		player.sendMessage(ChatColor.GOLD + "16 " + plugin.getConfig().getString("swap.name") + ChatColor.WHITE + " Swaps yourself with the target");
		player.sendMessage(ChatColor.GOLD + "17 " + plugin.getConfig().getString("draining.name") + ChatColor.WHITE + " Heal for 1/3 of your dealt damage");
		player.sendMessage(ChatColor.GOLD + "18 " + plugin.getConfig().getString("flintand.name") + ChatColor.WHITE + " Set stuff on fire " +
				ChatColor.AQUA + "FlintAndSteel(dura)");
		player.sendMessage(">>" + ChatColor.GREEN + "----" + ChatColor.WHITE + "> " + ChatColor.YELLOW + "Page 3/4" + 
				ChatColor.RED + " /da help #" + ChatColor.WHITE + " <" + ChatColor.GREEN + "----" + ChatColor.WHITE + "<<");
	}
	
	public static void showMainHelpPage2(Player player) {
		player.sendMessage(">>" + ChatColor.GREEN + "----" + ChatColor.WHITE + "> " + ChatColor.YELLOW + "DirtyArrows v1" + 
				ChatColor.RED + " MrSugarCaney" + ChatColor.WHITE + " <" + ChatColor.GREEN + "----" + ChatColor.WHITE + "<<");
		player.sendMessage(ChatColor.YELLOW + "Use /da to " + ChatColor.GREEN + "enable" + ChatColor.YELLOW + " or " +
				ChatColor.RED + "disable " + ChatColor.YELLOW + "the plugin.");
		player.sendMessage(ChatColor.YELLOW + "Rename your bow in an anvil to unlock their powers:");
		player.sendMessage(ChatColor.GOLD + "07 " + plugin.getConfig().getString("spruce.name") + ChatColor.WHITE + " Spawns a Pine " +
				ChatColor.AQUA + "SpruceSapl.(1) BoneMeal(1)");
		player.sendMessage(ChatColor.GOLD + "08 " + plugin.getConfig().getString("jungle.name") + ChatColor.WHITE + " Spawn JungleTree " +
				ChatColor.AQUA + "JungleSapl.(1) BoneM.(1)");
		player.sendMessage(ChatColor.GOLD + "09 " + plugin.getConfig().getString("batty.name") + ChatColor.WHITE + " Spawns 10 Bats " +
				ChatColor.AQUA + "RottenFlesh(3)");
		player.sendMessage(ChatColor.GOLD + "10 " + plugin.getConfig().getString("nuclear.name") + ChatColor.WHITE + " Nuclear Arrows " +
				ChatColor.AQUA + "TNT(64)");
		player.sendMessage(ChatColor.GOLD + "11 " + plugin.getConfig().getString("enlightened.name") + ChatColor.WHITE + " Places Torches " +
				ChatColor.AQUA + "Torch(1)");
		player.sendMessage(ChatColor.GOLD + "12 " + plugin.getConfig().getString("ranged.name") + ChatColor.WHITE + " Long ranged power shots");
		player.sendMessage(">>" + ChatColor.GREEN + "----" + ChatColor.WHITE + "> " + ChatColor.YELLOW + "Page 2/4" + 
				ChatColor.RED + " /da help #" + ChatColor.WHITE + " <" + ChatColor.GREEN + "----" + ChatColor.WHITE + "<<");
	}

	public static void showMainHelpPage1(Player player) {	
		player.sendMessage(">>" + ChatColor.GREEN + "----" + ChatColor.WHITE + "> " + ChatColor.YELLOW + "DirtyArrows v1" + 
					ChatColor.RED + " MrSugarCaney" + ChatColor.WHITE + " <" + ChatColor.GREEN + "----" + ChatColor.WHITE + "<<");
		player.sendMessage(ChatColor.YELLOW + "Use /da to " + ChatColor.GREEN + "enable" + ChatColor.YELLOW + " or " +
					ChatColor.RED + "disable " + ChatColor.YELLOW + "the plugin.");
		player.sendMessage(ChatColor.YELLOW + "Rename your bow in an anvil to unlock their powers:");
		player.sendMessage(ChatColor.GOLD + "01 " + plugin.getConfig().getString("exploding.name") + ChatColor.WHITE + " Exploding Arrows " +
					ChatColor.AQUA + "TNT(1)");
		player.sendMessage(ChatColor.GOLD + "02 " + plugin.getConfig().getString("lightning.name") + ChatColor.WHITE + "Creates Lightning " +
					ChatColor.AQUA + "GlowstoneDust(1)");
		player.sendMessage(ChatColor.GOLD + "03 " + plugin.getConfig().getString("clucky.name") + ChatColor.WHITE + " Spawns Chickens " +
					ChatColor.AQUA + "Egg(1)");
		player.sendMessage(ChatColor.GOLD + "04 " + plugin.getConfig().getString("ender.name") + ChatColor.WHITE + " Teleports " +
					ChatColor.AQUA + "EnderPearl(1)");
		player.sendMessage(ChatColor.GOLD + "05 " + plugin.getConfig().getString("oak.name") + ChatColor.WHITE + " Spawns an Oak " +
					ChatColor.AQUA + "OakSapling(1) BoneMeal(1)");
		player.sendMessage(ChatColor.GOLD + "06 " + plugin.getConfig().getString("birch.name") + ChatColor.WHITE + " Spawns a Birch " +
					ChatColor.AQUA + "BirchSapling(1) BoneMeal(1)");
		player.sendMessage(">>" + ChatColor.GREEN + "----" + ChatColor.WHITE + "> " + ChatColor.YELLOW + "Page 1/4" + 
					ChatColor.RED + " /da help #" + ChatColor.WHITE + " <" + ChatColor.GREEN + "----" + ChatColor.WHITE + "<<");
	}
	
}
