package nl.sugcube.dirtyarrows.bow.ability

import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.bow.BowAbility
import nl.sugcube.dirtyarrows.bow.DefaultBow
import nl.sugcube.dirtyarrows.util.showPotionParticle
import org.bukkit.Material
import org.bukkit.entity.Arrow
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.potion.PotionType
import kotlin.random.Random

/**
 * Shoots arrows that poisons the target.
 *
 * @author SugarCaney
 */
open class VenomousBow(plugin: DirtyArrows) : BowAbility(
        plugin = plugin,
        type = DefaultBow.VENOMOUS,
        canShootInProtectedRegions = false,
        protectionRange = 1.0,
        costRequirements = listOf(ItemStack(Material.SPIDER_EYE, 1)),
        description = "Poisons the target."
) {

    /**
     * How long the target gets poisoned in ticks.
     */
    val poisonTime = config.getInt("$node.poison-time")

    /**
     * Maximum deviation from the poison time in ticks.
     */
    val poisonTimeFuzzing = config.getInt("$node.poison-time-fuzzing")

    /**
     * The level of poison
     */
    val poisonLevel = config.getInt("$node.poison-level")

    init {
        check(poisonTime >= 0) { "$node.poison-time cannot be negative, got <$poisonTime>" }
        check(poisonTimeFuzzing >= 0) { "$node.poison-time cannot be negative, got <$poisonTimeFuzzing>" }
        check(poisonLevel >= 1) { "$node.poison-time must greater than or equal to 1, got <$poisonLevel>" }
    }

    override fun land(arrow: Arrow, player: Player, event: ProjectileHitEvent) {
        val target = event.hitEntity as? LivingEntity ?: return
        target.addPotionEffect(PotionEffect(
                PotionEffectType.POISON,
                poisonTime + Random.nextInt(-poisonTimeFuzzing, poisonTimeFuzzing),
                poisonLevel - 1
        ))
        target.location.showPotionParticle(PotionType.POISON)
    }
}