package nl.sugcube.dirtyarrows.bow.ability

import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.bow.BowAbility
import nl.sugcube.dirtyarrows.bow.DefaultBow
import nl.sugcube.dirtyarrows.util.*
import org.bukkit.Material
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
            // Notice that the facing direction is opposite from the solid direction.
            // I tried SOO MUCH, and finally settled on some weird legacy method.
            // The problem with setting type and data seperately from the API was that the type
            // would not change to torch (presumably because they cannot be placed regularly), after calling
            // block.setType.
            // This disabled me from actually getting the data. This is fine for now. (1.11).
            // Revise this for new API versions.
            val data = when {
                north().block.type.isSolid -> 0x3 /* SOUTH */
                south().block.type.isSolid -> 0x4 /* NORTH */
                east().block.type.isSolid -> 0x2 /* WEST */
                west().block.type.isSolid -> 0x1 /* EAST */
                else -> 0x5 /* UP */
            }
            @Suppress("DEPRECATION")
            block.setTypeIdAndData(Material.TORCH.id, data.toByte(), false)

            showFlameParticle()
        }
    }
}