package nl.sugcube.dirtyarrows.bow.ability

import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.bow.BowAbility
import nl.sugcube.dirtyarrows.bow.DefaultBow
import nl.sugcube.dirtyarrows.util.scheduleDelayed
import org.bukkit.Material
import org.bukkit.entity.Arrow
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

/**
 * Shoots the target up into the air.
 *
 * @author SugarCaney
 */
open class LaunchBow(plugin: DirtyArrows) : BowAbility(
        plugin = plugin,
        type = DefaultBow.LAUNCH,
        canShootInProtectedRegions = false,
        removeArrow = false,
        costRequirements = listOf(ItemStack(Material.POPPED_CHORUS_FRUIT, 3)),
        description = "Shoots the target up into the air."
) {

    /**
     * How many ticks the launch levitation effect lasts.
     */
    val effectDuration = 43

    /**
     * The levitation effect level.
     */
    val levitationAmplifier = 15

    override fun land(arrow: Arrow, player: Player, event: ProjectileHitEvent) {
        val target = event.hitEntity as? LivingEntity ?: return

        target.addPotionEffect(PotionEffect(
                PotionEffectType.LEVITATION,
                effectDuration,
                levitationAmplifier,
                false,
                false,
                false
        ))

        plugin.scheduleDelayed(effectDuration.toLong()) {
            target.addPotionEffect(PotionEffect(
                PotionEffectType.SLOW_FALLING,
                effectDuration * 2,
                0,
                false,
                false,
                false
            ))
        }
    }
}