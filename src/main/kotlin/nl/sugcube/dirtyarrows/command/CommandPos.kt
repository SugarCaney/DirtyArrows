package nl.sugcube.dirtyarrows.command

import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.util.sendFormattedMessage
import nl.sugcube.dirtyarrows.util.toCoordinateString
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

/**
 * @author SugarCaney
 */
open class CommandPos(val id: Int) : SubCommand(
        name = "pos$id",
        usage = "/da pos$id",
        argumentCount = 0,
        description = "Set region position $id."
) {

    init {
        addPermissions("dirtyarrows.admin")
    }

    override fun executeImpl(plugin: DirtyArrows, sender: CommandSender, vararg arguments: String) {
        val player = sender as? Player ?: error("Sender is not a player.")
        val location = player.location
        plugin.regionManager.setSelection(id, location)
        sender.sendFormattedMessage(plugin.broadcast.positionSet(id, location.toCoordinateString()))
    }

    override fun assertSender(sender: CommandSender) = sender is Player
}