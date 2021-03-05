package nl.sugcube.dirtyarrows.effect

import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

/**
 * Applies special effects when damaged:
 * - Wither effect when hit by a wither skull.
 *
 * @author SugarCaney
 */
open class DamageEffects : Listener {

    @EventHandler
    fun witherByWitherSkull(event: EntityDamageByEntityEvent) {
        if (event.damager.type != EntityType.WITHER_SKULL) return
        val target = event.entity as? LivingEntity ?: return
        target.addPotionEffect(PotionEffect(PotionEffectType.WITHER, 180, 1))
    }
}