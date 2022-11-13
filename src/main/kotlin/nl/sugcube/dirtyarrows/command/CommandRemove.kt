package nl.sugcube.dirtyarrows.command

import nl.sugcube.dirtyarrows.DirtyArrows
import org.bukkit.command.CommandSender

/**
 * @author SugarCaney
 */
open class CommandRemove : SubCommand(
        name = "remove",
        usage = "/da remove <region>",
        argumentCount = 1,
        description = "Removes the region with the given name."
) {

    init {
        addPermissions("dirtyarrows.admin")

        addAutoCompleteMeta(0, AutoCompleteArgument.REGIONS)
    }

    override fun executeImpl(plugin: DirtyArrows, sender: CommandSender, vararg arguments: String) {
        val regionName = arguments.firstOrNull() ?: run { sender.sendMessage(plugin.broadcast.noRegionSpecified()); return }
        val region = plugin.regionManager.regionByName(regionName)
                ?: run { sender.sendMessage(plugin.broadcast.noRegion(regionName)); return }
        plugin.regionManager.removeRegion(region.name)
        sender.sendMessage(plugin.broadcast.regionRemoved(region.name))
    }

    override fun assertSender(sender: CommandSender) = true
}