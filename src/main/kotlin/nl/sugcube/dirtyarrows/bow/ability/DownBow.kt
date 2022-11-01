package nl.sugcube.dirtyarrows.bow.ability

import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.bow.BowAbility
import nl.sugcube.dirtyarrows.bow.DefaultBow
import org.bukkit.entity.Arrow
import org.bukkit.entity.Player
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.util.Vector

/**
 * Pulls the target toward you.
 *
 * @author SugarCaney
 */
open class DownBow(plugin: DirtyArrows) : BowAbility(
        plugin = plugin,
        type = DefaultBow.DOWN,
        handleEveryNTicks = 1,
        canShootInProtectedRegions = true,
        removeArrow = false,
        description = "Arrows crash down at their apex."
) {

    /**
     * How fast the arrows will crash down in blocks/second.
     */
    val crashVelocity = config.getDouble("$node.crash-velocity")

    private val hasLanded = HashSet<Arrow>()

    override fun land(arrow: Arrow, player: Player, event: ProjectileHitEvent) {
        hasLanded.remove(arrow)
    }

    override fun effect() {
        arrows.forEach {
            if (it.velocity.y <= 0 && it !in hasLanded) {
                it.velocity = Vector(0.0, -crashVelocity, 0.0)
                hasLanded.add(it)
            }
        }
    }
}