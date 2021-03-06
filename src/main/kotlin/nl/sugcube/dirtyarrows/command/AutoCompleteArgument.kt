package nl.sugcube.dirtyarrows.command

import nl.sugcube.dirtyarrows.DirtyArrows
import org.bukkit.Bukkit

/**
 * @author SugarCaney
 */
enum class AutoCompleteArgument(

        /**
         * Generates the list of options
         */
        val optionProvider: (plugin: DirtyArrows) -> List<String>
) {

    /**
     * All names of online players.
     */
    PLAYERS({ Bukkit.getOnlinePlayers().map { it.name } }),

    /**
     * All technical IDS of the bows.
     */
    BOWS({ plugin -> plugin.bowManager.registeredTypes.map { it.node }.sorted() }),

    /**
     * Keyword "ench".
     */
    ENCHANTED({ listOf("ench") }),

    /**
     * Lists all region names.
     */
    REGIONS({ it.regionManager.allNames.sorted() }),

    /**
     * All page numbers for Help.
     */
    HELP_PAGES({ plugin -> List(plugin.help.pageCount) { "${it + 1}" } }),

    /**
     * No autocompletion items.
     */
    NOTHING({ emptyList() });

    /**
     * Get all autocomplete options that are available for the given query.
     */
    fun optionsFromQuery(query: String, plugin: DirtyArrows) = optionProvider(plugin).asSequence()
            .filter { it.startsWith(query, ignoreCase = true) }
            .sortedBy { it.toLowerCase() }
            .toList()
}