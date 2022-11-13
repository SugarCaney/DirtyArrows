package nl.sugcube.dirtyarrows.command

import nl.sugcube.dirtyarrows.DirtyArrows
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

/**
 * @author SugarCaney
 */
open class CommandHelp : SubCommand(
        name = "help",
        usage = "/da help [page]",
        argumentCount = 0,
        description = "Shows this help page."
) {

    init {
        addAutoCompleteMeta(0, AutoCompleteArgument.HELP_PAGES)
    }

    override fun executeImpl(plugin: DirtyArrows, sender: CommandSender, vararg arguments: String) {
        val player = sender as? Player ?: error("Sender is not a player")

        if (arguments.isEmpty()) {
            plugin.help.showCommandHelp(player)
            return
        }

        arguments.first().toIntOrNull()?.let { pageNo ->
            plugin.help.showHelpPage(player, pageNo)
        } ?: plugin.help.showCommandHelp(player)
    }

    override fun assertSender(sender: CommandSender) = sender is Player
}