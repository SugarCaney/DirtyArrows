package nl.sugcube.dirtyarrows.command

import nl.sugcube.dirtyarrows.Broadcast
import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.util.sendFormattedMessage
import org.bukkit.command.CommandSender

/**
 * @author SugarCaney
 */
open class CommandList : SubCommand<DirtyArrows>(
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
        val chat = regions.joinToString("&e, ") { "&a$it" }
        sender.sendFormattedMessage(Broadcast.REGIONS_LIST.format(regions.size, chat))
    }

    override fun assertSender(sender: CommandSender) = true
}