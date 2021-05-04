package nl.sugcube.dirtyarrows.bow.ability

import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.bow.BowAbility
import nl.sugcube.dirtyarrows.bow.DefaultBow
import nl.sugcube.dirtyarrows.util.itemInOffHand
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.entity.Arrow
import org.bukkit.entity.Player
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.inventory.ItemStack

/**
 * Places blocks from the offhand on the location of impact.
 *
 * @author SugarCaney
 */
open class BlockyBow(plugin: DirtyArrows) : BowAbility(
        plugin = plugin,
        type = DefaultBow.BLOCKY,
        canShootInProtectedRegions = false,
        protectionRange = 2.0,
        description = "Places blocks from your off-hand."
) {

    @Suppress("DEPRECATION")
    override fun land(arrow: Arrow, player: Player, event: ProjectileHitEvent) {
        val itemToPlace = player.itemInOffHand
        if (itemToPlace.type.isBlock.not() || itemToPlace.type == Material.AIR) return

        val block = arrow.location.block
        if (block.type != Material.AIR) return

        block.type = itemToPlace.type
        block.data = itemToPlace.data.data

        player.removeBlockItem(itemToPlace.type)
    }

    /**
     * Removes a block of the given type from the inventory, do nothing if the item should not be removed.
     */
    @Suppress("DEPRECATION")
    private fun Player.removeBlockItem(type: Material) {
        if (gameMode == GameMode.CREATIVE) return

        val offHandItem = itemInOffHand
        val newCount = offHandItem.amount - 1
        val itemData = offHandItem.data

        if (newCount == 0) {
            inventory.itemInOffHand = ItemStack(Material.AIR, 0)
        }
        else inventory.itemInOffHand = ItemStack(type, newCount, itemData.data.toShort())
    }
}