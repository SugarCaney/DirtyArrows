package nl.sugcube.dirtyarrows.command

import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.util.Help
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

/**
 * @author SugarCaney
 */
open class CommandHelp : SubCommand<DirtyArrows>(
    name = "help",
    usage = "/da help [page]",
    argumentCount = 0
) {

    init {
        addAutoCompleteMeta(0, AutoCompleteArgument.HELP_PAGES)
    }

    private fun Player.showHelp(page: Int) {
        when (page) {
            1 -> Help.showMainHelpPage1(this)
            2 -> Help.showMainHelpPage2(this)
            3 -> Help.showMainHelpPage3(this)
            4 -> Help.showMainHelpPage4(this)
            5 -> Help.showMainHelpPage5(this)
            6 -> Help.showMainHelpPage6(this)
            7 -> Help.showMainHelpPage7(this)
            else -> Help.showMainHelpPage1(this)
        }
    }

    override fun executeImpl(plugin: DirtyArrows, sender: CommandSender, vararg arguments: String) {
        val player = sender as? Player ?: error("Sender is not a player")

        if (arguments.isEmpty()) {
            player.showHelp(1)
            return
        }

        arguments.first().toIntOrNull()?.let { pageNo ->
            player.showHelp(pageNo)
        } ?: player.showHelp(1)
    }

    override fun assertSender(sender: CommandSender) = sender is Player
}