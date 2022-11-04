package nl.sugcube.dirtyarrows.bow.ability

import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.bow.BowAbility
import nl.sugcube.dirtyarrows.bow.DefaultBow
import nl.sugcube.dirtyarrows.util.scheduleDelayed
import nl.sugcube.dirtyarrows.util.scheduleRemoval
import org.bukkit.entity.Arrow
import org.bukkit.entity.Player
import org.bukkit.event.entity.ProjectileLaunchEvent

/**
 * Shoots arrows that explode on impact.
 *
 * @author SugarCaney
 */
open class DelayBow(plugin: DirtyArrows) : BowAbility(
        plugin = plugin,
        type = DefaultBow.DELAY,
        canShootInProtectedRegions = true,
        removeArrow = false,
        description = "Arrows shoot after a delay period."
) {

    /**
     * The amount of ticks to wait before shooting the arrow.
     */
    val delay = config.getInt("$node.delay")

    init {
        check(delay >= 0) { "$node.delay cannot be negative, got <$delay>" }
    }

    override fun launch(player: Player, arrow: Arrow, event: ProjectileLaunchEvent) {
        unregisterArrow(arrow)
        arrow.scheduleRemoval(plugin)

        val theLocation = arrow.location
        val theVelocity = arrow.velocity
        val theBaseDamage = arrow.damage
        val thePickupStatus = arrow.pickupStatus
        val theKnockbackStrength = arrow.knockbackStrength
        val theFireTicks = arrow.fireTicks
        val theCritical = arrow.isCritical

        plugin.scheduleDelayed(delay.toLong()) {
            val delayedArrow = theLocation.world!!.spawn(theLocation, Arrow::class.java).apply {
                shooter = player
                velocity = theVelocity
                damage = theBaseDamage
                pickupStatus = thePickupStatus
                knockbackStrength = theKnockbackStrength
                fireTicks = theFireTicks
                isCritical = theCritical
            }
            registerArrow(delayedArrow)
        }
    }
}