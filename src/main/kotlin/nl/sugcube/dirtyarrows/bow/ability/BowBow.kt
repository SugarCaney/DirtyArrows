package nl.sugcube.dirtyarrows.bow.ability

import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.bow.BowAbility
import nl.sugcube.dirtyarrows.bow.DefaultBow
import nl.sugcube.dirtyarrows.util.*
import org.bukkit.GameMode
import org.bukkit.entity.Arrow
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.event.entity.ProjectileLaunchEvent
import org.bukkit.util.Vector
import java.util.concurrent.ConcurrentHashMap

/**
 * Shoots arrows that act as bows, and shoot more arrows to the closes living entities.
 *
 * @author SugarCaney
 */
open class BowBow(plugin: DirtyArrows) : BowAbility(
        plugin = plugin,
        type = DefaultBow.BOW,
        handleEveryNTicks = 1,
        canShootInProtectedRegions = true,
        removeArrow = false,
        description = "Shoots arrows that shoot arrows."
) {

    /**
     * All arrows that are affected by this bow effect, mapped to their birth time as a unix timestamp.
     */
    private val bowArrows = ConcurrentHashMap<Arrow, Long>()

    /**
     * Arrows shoot an arrow every, this amount of, ticks.
     */
    val handleFrequency = config.getInt("$node.handle-every-n-ticks")

    /**
     * Maximum amount of milliseconds the arrows can shoot child arrows.
     */
    val maximumLifespan = config.getLong("$node.maximum-lifespan")

    /**
     * How far the arrow will look for targets to shoot, in blocks.
     */
    val shootRange = config.getDouble("$node.shoot-range")

    /**
     * The initial speed of the child arrows will be this multiplier times the original arrow speed.
     */
    val velocityMultiplier = config.getDouble("$node.velocity-multiplier")

    init {
        check(handleFrequency >= 1) { "$node.handle-every-n-ticks must be greater than or equal to 1, got <$handleFrequency>" }
    }

    override fun launch(player: Player, arrow: Arrow, event: ProjectileLaunchEvent) {
        bowArrows[arrow] = System.currentTimeMillis()
    }

    override fun land(arrow: Arrow, player: Player, event: ProjectileHitEvent) {
        bowArrows.remove(arrow)
    }

    override fun effect() {
        if (tickCounter % handleFrequency != 0) return

        bowArrows.entries.removeIf { (arrow, birthTime) ->
            arrow.shoot()
            System.currentTimeMillis() - birthTime > maximumLifespan
        }
    }

    /**
     * Shoots a child projectile from this arrow when a target is in range.
     */
    private fun Arrow.shoot() {
        // Check if the player has an arrow.
        val player = shooter as? Player ?: return
        val bow = player.bowItem() ?: return

        if (player.hasArrows().not()) return

        // Determine the direction of the arrow.
        val target = location.nearbyLivingEntities(shootRange).filter { it != player }.randomOrNull() ?: return
        val spawnLocation = location
        val direction = target.location.copyOf().subtract(spawnLocation)
        val speed = velocity.length()
        val arrowVelocity = direction.toVector().normalize().multiply(speed).add(Vector(0.0, 0.25, 0.0))

        player.removeArrow(bow)

        // Spawn the arrow.
        spawnLocation.world.spawnEntity(spawnLocation, EntityType.ARROW).apply {
            val arrow = this as Arrow
            arrow.shooter = player
            arrow.velocity = arrowVelocity.multiply(velocityMultiplier)
            arrow.applyBowEnchantments(bow)
            arrow.isCritical = false

            if (player.gameMode == GameMode.CREATIVE) {
                arrow.pickupStatus = Arrow.PickupStatus.CREATIVE_ONLY
            }

            registerArrow(arrow)
        }
        showSmokeParticle()
    }
}