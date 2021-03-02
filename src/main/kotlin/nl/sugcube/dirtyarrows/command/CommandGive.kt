package nl.sugcube.dirtyarrows.command

import nl.sugcube.dirtyarrows.Broadcast
import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.bow.DefaultBow
import nl.sugcube.dirtyarrows.bow.BowDistributor
import nl.sugcube.dirtyarrows.util.onlinePlayer
import nl.sugcube.dirtyarrows.util.sendError
import nl.sugcube.dirtyarrows.util.sendFormattedMessage
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender

/**
 * @author SugarCaney
 */
open class CommandGive : SubCommand<DirtyArrows>(
    name = "give",
    usage = "/da give <player> <bow> [ench]",
    argumentCount = 2
) {

    init {
        addPermissions("dirtyarrows.admin")

        addAutoCompleteMeta(0, AutoCompleteArgument.PLAYERS)
        addAutoCompleteMeta(1, AutoCompleteArgument.BOWS)
        addAutoCompleteMeta(2, AutoCompleteArgument.ENCHANTED)
    }

    override fun executeImpl(plugin: DirtyArrows, sender: CommandSender, vararg arguments: String) {
        // All players to give the resulting bow.
        val playerName = arguments.firstOrNull() ?: run { sender.sendError("No player name specified."); return }
        val players = if (playerName == "@a") {
            Bukkit.getOnlinePlayers()
        }
        else listOf(onlinePlayer(playerName) ?: run { sender.sendError("No online player '$playerName'"); return })

        // Which bow to give.
        val bowNode = arguments.getOrNull(1) ?: run { sender.sendError("No bow specified."); return }
        val bow = DefaultBow.parseBow(bowNode) ?: run { sender.sendError("Unknown bow '$bowNode'."); return }
        val bowName = plugin.config.getString(bow.nameNode)

        // Whether the bow must have Unbreaking X with Infinity I.
        val isEnchanted = "ench".equals(arguments.getOrNull(2), ignoreCase = true)

        // Distribute.
        BowDistributor(plugin.config, players).giveBow(bow, isEnchanted)
        sender.sendFormattedMessage(Broadcast.GAVE_BOW.format(bowName, players.joinToString(", ") { it.name }))
    }

    override fun assertSender(sender: CommandSender) = true
}