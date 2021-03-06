package nl.sugcube.dirtyarrows.bow.ability

import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.bow.BowAbility
import nl.sugcube.dirtyarrows.bow.DefaultBow
import nl.sugcube.dirtyarrows.util.fuzz
import org.bukkit.Material
import org.bukkit.entity.Arrow
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import kotlin.random.Random

/**
 * Paralyzes the target, which is a random cocktail of:
 * - Confusion
 * - Slowness
 * - Weakness
 * - Blindness
 *
 * @author SugarCaney
 */
open class ParalyzeBow(plugin: DirtyArrows) : BowAbility(
        plugin = plugin,
        type = DefaultBow.PARALYZE,
        canShootInProtectedRegions = true,
        costRequirements = listOf(ItemStack(Material.NETHER_STALK, 1)),
        description = "Paralyzes the target."
) {

    /**
     * How long the effects last in ticks.
     */
    val effectTime = config.getInt("$node.effect-time")

    /**
     * How much variance there is in the effect time, in ticks.
     */
    val effectTimeFuzzing = config.getInt("$node.effect-time-fuzzing")

    /**
     * How much chance the target has to be applied a certain effect in `[0,1]`.
     */
    val effectChance = config.getDouble("$node.effect-chance")

    /**
     * How much chance the target has to be confused in `[0,1]`.
     */
    val confusionChance = config.getDouble("$node.confusion-chance")

    init {
        check(effectTime >= 0) { "$node.effect-time must not be negative, got <$effectTime>" }
        check(effectTimeFuzzing >= 0) { "$node.effect-time-fuzzing must not be negative, got <$effectTimeFuzzing>" }
        check(effectChance in 0.0..1.0) { "$node.effect-chance must be between 0 and 1, got <$effectChance>" }
        check(confusionChance in 0.0..1.0) { "$node.confusion-chance must be between 0 and 1, got <$confusionChance>" }
    }

    override fun land(arrow: Arrow, player: Player, event: ProjectileHitEvent) {
        val target = event.hitEntity as? LivingEntity ?: return

        if (Random.nextDouble() < confusionChance) {
            target.giveEffect(PotionEffectType.CONFUSION)
        }
        if (Random.nextDouble() < effectChance) {
            target.giveEffect(PotionEffectType.SLOW)
        }
        if (Random.nextDouble() < effectChance) {
            target.giveEffect(PotionEffectType.WEAKNESS)
        }
        if (Random.nextDouble() < effectChance) {
            target.giveEffect(PotionEffectType.BLINDNESS)
        }
    }

    /**
     * Applies the given potion/paralyze effect for a random time.
     */
    private fun LivingEntity.giveEffect(type: PotionEffectType) {
        val time = effectTime.fuzz(effectTimeFuzzing) + if (type == PotionEffectType.CONFUSION) 60 else 0
        val level = if (type == PotionEffectType.CONFUSION) 1.fuzz(1) else 20
        addPotionEffect(PotionEffect(type, time, level))
    }
}