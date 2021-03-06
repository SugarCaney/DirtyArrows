package nl.sugcube.dirtyarrows.bow.ability

import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.bow.BowAbility
import nl.sugcube.dirtyarrows.bow.DefaultBow
import org.bukkit.entity.Arrow
import org.bukkit.entity.Player
import org.bukkit.event.entity.ProjectileHitEvent
import kotlin.math.max

/**
 * Removes 3 (1.5) hunger points from the target player.
 *
 * @author SugarCaney
 */
open class StarvationBow(plugin: DirtyArrows) : BowAbility(
        plugin = plugin,
        type = DefaultBow.STARVATION,
        canShootInProtectedRegions = true,
        description = "Target loses hunger points."
) {

    override fun land(arrow: Arrow, player: Player, event: ProjectileHitEvent) {
        val target = event.hitEntity as? Player ?: return
        if (target == player) return

        target.foodLevel = max(0, target.foodLevel - 3)
    }
}