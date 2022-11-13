package nl.sugcube.dirtyarrows.command

import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.bow.BowType
import nl.sugcube.dirtyarrows.bow.DefaultBow
import nl.sugcube.dirtyarrows.util.applyColours
import nl.sugcube.dirtyarrows.util.onlinePlayer
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import kotlin.math.max

/**
 * @author SugarCaney
 */
open class CommandAmmo : SubCommand(
        name = "ammo",
        usage = "/da ammo <player> <bow> [amount]",
        argumentCount = 2,
        description = "Gives the required ammo for a certain bow."
) {

    init {
        addPermissions("dirtyarrows.admin")

        addAutoCompleteMeta(0, AutoCompleteArgument.PLAYERS)
        addAutoCompleteMeta(1, AutoCompleteArgument.BOWS)
        addAutoCompleteMeta(2, AutoCompleteArgument.NOTHING)
    }

    override fun executeImpl(plugin: DirtyArrows, sender: CommandSender, vararg arguments: String) {
        // All players to give the resulting bow.
        val players = parsePlayers(plugin, sender, arguments.firstOrNull()) ?: return

        // Which bow to give ammo for.
        val bow = parseBow(plugin, sender, arguments.getOrNull(1)) ?: return
        val bowName = plugin.config.getString(bow.nameNode)?.applyColours()

        // Find the corresponding ability from the bow manager as the cost requirements are needed.
        val ability = plugin.bowManager[bow] ?: run {
            sender.sendMessage(plugin.broadcast.bowNotEnabled(bowName ?: "???"))
            return
        }

        // When there are no items required: don't do anyting. All is sorted.
        val items = ability.costRequirements.toTypedArray()
        if (items.isEmpty()) {
            sender.sendMessage(plugin.broadcast.noAmmoRequired(bowName ?: "???"))
            return
        }

        // Parse ammo count. Default to 1 if weird input is given.
        val ammoCount = max(1, arguments.getOrNull(2)?.toIntOrNull() ?: 1)

        // Give items.
        players.forEach { player ->
            repeat(ammoCount) {
                player.inventory.addItem(*items)
            }
        }

        // Send confirmation message.
        val primary = plugin.broadcast.colourPrimary
        val secondary = plugin.broadcast.colourSecondary
        val tertiary = plugin.broadcast.colourTertiary

        val ammoString = items.joinToString("$primary, ") {
            "$tertiary${it.amount * ammoCount}x ${it.type.name.toLowerCase()}"
        }
        val playerString = players.joinToString("$primary, ") {
            "$secondary${it.displayName}"
        }
        sender.sendMessage(plugin.broadcast.gaveAmmo(ammoString, playerString))
    }

    /**
     * Parses the player argument.
     * Sends an error to the sender when there is something wrong.
     *
     * @param plugin
     *          The DirtyArrows plugin.
     * @param sender
     *          The command sender.
     * @param argument
     *          The argument representing the players.
     * @return `null` when an error occured.
     */
    private fun parsePlayers(plugin: DirtyArrows, sender: CommandSender, argument: String?): Iterable<Player>? {
        val playerName = argument ?: run {
            sender.sendMessage(plugin.broadcast.noPlayerSpecified())
            return null
        }
        return if (playerName == "@a") {
            Bukkit.getOnlinePlayers()
        }
        else listOfNotNull(onlinePlayer(playerName) ?: run {
            sender.sendMessage(plugin.broadcast.noOnlinePlayer(playerName))
            null
        })
    }

    /**
     * Turns the bow argument into the correct bow.
     * Sends an error to the sender when there is something wrong.
     *
     * @param plugin
     *          The DirtyArrows plugin.
     * @param sender
     *          The command sender.
     * @param argument
     *          The argument specifying the bow.
     * @return `null` when an error occured.
     */
    private fun parseBow(plugin: DirtyArrows, sender: CommandSender, argument: String?): BowType? {
        val bowNode = argument ?: run {
            sender.sendMessage(plugin.broadcast.noBowSpecified())
            return null
        }
        return DefaultBow.parseBow(bowNode) ?: run {
            sender.sendMessage(plugin.broadcast.unknownBow(bowNode))
            null
        }
    }

    override fun assertSender(sender: CommandSender) = true
}