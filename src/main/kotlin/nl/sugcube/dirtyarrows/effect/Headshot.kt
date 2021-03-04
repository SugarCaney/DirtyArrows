package nl.sugcube.dirtyarrows.effect

import nl.sugcube.dirtyarrows.Broadcast
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

    @EventHandler
    fun headshotListener(event: EntityDamageByEntityEvent) {
        if (plugin.config.getBoolean("headshot").not()) return
        if (event.cause != EntityDamageEvent.DamageCause.PROJECTILE) return

        val projectile = event.damager as Projectile
        val damager = projectile.shooter as? Player ?: return
        val target = event.entity as? Player ?: return

        val y = projectile.location.y
        val targetY = target.location.y
        val isHeadshot = y - targetY > 1.35
        if (isHeadshot.not()) return

        val multiplier = plugin.config.getDouble("headshot-multiplier")
        event.damage = event.damage * multiplier

        target.sendMessage(Broadcast.headshot(damager, HeadshotType.BY, plugin))
        damager.sendMessage(Broadcast.headshot(target, HeadshotType.ON, plugin))
    }
}