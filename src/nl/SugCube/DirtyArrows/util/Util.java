package nl.sugcube.dirtyarrows.util;

import java.util.Collection;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class Util {

	public static final String NONE = "NONE--------";
	
	/**
	 * @return Util#NONE when there is no bastard. Otherwise it will return the config-node.
	 */
	public static String isBastard(String name) {
		name = name.trim();
		if (name.equalsIgnoreCase(Help.plugin.getConfig().getString("exploding.name"))) return "exploding";
		if (name.equalsIgnoreCase(Help.plugin.getConfig().getString("lightning.name"))) return "lightning";
		if (name.equalsIgnoreCase(Help.plugin.getConfig().getString("clucky.name"))) return "clucky";
		if (name.equalsIgnoreCase(Help.plugin.getConfig().getString("ender.name"))) return "ender";
		if (name.equalsIgnoreCase(Help.plugin.getConfig().getString("oak.name"))) return "oak";
		if (name.equalsIgnoreCase(Help.plugin.getConfig().getString("birch.name"))) return "birch";
		if (name.equalsIgnoreCase(Help.plugin.getConfig().getString("spruce.name"))) return "spruce";
		if (name.equalsIgnoreCase(Help.plugin.getConfig().getString("jungle.name"))) return "jungle";
		if (name.equalsIgnoreCase(Help.plugin.getConfig().getString("batty.name"))) return "batty";
		if (name.equalsIgnoreCase(Help.plugin.getConfig().getString("nuclear.name"))) return "nuclear";
		if (name.equalsIgnoreCase(Help.plugin.getConfig().getString("enlightened.name"))) return "enlightened";
		if (name.equalsIgnoreCase(Help.plugin.getConfig().getString("ranged.name"))) return "ranged";
		if (name.equalsIgnoreCase(Help.plugin.getConfig().getString("machine.name"))) return "machine";
		if (name.equalsIgnoreCase(Help.plugin.getConfig().getString("poisonous.name"))) return "poisonous";
		if (name.equalsIgnoreCase(Help.plugin.getConfig().getString("disorienting.name"))) return "disorienting";
		if (name.equalsIgnoreCase(Help.plugin.getConfig().getString("swap.name"))) return "swap";
		if (name.equalsIgnoreCase(Help.plugin.getConfig().getString("draining.name"))) return "draining";
		if (name.equalsIgnoreCase(Help.plugin.getConfig().getString("flintand.name"))) return "flintand";
		if (name.equalsIgnoreCase(Help.plugin.getConfig().getString("disarming.name"))) return "disarming";
		if (name.equalsIgnoreCase(Help.plugin.getConfig().getString("wither.name"))) return "wither";
		if (name.equalsIgnoreCase(Help.plugin.getConfig().getString("firey.name"))) return "firey";
		if (name.equalsIgnoreCase(Help.plugin.getConfig().getString("slow.name"))) return "slow";
		if (name.equalsIgnoreCase(Help.plugin.getConfig().getString("level.name"))) return "level";
		if (name.equalsIgnoreCase(Help.plugin.getConfig().getString("undead.name"))) return "undead";
		if (name.equalsIgnoreCase(Help.plugin.getConfig().getString("woodman.name"))) return "woodman";
		if (name.equalsIgnoreCase(Help.plugin.getConfig().getString("starvation.name"))) return "starvation";
		if (name.equalsIgnoreCase(Help.plugin.getConfig().getString("multi.name"))) return "multi";
		if (name.equalsIgnoreCase(Help.plugin.getConfig().getString("bomb.name"))) return "bomb";
		if (name.equalsIgnoreCase(Help.plugin.getConfig().getString("drop.name"))) return "drop";
		if (name.equalsIgnoreCase(Help.plugin.getConfig().getString("airstrike.name"))) return "airstrike";
		if (name.equalsIgnoreCase(Help.plugin.getConfig().getString("magmatic.name"))) return "magmatic";
		if (name.equalsIgnoreCase(Help.plugin.getConfig().getString("aquatic.name"))) return "aquatic";
		if (name.equalsIgnoreCase(Help.plugin.getConfig().getString("pull.name"))) return "pull";
		if (name.equalsIgnoreCase(Help.plugin.getConfig().getString("paralyze.name"))) return "paralyze";
		if (name.equalsIgnoreCase(Help.plugin.getConfig().getString("acacia.name"))) return "acacia";
		if (name.equalsIgnoreCase(Help.plugin.getConfig().getString("darkoak.name"))) return "darkoak";
		if (name.equalsIgnoreCase(Help.plugin.getConfig().getString("cluster.name"))) return "cluster";
		if (name.equalsIgnoreCase(Help.plugin.getConfig().getString("airship.name"))) return "airship";
		if (name.equalsIgnoreCase(Help.plugin.getConfig().getString("iron.name"))) return "iron";
		if (name.equalsIgnoreCase(Help.plugin.getConfig().getString("curse.name"))) return "curse";
		if (name.equalsIgnoreCase(Help.plugin.getConfig().getString("round.name"))) return "round";
		if (name.equalsIgnoreCase(Help.plugin.getConfig().getString("frozen.name"))) return "frozen";
		return NONE;
	}
	
	public static boolean inRegionOf(Location loc, Location origin, double margin) {
		if (Math.abs(loc.getX() - origin.getX()) > margin) {
			return false;
		}
		if (Math.abs(loc.getY() - origin.getY()) > margin) {
			return false;
		}
		if (Math.abs(loc.getZ() - origin.getZ()) > margin) {
			return false;
		}
		return true;
	}
	
	public static Vector ranCluster(Random ran) {
		Vector v = new Vector();
		v.setX(0.3 - 0.3 + ran.nextDouble() * 0.147 * 2);
		v.setY(0.7 + ran.nextDouble() * 0.35);
		v.setZ(0.3 - 0.3 + ran.nextDouble() * 0.147 * 2);
		return v;
	}
	
	public static double ranOffset(double base, double offset, Random ran) {
		return base + offset - (ran.nextDouble() * offset * 2);
	}
	
	public static Player[] toPlayerArray(Collection<? extends Player> list) {
		int count = list.size();
		Player[] array = new Player[count];
		int i = 0;
		for (Player p : list) {
			array[i] = p;
			i++;
		}
		return array;
	}
	
	public static Player[] getOnlinePlayers() {
		return toPlayerArray(Bukkit.getOnlinePlayers());
	}
	
}
