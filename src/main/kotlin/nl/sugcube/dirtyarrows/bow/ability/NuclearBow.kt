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
 * Also create fallout for a certain amount of time.
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
) {

    /**
     * Stores all fallout locations. Mapped to the time it spawned.
     */
    private val fallout = HashMap<Location, Long>()

    override fun land(arrow: Arrow, player: Player, event: ProjectileHitEvent) {
        arrow.location.createExplosion(power = 55f, setFire = true)
        arrow.location.createFallout()
    }

    override fun effect() {
        // Apply fallout effects.
        fallout.keys.forEach { falloutLocation ->
            // Poison entities.
            falloutLocation.world.entities.asSequence()
                    .mapNotNull { it as? LivingEntity }
                    .filter { it.location.distance(falloutLocation) < FALLOUT_DAMAGE_PROXIMITY }
                    .forEach {
                        it.addPotionEffect(PotionEffect(PotionEffectType.POISON, 200, FALLOUT_POISON_LEVEL), true)
                        it.addPotionEffect(PotionEffect(PotionEffectType.WITHER, 200, FALLOUT_POISON_LEVEL), true)
                    }

            // Slowly fall down.
            val block = falloutLocation.block
            if (block.type.isSolid.not()) {
                val multiplier = if (block.isWater()) 0.3 else 1.0
                falloutLocation.subtract(0.0, 0.1.fuzz(0.05) * multiplier, 0.0)
            }
        }
    }

    override fun particle(tickNumber: Int) {
        showArrowParticles(tickNumber)
        showFalloutParticles(tickNumber)
    }

    private fun showArrowParticles(tickNumber: Int) = arrows.forEach {
        it.showSmokeParticle()

        if (tickNumber % 5 == 0) {
            it.showFlameParticle()
        }
    }

    private fun showFalloutParticles(tickNumber: Int) = fallout.keys.forEach {
        if (tickNumber % 5 != 0) return

        // Some yellowgreen/greenish colour.
        it.fuzz(FALLOUT_PARTICLE_FUZZ).showColoredDust(130.fuzz(75), 235, 52, 1)

        plugin.server.scheduler.scheduleSyncDelayedTask(plugin, {
            it.fuzz(FALLOUT_PARTICLE_FUZZ).showColoredDust(130.fuzz(75), 235, 52, 1)
        }, 3L.fuzz(2L))
    }

    /**
     * Creates fallout locations near this explosion site.
     */
    private fun Location.createFallout() {
        forXYZ(
                blockX - FALLOUT_RANGE..blockX + FALLOUT_RANGE,
                blockY - FALLOUT_RANGE..blockY + FALLOUT_RANGE,
                blockZ - FALLOUT_RANGE..blockZ + FALLOUT_RANGE
        ) { newX, newY, newZ ->
            val falloutLocation = copyOf(x = newX.toDouble(), y = newY.toDouble(), z = newZ.toDouble())
            val distance = distance(falloutLocation)
            val spawnChance = spawnChance(distance)
            if (Random.nextDouble() < spawnChance) {
                falloutLocation.registerFalloutLocation()
            }
        }
    }

    /**
     * Calculates the chance for fallout based on the distance to the center.
     *
     * @param distance
     *          The distance to the center of the explosion.
     * @return The chance in range `[0,1]`
     */
    private fun spawnChance(distance: Double): Double {
        if (distance == 0.0) return 1.0
        val result = 3.0 / distance.pow(2.4)
        return max(min(1.0, result), 0.0)
    }

    /**
     * Registers the location as a fallout location.
     */
    private fun Location.registerFalloutLocation() {
        val birthTime = System.currentTimeMillis().fuzz(FALLOUT_TIME_FUZZING.toLong())
        fallout[this] = birthTime
    }

    companion object {

        /**
         * The time range for fallout to disappear in milliseconds.
         */
        private const val FALLOUT_TIME = 60 * 6500

        /**
         * How much less, or more time than [FALLOUT_TIME] it might take.
         */
        private const val FALLOUT_TIME_FUZZING = 60 * 3500

        /**
         * How close to a fallout point an entity must be in order to get damaged.
         */
        private const val FALLOUT_DAMAGE_PROXIMITY = 4.5

        /**
         * How many levels of poison to given when near a fallout point.
         */
        private const val FALLOUT_POISON_LEVEL = 0

        /**
         * How many blocks the fallout particle locations must be fuzzed.
         */
        private const val FALLOUT_PARTICLE_FUZZ = 2.5

        /**
         * Up until how many blocks from the place of impact fallout could spawn.
         */
        private const val FALLOUT_RANGE = 64
    }
}