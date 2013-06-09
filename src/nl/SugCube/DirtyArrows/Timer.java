package nl.SugCube.DirtyArrows;

import org.bukkit.entity.Projectile;

public class Timer implements Runnable {

	public static DirtyArrows plugin;
	
	public Timer(DirtyArrows instance) {
		plugin = instance;
	}
	
	@Override
	public void run() {
		for (Projectile proj : plugin.slow) {
			proj.setVelocity(plugin.slowVec.get(plugin.slow.indexOf(proj)));
		}
	}

}