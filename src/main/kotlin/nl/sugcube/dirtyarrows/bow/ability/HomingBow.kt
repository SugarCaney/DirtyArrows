package nl.sugcube.dirtyarrows.bow.ability

import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.bow.BowAbility
import nl.sugcube.dirtyarrows.bow.DefaultBow
import nl.sugcube.dirtyarrows.util.*
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Arrow
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.event.entity.ProjectileLaunchEvent
import org.bukkit.inventory.ItemStack
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin

/**
 * Shoots arrows that home in on nearby targets.
 *
 * @author SugarCaney
 */
open class HomingBow(plugin: DirtyArrows) : BowAbility(
        plugin = plugin,
        type = DefaultBow.HOMING,
        handleEveryNTicks = 1,
        canShootInProtectedRegions = true,
        removeArrow = false,
        description = "Shoot mini homing rockets.",
        costRequirements = listOf(ItemStack(Material.SULPHUR, 1))
) {

    /**
     * Maps each arrow to the time when the arrow was shot.
     */
    private val arrowShootTime = ConcurrentHashMap<Arrow, Long>()

    /**
     * How many milliseconds until the homing effect wears out.
     */
    val homingPeriod = config.getInt("$node.homing-period")

    /**
     * How many milliseconds after firing the homing effects should be applied.
     */
    val startTime = config.getInt("$node.start-time")

    /**
     * How many blocks away the shot can redirect.
     */
    val targetRange = config.getDouble("$node.target-range")

    /**
     * How far from the arrow's location to start the search for targets.
     */
    val sightDistance = config.getDouble("$node.sight-distance")

    /**
     * Value between [0.0, 1.0] where 0 is no homing and 1.0 is immediate homing.
     */
    val homingStrength = config.getDouble("$node.homing-strength")

    /**
     * Strength of the explosions.
     */
    val explosivePower = config.getFloat("$node.explosive-power")

    override fun launch(player: Player, arrow: Arrow, event: ProjectileLaunchEvent) {
        arrowShootTime[arrow] = System.currentTimeMillis()
    }

    override fun land(arrow: Arrow, player: Player, event: ProjectileHitEvent) {
        if (explosivePower > 0) {
            arrow.location.createExplosion(power = explosivePower, breakBlocks = false)
        }
    }

    override fun effect() {
        removeOldArrows()
        adjustVelocities()
    }

    override fun particle(tickNumber: Int) = arrows.forEach {
        it.location.copyOf().fuzz(0.2).showColoredDust(Color.WHITE, 20)
    }

    /**
     * Removes all arrows from the [arrowShootTime] map if their homing period has exceeded.
     */
    private fun removeOldArrows() {
        val now = System.currentTimeMillis()
        arrowShootTime.entries.removeIf { (_, time) ->
            now - time >= homingPeriod
        }
    }

    /**
     * Changes the velocities of the arrows such that they fly toward their target.
     */
    private fun adjustVelocities() = arrows.forEach { arrow ->
        val lifespan = System.currentTimeMillis() - (arrowShootTime[arrow] ?: System.currentTimeMillis())
        if (lifespan < startTime) return@forEach
        val target = arrow.findTarget() ?: return@forEach

        // Gives a more floaty and rockety feel to the arrow.
        // Without gravity, homing upwards becomes much easier/smoother.
        arrow.setGravity(false)

        // Use the Rodrigues' rotation formula to change the direction to the target, but maintain length/velocity.
        // https://en.wikipedia.org/wiki/Rodrigues%27_rotation_formula
        //
        // phi / angle := arccos ( (a, T-A) / |a||T-A| )    also defined in Bukkit's Vector.angle
        // k / planeNormal := ( a X (T-A) ) / |a||T-A| )
        // a_new = a cos phi + (k X a) sin phi + k (k, a) (1 - cos phi)
        //
        // Where phi is the angle to rotate in the plane k through a and A-T.
        val targetLocation = target.location.copyOf(y = target.location.y + 0.75)
        val arrowLocation = arrow.location
        val arrowVelocity = arrow.velocity
        val targetDirection = targetLocation.toVector().subtract(arrowLocation.toVector())

        // arrowVelocity diverges to targetMinusArrow, results in wacky cross products if they are equal.
        // Bukkit equals already has an EPSILON defined to compare doubles.
        if (arrowVelocity == targetDirection) return@forEach

        // How much the velocity much rotate toward the target. Homing strength in 0.0 through 1.0 for
        // proper behaviour.
        val angle = arrowVelocity.angle(targetDirection) * homingStrength

        // The vector that defines the plane to rotate the arrow over.
        val k = arrowVelocity.copyOf().crossProduct(targetDirection)
                .multiply(1.0 / arrowVelocity.length() * targetDirection.length())
                .normalize()

        // Use Rodrigues rotation formula to apply the rotation.
        val updatedArrowVelocity = arrowVelocity.copyOf().multiply(cos(angle))
                .add(k.copyOf().crossProduct(arrowVelocity).multiply(sin(angle)))
                .add(k.copyOf().multiply(k.copyOf().dot(arrowVelocity)).multiply(1.0 - cos(angle)))

        // Ensure that the velocity size stays the same.
        arrow.velocity = updatedArrowVelocity
                .normalize()
                .multiply(arrowVelocity.length())
    }

    /**
     * Looks for the target of this arrow.
     *
     * @return The target entity, or `null` when no target could be found.
     */
    private fun Arrow.findTarget(): LivingEntity? {
        val searchLocation = searchLocation()
        return world.getNearbyEntities(searchLocation, targetRange, targetRange, targetRange)
                .asSequence()
                .mapNotNull { it as? LivingEntity }
                .filter { it != this.shooter }
                .minByOrNull { it.location.distance(searchLocation) }
    }

    /**
     * Get the location from where to search for targets.
     */
    private fun Arrow.searchLocation(): Location {
        val shooter = shooter as? LivingEntity ?: return location
        val distanceFromShooter = shooter.location.distance(location)
        val lookThisFarAhead = max(0.0, sightDistance - distanceFromShooter)
        val direction = velocity.copyOf().normalize()
        val toAdd = direction.multiply(lookThisFarAhead)
        return location.copyOf(y = location.y + sightDistance / 2).add(toAdd)
    }
}