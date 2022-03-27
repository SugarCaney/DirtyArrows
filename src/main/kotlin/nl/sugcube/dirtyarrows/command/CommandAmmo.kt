package nl.sugcube.dirtyarrows.command

import nl.sugcube.dirtyarrows.Broadcast
import nl.sugcube.dirtyarrows.Broadcast.Colour.PRIMARY
import nl.sugcube.dirtyarrows.Broadcast.Colour.SECONDARY
import nl.sugcube.dirtyarrows.Broadcast.Colour.TERTIARY
import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.bow.BowType
import nl.sugcube.dirtyarrows.bow.DefaultBow
import nl.sugcube.dirtyarrows.util.applyColours
import nl.sugcube.dirtyarrows.util.onlinePlayer
import nl.sugcube.dirtyarrows.util.sendError
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import kotlin.math.max

/**
 * @author SugarCaney
 */
open class CommandAmmo : SubCommand<DirtyArrows>(
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
        val players = parsePlayers(sender, arguments.firstOrNull()) ?: return

        // Which bow to give ammo for.
        val bow = parseBow(sender, arguments.getOrNull(1)) ?: return
        val bowName = plugin.config.getString(bow.nameNode)?.applyColours()

        // Find the corresponding ability from the bow manager as the cost requirements are needed.
        val ability = plugin.bowManager[bow] ?: run {
            sender.sendError("$bowName is not enabled.")
            return
        }

        // When there are no items required: don't do anyting. All is sorted.
        val items = ability.costRequirements.toTypedArray()
        if (items.isEmpty()) {
            sender.sendMessage(Broadcast.NO_AMMO_REQUIRED.format(bowName))
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
        val ammoString = items.joinToString("$PRIMARY, ") {
            "$TERTIARY${it.amount * ammoCount}x ${it.type.name.toLowerCase()}"
        }
        val playerString = players.joinToString("$PRIMARY, ") {
            "$SECONDARY${it.displayName}"
        }
        sender.sendMessage(Broadcast.GAVE_AMMO.format(ammoString, playerString))
    }

    /**
     * Parses the player argument.
     * Sends an error to the sender when there is something wrong.
     *
     * @param sender
     *          The command sender.
     * @param argument
     *          The argument representing the players.
     * @return `null` when an error occured.
     */
    private fun parsePlayers(sender: CommandSender, argument: String?): Iterable<Player>? {
        val playerName = argument ?: run {
            sender.sendError("No player name specified.")
            return null
        }
        return if (playerName == "@a") {
            Bukkit.getOnlinePlayers()
        }
        else listOfNotNull(onlinePlayer(playerName) ?: run {
            sender.sendError("No online player '$playerName'")
            null
        })
    }

    /**
     * Turns the bow argument into the correct bow.
     * Sends an error to the sender when there is something wrong.
     *
     * @param sender
     *          The command sender.
     * @param argument
     *          The argument specifying the bow.
     * @return `null` when an error occured.
     */
    private fun parseBow(sender: CommandSender, argument: String?): BowType? {
        val bowNode = argument ?: run {
            sender.sendError("No bow specified.")
            return null
        }
        return DefaultBow.parseBow(bowNode) ?: run {
            sender.sendError("Unknown bow '$bowNode'.")
            null
        }
    }

    override fun assertSender(sender: CommandSender) = true
}