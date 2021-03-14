package nl.sugcube.dirtyarrows.bow.ability

import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.bow.BowAbility
import nl.sugcube.dirtyarrows.bow.DefaultBow
import nl.sugcube.dirtyarrows.util.createExplosion
import nl.sugcube.dirtyarrows.util.getFloat
import nl.sugcube.dirtyarrows.util.nearbyLivingEntities
import nl.sugcube.dirtyarrows.util.showSmokeParticle
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Arrow
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.inventory.ItemStack

/**
 * Pulls in all close entities and damages from suffocation.
 *
 * @author SugarCaney
 */
open class SingularityBow(plugin: DirtyArrows) : BowAbility(
        plugin = plugin,
        type = DefaultBow.SINGULARITY,
        handleEveryNTicks = 1,
        canShootInProtectedRegions = false,
        removeArrow = true,
        description = "Pulls in entities & suffocates.",
        costRequirements = listOf(ItemStack(Material.REDSTONE, 3))
) {

    /**
     * Contains all locations that pull in entities mapped to their spawn time.
     */
    private val pullLocations = HashMap<Location, Long>()

    /**
     * How far from the impact site entities can be pulled in.
     */
    val impactPullRange = config.getDouble("$node.impact.pull-range")

    /**
     * How strong to pull entities toward the arrow when it landed.
     */
    val impactPullPower = config.getDouble("$node.impact.pull-power")

    /**
     * How many milliseconds the landed arrow can pull entities in.
     */
    val impactPullDuration = config.getInt("$node.impact.pull-duration")

    /**
     * How far from flying arrows entities can be pulled in (in blocks).
     */
    val flightPullRange = config.getDouble("$node.flight.pull-range")

    /**
     * How strong to pull entities toward the arrow when it flies.
     */
    val flightPullPower = config.getDouble("$node.flight.pull-power")

    /**
     * How far around the implosion location to damage entities.
     */
    val damageRange = config.getDouble("$node.damage.range")

    /**
     * How much damage to inflict each tick to entities in an implosion.
     */
    val damageAmount = config.getDouble("$node.damage.amount")

    /**
     * How strong the final explosion must be, <=0 for no explosion.
     */
    val explosionPower = config.getFloat("$node.damage.explosion-power")

    override fun land(arrow: Arrow, player: Player, event: ProjectileHitEvent) {
        pullLocations[arrow.location] = System.currentTimeMillis()
    }

    override fun effect() {
        flyingArrowEffect()

        // landed arrows.
        val now = System.currentTimeMillis()
        pullLocations.entries.removeIf { (pullLocation, birthTime) ->
            pullLocation.pullInEntities()
            pullLocation.showSmokeParticle()

            // Explode
            val age = now - birthTime
            if (age >= impactPullDuration) {
                if (explosionPower > 0f) {
                    pullLocation.createExplosion(power = explosionPower)
                }
                true
            }
            else false
        }
    }

    /**
     * Pulls in all entities towards this landing location.
     */
    private fun Location.pullInEntities() = nearbyLivingEntities(impactPullRange).forEach { target ->
        // Pull in entities.
        val distance = distance(target.location)
        if (distance > 0.2) {
            target.pullTowards(this, impactPullPower * (1 - distance / impactPullRange))
        }

        // Damage nearby entities.
        if (distance <= damageRange) {
            target.damage(damageAmount)
            target.showSmokeParticle()
        }
    }

    /**
     * Handles effects of flying arrows (pull in arrows).
     */
    private fun flyingArrowEffect() = arrows.forEach { arrow ->
        arrow.location.nearbyLivingEntities(flightPullRange).forEach { entity ->
            if (entity != arrow.shooter) {
                entity.pullTowards(arrow.location, flightPullPower)
            }
        }
    }

    /**
     * Pulls this entity toward the location with the given power (velocity/tick).
     */
    private fun LivingEntity.pullTowards(location: Location, power: Double) {
        val direction = location.toVector().subtract(this.location.toVector()).normalize()
        this.velocity = direction.multiply(power).add(velocity)
    }
}