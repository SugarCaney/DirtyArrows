package nl.sugcube.dirtyarrows.bow.ability

import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.bow.BowAbility
import nl.sugcube.dirtyarrows.bow.DefaultBow
import nl.sugcube.dirtyarrows.util.*
import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.block.data.BlockData
import org.bukkit.block.data.Directional
import org.bukkit.entity.Arrow
import org.bukkit.entity.Player
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.inventory.ItemStack

/**
 * Shoots arrows that place torches.
 *
 * @author SugarCaney
 */
open class EnlightenedBow(plugin: DirtyArrows) : BowAbility(
        plugin = plugin,
        type = DefaultBow.ENLIGHTENED,
        canShootInProtectedRegions = false,
        protectionRange = 2.0,
        costRequirements = listOf(ItemStack(Material.TORCH, 1)),
        description = "Places torches."
) {

    override fun land(arrow: Arrow, player: Player, event: ProjectileHitEvent) {
        arrow.location.apply {
            if (block.type != Material.AIR) {
                player.reimburseBowItems()
                return
            }

            // When the torch has something to stand on, all is fine. However, ...
            if (block.onSolid()) {
                block.type = Material.TORCH
                showFlameParticle()
                return
            }

            // ... if there isn't, find a wall face (note: torches face opposite of attached face).
            // But first check if there is a face to attach to.
            if (attachedBlockLocations().none { it.block.type.isSolid }) {
                player.reimburseBowItems()
                return
            }

            // Then find a face.
            // Notice that the facing direction is opposite to the solid direction.
            val blockFace = when {
                north().block.type.isSolid -> BlockFace.SOUTH
                south().block.type.isSolid -> BlockFace.NORTH
                east().block.type.isSolid -> BlockFace.WEST
                west().block.type.isSolid -> BlockFace.EAST
                else -> BlockFace.UP
            }

            block.type = Material.WALL_TORCH
            block.setBlockData(block.blockData.useAs<BlockData, Directional> {
                it.facing = blockFace
            }, true)

            showFlameParticle()
        }
    }
}