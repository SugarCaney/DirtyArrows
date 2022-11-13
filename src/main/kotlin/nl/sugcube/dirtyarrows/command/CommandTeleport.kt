package nl.sugcube.dirtyarrows.command

import nl.sugcube.dirtyarrows.DirtyArrows
import org.bukkit.Location
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

/**
 * @author SugarCaney
 */
open class CommandTeleport : SubCommand(
        name = "tp",
        usage = "/da tp <region>",
        argumentCount = 1,
        description = "Teleports you to the center of the region."
) {

    init {
        addPermissions("dirtyarrows.admin")

        addAutoCompleteMeta(0, AutoCompleteArgument.REGIONS)
    }

    override fun executeImpl(plugin: DirtyArrows, sender: CommandSender, vararg arguments: String) {
        val regionName = arguments.first()

        val region = plugin.regionManager.regionByName(regionName)
        if (region == null) {
            sender.sendMessage(plugin.broadcast.noRegion(regionName))
            return
        }

        // Calculate middle of the region.
        val (location1, location2) = region
        val middleX = (location1.x + location2.x) / 2.0
        val middleZ = (location1.z + location2.z) / 2.0

        val highestBlock = location1.world?.getHighestBlockYAt(middleX.toInt(), middleZ.toInt()) ?: return
        val middleY = highestBlock + 1.0

        // Teleport the player to the region.
        val player = sender as Player
        val regionLocation = Location(location1.world, middleX, middleY, middleZ, player.location.yaw, player.location.pitch)
        player.teleport(regionLocation)

        sender.sendMessage(plugin.broadcast.regionTeleported(region.name))
    }

    override fun assertSender(sender: CommandSender) = sender is Player
}