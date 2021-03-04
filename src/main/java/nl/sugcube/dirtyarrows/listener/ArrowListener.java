package nl.sugcube.dirtyarrows.listener;

import nl.sugcube.dirtyarrows.Broadcast;
import nl.sugcube.dirtyarrows.DirtyArrows;
import nl.sugcube.dirtyarrows.effect.HeadshotType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class ArrowListener implements Listener {

	public static DirtyArrows plugin;

	public ArrowListener(DirtyArrows instance) {
		plugin = instance;
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if (event.getCause() == DamageCause.PROJECTILE && plugin.getConfig().getBoolean("headshot")) {
			Projectile proj = (Projectile) event.getDamager();
			if (proj.getShooter() instanceof Player) {
				Entity shot = event.getEntity();
				
				double y = proj.getLocation().getY();
				double shotY = shot.getLocation().getY();
				boolean headshot = y - shotY > 1.35d;
				if (headshot) {
					event.setDamage(event.getDamage() * plugin.getConfig().getDouble("headshot-multiplier"));
					if (shot instanceof Player) {
						Player p = (Player) event.getEntity();
						Player shoot = (Player) proj.getShooter();
						p.sendMessage(Broadcast.INSTANCE.headshot(shoot, HeadshotType.BY, plugin));
						shoot.sendMessage(Broadcast.INSTANCE.headshot(shoot, HeadshotType.ON, plugin));
					}
				}
			}
		}
	}
}