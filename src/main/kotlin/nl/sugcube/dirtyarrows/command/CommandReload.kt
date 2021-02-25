package nl.sugcube.dirtyarrows.command

import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.util.sendError
import nl.sugcube.dirtyarrows.util.sendFormattedMessage
import org.bukkit.command.CommandSender

/**
 * @author SugarCaney
 */
open class CommandReload : SubCommand<DirtyArrows>(
    name = "reload",
    usage = "/da reload",
    argumentCount = 0
) {

    init {
        addPermissions("dirtyarrows.admin")
    }

    override fun executeImpl(plugin: DirtyArrows, sender: CommandSender, vararg arguments: String) {
        try {
            plugin.reloadConfiguration()
            sender.sendFormattedMessage("&eReloaded config.yml")
        }
        catch (e: Exception) {
            e.printStackTrace()
            sender.sendError("Failed to load config.yml: ${e.message}")
        }
    }

    override fun assertSender(sender: CommandSender) = true
}