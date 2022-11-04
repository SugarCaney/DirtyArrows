package nl.sugcube.dirtyarrows.bow.ability

import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.bow.BowAbility
import nl.sugcube.dirtyarrows.bow.DefaultBow
import nl.sugcube.dirtyarrows.util.scheduleRemoval
import org.bukkit.entity.Arrow
import org.bukkit.entity.Player
import org.bukkit.event.entity.ProjectileLaunchEvent

/**
 * Opens a workbench.
 *
 * @author SugarCaney
 */
open class CraftingBow(plugin: DirtyArrows) : BowAbility(
        plugin = plugin,
        type = DefaultBow.CRAFTING,
        canShootInProtectedRegions = true,
        removeArrow = true,
        description = "Opens a workbench."
) {

    override fun launch(player: Player, arrow: Arrow, event: ProjectileLaunchEvent) {
        unregisterArrow(arrow)
        arrow.scheduleRemoval(plugin)

        player.openWorkbench(player.location, true)
    }
}