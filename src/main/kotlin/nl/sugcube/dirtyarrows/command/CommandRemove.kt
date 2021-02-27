package nl.sugcube.dirtyarrows.command

import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.util.Message
import nl.sugcube.dirtyarrows.util.sendError
import org.bukkit.command.CommandSender

/**
 * @author SugarCaney
 */
open class CommandRemove : SubCommand<DirtyArrows>(
    name = "remove",
    usage = "/da remove <region>",
    argumentCount = 1
) {

    init {
        addPermissions("dirtyarrows.admin")

        addAutoCompleteMeta(0, AutoCompleteArgument.REGIONS)
    }

    override fun executeImpl(plugin: DirtyArrows, sender: CommandSender, vararg arguments: String) {
        val regionName = arguments.firstOrNull() ?: run { sender.sendError("No region name specified."); return }
        val region = plugin.regionManager.regionByName(regionName) ?: run { sender.sendError("There is no region with name '$regionName'"); return }
        plugin.regionManager.removeRegion(region.name)
        sender.sendMessage(Message.REGION_REMOVED.format(region.name))
    }

    override fun assertSender(sender: CommandSender) = true
}