package nl.SugCube.DirtyArrows;

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
	public void onPlayerJoin(PlayerJoinEvent event) {
		
		if (plugin.getConfig().getBoolean("auto-enable")) {
			Player player = event.getPlayer();
			
			if (player.hasPermission("dirtyarrows")) {
				if (plugin.activated.contains(player.getUniqueId().toString())) {
					plugin.activated.remove(player.getUniqueId().toString());
					player.sendMessage(ChatColor.YELLOW + "Dirty Arrows have been" + ChatColor.RED + " disabled!");
				} else {
					plugin.activated.add(player.getUniqueId().toString());
					player.sendMessage(ChatColor.YELLOW + "Dirty Arrows have been " + ChatColor.GREEN + "enabled!");
				}
			}
		}
		
	}

}
