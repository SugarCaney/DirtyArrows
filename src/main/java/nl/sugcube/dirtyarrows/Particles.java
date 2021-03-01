package nl.sugcube.dirtyarrows;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Projectile;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles all particles as scheduled task.
 */
public class Particles implements Runnable {

	public static DirtyArrows plugin;
	
	private List<FallingBlock> toRemove = new ArrayList<FallingBlock>();
	
	public Particles(DirtyArrows instance) {
		plugin = instance;
	}
	
	@Override
	public void run() {
		for (Projectile proj : plugin.particleExploding) {
			proj.getLocation().getWorld().playEffect(proj.getLocation().add(0.5, 0.5, 0.5), Effect.SMOKE, 0);
		}
		
		for (Projectile proj : plugin.particleFire) {
			proj.getLocation().getWorld().playEffect(proj.getLocation().add(0.5, 0.5, 0.5), Effect.MOBSPAWNER_FLAMES, 0);
		}
		
		for (Projectile proj : plugin.iceParticle) {
			proj.getLocation().getWorld().playEffect(proj.getLocation().add(0.1, 0.1, 0.1), Effect.STEP_SOUND, Material.SNOW_BLOCK);
		}
		
		for (FallingBlock proj : plugin.particleLava) {
			proj.getLocation().getWorld().playEffect(proj.getLocation().add(0.5, 0.5, 0.5), Effect.STEP_SOUND, Material.LAVA);
			if (proj.isOnGround()) {
				toRemove.add(proj);
			}
		}
		
		for (FallingBlock proj : plugin.particleWater) {
			proj.getLocation().getWorld().playEffect(proj.getLocation().add(0.5, 0.5, 0.5), Effect.STEP_SOUND, Material.WATER);
			if (proj.isOnGround()) {
				toRemove.add(proj);
			}
		}
		
		for (FallingBlock fb : toRemove) {
			plugin.particleLava.remove(fb);
			plugin.particleWater.remove(fb);
		}
	}

}