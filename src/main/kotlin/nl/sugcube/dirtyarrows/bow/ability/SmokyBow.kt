package nl.sugcube.dirtyarrows.bow.ability

import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.bow.BowAbility
import nl.sugcube.dirtyarrows.bow.DefaultBow
import nl.sugcube.dirtyarrows.util.scheduleDelayed
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.entity.Arrow
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

/**
 * Erects a smokescreen at the location of impact.
 * Blinds nearby entities.
 *
 * @author SugarCaney
 */
open class SmokyBow(plugin: DirtyArrows) : BowAbility(
        plugin = plugin,
        type = DefaultBow.SMOKY,
        canShootInProtectedRegions = false,
        protectionRange = 10.0,
        costRequirements = listOf(ItemStack(Material.INK_SACK, 1)),
        description = "Create smokescreens and blind entities."
) {

    /**
     * Whether a smokescreen cloud must be shown on impact.
     */
    val particleCloudEnabled = config.getBoolean("$node.particle-cloud.enabled")

    /**
     * The radius of the smokescreen cloud, in blocks.
     */
    val particleCloudRange = config.getDouble("$node.particle-cloud.range")

    /**
     * How long to spawn smokescreen cloud particles, in ticks (20 ticks/second).
     */
    val particleCloudDuration = config.getInt("$node.particle-cloud.duration")

    /**
     * The amount of cloud particles to spawn each tick.
     */
    val particleCloudCount = config.getInt("$node.particle-cloud.count")

    /**
     * The particle speed for the cloud particles.
     */
    val particleCloudSpeed = config.getDouble("$node.particle-cloud.speed")

    /**
     * Whether nearby entities must be blinded.
     */
    val blindnessEnabled = config.getBoolean("$node.blindness.enabled")

    /**
     * How far from the impact location entities can be blinded.
     */
    val blindnessRange = config.getDouble("$node.blindness.range")

    /**
     * For how many ticks entities are blinded (20 ticks/second).
     */
    val blindnesDuration = config.getInt("$node.blindness.duration")

    /**
     * The potion level of the blindness effect starting with 0.
     */
    val blindnessLevel = config.getInt("$node.blindness.level")

    override fun land(arrow: Arrow, player: Player, event: ProjectileHitEvent) {
        if (particleCloudEnabled) {
            arrow.location.createCloud()
        }
        if (blindnessEnabled) {
            arrow.blindNearbyEntities()
        }
    }

    /**
     * Creates a smokescreen cloud at this location.
     */
    private fun Location.createCloud() = repeat(particleCloudDuration) {
        plugin.scheduleDelayed(it.toLong()) {
            world.spawnParticle(
                    Particle.CLOUD,
                    x, y, z,
                    particleCloudCount,
                    particleCloudRange, particleCloudRange, particleCloudRange,
                    particleCloudSpeed
            )
        }
    }

    /**
     * Blinds all entities around this arrow.
     */
    private fun Arrow.blindNearbyEntities() = getNearbyEntities(blindnessRange, blindnessRange, blindnessRange).asSequence()
            .mapNotNull { it as? LivingEntity }
            .forEach {
                it.addPotionEffect(PotionEffect(
                        PotionEffectType.BLINDNESS,
                        blindnesDuration,
                        blindnessLevel,
                        true,
                        false
                ))
            }
}