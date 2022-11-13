package nl.sugcube.dirtyarrows

import nl.sugcube.dirtyarrows.util.Update
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

/**
 * Update checking for admins (when they join).
 *
 * @author SugarCaney
 */
open class UpdateCheck(private val plugin: DirtyArrows) : Listener {

    @EventHandler
    fun updateChecker(event: PlayerJoinEvent) {
        if (plugin.config.getBoolean("updates.check-for-updates").not()) return
        if (plugin.config.getBoolean("updates.show-admin").not()) return

        val player = event.player
        if (player.hasPermission("dirtyarrows.admin").not()) return

        val updateChecker = Update(DirtyArrows.BUKKIT_DEV_PROJECT_ID, plugin.description.version)
        if (updateChecker.query()) {
            player.sendMessage(plugin.broadcast.newVersionAvailable(updateChecker.latestVersion ?: "???"))
        }
    }
}