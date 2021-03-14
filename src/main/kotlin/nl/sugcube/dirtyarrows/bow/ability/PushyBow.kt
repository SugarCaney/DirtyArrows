package nl.sugcube.dirtyarrows.bow.ability

import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.bow.BowAbility
import nl.sugcube.dirtyarrows.bow.DefaultBow
import nl.sugcube.dirtyarrows.util.nearbyLivingEntities
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.entity.Arrow
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector

/**
 * Pushes away entities. During flight and on impact.
 *
 * @author SugarCaney
 */
open class PushyBow(plugin: DirtyArrows) : BowAbility(
        plugin = plugin,
        type = DefaultBow.PUSHY,
        handleEveryNTicks = 1,
        canShootInProtectedRegions = false,
        removeArrow = true,
        description = "Pushes away entities.",
        costRequirements = listOf(ItemStack(Material.FEATHER, 3))
) {

    /**
     * How far from the impact site entities can be pushed aawy.
     */
    val impactPushRange = config.getDouble("$node.impact.push-range")

    /**
     * How strong to push entities away from the arrow when it landed.
     */
    val impactPushPower = config.getDouble("$node.impact.push-power")

    /**
     * How far from flying arrows entities can be pushed away (in blocks).
     */
    val flightPushRange = config.getDouble("$node.flight.push-range")

    /**
     * How strong to push entities away from the arrow when it flies.
     */
    val flightPushPower = config.getDouble("$node.flight.push-power")

    override fun land(arrow: Arrow, player: Player, event: ProjectileHitEvent) {
        arrow.location.nearbyLivingEntities(impactPushRange).forEach { entity ->
            val ratio = 1.0 - entity.location.distance(arrow.location) / impactPushRange
            entity.pushAway(arrow.location, impactPushPower * ratio)
            arrow.world.spawnParticle(Particle.CRIT, arrow.location, 1)
        }
    }

    override fun effect() = arrows.forEach { arrow ->
        arrow.location.nearbyLivingEntities(flightPushRange).forEach { entity ->
            if (entity != arrow.shooter) {
                entity.pushAway(arrow.location, flightPushPower)
            }
        }
    }

    /**
     * Pushes this entity away from the location with the given power (velocity/tick).
     */
    private fun LivingEntity.pushAway(location: Location, power: Double) {
        val direction = this.location.toVector().subtract(location.toVector()).normalize()
        this.velocity = direction.multiply(power).add(velocity).add(Vector(0.0, 0.4, 0.0))
    }
}