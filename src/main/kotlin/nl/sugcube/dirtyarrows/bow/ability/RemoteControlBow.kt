package nl.sugcube.dirtyarrows.bow.ability

import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.bow.BowAbility
import nl.sugcube.dirtyarrows.bow.DefaultBow
import nl.sugcube.dirtyarrows.util.copyOf
import nl.sugcube.dirtyarrows.util.rotateAround
import org.bukkit.entity.Entity

/**
 * Change the direction of the arrows by looking different ways.
 *
 * @author SugarCaney
 */
open class RemoteControlBow(plugin: DirtyArrows) : BowAbility(
        plugin = plugin,
        type = DefaultBow.REMOTECONTROL,
        handleEveryNTicks = 1,
        canShootInProtectedRegions = true,
        removeArrow = false,
        description = "Change the direction mid-air."
) {

    /**
     * Gravity multiplier per tick, used to compensate rotating upward.
     */
    val gravityDamping = config.getDouble("$node.gravity-damping")

    override fun effect() = arrows.forEach {
        val shooter = it.shooter as? Entity ?: return@forEach

        // Target direction vectory. Only X and Z element are relevant to preserve gravity.
        val targetDirection = shooter.location.direction
        val arrowDirection = it.velocity.normalize()
        val oldGravity = it.velocity.y

        val angle = arrowDirection.angle(targetDirection)

        // The vector that defines the plane to rotate the arrow over.
        val planeNormal = arrowDirection.copyOf().crossProduct(targetDirection)
            .multiply(1.0 / arrowDirection.length() * targetDirection.length())
            .normalize()

        it.velocity = arrowDirection.rotateAround(planeNormal, angle.toDouble())
            .normalize()
            .multiply(it.velocity.length())
            .copyOf(y = oldGravity * gravityDamping)
    }
}