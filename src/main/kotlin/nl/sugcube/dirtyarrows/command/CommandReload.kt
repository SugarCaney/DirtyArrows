package nl.sugcube.dirtyarrows.command

import nl.sugcube.dirtyarrows.Broadcast
import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.util.sendError
import org.bukkit.command.CommandSender

/**
 * @author SugarCaney
 */
open class CommandReload : SubCommand<DirtyArrows>(
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
            recipeManager.reloadRecipes()

            sender.sendMessage(Broadcast.RELOADED_CONFIG)
        } catch (e: Exception) {
            e.printStackTrace()
            sender.sendError("Failed to load config.yml: ${e.message}")
        }
    }

    override fun assertSender(sender: CommandSender) = true
}