package nl.sugcube.dirtyarrows.ability;

import nl.sugcube.dirtyarrows.DirtyArrows;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.inventory.ItemStack;

public class Airstrike implements Runnable {

	public static DirtyArrows plugin;
	
	public Airstrike(DirtyArrows instance) {
		plugin = instance;
	}
	
	@Override
	public void run() {
		for (Projectile proj : plugin.airstrike) {
			if (proj.getShooter() instanceof Player) {
				Player player = (Player) proj.getShooter();
				
				if (plugin.rm.isWithinAXZMargin(proj.getLocation(), 6) == null) {
					if (player.getGameMode() == GameMode.CREATIVE) {
						player.getWorld().spawnEntity(proj.getLocation().add(0, 2, 0), EntityType.PRIMED_TNT);
					} else {
						if (player.getInventory().contains(Material.TNT)) {
							player.getWorld().spawnEntity(proj.getLocation().add(0, 2, 0), EntityType.PRIMED_TNT);
							player.getInventory().removeItem(new ItemStack(Material.TNT, 1));
						}
					}
				}
			}
		}
	}

}