package nl.sugcube.dirtyarrows.bow.ability

import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.bow.BowAbility
import nl.sugcube.dirtyarrows.bow.DefaultBow
import org.bukkit.entity.Arrow
import org.bukkit.entity.Player
import org.bukkit.event.entity.ProjectileHitEvent
import kotlin.math.max

/**
 * Removes hunger points from the target player.
 *
 * @author SugarCaney
 */
open class StarvationBow(plugin: DirtyArrows) : BowAbility(
        plugin = plugin,
        type = DefaultBow.STARVATION,
        canShootInProtectedRegions = true,
        description = "Target loses hunger points."
) {

    /**
     * How many hunger points the target loses.
     */
    val hungerPoints = config.getInt("$node.hunger-points")

    override fun land(arrow: Arrow, player: Player, event: ProjectileHitEvent) {
        val target = event.hitEntity as? Player ?: return
        if (target == player) return

        target.foodLevel = max(0, target.foodLevel - hungerPoints)
    }
}