package nl.sugcube.dirtyarrows.command

import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.util.sendFormattedMessage
import org.bukkit.command.CommandSender

/**
 * A subcommand is a command after the base (\da in DirtyArrow's case).
 *
 * For example "give (player) (id) (ench)" is a Sub command.
 *
 * @author SugarCaney
 */
abstract class SubCommand(

    /**
     * The name of the command as it appears after the base command.
     */
    val name: String,

    /**
     * The usage string for the command (without prefix).
     */
    val usage: String,

    /**
     * The minimum amount of arguments that are expected for the command.
     * This is the amount of required arguments.
     * Optional arguments do not count.
     */
    val argumentCount: Int,

    /**
     * Human-readable information string for the command.
     */
    val description: String = ""
) {

    /**
     * Permissions required to issue the command.
     *
     * Only one of the permissions in this list is required in order to be allowed to execute the
     * command. When the set is empty, it means that everyone is allowed to execute the command.
     */
    private val permissions: MutableSet<String> = HashSet()

    /**
     * Contains what type of content must be auto completed at what index of the command.
     *
     * Maps the index of the argument (NOT including the command name itself) to the type of
     * stuff that needs to be autocompleted. This map only contains arguments that must be auto
     * completed.
     */
    private val autoCompleteMeta: MutableMap<Int, AutoCompleteArgument> = HashMap()

    /**
     * Executes the given command.
     *
     * @param plugin
     *          The main plugin instance.
     * @param sender
     *          Who executed the command.
     * @param arguments
     *      The arguments that appear after `\baseCommand [name]`.
     */
    fun execute(plugin: DirtyArrows, sender: CommandSender, vararg arguments: String) {
        // Check permissions.
        if (!hasPermission(sender)) {
            sender.sendMessage(plugin.broadcast.noCommandPermission())
            return
        }

        // Check sender.
        if (!assertSender(sender)) {
            sender.sendMessage(plugin.broadcast.onlyInGame())
            return
        }

        // Check argument count.
        if (arguments.size < argumentCount) {
            sender.sendFormattedMessage(plugin.broadcast.usage(usage))
            return
        }

        // Execute command
        executeImpl(plugin, sender, *arguments)
    }

    /**
     * Get what type must be autocompleted at the given index.
     *
     * @param index
     *          The argument index.
     */
    fun getAutoComplete(index: Int): AutoCompleteArgument? {
        return autoCompleteMeta[index]
    }

    /**
     * Implementation of the command execution.
     *
     * @param plugin
     *          The main plugin instance.
     * @param sender
     *          Who executed the command.
     * @param arguments
     *          The arguments that appear after `/baseCommand name`.
     */
    protected abstract fun executeImpl(plugin: DirtyArrows, sender: CommandSender, vararg arguments: String)

    /**
     * Checks whether the given CommandSender may execute the command or not.
     *
     * @return Whether the command sender may execute the command or not.
     */
    protected abstract fun assertSender(sender: CommandSender): Boolean

    /**
     * Adds the given permissions to the command.
     */
    protected fun addPermissions(vararg permissions: String) {
        this.permissions.addAll(permissions)
    }

    /**
     * Checks whether the given sender has enough permissions to execute the command.
     *
     * @return Whether the sender has enough permissions to execute the command.
     */
    fun hasPermission(sender: CommandSender): Boolean {
        if (permissions.isEmpty()) return true
        return permissions.any {
            sender.hasPermission(it)
        }
    }

    /**
     * @see [autoCompleteMeta]
     */
    protected fun addAutoCompleteMeta(index: Int, argumentType: AutoCompleteArgument) {
        autoCompleteMeta[index] = argumentType
    }
}