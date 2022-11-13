package nl.sugcube.dirtyarrows.effect

import nl.sugcube.dirtyarrows.DirtyArrows
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent

/**
 * Damage multiplier for hits on the head of another player.
 *
 * @author SugarCaney
 */
open class Headshot(private val plugin: DirtyArrows) : Listener {

    /**
     * Whether headshots are enabled.
     */
    val enabled: Boolean
        get() = plugin.config.getBoolean("headshot.enabled")

    /**
     * With what number to multiply the damage dealt when a headshot was made.
     */
    val damageMultiplier: Double
        get() = plugin.config.getDouble("headshot.damage-multiplier")

    @EventHandler
    fun headshotListener(event: EntityDamageByEntityEvent) {
        if (enabled.not()) return
        if (event.cause != EntityDamageEvent.DamageCause.PROJECTILE) return

        val projectile = event.damager as Projectile
        val damager = projectile.shooter as? Player ?: return
        val target = event.entity as? Player ?: return

        val y = projectile.location.y
        val targetY = target.location.y
        val isHeadshot = y - targetY > 1.35
        if (isHeadshot.not()) return

        event.damage = event.damage * damageMultiplier

        target.sendMessage(plugin.broadcast.headshotBy(damager.displayName))
        damager.sendMessage(plugin.broadcast.headshotOn(target.displayName))
    }
}