package nl.sugcube.dirtyarrows.ability;

import nl.sugcube.dirtyarrows.DirtyArrows;
import nl.sugcube.dirtyarrows.util.Message;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Random;

/**
 * Scheduled repeating task : Applies the effects of the cursed bastard.
 */
public class CurseListener implements Runnable {
	
	public DirtyArrows plugin;
	
	private Random ran;
	
	public CurseListener(DirtyArrows instance) {
		plugin = instance;
		ran = new Random();
	}
	
	@Override
	public void run() {
		for (Entity ent : plugin.cursed.keySet()) {
			plugin.cursed.put(ent, plugin.cursed.get(ent) - 1);
			if (plugin.cursed.get(ent) <= 0) {
				plugin.cursed.remove(ent);
				
				if (ent instanceof Player) {
					Player player = (Player) ent;
					if (player.isOnline()) {
						player.sendMessage(Message.getTag(plugin) + ChatColor.GRAY + "The curse has been lifted.");
					}
				}
			}
			
			addRandomEffect(ent);
		}
	}
	 
	public void addRandomEffect(Entity ent) {
		if (ent instanceof Player) {
			Player player = (Player) ent;
			if (!player.isOnline()) {
				return;
			}
		}
		
		if (ran.nextFloat() < 0.345) {
			if (ran.nextFloat() < 0.2) {
				Location loc = ent.getLocation().clone();
				float yaw = loc.getYaw() - 60 + 120 * ran.nextFloat();
				float pitch = loc.getPitch() - 60 + 120 * ran.nextFloat();
				loc.setYaw(yaw);
				loc.setPitch(pitch);
				ent.teleport(loc);
			}
			else if (ran.nextFloat() < 0.25) {
				ent.setVelocity(ent.getVelocity().add(new Vector(0, 0.94, 0)));
			}
			else if (ran.nextFloat() < 0.33) {
				ent.setFireTicks((int) (45 * ran.nextFloat()) + 25);
			}
			else if (ran.nextBoolean()) {
				double x = ent.getLocation().getX() - 0.2 + 0.4 * ran.nextDouble();
				double z = ent.getLocation().getZ() - 0.2 + 0.4 * ran.nextDouble();
				Vector vec = ent.getVelocity().clone().add(new Vector(x, 0.025, z));
				ent.setVelocity(vec);
			}
			else {
				double x = ent.getLocation().getX();
				double y = ent.getLocation().getY() + 0.5;
				double z = ent.getLocation().getZ();
				ent.getWorld().createExplosion(x, y, z, 1f, false, true);
			}
		}
	}
	
}
