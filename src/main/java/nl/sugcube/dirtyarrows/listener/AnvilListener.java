package nl.sugcube.dirtyarrows.listener;

import nl.sugcube.dirtyarrows.DirtyArrows;
import nl.sugcube.dirtyarrows.util.Message;
import nl.sugcube.dirtyarrows.util.Util;

import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;

public class AnvilListener implements Listener {

	public DirtyArrows plugin;
	
	public AnvilListener(DirtyArrows instance) {
		plugin = instance;
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		if (e.getInventory() instanceof AnvilInventory && e.getWhoClicked() instanceof Player) {
			Player player = (Player) e.getWhoClicked();
			AnvilInventory inv = (AnvilInventory) e.getInventory();
			if (e.getSlot() == 2) {
				ItemStack item = inv.getItem(2);
				
				if (!item.hasItemMeta()) {
					return;
				}

				if (!item.getItemMeta().hasDisplayName()) {
					return;
				}
				
				String name = item.getItemMeta().getDisplayName();
				String node = "";
				
				if ((node = Util.isBastard(name)) == Util.NONE) {
					return;
				}
				
				int levels = plugin.getConfig().getInt(node + ".levels");
				
				if (levels < 0) {
					return;
				}
				
				int required = (player.getGameMode() == GameMode.CREATIVE ? 0 : levels);
				
				if (player.getLevel() < required) {
					player.sendMessage(Message.NOT_ENOUGH_LEVELS_1);
					player.sendMessage(Message.NOT_ENOUGH_LEVELS_2.replace("%l%", required + ""));
					player.playSound(player.getLocation(), Sound.ENTITY_BAT_HURT, 1L, 1L);
					e.setCancelled(true);
					return;
				}
				
				player.setLevel(player.getLevel() - required);
				if (player.getGameMode() != GameMode.CREATIVE) {
					player.getInventory().addItem(e.getCurrentItem());
					inv.setItem(0, null);
					player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1L, 1L);
					player.closeInventory();
				}
			}
		}
	}
	
}
