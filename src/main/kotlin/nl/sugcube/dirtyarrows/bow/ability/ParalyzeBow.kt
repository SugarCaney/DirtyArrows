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
        costRequirements = listOf(ItemStack(Material.NETHER_STALK, 1))
) {

    override fun land(arrow: Arrow, player: Player, event: ProjectileHitEvent) {
        val target = event.hitEntity as? LivingEntity ?: return

        if (Random.nextDouble() < CONFUSION_CHANCE) {
            target.giveEffect(PotionEffectType.CONFUSION)
        }
        if (Random.nextDouble() < EFFECT_CHANCE) {
            target.giveEffect(PotionEffectType.SLOW)
        }
        if (Random.nextDouble() < EFFECT_CHANCE) {
            target.giveEffect(PotionEffectType.WEAKNESS)
        }
        if (Random.nextDouble() < EFFECT_CHANCE) {
            target.giveEffect(PotionEffectType.BLINDNESS)
        }
    }

    /**
     * Applies the given potion/paralyze effect for a random time.
     */
    private fun LivingEntity.giveEffect(type: PotionEffectType) {
        val time = EFFECT_TIME.fuzz(EFFECT_TIME_FUZZING) + if (type == PotionEffectType.CONFUSION) 60 else 0
        val level = if (type == PotionEffectType.CONFUSION) 1.fuzz(1) else 20
        addPotionEffect(PotionEffect(type, time, level))
    }

    companion object {

        /**
         * How long the effects last in ticks.
         */
        private const val EFFECT_TIME = 12 * 20

        /**
         * How much variance there is in the effect time, in ticks.
         */
        private const val EFFECT_TIME_FUZZING = 3 * 20

        /**
         * How much chance the target has to be applied a certain effect in `[0,1]`.
         */
        private const val EFFECT_CHANCE = 0.4

        /**
         * How much chance the target has to be confused in `[0,1]`.
         */
        private const val CONFUSION_CHANCE = 0.5
    }
}