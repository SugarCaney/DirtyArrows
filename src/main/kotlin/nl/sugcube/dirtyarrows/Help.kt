package nl.sugcube.dirtyarrows

import nl.sugcube.dirtyarrows.Broadcast.Colour.PRIMARY
import nl.sugcube.dirtyarrows.Broadcast.Colour.SECONDARY
import nl.sugcube.dirtyarrows.Broadcast.Colour.TERTIARY
import nl.sugcube.dirtyarrows.Broadcast.TAG_HELP
import nl.sugcube.dirtyarrows.bow.BowAbility
import nl.sugcube.dirtyarrows.util.sendFormattedMessage
import org.bukkit.command.CommandSender
import kotlin.math.ceil
import kotlin.math.min

/**
 * @author SugarCaney
 */
class Help(val plugin: DirtyArrows) {

    /**
     * Header to show above each help message.
     *
     * %s = The version number.
     * %d = The help page number.
     * %d = The total amount of pages.
     */
    private val helpHeader: String
        get() = "$TERTIARY>>$SECONDARY-----$TERTIARY>$TERTIARY DirtyArrows %s${SECONDARY}Page %d/%d$TERTIARY <$SECONDARY-----$TERTIARY<<"

    /**
     * How many bows to show per help page.
     */
    private val bowsPerPage: Int
        get() = plugin.config.getInt("help.bows-per-page")

    /**
     * Get the total amount of help pages.
     */
    val pageCount: Int
        get() {
            val bowPages = ceil(plugin.bowManager.registeredTypes.size / bowsPerPage.toDouble())
            return (bowPages + 1).toInt()
        }

    /**
     * Sends the help page header line.
     */
    private fun CommandSender.sendHeader(pageNumber: Int) {
        val version = (version() + " ")
        sendMessage(helpHeader.format(version, pageNumber, pageCount))
    }

    /**
     * Get the version number of the plugin to display.
     * Does only display for admins.
     */
    private fun CommandSender.version() = when {
        hasPermission("dirtyarrows.admin") -> "v${plugin.version}"
        else -> ""
    }

    /**
     * Sends the help overview for all DirtyArrows commands.
     *
     * @param sender
     *          To whom to show the help.
     */
    fun showCommandHelp(sender: CommandSender) {
        sender.sendHeader(1)
        val commands = plugin.commandManager.commands.filter { it.hasPermission(sender) }

        // Enable/disable command is not part of the regular list of commands.
        if (sender.hasPermission("dirtyarrows")) {
            sender.sendFormattedMessage("$TAG_HELP $SECONDARY/da $TERTIARY-$PRIMARY Enables/disables Dirty Arrows.")
        }

        // List all available commands.
        commands.forEach {
            sender.sendFormattedMessage("$TAG_HELP $SECONDARY${it.usage} $TERTIARY- $PRIMARY${it.description}")
        }

        sender.sendFormattedMessage("$TAG_HELP$PRIMARY Use $SECONDARY/da help [2-$pageCount]$PRIMARY to see an overview of all bows.")
    }

    /**
     * Sends the help overview for all given bows.
     *
     * @param sender
     *      To whom to show the help.
     * @param bows
     *      The bow abilities to list on this page.
     * @param pageNumber
     *      The page number to display.
     */
    fun showBowHelp(sender: CommandSender, bows: Iterable<BowAbility>, pageNumber: Int) {
        sender.sendHeader(pageNumber)

        bows.forEach { bow ->
            val name = bow.bowName()
            val id = bow.type.node
            val costs = bow.costRequirements.joinToString(", ") {
                "${it.amount}x ${it.type.name.toLowerCase()}"
            }
            val description = bow.description

            val space = if (costs.isEmpty()) "" else " "
            sender.sendFormattedMessage("$TAG_HELP $SECONDARY$name $PRIMARY($id) $TERTIARY$costs$space- $PRIMARY$description")
        }

        sender.sendFormattedMessage("$TAG_HELP$PRIMARY Use $SECONDARY/da help$PRIMARY to see an overview of all commands.")
    }

    /**
     * Sends help page #`index` (index >= 1) to the sender.
     *
     * @param sender
     *          To whom to show the help.
     * @param pageNumber
     *          The help page index >= 1, where page 1 is the command page.
     */
    fun showHelpPage(sender: CommandSender, pageNumber: Int) {
        val allBows = plugin.bowManager.registeredTypes.sortedBy { it.node }
        val index = pageNumber - 2

        // Bows have pages 2-pageCount.
        if (pageNumber in 2..pageCount) {
            val from = bowsPerPage * index
            val to = min(allBows.size, from + bowsPerPage)
            val bowsOnPage = allBows.subList(from, to).mapNotNull { plugin.bowManager[it] }
            showBowHelp(sender, bowsOnPage, pageNumber)
        }
        else showCommandHelp(sender)
    }
}