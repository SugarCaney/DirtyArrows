package nl.SugCube.DirtyArrows;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Help {
	
	public static DirtyArrows plugin;
	
	public Help(DirtyArrows instance) {
		plugin = instance;
	}
	
	public static void showMainHelpPage6(Player player) {
		player.sendMessage(">>" + ChatColor.GREEN + "----" + ChatColor.WHITE + "> " + ChatColor.YELLOW + "DirtyArrows v2.5" + 
				ChatColor.RED + " MrSugarCaney" + ChatColor.WHITE + " <" + ChatColor.GREEN + "----" + ChatColor.WHITE + "<<");
		player.sendMessage(ChatColor.YELLOW + "Use /da to " + ChatColor.GREEN + "enable" + ChatColor.YELLOW + " or " +
				ChatColor.RED + "disable " + ChatColor.YELLOW + "the plugin.");
		player.sendMessage(ChatColor.YELLOW + "Rename your bow in an anvil to unlock their powers:");
		if (player.hasPermission("dirtyarrows.magmatic") && plugin.getConfig().getBoolean("magmatic.enabled")) {
			player.sendMessage(ChatColor.GOLD + "31 " + Methods.setColours(plugin.getConfig().getString("magmatic.name")) + ChatColor.WHITE + " Shoot lava! " 
					+ ChatColor.AQUA + "LavaBucket(1)");
		}
		if (player.hasPermission("dirtyarrows.aquatic") && plugin.getConfig().getBoolean("aquatic.enabled")) {
			player.sendMessage(ChatColor.GOLD + "32 " + Methods.setColours(plugin.getConfig().getString("aquatic.name")) + ChatColor.WHITE + " Shoot water! "
					+ ChatColor.AQUA + "WaterBucket(1)");
		}
		if (player.hasPermission("dirtyarrows.pull") && plugin.getConfig().getBoolean("pull.enabled")) {
			player.sendMessage(ChatColor.GOLD + "33 " + Methods.setColours(plugin.getConfig().getString("pull.name")) + ChatColor.WHITE + " Pull others towards you!");
		}
		if (player.hasPermission("dirtyarrows.paralyze") && plugin.getConfig().getBoolean("iron.enabled")) {
			player.sendMessage(ChatColor.GOLD + "34 " + Methods.setColours(plugin.getConfig().getString("paralyze.name")) + ChatColor.WHITE + " Paralyze your opponents!");
		}
		player.sendMessage(">>" + ChatColor.GREEN + "----" + ChatColor.WHITE + "> " + ChatColor.YELLOW + "Page 6/6" + 
				ChatColor.RED + " /da help #" + ChatColor.WHITE + " <" + ChatColor.GREEN + "----" + ChatColor.WHITE + "<<");
	}
	
	public static void showMainHelpPage5(Player player) {
		player.sendMessage(">>" + ChatColor.GREEN + "----" + ChatColor.WHITE + "> " + ChatColor.YELLOW + "DirtyArrows v2.5" + 
				ChatColor.RED + " MrSugarCaney" + ChatColor.WHITE + " <" + ChatColor.GREEN + "----" + ChatColor.WHITE + "<<");
		player.sendMessage(ChatColor.YELLOW + "Use /da to " + ChatColor.GREEN + "enable" + ChatColor.YELLOW + " or " +
				ChatColor.RED + "disable " + ChatColor.YELLOW + "the plugin.");
		player.sendMessage(ChatColor.YELLOW + "Rename your bow in an anvil to unlock their powers:");
		if (player.hasPermission("dirtyarrows.woodsman") && plugin.getConfig().getBoolean("woodsman.enabled")) {
			player.sendMessage(ChatColor.GOLD + "25 " + Methods.setColours(plugin.getConfig().getString("woodsman.name")) + ChatColor.WHITE + " Cut down trees like hell");
		}
		if (player.hasPermission("dirtyarrows.starvation") && plugin.getConfig().getBoolean("starvation.enabled")) {
			player.sendMessage(ChatColor.GOLD + "26 " + Methods.setColours(plugin.getConfig().getString("starvation.name")) + ChatColor.WHITE + " Let your enemy starve");
		}
		if (player.hasPermission("dirtyarrows.multi") && plugin.getConfig().getBoolean("multi.enabled")) {
			player.sendMessage(ChatColor.GOLD + "27 " + Methods.setColours(plugin.getConfig().getString("multi.name")) + ChatColor.WHITE + " Let it rain arrows! " +
					ChatColor.AQUA + "Arrow(8)");
		}
		if (player.hasPermission("dirtyarrows.bomb") && plugin.getConfig().getBoolean("bomb.enabled")) {
			player.sendMessage(ChatColor.GOLD + "28 " + Methods.setColours(plugin.getConfig().getString("bomb.name")) + ChatColor.WHITE + " May death rain upon them! " +
					ChatColor.AQUA + "TNT(3)");
		}
		if (player.hasPermission("dirtyarrows.drop") && plugin.getConfig().getBoolean("drop.enabled")) {
			player.sendMessage(ChatColor.GOLD + "29 " + Methods.setColours(plugin.getConfig().getString("drop.name")) + ChatColor.WHITE + " Let your target drop down");
		}
		if (player.hasPermission("dirtyarrows.airstrike") && plugin.getConfig().getBoolean("airstrike.enabled")) {
			player.sendMessage(ChatColor.GOLD + "30 " + Methods.setColours(plugin.getConfig().getString("airstrike.name")) + ChatColor.WHITE + " Shoot a line of TNT " +
					ChatColor.AQUA + "TNT(X)");
		}
		player.sendMessage(">>" + ChatColor.GREEN + "----" + ChatColor.WHITE + "> " + ChatColor.YELLOW + "Page 5/6" + 
				ChatColor.RED + " /da help #" + ChatColor.WHITE + " <" + ChatColor.GREEN + "----" + ChatColor.WHITE + "<<");
	}
	
	public static void showMainHelpPage4(Player player) {
		player.sendMessage(">>" + ChatColor.GREEN + "----" + ChatColor.WHITE + "> " + ChatColor.YELLOW + "DirtyArrows v2.5" + 
				ChatColor.RED + " MrSugarCaney" + ChatColor.WHITE + " <" + ChatColor.GREEN + "----" + ChatColor.WHITE + "<<");
		player.sendMessage(ChatColor.YELLOW + "Use /da to " + ChatColor.GREEN + "enable" + ChatColor.YELLOW + " or " +
				ChatColor.RED + "disable " + ChatColor.YELLOW + "the plugin.");
		player.sendMessage(ChatColor.YELLOW + "Rename your bow in an anvil to unlock their powers:");
		if (player.hasPermission("dirtyarrows.disarming") && plugin.getConfig().getBoolean("disarming.enabled")) {
			player.sendMessage(ChatColor.GOLD + "19 " + Methods.setColours(plugin.getConfig().getString("disarming.name")) + ChatColor.WHITE + " Disarm your target!");
		}
		if (player.hasPermission("dirtyarrows.wither") && plugin.getConfig().getBoolean("wither.enabled")) {
			player.sendMessage(ChatColor.GOLD + "20 " + Methods.setColours(plugin.getConfig().getString("wither.name")) + ChatColor.WHITE + " Shoot wither skulls " +
					ChatColor.AQUA + "SoulSand(3)");
		}
		if (player.hasPermission("dirtyarrows.firey") && plugin.getConfig().getBoolean("firey.enabled")) {
			player.sendMessage(ChatColor.GOLD + "21 " + Methods.setColours(plugin.getConfig().getString("firey.name")) + ChatColor.WHITE + " Shoot fireballs " +
					ChatColor.AQUA + "FireCharge(1)");
		}
		if (player.hasPermission("dirtyarrows.slow") && plugin.getConfig().getBoolean("slow.enabled")) {
			player.sendMessage(ChatColor.GOLD + "22 " + Methods.setColours(plugin.getConfig().getString("slow.name")) + ChatColor.WHITE + " Slow, but powerful shot!");
		}
		if (player.hasPermission("dirtyarrows.level") && plugin.getConfig().getBoolean("level.enabled")) {
			player.sendMessage(ChatColor.GOLD + "23 " + Methods.setColours(plugin.getConfig().getString("level.name")) + ChatColor.WHITE + " Steals a level from a player");
		}
		if (player.hasPermission("dirtyarrows.undead") && plugin.getConfig().getBoolean("undead.enabled")) {
			player.sendMessage(ChatColor.GOLD + "24 " + Methods.setColours(plugin.getConfig().getString("undead.name")) + ChatColor.WHITE + " Summons the undead " +
					ChatColor.AQUA + "RottenFlesh(64)");
		}
		player.sendMessage(">>" + ChatColor.GREEN + "----" + ChatColor.WHITE + "> " + ChatColor.YELLOW + "Page 4/6" + 
				ChatColor.RED + " /da help #" + ChatColor.WHITE + " <" + ChatColor.GREEN + "----" + ChatColor.WHITE + "<<");
	}
	
	public static void showMainHelpPage3(Player player) {
		player.sendMessage(">>" + ChatColor.GREEN + "----" + ChatColor.WHITE + "> " + ChatColor.YELLOW + "DirtyArrows v2.5" + 
				ChatColor.RED + " MrSugarCaney" + ChatColor.WHITE + " <" + ChatColor.GREEN + "----" + ChatColor.WHITE + "<<");
		player.sendMessage(ChatColor.YELLOW + "Use /da to " + ChatColor.GREEN + "enable" + ChatColor.YELLOW + " or " +
				ChatColor.RED + "disable " + ChatColor.YELLOW + "the plugin.");
		player.sendMessage(ChatColor.YELLOW + "Rename your bow in an anvil to unlock their powers:");
		if (player.hasPermission("dirtyarrows.machine") && plugin.getConfig().getBoolean("machine.enabled")) {
			player.sendMessage(ChatColor.GOLD + "13 " + Methods.setColours(plugin.getConfig().getString("machine.name")) + ChatColor.WHITE + " Machine Gun!");
		}
		if (player.hasPermission("dirtyarrows.poisonous") && plugin.getConfig().getBoolean("poisonous.enabled")) {
			player.sendMessage(ChatColor.GOLD + "14 " + Methods.setColours(plugin.getConfig().getString("poisonous.name")) + ChatColor.WHITE + " Poisons Target " +
					ChatColor.AQUA + "SpiderEye(1)");
		}
		if (player.hasPermission("dirtyarrows.disorienting") && plugin.getConfig().getBoolean("disorienting.enabled")) {
			player.sendMessage(ChatColor.GOLD + "15 " + Methods.setColours(plugin.getConfig().getString("disorienting.name")) + ChatColor.WHITE + " Disorients Target");
		}
		if (player.hasPermission("dirtyarrows.swap") && plugin.getConfig().getBoolean("swap.enabled")) {
			player.sendMessage(ChatColor.GOLD + "16 " + Methods.setColours(plugin.getConfig().getString("swap.name")) + ChatColor.WHITE + " Swaps yourself with the target");
		}
		if (player.hasPermission("dirtyarrows.draining") && plugin.getConfig().getBoolean("draining.enabled")) {
			player.sendMessage(ChatColor.GOLD + "17 " + Methods.setColours(plugin.getConfig().getString("draining.name")) + ChatColor.WHITE + " Heal for 1/3 of your dealt damage");
		}
		if (player.hasPermission("dirtyarrows.flintand") && plugin.getConfig().getBoolean("flintand.enabled")) {
			player.sendMessage(ChatColor.GOLD + "18 " + Methods.setColours(plugin.getConfig().getString("flintand.name")) + ChatColor.WHITE + " Set stuff on fire " +
					ChatColor.AQUA + "FlintAndSteel(dura)");
		}
		player.sendMessage(">>" + ChatColor.GREEN + "----" + ChatColor.WHITE + "> " + ChatColor.YELLOW + "Page 3/6" + 
				ChatColor.RED + " /da help #" + ChatColor.WHITE + " <" + ChatColor.GREEN + "----" + ChatColor.WHITE + "<<");
	}
	
	public static void showMainHelpPage2(Player player) {
		player.sendMessage(">>" + ChatColor.GREEN + "----" + ChatColor.WHITE + "> " + ChatColor.YELLOW + "DirtyArrows v2.5" + 
				ChatColor.RED + " MrSugarCaney" + ChatColor.WHITE + " <" + ChatColor.GREEN + "----" + ChatColor.WHITE + "<<");
		player.sendMessage(ChatColor.YELLOW + "Use /da to " + ChatColor.GREEN + "enable" + ChatColor.YELLOW + " or " +
				ChatColor.RED + "disable " + ChatColor.YELLOW + "the plugin.");
		player.sendMessage(ChatColor.YELLOW + "Rename your bow in an anvil to unlock their powers:");
		if (player.hasPermission("dirtyarrows.birch") && plugin.getConfig().getBoolean("birch.enabled")) {
			player.sendMessage(ChatColor.GOLD + "07 " + Methods.setColours(plugin.getConfig().getString("birch.name")) + ChatColor.WHITE + " Spawns a Birch " +
						ChatColor.AQUA + "BirchSapling(1) BoneMeal(1)");
		}
		if (player.hasPermission("dirtyarrows.jungle") && plugin.getConfig().getBoolean("jungle.enabled")) {
			player.sendMessage(ChatColor.GOLD + "08 " + Methods.setColours(plugin.getConfig().getString("jungle.name")) + ChatColor.WHITE + " Spawn JungleTree " +
					ChatColor.AQUA + "JungleSapl.(1) BoneM.(1)");
		}
		if (player.hasPermission("dirtyarrows.batty") && plugin.getConfig().getBoolean("batty.enabled")) {
			player.sendMessage(ChatColor.GOLD + "09 " + Methods.setColours(plugin.getConfig().getString("batty.name")) + ChatColor.WHITE + " Spawns 10 Bats " +
					ChatColor.AQUA + "RottenFlesh(3)");
		}
		if (player.hasPermission("dirtyarrows.nuclear") && plugin.getConfig().getBoolean("nuclear.enabled")) {
			player.sendMessage(ChatColor.GOLD + "10 " + Methods.setColours(plugin.getConfig().getString("nuclear.name")) + ChatColor.WHITE + " Nuclear Arrows " +
					ChatColor.AQUA + "TNT(64)");
		}
		if (player.hasPermission("dirtyarrows.enlightened") && plugin.getConfig().getBoolean("enlightened.enabled")) {
			player.sendMessage(ChatColor.GOLD + "11 " + Methods.setColours(plugin.getConfig().getString("enlightened.name")) + ChatColor.WHITE + " Places Torches " +
					ChatColor.AQUA + "Torch(1)");
		}
		if (player.hasPermission("dirtyarrows.ranged") && plugin.getConfig().getBoolean("ranged.enabled")) {
			player.sendMessage(ChatColor.GOLD + "12 " + Methods.setColours(plugin.getConfig().getString("ranged.name")) + ChatColor.WHITE + " Long ranged power shots");
		}
		player.sendMessage(">>" + ChatColor.GREEN + "----" + ChatColor.WHITE + "> " + ChatColor.YELLOW + "Page 2/6" + 
				ChatColor.RED + " /da help #" + ChatColor.WHITE + " <" + ChatColor.GREEN + "----" + ChatColor.WHITE + "<<");
	}

	public static void showMainHelpPage1(Player player) {	
		player.sendMessage(">>" + ChatColor.GREEN + "----" + ChatColor.WHITE + "> " + ChatColor.YELLOW + "DirtyArrows v2.5" + 
					ChatColor.RED + " MrSugarCaney" + ChatColor.WHITE + " <" + ChatColor.GREEN + "----" + ChatColor.WHITE + "<<");
		player.sendMessage(ChatColor.YELLOW + "Use /da to " + ChatColor.GREEN + "enable" + ChatColor.YELLOW + " or " +
					ChatColor.RED + "disable " + ChatColor.YELLOW + "the plugin.");
		player.sendMessage(ChatColor.YELLOW + "Rename your bow in an anvil to unlock their powers:");
		if (player.hasPermission("dirtyarrows.exploding") && plugin.getConfig().getBoolean("exploding.enabled")) {
			player.sendMessage(ChatColor.GOLD + "01 " + Methods.setColours(plugin.getConfig().getString("exploding.name")) + ChatColor.WHITE + " Exploding Arrows " +
						ChatColor.AQUA + "TNT(1)");
		}
		if (player.hasPermission("dirtyarrows.lightning") && plugin.getConfig().getBoolean("lightning.enabled")) {
			player.sendMessage(ChatColor.GOLD + "02 " + Methods.setColours(plugin.getConfig().getString("lightning.name")) + ChatColor.WHITE + " Creates Lightning " +
						ChatColor.AQUA + "GlowstoneDust(1)");
		}
		if (player.hasPermission("dirtyarrows.clucky") && plugin.getConfig().getBoolean("clucky.enabled")) {
			player.sendMessage(ChatColor.GOLD + "03 " + Methods.setColours(plugin.getConfig().getString("clucky.name")) + ChatColor.WHITE + " Spawns Chickens " +
						ChatColor.AQUA + "Egg(1)");
		}
		if (player.hasPermission("dirtyarrows.ender") && plugin.getConfig().getBoolean("ender.enabled")) {
			player.sendMessage(ChatColor.GOLD + "04 " + Methods.setColours(plugin.getConfig().getString("ender.name")) + ChatColor.WHITE + " Teleports " +
						ChatColor.AQUA + "EnderPearl(1)");
		}
		if (player.hasPermission("dirtyarrows.oak") && plugin.getConfig().getBoolean("oak.enabled")) {
			player.sendMessage(ChatColor.GOLD + "05 " + Methods.setColours(plugin.getConfig().getString("oak.name")) + ChatColor.WHITE + " Spawns an Oak " +
						ChatColor.AQUA + "OakSapling(1) BoneMeal(1)");
		}
		if (player.hasPermission("dirtyarrows.spruce") && plugin.getConfig().getBoolean("spruce.enabled")) {
			player.sendMessage(ChatColor.GOLD + "06 " + Methods.setColours(plugin.getConfig().getString("spruce.name")) + ChatColor.WHITE + " Spawns a Pine " +
					ChatColor.AQUA + "SpruceSapl.(1) BoneMeal(1)");
		}
		player.sendMessage(">>" + ChatColor.GREEN + "----" + ChatColor.WHITE + "> " + ChatColor.YELLOW + "Page 1/6" + 
					ChatColor.RED + " /da help #" + ChatColor.WHITE + " <" + ChatColor.GREEN + "----" + ChatColor.WHITE + "<<");
	}
	
}
