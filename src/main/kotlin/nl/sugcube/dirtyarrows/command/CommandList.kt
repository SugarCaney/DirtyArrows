package nl.sugcube.dirtyarrows.command

import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.util.sendFormattedMessage
import org.bukkit.command.CommandSender

/**
 * @author SugarCaney
 */
open class CommandList : SubCommand<DirtyArrows>(
    name = "list",
    usage = "/da list",
    argumentCount = 0
) {

    init {
        addPermissions("dirtyarrows.admin")
    }

    override fun executeImpl(plugin: DirtyArrows, sender: CommandSender, vararg arguments: String) {
        val regions = plugin.regionManager.allNames
        val chat = regions.joinToString("&e, ") { "&a$it" }
        sender.sendFormattedMessage("&eRegions (${regions.size}): &a$chat")
    }

    override fun assertSender(sender: CommandSender) = true
}