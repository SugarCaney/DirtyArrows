package nl.sugcube.dirtyarrows.command

import nl.sugcube.dirtyarrows.Broadcast
import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.util.sendFormattedMessage
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

/**
 * @author SugarCaney
 */
open class CommandCheck : SubCommand<DirtyArrows>(
        name = "check",
        usage = "/da check",
        argumentCount = 0,
        description = "Checks in which registered region you are."
) {

    init {
        addPermissions("dirtyarrows.admin")
    }

    override fun executeImpl(plugin: DirtyArrows, sender: CommandSender, vararg arguments: String) {
        val player = sender as? Player ?: error("Sender is not a player.")
        val region = plugin.regionManager.isWithinARegion(player.location)

        if (region == null) {
            sender.sendMessage(Broadcast.NOT_IN_REGION)
        } else sender.sendFormattedMessage(Broadcast.IN_REGION.format(region.name))
    }

    override fun assertSender(sender: CommandSender) = sender is Player
}