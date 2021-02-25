package nl.sugcube.dirtyarrows.listener;

import nl.sugcube.dirtyarrows.DirtyArrows;

import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class PlayerDamageListener implements Listener {

	public DirtyArrows plugin;
	
	public PlayerDamageListener(DirtyArrows instance) {
		plugin = instance;
	}
	
	@EventHandler
	public void onPlayerDamage(EntityDamageEvent e) {
		if (e.getEntity() instanceof Player) {
			Player player = (Player) e.getEntity();
			if (e.getCause() == DamageCause.FALL) {
				boolean noDamage = false;
				
				for (Projectile proj : plugin.airship) {
					if (proj.getShooter() == player) {
						noDamage = true;
						break;
					}
				}
				
				if (noDamage || plugin.noFallDamage.contains(player.getUniqueId())) {
					e.setDamage(0);
					e.setCancelled(true);
					plugin.noFallDamage.remove(player.getUniqueId());
				}
			}
		}
	}
	
}
