package nl.sugcube.dirtyarrows.listener;

import java.util.ArrayList;
import java.util.Random;

import nl.sugcube.dirtyarrows.DirtyArrows;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;

public class EnchantmentListener implements Listener {
	
	public static DirtyArrows plugin;
	
	public EnchantmentListener(DirtyArrows instance) {
		plugin = instance;
	}
	
	Random ran = new Random();
	int level;
	ArrayList<String> lores = new ArrayList<String>();
	
	@EventHandler
	public void onEnchantItem(EnchantItemEvent event) {
		if (event.getItem().getType().equals(Material.BOW)) {			
			if (event.getExpLevelCost() > 10) {
				if (ran.nextInt(8) == 0) {
					if (event.getExpLevelCost() < 18) {
						level = 1;
					} else if (event.getExpLevelCost() < 25){
						level = 2;
					} else {
						level = 3;
					}
					event.getItem().addUnsafeEnchantment(Enchantment.LOOT_BONUS_MOBS, level);
				}
			}
		}
	}

}
