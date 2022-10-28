package nl.sugcube.dirtyarrows.bow.ability

import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.bow.BowAbility
import nl.sugcube.dirtyarrows.bow.DefaultBow
import nl.sugcube.dirtyarrows.util.createExplosion
import nl.sugcube.dirtyarrows.util.fuzz
import nl.sugcube.dirtyarrows.util.getFloat
import org.bukkit.Material
import org.bukkit.entity.*
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/**
 * Pulls the target toward you.
 *
 * @author SugarCaney
 */
open class BurstBow(plugin: DirtyArrows) : BowAbility(
        plugin = plugin,
        type = DefaultBow.BURST,
        canShootInProtectedRegions = false,
        removeArrow = false,
        costRequirements = listOf(ItemStack(Material.GUNPOWDER, 2)),
        description = "Bursts into projectiles on hit."
) {

    /**
     * In how many arrows the arrow must burst on hit.
     */
    val burstCount = config.getInt("$node.burst-count")

    /**
     * How powerful the explosions of the burst arrows mus be.
     */
    val power = config.getFloat("$node.power")

    /**
     * Whether the burst arrows can set blocks on fire when exploding.
     */
    val setOnFire = config.getBoolean("$node.set-on-fire")

    /**
     * Whether the burst arrows can break blocks when exploding.
     */
    val breakBlocks = config.getBoolean("$node.break-blocks")

    /**
     * The horizontal velocity the burst pellets must have on burst.
     */
    val horizontalVelocity = config.getDouble("$node.horizontal-velocity")

    /**
     * The maximum deviation from the horizontal velocity (randomly picked).
     */
    val horizontalVelocityFuzzing = config.getDouble("$node.horizontal-velocity-fuzzing")

    /**
     * The vertical veolcity the burst pellets must have on burst.
     */
    val verticalVelocity = config.getDouble("$node.vertical-velocity")

    /**
     * The maximum deviation from the vertical velocity (randomly picked).
     */
    val verticalVelocityFuzzing = config.getDouble("$node.vertical-velocity-fuzzing")

    /**
     * All burst arrows.
     */
    private val pellets = HashSet<Arrow>()

    override fun land(arrow: Arrow, player: Player, event: ProjectileHitEvent) {
        if (arrow in pellets) return

        // Randomise the angle of the pellets each shot.
        val baseAngle = Random.nextDouble(Math.PI * 2)
        val anglePerPellet = Math.PI * 2 / burstCount

        repeat(burstCount) { index ->
            val angle = baseAngle + index * anglePerPellet
            val x = cos(angle) * horizontalVelocity
            val z = sin(angle) * horizontalVelocity

            arrow.spawnBurst(x, verticalVelocity, z)
        }
    }

    private fun Arrow.spawnBurst(xInitial: Double, yInitial: Double, zInitial: Double) = world.spawnEntity(location, EntityType.ARROW).apply {
        val createdArrow = this as Arrow
        createdArrow.velocity = Vector(
            xInitial.fuzz(horizontalVelocityFuzzing),
            yInitial.fuzz(verticalVelocityFuzzing),
            zInitial.fuzz(horizontalVelocityFuzzing)
        )
        createdArrow.isCritical = false
        createdArrow.pickupStatus = AbstractArrow.PickupStatus.DISALLOWED
        createdArrow.knockbackStrength = this@spawnBurst.knockbackStrength
        createdArrow.fireTicks = this@spawnBurst.fireTicks
        createdArrow.shooter = this@spawnBurst.shooter
        registerArrow(createdArrow)
        pellets.add(createdArrow)
    } as Arrow

    @EventHandler
    fun pelletLandEvent(event: ProjectileHitEvent) {
        val arrow = event.entity as? Arrow ?: return
        if (arrow !in pellets || power <= 0.0) return
        if (arrow.location.isInProtectedRegion(arrow.shooter as? LivingEntity)) return

        pellets.remove(arrow)
        arrow.location.createExplosion(power, setOnFire, breakBlocks)
        unregisterArrow(arrow)
        arrow.remove()
    }
}