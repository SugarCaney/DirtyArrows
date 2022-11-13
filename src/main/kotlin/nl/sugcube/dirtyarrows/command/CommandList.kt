package nl.sugcube.dirtyarrows.command

import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.util.sendFormattedMessage
import org.bukkit.command.CommandSender

/**
 * @author SugarCaney
 */
open class CommandList : SubCommand(
        name = "list",
        usage = "/da list",
        argumentCount = 0,
        description = "Lists all registered protection regions."
) {

    init {
        addPermissions("dirtyarrows.admin")
    }

    override fun executeImpl(plugin: DirtyArrows, sender: CommandSender, vararg arguments: String) {
        val regions = plugin.regionManager.allNames
        val chat = regions.joinToString("${plugin.broadcast.colourPrimary}, ") { "${plugin.broadcast.colourSecondary}$it" }
        sender.sendFormattedMessage(plugin.broadcast.regionsList(regions.size, chat))
    }

    override fun assertSender(sender: CommandSender) = true
}