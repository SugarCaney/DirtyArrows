package nl.sugcube.dirtyarrows.ability;

import nl.sugcube.dirtyarrows.DirtyArrows;
import nl.sugcube.dirtyarrows.util.Message;

import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffectType;

public class FrozenListener implements Listener, Runnable {

	public DirtyArrows plugin;

	public int clean = 100;
	
	public FrozenListener(DirtyArrows instance) {
		plugin = instance;
	}

	@Override
	public void run() {
		for (Entity ent : plugin.frozen.keySet()) {
			plugin.frozen.put(ent, plugin.frozen.get(ent) - 1);
			if (plugin.frozen.get(ent) <= 0) {
				plugin.frozen.remove(ent);

				if (ent instanceof Player) {
					Player player = (Player) ent;
					if (player.isOnline()) {
						player.sendMessage(Message.getTag() + ChatColor.GRAY + "You defrosted.");
						player.removePotionEffect(PotionEffectType.SLOW);
						plugin.noInteract.remove(player);
					}
				}
			}
		}
		
		if (clean-- <= 0) {
			clean = 100;
			plugin.frozen.clear();
		}
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		if (plugin.noInteract.contains(e.getPlayer())) {
			e.setCancelled(true);
		}
	}

}
