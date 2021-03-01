package nl.sugcube.dirtyarrows.ability;

import nl.sugcube.dirtyarrows.DirtyArrows;
import nl.sugcube.dirtyarrows.util.Locations;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;

/**
 * Handles the iron bow:
 * - Damages entities in range while flying (done in the scheduled repeating task).
 * - Damages entities in 3 range of the landing spot (done with event handler).
 */
public class Iron implements Runnable, Listener {

	public DirtyArrows plugin;
	
	public Iron(DirtyArrows instance) {
		plugin = instance;
	}
	
	@Override
	public void run() {
		for (FallingBlock fb : plugin.anvils.keySet()) {
			for (Entity en : fb.getWorld().getEntities()) {
				if (!(en instanceof LivingEntity)) {
					continue;
				}
				if (Locations.isCloseTo(fb.getLocation(), en.getLocation(), 1)) {
					((LivingEntity) en).damage(3.0f);
				}
			}
			
			plugin.anvils.put(fb, plugin.anvils.get(fb) - 1);
			if (plugin.anvils.get(fb) < 0) {
				plugin.anvils.remove(fb);
			}
		}
	}
	
	@EventHandler
	public void onBlockChange(EntityChangeBlockEvent e) {
		if (e.getEntity() instanceof FallingBlock) {
			if (plugin.anvils.containsKey(e.getEntity())) {
				for (Entity ent : e.getEntity().getWorld().getEntities()) {
					if (ent instanceof LivingEntity) {
						LivingEntity len = (LivingEntity) ent;
						if (Locations.isCloseTo(len.getLocation(), e.getEntity().getLocation(), 3)) {
							if (len instanceof Player) {
								((Player) len).playSound(e.getEntity().getLocation(), Sound.BLOCK_ANVIL_LAND, 1, 1);
							}
							len.damage(10.0f);
						}
					}
				}
			}
			plugin.anvils.remove(e.getEntity());
		}
	}
	
}
