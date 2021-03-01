package nl.sugcube.dirtyarrows;

import org.bukkit.entity.Projectile;

/**
 * Scheduled ability for the slow bow.
 */
public class Slow implements Runnable {

	public static DirtyArrows plugin;
	
	public Slow(DirtyArrows instance) {
		plugin = instance;
	}
	
	@Override
	public void run() {
		for (Projectile proj : plugin.slow) {
			proj.setVelocity(plugin.slowVec.get(plugin.slow.indexOf(proj)));
		}
	}

}