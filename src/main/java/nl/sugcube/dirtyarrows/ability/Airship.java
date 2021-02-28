package nl.sugcube.dirtyarrows.ability;

import nl.sugcube.dirtyarrows.DirtyArrows;

import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;

/**
 * Handler for the airship bow: makes sure to constantly keep flying.
 */
public class Airship implements Runnable {

public static DirtyArrows plugin;
	
	public Airship(DirtyArrows instance) {
		plugin = instance;
	}
	
	@Override
	public void run() {
		for (Projectile proj : plugin.airship) {
			if (proj.getShooter() instanceof Player) {
				Player player = (Player) proj.getShooter();
				player.setVelocity(proj.getVelocity().multiply(0.8));
			}
		}
	}
	
}
