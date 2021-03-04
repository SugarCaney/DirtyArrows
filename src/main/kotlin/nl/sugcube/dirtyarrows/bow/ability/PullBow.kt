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
        canShootInProtectedRegions = true
) {

    override fun land(arrow: Arrow, player: Player, event: ProjectileHitEvent) {
        val target = event.hitEntity ?: return
        if (target == player) return

        target.velocity = player.location.direction.multiply(-PULL_STRENGTH)
    }

    companion object {

        /**
         * How hard to the bow pulls (quite magic value).
         */
        private const val PULL_STRENGTH = 7.5
    }
}