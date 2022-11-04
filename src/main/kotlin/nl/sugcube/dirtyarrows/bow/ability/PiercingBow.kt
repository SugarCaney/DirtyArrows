package nl.sugcube.dirtyarrows.bow.ability

import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.bow.BowAbility
import nl.sugcube.dirtyarrows.bow.DefaultBow
import nl.sugcube.dirtyarrows.util.scheduleRemoval
import org.bukkit.entity.Arrow
import org.bukkit.entity.Player
import org.bukkit.event.entity.ProjectileHitEvent

/**
 * Arrows pierce through enemies, increasing the damage every time they pass through an enemy.
 *
 * @author SugarCaney
 */
open class PiercingBow(plugin: DirtyArrows) : BowAbility(
        plugin = plugin,
        type = DefaultBow.PIERCING,
        canShootInProtectedRegions = true,
        removeArrow = false,
        description = "Arrows pierce through enemies, increasing damage."
) {

    /**
     * With how much to multiply the damage after piercing through each target.
     */
    val damageMultiplier = config.getDouble("$node.damage-multiplier")

    override fun land(arrow: Arrow, player: Player, event: ProjectileHitEvent) {
        event.hitEntity ?: return

        unregisterArrow(arrow)
        arrow.scheduleRemoval(plugin)

        // Make sure to spawn a block ahead to not get the arrow stuck in the target entity.
        val theLocation = arrow.location.clone().add(arrow.velocity)
        val theVelocity = arrow.velocity
        val theBaseDamage = arrow.damage * damageMultiplier
        val thePickupStatus = arrow.pickupStatus
        val theKnockbackStrength = arrow.knockbackStrength
        val theFireTicks = arrow.fireTicks
        val theCritical = arrow.isCritical

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