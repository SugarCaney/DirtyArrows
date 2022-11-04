package nl.sugcube.dirtyarrows.bow.ability

import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.bow.BowAbility
import nl.sugcube.dirtyarrows.bow.DefaultBow
import nl.sugcube.dirtyarrows.util.copyOf
import org.bukkit.entity.Arrow
import org.bukkit.entity.Player
import org.bukkit.event.entity.ProjectileLaunchEvent

/**
 * Arrows shoot backward.
 *
 * @author SugarCaney
 */
open class BackwardsBow(plugin: DirtyArrows) : BowAbility(
        plugin = plugin,
        type = DefaultBow.BACKWARDS,
        canShootInProtectedRegions = true,
        removeArrow = false,
        description = "Arrows shoot backward."
) {

    override fun launch(player: Player, arrow: Arrow, event: ProjectileLaunchEvent) {
        arrow.velocity = arrow.velocity.copyOf(
            x = -arrow.velocity.x,
            z = -arrow.velocity.z
        )
    }
}