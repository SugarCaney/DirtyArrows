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

    /**
     * How much upward force to apply.
     */
    val launchIntensity = config.getDouble("$node.launch-intensity")

    /**
     * The maximum amount of deviation from the launch intensity.
     */
    val launchIntensityFuzzing = config.getDouble("$node.launch-intensity-fuzzing")

    /**
     * The maximum amount of horizontal force to apply per axis.
     */
    val horizontalFuzzing = config.getDouble("$node.horizontal-fuzzing")

    override fun land(arrow: Arrow, player: Player, event: ProjectileHitEvent) {
        val target = event.hitEntity as? LivingEntity ?: return
        if (target == player) return

        target.velocity = Vector(
                0.0.fuzz(horizontalFuzzing),
                launchIntensity.fuzz(launchIntensityFuzzing),
                0.0.fuzz(horizontalFuzzing)
        )
    }
}