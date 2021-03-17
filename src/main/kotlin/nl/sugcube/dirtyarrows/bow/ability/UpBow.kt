package nl.sugcube.dirtyarrows.bow.ability

import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.bow.BowAbility
import nl.sugcube.dirtyarrows.bow.DefaultBow
import org.bukkit.entity.Arrow
import org.bukkit.entity.Player
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.event.entity.ProjectileLaunchEvent
import org.bukkit.util.Vector

/**
 * Arrows fall up instead of down.
 *
 * @author SugarCaney
 */
open class UpBow(plugin: DirtyArrows) : BowAbility(
        plugin = plugin,
        type = DefaultBow.UP,
        handleEveryNTicks = 1,
        canShootInProtectedRegions = true,
        removeArrow = false,
        description = "Shoots upwards."
) {

    /**
     * Stores all shot arrows, and their starting unix timestamp.
     */
    private val arrowTimes = HashMap<Arrow, Long>()

    /**
     * After what time the arrows should drop down again, in milliseconds.
     */
    val riseTime = config.getInt("$node.rise-time")

    /**
     * Acceleration in blocks per tick.
     */
    val arrowGravity = Vector(0.0, config.getDouble("$node.gravity"), 0.0)

    override fun launch(player: Player, arrow: Arrow, event: ProjectileLaunchEvent) {
        arrowTimes[arrow] = System.currentTimeMillis()
        arrow.setGravity(false)
    }

    override fun land(arrow: Arrow, player: Player, event: ProjectileHitEvent) {
        arrowTimes.remove(arrow)
    }

    override fun effect() {
        arrowTimes.entries.removeIf { (arrow, birthTime) ->
            arrow.velocity = arrow.velocity.add(arrowGravity)

            if (System.currentTimeMillis() - birthTime >= riseTime) {
                arrow.setGravity(true)
                true
            }
            else false
        }
    }
}