package nl.SugCube.DirtyArrows;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class TreeCut {
	
	public static boolean returnValue;

	public static boolean cutDownTree(Location loc, Block block, Player player) {
		
		Location location = new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ());
		
		if (block.getTypeId() == 17) {
			
			boolean isLog = true;
			
			while (isLog) {
				if (location.getBlock().getTypeId() == 17) {
					location.setY(location.getY() - 1);
				} else {
					location.setY(location.getY() + 1);
					isLog = false;
				}
			}
			
			boolean isLogs = true;
			
			while (isLogs) {
				if (location.getBlock().getTypeId() == 17) {
					dropWood(loc, location.getBlock().getData(), player);
					location.getBlock().setType(Material.AIR);
					location.setY(location.getY() + 1);
				} else {
					isLogs = false;
				}
			}
			
			return true;
		} else {
			return false;
		}
	}
	
	public static void dropWood(Location loc, int damage, Player player) {
		
		if (new Random().nextInt(4) != 0) {
			loc.getWorld().dropItem(loc, new ItemStack(Material.LOG, 1, (short) damage));
		} else {
			if (new Random().nextInt(2) != 0) {
				loc.getWorld().dropItem(loc, new ItemStack(Material.WOOD, new Random().nextInt(5) + 1, (short) damage));
			}
			if (new Random().nextInt(2) != 0) {
				loc.getWorld().dropItem(loc, new ItemStack(Material.STICK, new Random().nextInt(4) + 1));
			}
		}
		
	}
	
}
