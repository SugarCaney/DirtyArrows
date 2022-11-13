package nl.sugcube.dirtyarrows.command

import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.bow.BowDistributor
import nl.sugcube.dirtyarrows.bow.DefaultBow
import nl.sugcube.dirtyarrows.util.onlinePlayer
import nl.sugcube.dirtyarrows.util.sendFormattedMessage
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender

/**
 * @author SugarCaney
 */
open class CommandGive : SubCommand(
        name = "give",
        usage = "/da give <player> <bow> ['ench']",
        argumentCount = 2,
        description = "Give DirtyArrows bows to players."
) {

    init {
        addPermissions("dirtyarrows.admin")

        addAutoCompleteMeta(0, AutoCompleteArgument.PLAYERS)
        addAutoCompleteMeta(1, AutoCompleteArgument.BOWS)
        addAutoCompleteMeta(2, AutoCompleteArgument.ENCHANTED)
    }

    override fun executeImpl(plugin: DirtyArrows, sender: CommandSender, vararg arguments: String) {
        // All players to give the resulting bow.
        val playerName = arguments.firstOrNull() ?: run { sender.sendMessage(plugin.broadcast.noPlayerSpecified()); return }
        val players = if (playerName == "@a") {
            Bukkit.getOnlinePlayers()
        } else listOf(onlinePlayer(playerName) ?: run { sender.sendMessage(plugin.broadcast.noOnlinePlayer(playerName)); return })

        // Which bow to give.
        val bowNode = arguments.getOrNull(1) ?: run { sender.sendMessage(plugin.broadcast.noBowSpecified()); return }
        val bow = DefaultBow.parseBow(bowNode) ?: run { sender.sendMessage(plugin.broadcast.unknownBow(bowNode)); return }
        val bowName = plugin.config.getString(bow.nameNode)

        // Whether the bow must have Unbreaking X with Infinity I.
        val isEnchanted = "ench".equals(arguments.getOrNull(2), ignoreCase = true)

        // Distribute.
        BowDistributor(plugin.config, players).giveBow(bow, isEnchanted)
        sender.sendFormattedMessage(plugin.broadcast.gaveBow(bowName ?: "???", players.joinToString(", ") { it.name }))
    }

    override fun assertSender(sender: CommandSender) = true
}