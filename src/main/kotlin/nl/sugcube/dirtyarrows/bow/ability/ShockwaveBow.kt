package nl.sugcube.dirtyarrows.bow.ability

import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.bow.BowAbility
import nl.sugcube.dirtyarrows.bow.DefaultBow
import nl.sugcube.dirtyarrows.util.copyOf
import nl.sugcube.dirtyarrows.util.showColoredDust
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.entity.Arrow
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.entity.ProjectileHitEvent
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin

/**
 * Arrows create a shockwave on impact dealing damage to all entities that hit it.
 *
 * @author SugarCaney
 */
open class ShockwaveBow(plugin: DirtyArrows) : BowAbility(
        plugin = plugin,
        type = DefaultBow.SHOCKWAVE,
        handleEveryNTicks = 1,
        canShootInProtectedRegions = false,
        removeArrow = true,
        description = "Arrows shoot after a delay period."
) {

    /**
     * Maximum radius of the shockwave (diameter / 2).
     */
    val radius = config.getDouble("$node.radius")

    /**
     * The amount of blocks per second the shockwave travels.
     */
    val speed = config.getDouble("$node.speed")

    /**
     * With how much to multiply the arrow damage to get the shockwave damage.
     */
    val damageMultiplier = config.getDouble("$node.damage-multiplier")

    /**
     * The amount of particles to show for the shockwave circle each particle tick.
     */
    val particleCount = config.getInt("$node.particle-count")

    /**
     * Show the shockwave particles every N ticks.
     */
    val ticksPerParticle = config.getInt("$node.ticks-per-particle")

    init {
        check(radius >= 0) { "$node.radius cannot be negative, got <$radius>" }
        check(speed >= 0) { "$node.radius cannot be negative, got <$speed>" }
        check(particleCount >= 0) { "$node.particle-count cannot be negative, got <$particleCount>" }
        check(ticksPerParticle > 0) { "$node.ticks-per-particle must be greater than 0, got <$ticksPerParticle>" }
    }

    private val shockwaves = HashSet<Shockwave>()

    override fun land(arrow: Arrow, player: Player, event: ProjectileHitEvent) {
        shockwaves += Shockwave(
            player,
            arrow.location,
            speed,
            arrow.fireTicks > 0,
            arrow.damage * damageMultiplier,
            particleCount
        )
    }

    override fun effect() {
        shockwaves.forEach { it.tick() }
        shockwaves.removeIf { it.currentRadius >= radius }
    }

    override fun particle(tickNumber: Int) {
        if (tickNumber % ticksPerParticle != 0) return

        shockwaves.forEach { it.particles() }
    }

    /**
     * @author SugarCaney
     */
    class Shockwave(

        /**
         * Who caused the shockwave.
         */
        val shooter: LivingEntity,

        /**
         * The epicentre of the shockwave.
         */
        val epicentre: Location,

        /**
         * The speed of the shockwave in blocks per second.
         */
        val speed: Double,

        /**
         * Whether to set entities on fire.
         */
        val flame: Boolean,

        /**
         * The damage to deal by the shockwave.
         */
        val damage: Double,

        /**
         * How many particles to show per shockwave circle.
         */
        val particleCount: Int = 72
    ) {

        var currentRadius: Double = 0.0
        private val radiansPerParticle = Math.PI * 2 / particleCount

        fun tick() {
            currentRadius += speed / 20.0
            damageEntities()
        }

        fun damageEntities() {
            epicentre.world!!.livingEntities.asSequence()
                .filter { it.location.distance(epicentre) - currentRadius <= 0.1 }
                .forEach {
                    it.damage(damage, shooter)

                    if (flame) {
                        it.fireTicks = max(it.fireTicks, 200)
                    }
                }
        }

        fun particles() {
            repeat(particleCount) {
                val x = currentRadius * cos(it * radiansPerParticle) + epicentre.x
                val z = currentRadius * sin(it * radiansPerParticle) + epicentre.z

                epicentre.copyOf(x = x, z = z)
                    .showColoredDust(if (flame) FIRE_DUST_COLOUR else DUST_COLOUR, 1)
            }
        }

        companion object {

            private val DUST_COLOUR = Color.fromRGB(200, 200, 200)
            private val FIRE_DUST_COLOUR = Color.fromRGB(252, 188, 128)
        }
    }
}