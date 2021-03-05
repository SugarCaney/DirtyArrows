package nl.sugcube.dirtyarrows

import nl.sugcube.dirtyarrows.util.Update
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

/**
 * Handles all join events:
 * - checking for updates for admins
 * - auto enable dirtyarrows
 *
 * @author SugarCaney
 */
open class JoinListener(private val plugin: DirtyArrows) : Listener {

    @EventHandler
    fun updateChecker(event: PlayerJoinEvent) {
        if (plugin.config.getBoolean("updates.check-for-updates").not()) return
        if (plugin.config.getBoolean("updates.show-admin").not()) return

        val player = event.player
        if (player.hasPermission("dirtyarrows.admin").not()) return

        val updateChecker = Update(DirtyArrows.BUKKIT_DEV_PROJECT_ID, plugin.description.version)
        if (updateChecker.query()) {
            player.sendMessage(Broadcast.NEW_VERSION_AVAILABLE.format(updateChecker.latestVersion))
        }
    }

    @EventHandler
    fun autoEnable(event: PlayerJoinEvent) {
        if (plugin.config.getBoolean("auto-enable").not()) return

        val player = event.player
        plugin.activationManager.activateFor(player)

        if (plugin.config.getBoolean("show-enable-message")) {
            player.sendMessage(Broadcast.enabledMessage(plugin, enabled = true))
        }
    }
}