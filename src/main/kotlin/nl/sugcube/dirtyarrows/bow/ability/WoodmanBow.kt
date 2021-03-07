package nl.sugcube.dirtyarrows.bow.ability

import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.bow.BowAbility
import nl.sugcube.dirtyarrows.bow.DefaultBow
import nl.sugcube.dirtyarrows.effect.cutDownTree
import nl.sugcube.dirtyarrows.util.hitBlock
import org.bukkit.entity.Arrow
import org.bukkit.entity.Player
import org.bukkit.event.entity.ProjectileHitEvent

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
        val hitBlock = arrow.hitBlock()
        if (hitBlock.location.cutDownTree()) {
            arrow.remove()
        }
    }
}