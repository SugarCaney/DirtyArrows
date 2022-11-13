package nl.sugcube.dirtyarrows.command

import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.region.Region
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

/**
 * @author SugarCaney
 */
open class CommandRegister : SubCommand(
        name = "register",
        usage = "/da register <region>",
        argumentCount = 1,
        description = "Registers the region between positions 1 & 2."
) {

    init {
        addPermissions("dirtyarrows.admin")

        addAutoCompleteMeta(0, AutoCompleteArgument.NOTHING)
    }

    override fun executeImpl(plugin: DirtyArrows, sender: CommandSender, vararg arguments: String) {
        val regionName = arguments.first()

        if (regionName.matches(Region.NAME_REGEX).not()) {
            sender.sendMessage(plugin.broadcast.regionInvalidCharacters(regionName))
            return
        }

        if (plugin.regionManager.regionByName(regionName) != null) {
            sender.sendMessage(plugin.broadcast.regionExists(regionName))
            return
        }

        val region = plugin.regionManager.createRegion(regionName) ?: run {
            sender.sendMessage(plugin.broadcast.couldNotCreateRegion(regionName))
            return
        }
        sender.sendMessage(plugin.broadcast.regionCreated(region.name))
    }

    override fun assertSender(sender: CommandSender) = sender is Player
}