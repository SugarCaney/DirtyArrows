package nl.sugcube.dirtyarrows.listener;

import nl.sugcube.dirtyarrows.DirtyArrows;
import nl.sugcube.dirtyarrows.util.Util;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;

import static java.lang.Math.max;

public class AnvilListener implements Listener {

    public DirtyArrows plugin;

    public AnvilListener(DirtyArrows instance) {
        plugin = instance;
    }

    @EventHandler
    public void onInventoryClick(PrepareAnvilEvent event) {
        AnvilInventory anvil = (AnvilInventory)event.getInventory();
        ItemStack item = event.getResult();

        if (item == null || item.getType() != Material.BOW) {
            return;
        }

        String name = anvil.getRenameText();
        String node = "";

        if ((node = Util.isBastard(name)) == Util.NONE) {
            return;
        }

        int levels = max(1, plugin.getConfig().getInt(node + ".levels"));
        anvil.setRepairCost(levels);
    }
}
