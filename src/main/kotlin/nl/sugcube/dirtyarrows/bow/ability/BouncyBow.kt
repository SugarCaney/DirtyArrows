package nl.sugcube.dirtyarrows.bow.ability

import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.bow.BowAbility
import nl.sugcube.dirtyarrows.bow.DefaultBow
import nl.sugcube.dirtyarrows.util.arrowDamage
import nl.sugcube.dirtyarrows.util.hitBlockFace
import org.bukkit.Sound
import org.bukkit.block.BlockFace
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.*
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.projectiles.ProjectileSource
import org.bukkit.util.Vector

/**
 * Arrows bounce a few times when they don't hit an enemy.
 * Preserves enchantments (incl. Power).
 *
 * @author SugarCaney
 */
open class BouncyBow(plugin: DirtyArrows) : BowAbility(
        plugin = plugin,
        type = DefaultBow.BOUNCY,
        canShootInProtectedRegions = true,
        removeArrow = false,
        description = "Bounces off block surfaces."
) {

    /**
     * Contains the power level for each arrow.
     */
    private val powerLevelMap = HashMap<Arrow, Int>()

    /**
     * Multiplier for the velocity of the arrow after the bounce.
     */
    val softening = config.getDouble("$node.softening")

    /**
     * Below which speed arrows cannot bounce anymore.
     */
    val bounceThreshold = config.getDouble("$node.bounce-threshold")

    /**
     * Multiplier for the velocity to bounce off this block face.
     */
    private val BlockFace.bounceVector: Vector
        get() = when (this) {
            BlockFace.EAST, BlockFace.WEST -> Vector(-1, 1, 1)
            BlockFace.UP, BlockFace.DOWN -> Vector(1, -1, 1)
            BlockFace.NORTH, BlockFace.SOUTH -> Vector(1, 1, -1)
            else -> Vector(1, 1, 1)
        }

    override fun land(arrow: Arrow, player: Player, event: ProjectileHitEvent) {
        // When an entity is hit, don't bounce.
        event.hitEntity?.let {
            it.inflictDamage(arrow)
            powerLevelMap.remove(arrow)
            arrow.remove()
            return
        }
        powerLevelMap.remove(arrow)

        // Stop bouncing when the threshold is met.
        if (arrow.velocity.length() < bounceThreshold) return

        // Bounce off face.
        val hitBlock = event.hitBlock ?: return
        val hitFace = arrow.hitBlockFace(hitBlock) ?: return
        val bounceDirection = hitFace.bounceVector.multiply(softening)
        val created = arrow.bounce(bounceDirection, player)

        // Keep track of the power level.
        val powerLevel = player.bowItem()?.getEnchantmentLevel(Enchantment.ARROW_DAMAGE) ?: 0
        powerLevelMap[created] = powerLevel
    }

    /**
     * Inflicts arrow damage to this entity, also takes power level into account.
     *
     * @param this
     *          The entity to damage.
     * @param arrow
     *          The arrow that damages the entity.
     */
    private fun Entity.inflictDamage(arrow: Arrow) {
        val target = this as? Damageable ?: return
        val powerLevel = powerLevelMap[arrow] ?: 0
        val damage = arrowDamage(arrow.velocity.length(), powerLevel = powerLevel)

        val shooter = arrow.shooter as? LivingEntity ?: return
        target.damage(damage, shooter)

        if (target is Player && shooter is Player) {
            shooter.playSound(shooter.location, Sound.ENTITY_ARROW_HIT_PLAYER, 10f, 1f)
        }
    }

    /**
     * Executes the bouncing.
     *
     * @param bounceDirection
     *          The vector to multiply with the original arrow velocity.
     * @param shooter
     *          Who shot the arrow.
     * @return The spawned arrow.
     */
    private fun Arrow.bounce(bounceDirection: Vector, shooter: ProjectileSource): Arrow {
        val landedArrow = this
        landedArrow.remove()
        return landedArrow.world.spawnEntity(landedArrow.location, EntityType.ARROW).apply {
            val createdArrow = this as Arrow
            createdArrow.velocity = landedArrow.velocity.multiply(bounceDirection)
            createdArrow.isCritical = landedArrow.isCritical
            createdArrow.pickupStatus = landedArrow.pickupStatus
            createdArrow.knockbackStrength = landedArrow.knockbackStrength
            createdArrow.fireTicks = landedArrow.fireTicks
            createdArrow.shooter = shooter
            registerArrow(createdArrow)
        } as Arrow
    }
}