package nl.sugcube.dirtyarrows.command

import nl.sugcube.dirtyarrows.Broadcast
import nl.sugcube.dirtyarrows.DirtyArrows
import org.bukkit.command.CommandSender

/**
 * @author SugarCaney
 */
open class CommandReload : SubCommand(
        name = "reload",
        usage = "/da reload",
        argumentCount = 0,
        description = "Reloads the configuration file."
) {

    init {
        addPermissions("dirtyarrows.admin")
    }

    override fun executeImpl(plugin: DirtyArrows, sender: CommandSender, vararg arguments: String) = with(plugin) {
        try {
            configurationManager.loadConfig()
            configurationManager.loadLang()
            recipeManager.reloadConfig()
            bowManager.reload()
            plugin.broadcast = Broadcast(plugin)

            sender.sendMessage(plugin.broadcast.reloadedConfig())

        } catch (e: Exception) {
            e.printStackTrace()
            sender.sendMessage(plugin.broadcast.reloadFailed(e.message ?: "???"))
        }
    }

    override fun assertSender(sender: CommandSender) = true
}