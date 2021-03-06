package nl.sugcube.dirtyarrows.bow.ability

import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.bow.BowAbility
import nl.sugcube.dirtyarrows.bow.DefaultBow
import org.bukkit.entity.Arrow
import org.bukkit.entity.Player
import org.bukkit.event.entity.ProjectileHitEvent

/**
 * Pulls the target toward you.
 *
 * @author SugarCaney
 */
open class PullBow(plugin: DirtyArrows) : BowAbility(
        plugin = plugin,
        type = DefaultBow.PULL,
        canShootInProtectedRegions = true,
        description = "Pulls the target toward you."
) {

    /**
     * How hard to the bow pulls (quite magic value).
     */
    val pullStrength = config.getDouble("$node.strength")

    override fun land(arrow: Arrow, player: Player, event: ProjectileHitEvent) {
        val target = event.hitEntity ?: return
        if (target == player) return

        target.velocity = player.location.direction.multiply(-pullStrength)
    }
}