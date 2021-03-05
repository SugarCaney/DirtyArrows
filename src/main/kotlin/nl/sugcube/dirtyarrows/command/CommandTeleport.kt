package nl.sugcube.dirtyarrows.command

import nl.sugcube.dirtyarrows.Broadcast
import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.util.sendError
import org.bukkit.Location
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

/**
 * @author SugarCaney
 */
open class CommandTeleport : SubCommand<DirtyArrows>(
    name = "tp",
    usage = "/da tp <region>",
    argumentCount = 1
) {

    init {
        addPermissions("dirtyarrows.admin")

        addAutoCompleteMeta(0, AutoCompleteArgument.REGIONS)
    }

    override fun executeImpl(plugin: DirtyArrows, sender: CommandSender, vararg arguments: String) {
        val regionName = arguments.first()

        val region = plugin.regionManager.regionByName(regionName)
        if (region == null) {
            sender.sendError("There is no region with name '$regionName'.")
            return
        }

        // Calculate middle of the region.
        val (location1, location2) = region
        val middleX = (location1.x + location2.x) / 2.0
        val middleZ = (location1.z + location2.z) / 2.0
        val middleY = location1.world.getHighestBlockYAt(middleX.toInt(), middleZ.toInt()) + 1.0

        // Teleport the player to the region.
        val player = sender as Player
        val regionLocation = Location(location1.world, middleX, middleY, middleZ, player.location.yaw, player.location.pitch)
        player.teleport(regionLocation)

        sender.sendMessage(Broadcast.REGION_TELEPORTED.format(region.name))
    }

    override fun assertSender(sender: CommandSender) = sender is Player
}