package nl.sugcube.dirtyarrows.bow.ability

import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.bow.BowAbility
import nl.sugcube.dirtyarrows.bow.DefaultBow
import nl.sugcube.dirtyarrows.effect.cutDownTree
import org.bukkit.Material
import org.bukkit.entity.Arrow
import org.bukkit.entity.Player
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.util.BlockIterator

/**
 * Shoots arrows that cut down trees.
 *
 * @author SugarCaney
 */
open class WoodmanBow(plugin: DirtyArrows) : BowAbility(
        plugin = plugin,
        type = DefaultBow.WOODMAN,
        canShootInProtectedRegions = false,
        protectionRange = 1.0,
        removeArrow = false,
        description = "Tear down trees quickly."
) {

    override fun land(arrow: Arrow, player: Player, event: ProjectileHitEvent) {
        // Find the block that the arrow hits.
        val iterator = BlockIterator(arrow.world, arrow.location.toVector(), arrow.velocity.normalize(), 0.0, 4)
        var hitBlock = iterator.next()
        while (iterator.hasNext()) {
            hitBlock = iterator.next()
            if (hitBlock.type != Material.AIR) break
        }

        // Tear down the tree.
        if (hitBlock.location.cutDownTree()) {
            arrow.remove()
        }
    }
}