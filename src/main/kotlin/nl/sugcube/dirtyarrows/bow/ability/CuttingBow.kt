package nl.sugcube.dirtyarrows.bow.ability

import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.bow.BowAbility
import nl.sugcube.dirtyarrows.bow.DefaultBow
import nl.sugcube.dirtyarrows.util.*
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Arrow
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.entity.ProjectileLaunchEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

/**
 * Shoots two projectiles that connect by a damaging laser beam.
 *
 * @author SugarCaney
 */
open class CuttingBow(plugin: DirtyArrows) : BowAbility(
    plugin = plugin,
    type = DefaultBow.CUTTING,
    canShootInProtectedRegions = false,
    removeArrow = false,
    handleEveryNTicks = 1,
    costRequirements = listOf(ItemStack(Material.REDSTONE, 3)),
    description = "Shoot two projectiles that connect via a damaging beam."
) {

    /**
     * How many blocks per second the cutting beams travel.
     */
    val speed = config.getDouble("$node.speed")

    /**
     * How far the endpoints of the beam diverge in radians (the actual angle will be doubled).
     */
    val angle = config.getDouble("$node.angle")

    /**
     * How many ticks the beam endpoints diverge. After this tick count the beam no longer diverges, and goes straight.
     */
    val ticksToDiverge = config.getInt("$node.ticks-to-diverge")

    /**
     * How many ticks before the beam despawns.
     */
    val lifespan = config.getInt("$node.lifespan")

    /**
     * How far a target can be distanced from the beam before they get damaged.
     */
    val damageThreshold = config.getDouble("$node.damage-threshold")

    /**
     * How much damage the laser beam inflicts.
     */
    val baseDamage = config.getDouble("$node.base-damage")

    /**
     * How many fire ticks the beam inflicts when it was shot with a Flame bow.
     */
    val flameTicks = config.getInt("$node.flame-ticks")

    /**
     * Multiplier for how much extra damage to inflict for each Power level.
     */
    val powerMultiplier = config.getDouble("$node.power-multiplier")

    /**
     * The colour of the bullets.
     */
    val bulletColour: Color = config.getString("$node.bullet-colour").toColour("$node.bullet-colour")

    /**
     * The colour of the beam.
     */
    val beamColour: Color = config.getString("$node.beam-colour").toColour("$node.beam-colour")

    /**
     * The colour of the beam when shot with a Flame bow.
     */
    val beamColourFlame: Color = config.getString("$node.beam-colour-flame").toColour("$node.beam-colour-flame")

    private val beams = ArrayList<Beam>()

    override fun launch(player: Player, arrow: Arrow, event: ProjectileLaunchEvent) {
        val direction = arrow.velocity.clone().normalize()
        val bowItem = player.bowItem()

        beams.add(Beam(
            shooter = player,
            startLocation = arrow.location.clone(),
            direction = direction,
            speed = speed,
            angle = angle,
            ticksToDiverge = ticksToDiverge,
            flameTicks = if (bowItem?.containsEnchantment(Enchantment.ARROW_FIRE) == true) flameTicks else 0,
            powerLevel = bowItem?.getEnchantmentLevel(Enchantment.ARROW_DAMAGE) ?: 0
        ))

        unregisterArrow(arrow)
        arrow.scheduleRemoval(plugin)
    }

    override fun effect() {
        beams.forEach { beam ->
            // Update beam.
            beam.tick()
            val (first, second) = beam.droneLocations()
            val world = beam.world ?: error("Beam $beam has no world.")

            // Particles.
            first.lineIteration(second, 0.2).forEach {
                val colour = if (beam.flameTicks > 0) beamColourFlame else beamColour
                it.toLocation(world).showColoredDust(colour, 1)
            }
            first.toLocation(world).showColoredDust(bulletColour, 5)
            second.toLocation(world).showColoredDust(bulletColour, 5)

            // Damage.
            first.toLocation(world).nearbyLivingEntities(first.distance(second) + damageThreshold * 2).asSequence()
                .filter { it != beam.shooter }
                .filter {
                    val a = it.centre
                    val da = a.toVector().distance(first)
                    val db = a.toVector().distance(second)
                    val beamWidth = first.distance(second)
                    da + db < beamWidth + damageThreshold
                }
                .forEach {
                    val damage = baseDamage * (1 + powerMultiplier * beam.powerLevel)
                    it.damage(damage, beam.shooter)

                    if (beam.flameTicks > 0) {
                        it.fireTicks = max(it.fireTicks, beam.flameTicks)
                    }
                }

            // Check for collision: if one drone location can be removed, the whole beam must go.
            if (first.toLocation(world).block.type.isSolid || second.toLocation(world).block.type.isSolid) {
                beam.toRemove = true
            }
        }

        beams.removeIf { it.toRemove || it.timePassed >= lifespan }
    }

    /**
     * @author SugarCaney
     */
    data class Beam(

        /**
         * Who shot the beam.
         */
        val shooter: LivingEntity,

        /**
         * The start location of the beam.
         */
        val startLocation: Location,

        /**
         * Normalised direction of the center of the beam.
         */
        val direction: Vector,

        /**
         * Blocks/second.
         */
        val speed: Double = 6.0,

        /**
         * The angle of diversion.
         */
        val angle: Double = Math.PI / 14,

        /**
         * How long the beam can diverge in ticks before it just continues straight.
         */
        val ticksToDiverge: Int = 50,

        /**
         * How many fire ticks to inflict on damage.
         * 0 to not inflict fire damage.
         */
        val flameTicks: Int = 0,

        /**
         * The level of the power enchantment on the bow.
         */
        val powerLevel: Int = 0
    ) {

        /**
         * The amount of ticks that have passed.
         */
        var timePassed: Int = 0

        /**
         * The world the beam is in.
         */
        val world = startLocation.world

        /**
         * Whether the beam must be removed.
         */
        var toRemove: Boolean = false

        fun tick() {
            timePassed++
        }

        /**
         * Get the locations where the left (first) and right (right) bullets of this beam.
         */
        fun droneLocations(): Pair<Vector, Vector> {
            val bulletDistance = timePassed / 20.0 * speed
            val middlePoint = startLocation.toVector().add(direction.clone().multiply(bulletDistance))

            // Beam is perpendicular to shooting plane (UP vector X shooting direction)
            val planeNormal = UP.getCrossProduct(direction).normalize()
            val distanceFromPlane = bulletDistance * sin(angle)
            val maximumDistanceFromPlane = (ticksToDiverge / 20.0 * speed) * sin(angle)

            val distance = min(distanceFromPlane, maximumDistanceFromPlane)

            return Pair(
                middlePoint.clone().add(planeNormal.clone().multiply(distance)),
                middlePoint.clone().add(planeNormal.clone().multiply(-distance))
            )
        }
    }

    companion object {

        /**
         * UP vector.
         */
        private val UP = Vector(0, 1, 0)
    }
}