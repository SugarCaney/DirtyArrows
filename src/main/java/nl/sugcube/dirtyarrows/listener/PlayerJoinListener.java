package nl.sugcube.dirtyarrows.listener;

import nl.sugcube.dirtyarrows.DirtyArrows;
import nl.sugcube.dirtyarrows.util.Message;
import nl.sugcube.dirtyarrows.util.Update;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {
	
	public static DirtyArrows plugin;
	
	public PlayerJoinListener(DirtyArrows instance) {
		plugin = instance;
	}
	
	@EventHandler
	public void onPlayerJoinUpdates(PlayerJoinEvent e) {
		if (plugin.getConfig().getBoolean("updates.check-for-updates")) {
			if (plugin.getConfig().getBoolean("updates.show-admin")) {
				if (e.getPlayer().hasPermission("dirtyarrows.admin")) {
					Update uc = new Update(57131, plugin.getDescription().getVersion());
					if (uc.query()) {
						e.getPlayer().sendMessage(ChatColor.GREEN + "A new version of DirtyArrows is available!");
						e.getPlayer().sendMessage(ChatColor.GREEN + "Get it at the BukkitDev-page!");
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		if (plugin.getConfig().getBoolean("auto-enable")) {
			Player player = event.getPlayer();
			
			if (player.hasPermission("dirtyarrows")) {
				if (plugin.activated.contains(player.getUniqueId().toString())) {
					plugin.activated.remove(player.getUniqueId().toString());
					player.sendMessage(Message.getEnabled(false));
				} else {
					plugin.activated.add(player.getUniqueId().toString());
					player.sendMessage(Message.getEnabled(true));
				}
			}
		}
		
	}

}
