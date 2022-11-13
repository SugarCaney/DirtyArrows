package nl.sugcube.dirtyarrows.command

import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.region.showPositionMarkers
import nl.sugcube.dirtyarrows.region.showRegionMarkers
import org.bukkit.command.CommandSender

/**
 * @author SugarCaney
 */
open class CommandVisualize : SubCommand(
        name = "visualize",
        usage = "/da visualize [region]",
        argumentCount = 0,
        description = "Shows the set positions, or the corners of the region."
) {

    init {
        addPermissions("dirtyarrows.admin")

        addAutoCompleteMeta(0, AutoCompleteArgument.REGIONS)
    }

    override fun executeImpl(plugin: DirtyArrows, sender: CommandSender, vararg arguments: String) {
        val regionName = arguments.firstOrNull()

        // No region specified : show the current positions.
        if (regionName == null) {
            plugin.showPositionMarkers(5)
            return
        }

        // Region specified
        val region = plugin.regionManager.regionByName(regionName)
        if (region == null) {
            sender.sendMessage(plugin.broadcast.noRegion(regionName))
            return
        }

        plugin.showRegionMarkers(region, 5)
    }

    override fun assertSender(sender: CommandSender) = true
}