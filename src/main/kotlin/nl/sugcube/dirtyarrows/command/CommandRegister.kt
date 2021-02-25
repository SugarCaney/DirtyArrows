package nl.sugcube.dirtyarrows.command

import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.util.Message
import nl.sugcube.dirtyarrows.util.sendError
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

/**
 * @author SugarCaney
 */
open class CommandRegister : SubCommand<DirtyArrows>(
    name = "register",
    usage = "/da register <region>",
    argumentCount = 1
) {

    init {
        addPermissions("dirtyarrows.admin")

        addAutoCompleteMeta(0, AutoCompleteArgument.NOTHING)
    }

    override fun executeImpl(plugin: DirtyArrows, sender: CommandSender, vararg arguments: String) {
        val regionName = arguments.first()

        if (regionName.matches(Regex("^[A-Za-z\\d_]+$")).not()) {
            sender.sendError("Region must only contain letters, numbers or underscores ('$regionName')")
            return
        }

        if (plugin.rm.getRegionByName(regionName) != null) {
            sender.sendError("Region '$regionName' already exists.")
            return
        }

        val region = plugin.rm.createRegion(regionName) ?: run {
            sender.sendError("Could not create region '$regionName', check if positions 1 & 2 are set.")
            return
        }
        sender.sendMessage(Message.REGION_CREATED.format(region.name))
    }

    override fun assertSender(sender: CommandSender) = sender is Player
}