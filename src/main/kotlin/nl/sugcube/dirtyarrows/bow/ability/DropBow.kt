package nl.sugcube.dirtyarrows.bow.ability

import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.bow.BowAbility
import nl.sugcube.dirtyarrows.bow.DefaultBow
import nl.sugcube.dirtyarrows.util.fuzz
import org.bukkit.entity.Arrow
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.util.Vector

/**
 * Shoots the target entity up so it will drop down.
 *
 * @author SugarCaney
 */
open class DropBow(plugin: DirtyArrows) : BowAbility(
        plugin = plugin,
        type = DefaultBow.DROP,
        canShootInProtectedRegions = true,
        description = "Launches the target in the air."
) {

    override fun land(arrow: Arrow, player: Player, event: ProjectileHitEvent) {
        val target = event.hitEntity as? LivingEntity ?: return
        if (target == player) return

        target.velocity = Vector(0.0.fuzz(0.2), LAUNCH_INTENSITY.fuzz(0.2), 0.0.fuzz(0.2))
    }

    companion object {

        /**
         * How much upward force to apply.
         */
        const val LAUNCH_INTENSITY = 1.0
    }
}