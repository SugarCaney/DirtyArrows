package nl.sugcube.dirtyarrows.bow.ability

import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.bow.BowAbility
import nl.sugcube.dirtyarrows.bow.DefaultBow
import org.bukkit.entity.Arrow
import org.bukkit.entity.Player
import org.bukkit.event.entity.ProjectileLaunchEvent
import org.bukkit.util.Vector
import kotlin.math.min

/**
 * Shoots arrows that start slow, but move faster and faster.
 * The faster the arrow flies, the more damage it does.
 *
 * @author SugarCaney
 */
open class AcceleratingBow(plugin: DirtyArrows) : BowAbility(
        plugin = plugin,
        type = DefaultBow.ACCELERATING,
        handleEveryNTicks = 1,
        canShootInProtectedRegions = true,
        removeArrow = false,
        description = "Arrows accelerate."
) {

    /**
     * Maps each arrow to the unix timestamp they have been launched.
     */
    private val birthTime = HashMap<Arrow, Long>()

    /**
     * The minimum speed of the arrow in blocks/tick. This is also the start speed.
     */
    val minimumSpeed = config.getDouble("$node.minimum-speed")

    /**
     * The maximum speed of the arrows in blocks/tick. For a regular bow, this is roughly 3.0
     */
    val maximumSpeed = config.getDouble("$node.maximum-speed")

    /**
     * The maximum amount of milliseconds the arrow can accelerate for.
     */
    val accelerateTime = config.getInt("$node.accelerate-time")

    /**
     * How many blocks/tick the arrow should move upward to counteract the downward tendency of the arrow.
     * Generally needs to be higher at higher velocities, and lower at lower velocities.
     */
    val verticalCompensation = config.getDouble("$node.vertical-compensation")

    override fun launch(player: Player, arrow: Arrow, event: ProjectileLaunchEvent) {
        birthTime[arrow] = System.currentTimeMillis()
        arrow.updateSpeed()
    }

    override fun effect() {
        birthTime.entries.removeIf { (arrow, birthTime) ->
            arrow.updateSpeed()
            System.currentTimeMillis() - birthTime > accelerateTime
        }
    }

    /**
     * Sets the arrow speed based on how long the arrow has flown.
     */
    private fun Arrow.updateSpeed() {
        val birthTime = birthTime[this] ?: return
        val now = System.currentTimeMillis()
        val age = now - birthTime

        val percentageDone = min(1.0, age / accelerateTime.toDouble())
        val totalSpeedIncrease = maximumSpeed - minimumSpeed
        val newSpeed = minimumSpeed + percentageDone * totalSpeedIncrease

        velocity = velocity.normalize().multiply(newSpeed).add(Vector(0.0, verticalCompensation, 0.0))
    }
}