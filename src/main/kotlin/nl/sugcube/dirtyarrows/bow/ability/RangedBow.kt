package nl.sugcube.dirtyarrows.bow.ability

import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.bow.BowAbility
import nl.sugcube.dirtyarrows.bow.DefaultBow
import org.bukkit.entity.Arrow
import org.bukkit.entity.Player
import org.bukkit.event.entity.ProjectileLaunchEvent

/**
 * Shoots a very fast and powerful arrow.
 *
 * @author SugarCaney
 */
open class RangedBow(plugin: DirtyArrows) : BowAbility(
        plugin = plugin,
        type = DefaultBow.RANGED,
        canShootInProtectedRegions = true,
        removeArrow = false,
        description = "Shoot powerful, far-reaching arrows."
) {

    /**
     * How much quicker the arrow fires.
     */
    val speedMultiplier = config.getDouble("$node.speed-multiplier")

    override fun launch(player: Player, arrow: Arrow, event: ProjectileLaunchEvent) {
        arrow.velocity = player.eyeLocation.direction.multiply(speedMultiplier)
    }
}