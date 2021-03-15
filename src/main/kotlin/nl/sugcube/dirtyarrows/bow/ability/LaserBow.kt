package nl.sugcube.dirtyarrows.bow.ability

import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.bow.BowAbility
import nl.sugcube.dirtyarrows.bow.DefaultBow
import nl.sugcube.dirtyarrows.util.*
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Arrow
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.entity.ProjectileLaunchEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector

/**
 * Shoots straight at the target. Slightly less damage than a regular bow.
 * Instant damage. Laser beams can travel through transparent blocks.
 *
 * @author SugarCaney
 */
open class LaserBow(plugin: DirtyArrows) : BowAbility(
        plugin = plugin,
        type = DefaultBow.LASER,
        canShootInProtectedRegions = true,
        removeArrow = false,
        description = "Shoot fast laser beams.",
        costRequirements = listOf(ItemStack(Material.REDSTONE, 1))
) {

    /**
     * The chance that a laser beam crits. Number between 0 and 1.
     */
    val criticalHitChance = config.getDouble("$node.critical-hit-chance")

    /**
     * With what number to multiply the damage dealt compared to the vanilla damage calculation.
     */
    val damageMultiplier = config.getDouble("$node.damage-multiplier")

    /**
     * The maximum amount of blocks a laser beam can travel.
     */
    val maximumRange = config.getDouble("$node.maximum-range")

    /**
     * In what increment the laser travels (in blocks).
     */
    val step = config.getDouble("$node.step")

    init {
        check(criticalHitChance in 0.0..1.0) { "$node.critical-hit-chance must be between 0 and 1, got <$criticalHitChance>" }
        check(step > 0) { "$node.step must be greater than 0, got <$step>" }
    }

    override fun launch(player: Player, arrow: Arrow, event: ProjectileLaunchEvent) {
        val launchLocation = player.location
        val direction = player.location.direction.normalize()

        player.playSound(player.location, Sound.ENTITY_VEX_HURT, 30f, 1f)

        // Continue checking the line from the player's direction until the maximum distance is exceeded.
        // Stop prematurely when a solid block is hit, or when an entity is hit.
        val checkLocation = arrow.location
        while (checkLocation.distance(launchLocation) <= maximumRange) {
            checkLocation.add(direction.copyOf().multiply(step))

            // Beam can only go through transparent blocks.
            if (checkLocation.block.type.isOccluding) break

            // Check if it hit an entity.
            val target = checkLocation.findEntity(player)
            if (target != null) {
                target.damage(arrow, player)
                player.playSound(player.location, Sound.ENTITY_ARROW_HIT_PLAYER, 10f, 1f)
                break
            }

            checkLocation.showBeamPart(direction)
        }

        // Only launch the laser beam.
        unregisterArrow(arrow)
        arrow.remove()
    }

    /**
     * Damages this entity.
     *
     * @param arrow
     *          The arrow that was shot.
     * @param player
     *          The player that shot the laser.
     */
    private fun LivingEntity.damage(arrow: Arrow, player: Player) {
        val damageAmount = arrow.damage(player.bowItem())
        damage(damageAmount, player)
    }

    /**
     * Get the first living entity near this location.
     */
    private fun Location.findEntity(shooter: LivingEntity): LivingEntity? {
        return nearbyLivingEntities(0.5).asSequence()
                .filter { it != shooter }
                .firstOrNull()
    }

    /**
     * Shows the laser beam part at this location in the direction of the given vector.
     */
    private fun Location.showBeamPart(direction: Vector) {
        copyOf().add(direction.copyOf().multiply(step * 0.67))
                .fuzz(0.05)
                .showColoredDust(org.bukkit.Color.RED, 1)

        fuzz(0.05).showColoredDust(org.bukkit.Color.RED, 1)

        copyOf().add(direction.copyOf().multiply(step * 1.33))
                .fuzz(0.05)
                .showColoredDust(org.bukkit.Color.RED, 1)
    }

    /**
     * Calculates how much damage to deal with the laser beam.
     *
     * @param bow
     *          The bow that the shooter holds.
     */
    private fun Arrow.damage(bow: ItemStack?): Double {
        val powerLevel = bow?.getEnchantmentLevel(Enchantment.ARROW_DAMAGE) ?: 0
        val velocity = velocity.length()
        return arrowDamage(velocity, criticalHitChance, powerLevel) * damageMultiplier
    }
}