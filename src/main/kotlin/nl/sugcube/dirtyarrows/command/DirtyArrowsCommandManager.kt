package nl.sugcube.dirtyarrows.command

import nl.sugcube.dirtyarrows.DirtyArrows
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

/**
 * @author SugarCaney
 */
open class DirtyArrowsCommandManager(private val plugin: DirtyArrows) : CommandExecutor, TabCompleter {

    /**
     * All available \da commands.
     */
    val commands: List<SubCommand> = listOf(
            CommandReload(),
            CommandGive(),
            CommandAmmo(),
            CommandList(),
            CommandCheck(),
            CommandTeleport(),
            CommandPos(1),
            CommandPos(2),
            CommandVisualize(),
            CommandRegister(),
            CommandRemove(),
            CommandHelp()
    )

    /**
     * Enables DirtyArrows for the player if DA is disabled, and vice versa.
     */
    private fun toggleDirtyArrows(sender: CommandSender) {
        if (sender !is Player) return
        if (sender.hasPermission("dirtyarrows").not()) {
            sender.sendMessage(plugin.broadcast.noCommandPermission())
        }

        val isActivated = plugin.activationManager.toggleActivation(sender.uniqueId)
        if (plugin.config.getBoolean("show-enable-message")) {
            sender.sendMessage(plugin.broadcast.enabledMessage(isActivated))
        }
    }
    override fun onCommand(
            sender: CommandSender,
            command: Command,
            label: String,
            args: Array<out String>
    ): Boolean {
        // When no arguments are supplied, toggle dirtyarrows on/off.
        if (args.isEmpty()) {
            toggleDirtyArrows(sender)
            return true
        }

        // Otherwise, handle the correct command.
        commands.firstOrNull { it.name.equals(args.first(), ignoreCase = true) }
                ?.execute(plugin, sender, *args.copyOfRange(1, args.size))
                ?.also { return true }

        // No corresponding command found: do nothing.
        return false
    }

    override fun onTabComplete(
            sender: CommandSender,
            command: Command,
            alias: String,
            args: Array<out String>
    ): MutableList<String>? {
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
        val subCommand = commands.firstOrNull() {
            it.name.equals(args.first(), ignoreCase = true)
        }

        // Get autocompletion.
        return subCommand?.getAutoComplete(args.size - 2)
                ?.optionsFromQuery(args.last(), plugin)?.toMutableList()
    }
}