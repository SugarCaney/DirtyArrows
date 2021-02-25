package nl.sugcube.dirtyarrows.command

import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.util.Message
import nl.sugcube.dirtyarrows.util.sendError
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import java.util.*
import kotlin.collections.ArrayList

/**
 * @author SugarCaney
 */
open class DirtyArrowsCommandManager(private val plugin: DirtyArrows) : CommandExecutor, TabCompleter {

    /**
     * All available \da commands.
     */
    private val commands = listOf(
        CommandGive(),
        CommandRegister(),
        CommandRemove(),
        CommandReload(),
        CommandPos(1),
        CommandPos(2),
        CommandList(),
        CommandCheck(),
        CommandHelp()
    )

    /**
     * Enables DirtyArrows for the player if DA is disabled, and vice versa.
     */
    private fun toggleDirtyArrows(sender: CommandSender) {
        if (sender !is Player) return
        if (sender.hasPermission("dirtyarrows").not()) {
            sender.sendError("You don't have permission to perform this command!")
        }

        if (sender.uniqueId in plugin.activated) {
            plugin.activated.remove(sender.uniqueId)
            sender.sendMessage(Message.getEnabled(false))
        }
        else {
            plugin.activated.add(sender.uniqueId)
            sender.sendMessage(Message.getEnabled(true))
        }
    }

    override fun onCommand(
        sender: CommandSender?,
        command: Command?,
        label: String?,
        args: Array<out String>?
    ): Boolean {
        if (sender == null || command == null || label == null || args == null) return false

        // When no arguments are supplied, toggle dirtyarrows on/off.
        if (args.isEmpty()) {
            toggleDirtyArrows(sender)
            return true
        }

        // Otherwise, handle the correct command.
        commands.firstOrNull { it.name.equals(args.first(), ignoreCase = true) }
            ?.execute(plugin, sender, *Arrays.copyOfRange(args, 1, args.size))
            ?.also { return true }

        // No corresponding command found: do nothing.
        return false
    }

    override fun onTabComplete(
        sender: CommandSender?,
        command: Command?,
        alias: String?,
        args: Array<out String>?
    ): MutableList<String>? {
        if (sender == null || command == null || args == null) return ArrayList()

        // Main command suggestions.
        if (args.size <= 1) {
            return commands.asSequence()
                .filter { it.hasPermission(sender) }
                .map { it.name }
                .filter {
                    args.firstOrNull()?.let { firstArgument ->
                        it.startsWith(firstArgument, ignoreCase = true)
                    } ?: false
                }
                .toMutableList()
        }

        // Find the corresponding command.
        val subCommand = commands.first {
            it.name.equals(args.first(), ignoreCase = true)
        }

        // Get autocompletion.
        return subCommand.getAutoComplete(args.size - 2)
            ?.optionsFromQuery(args.last(), plugin)?.toMutableList()
    }
}