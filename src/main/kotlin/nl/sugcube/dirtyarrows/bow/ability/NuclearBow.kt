package nl.sugcube.dirtyarrows.bow.ability

import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.bow.BowAbility
import nl.sugcube.dirtyarrows.bow.DefaultBow
import nl.sugcube.dirtyarrows.util.*
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Arrow
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.random.Random

/**
 * Shoots arrows that create a huge explode on impact.
 * Also create radiation for a certain amount of time.
 *
 * @author SugarCaney
 */
open class NuclearBow(plugin: DirtyArrows) : BowAbility(
        plugin = plugin,
        type = DefaultBow.NUCLEAR,
        handleEveryNTicks = 20,
        canShootInProtectedRegions = false,
        protectionRange = 64.0,
        costRequirements = listOf(ItemStack(Material.TNT, 64)),
        description = "Huge boom. Radiation included."
) {

    /**
     * Stores all radiation locations. Mapped to the time it spawned.
     */
    private val radiation = HashMap<Location, Long>()

    /**
     * The power of the explosion. TNT has a power of 4.0.
     */
    val power = config.getDouble("$node.explosion.power").toFloat()

    /**
     * Whether the explosion should set blocks on fire.
     */
    val setOnFire = config.getBoolean("$node.explosion.set-on-fire")

    /**
     * Whether the explosion will break blocks.
     */
    val breakBlocks = config.getBoolean("$node.explosion.break-blocks")

    /**
     * Whether to generate radiation after the explosion.
     */
    val radiationEnabled = config.getBoolean("$node.radiation.enabled")

    /**
     * The time range for radiation to disappear in milliseconds.
     */
    val radiationTime = config.getInt("$node.radiation.time")

    /**
     * How much less, or more time than [radiationTime] it might take.
     */
    val radiationTimeFuzzing = config.getInt("$node.radiation.fuzzing")

    /**
     * How close to a radiation point an entity must be in order to get damaged.
     */
    val radiationDamageProximity = config.getDouble("$node.radiation.damage-proximity")

    /**
     * How many levels of poison to given when near a radiation point.
     */
    val radiationPoisonLevel = config.getInt("$node.radiation.poison-level")

    /**
     * The amount of ticks the radiation poison lasts after it being inflicted.
     */
    val radiationPoisonDuration = config.getInt("$node.radiation.poison-duration")

    /**
     * The maximum deviation of radiation particles from their original location.
     */
    val radiationParticleFuzzing = config.getDouble("$node.radiation.particle-fuzzing")

    /**
     * Up until how many blocks from the place of impact radiation could spawn.
     */
    val radiationRange = config.getInt("$node.radiation.range")

    /**
     * How many blocks the radiation particles fall per second.
     */
    val radiationFallSpeed = config.getDouble("$node.radiation.fall-speed.mean")

    /**
     * The maximum deviation from the radiation fall speed.
     */
    val radiationFallSpeedFuzzing = config.getDouble("$node.radiation.fall-speed.fuzzing")

    /**
     * How much slower (<1) or faster (>1) radiation falls down in water.
     */
    val radiationFallSpeedWaterMultiplier = config.getDouble("$node.radiation.fall-speed.water-multiplier")

    /**
     * Impacts the amount of radiation that is being generated.
     * Each block has a 3/x^y chance of spawning a radiation location, where y is the chance exponent.
     * Higher values = less radiation. Lower values = more radiations.
     * Be careful when tweaking this value. Setting it lower can drastically increase the amount of radiation
     * spots and impact the performance of the server and clients.
     */
    val radiationDistributionChanceExponent = config.getDouble("$node.radiation.distribution-exponent")

    /**
     * Whether to show radiation particles.
     */
    val radiationParticles = config.getBoolean("$node.radiation.particles")

    /**
     * Each radiation spot will spawn a particle every N ticks.
     */
    val particleEveryNTicks = config.getInt("$node.radiation.particle-every-n-ticks")

    init {
        check(radiationPoisonDuration >= 0) { "$node.radiation.poison-duration must not be negative, got <$radiationPoisonDuration>" }
        check(radiationPoisonLevel >= 0) { "$node.radiation.poison-level must not be negative, got <$radiationPoisonLevel>" }
    }

    override fun land(arrow: Arrow, player: Player, event: ProjectileHitEvent) {
        arrow.location.createExplosion(power = power, setFire = setOnFire, breakBlocks = breakBlocks)

        if (radiationEnabled) {
            arrow.location.createRadiation()
        }
    }

    override fun effect() {
        if (radiationParticles.not()) return

        // Apply radiation effects.
        radiation.keys.forEach { radiationLocation ->
            // Poison entities.
            radiationLocation.world?.getNearbyEntities(
                    radiationLocation,
                    radiationDamageProximity,
                    radiationDamageProximity,
                    radiationDamageProximity
            )?.asSequence()
                    ?.mapNotNull { it as? LivingEntity }
                    ?.forEach {
                        it.addPotionEffect(PotionEffect(PotionEffectType.POISON, radiationPoisonDuration, radiationPoisonLevel), true)
                        it.addPotionEffect(PotionEffect(PotionEffectType.WITHER, radiationPoisonDuration, radiationPoisonLevel), true)
                    }

            // Slowly fall down.
            val block = radiationLocation.block
            if (block.type.isSolid.not()) {
                val multiplier = if (block.isWater()) radiationFallSpeedWaterMultiplier else 1.0
                radiationLocation.subtract(0.0, radiationFallSpeed.fuzz(radiationFallSpeedFuzzing) * multiplier, 0.0)
            }
        }
    }

    override fun particle(tickNumber: Int) {
        showArrowParticles(tickNumber)

        if (radiationEnabled && radiationParticles) {
            showRadiationParticles(tickNumber)
        }
    }

    private fun showArrowParticles(tickNumber: Int) = arrows.forEach {
        it.showSmokeParticle()

        if (tickNumber % 5 == 0) {
            it.showFlameParticle()
        }
    }

    private fun showRadiationParticles(tickNumber: Int) = radiation.keys.forEach {
        if (tickNumber % particleEveryNTicks != 0) return

        // Some yellowgreen/greenish colour.
        it.fuzz(radiationParticleFuzzing).showColoredDust(130.fuzz(75), 235, 52, 1)

        plugin.server.scheduler.scheduleSyncDelayedTask(plugin, {
            it.fuzz(radiationParticleFuzzing).showColoredDust(130.fuzz(75), 235, 52, 1)
        }, 3L.fuzz(2L))
    }

    /**
     * Creates radiation locations near this explosion site.
     */
    private fun Location.createRadiation() {
        forXYZ(
                blockX - radiationRange..blockX + radiationRange,
                blockY - radiationRange..blockY + radiationRange,
                blockZ - radiationRange..blockZ + radiationRange
        ) { newX, newY, newZ ->
            val radiationLocation = copyOf(x = newX.toDouble(), y = newY.toDouble(), z = newZ.toDouble())
            val distance = distance(radiationLocation)
            val spawnChance = spawnChance(distance)
            if (Random.nextDouble() < spawnChance) {
                radiationLocation.registerFalloutLocation()
            }
        }
    }

    /**
     * Calculates the chance for radiation based on the distance to the center.
     *
     * @param distance
     *          The distance to the center of the explosion.
     * @return The chance in range `[0,1]`
     */
    private fun spawnChance(distance: Double): Double {
        if (distance == 0.0) return 1.0
        val result = 3.0 / distance.pow(radiationDistributionChanceExponent)
        return max(min(1.0, result), 0.0)
    }

    /**
     * Registers the location as a radiation location.
     */
    private fun Location.registerFalloutLocation() {
        val birthTime = System.currentTimeMillis().fuzz(radiationTimeFuzzing.toLong())
        radiation[this] = birthTime
    }
}