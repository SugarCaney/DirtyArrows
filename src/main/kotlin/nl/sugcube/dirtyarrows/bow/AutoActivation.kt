package nl.sugcube.dirtyarrows.bow

import nl.sugcube.dirtyarrows.Broadcast
import nl.sugcube.dirtyarrows.DirtyArrows
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

/**
 * Auto enable dirtyarrows for players when disabled (also disable on leave).
 *
 * @author SugarCaney
 */
open class AutoActivation(private val plugin: DirtyArrows) : Listener {

    @EventHandler
    fun autoEnable(event: PlayerJoinEvent) {
        if (plugin.config.getBoolean("auto-enable").not()) return

        val player = event.player
        if (player.hasPermission("dirtyarrows").not()) return

        plugin.activationManager.activateFor(player)
        if (plugin.config.getBoolean("show-enable-message")) {
            player.sendMessage(Broadcast.enabledMessage(plugin, enabled = true))
        }
    }

    @EventHandler
    fun autoDisable(event: PlayerQuitEvent) {
        val player = event.player
        plugin.activationManager.deactivateFor(player)
    }
}