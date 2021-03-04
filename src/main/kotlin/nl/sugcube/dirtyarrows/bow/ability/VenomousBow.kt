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
) {

    override fun land(arrow: Arrow, player: Player, event: ProjectileHitEvent) {
        val target = event.hitEntity as? LivingEntity ?: return
        target.addPotionEffect(PotionEffect(
                PotionEffectType.POISON,
                POISON_TIME + Random.nextInt(-POISON_TIME_FUZZING, POISON_TIME_FUZZING),
                2
        ), true)
        target.location.showPotionParticle(PotionType.POISON)
    }

    companion object {

        /**
         * How long the target gets poisoned in ticks.
         */
        private const val POISON_TIME = 8 * 20

        /**
         * (Random) varience in poison time (ticks).
         */
        private const val POISON_TIME_FUZZING = 30
    }
}