package nl.sugcube.dirtyarrows.bow.ability

import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.bow.BowAbility
import nl.sugcube.dirtyarrows.bow.DefaultBow
import nl.sugcube.dirtyarrows.util.*
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.Arrow
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.material.Leaves
import org.bukkit.material.LongGrass

/**
 * Breaks and drops all shearable blocks.
 *
 * @author SugarCaney
 */
open class ShearBow(plugin: DirtyArrows) : BowAbility(
        plugin = plugin,
        type = DefaultBow.SHEAR,
        handleEveryNTicks = 1,
        canShootInProtectedRegions = false,
        removeArrow = true,
        description = "Breaks all shearable blocks."
) {

    override fun land(arrow: Arrow, player: Player, event: ProjectileHitEvent) {
        // When an entity is hit, don't bounce.
        if (event.hitEntity != null) {
            arrow.remove()
            return
        }

        if (event.hitBlock.type != Material.AIR && event.hitBlock.type.isShearable.not()) {
            return
        }

        event.hitBlock.shearBlocks()
        registerArrow(arrow.respawnArrow())
    }

    override fun effect() {
        arrows.forEach {
            if (it.location.isInProtectedRegion(it.shooter as? LivingEntity, showError = false).not()) {
                it.location.block.shearBlocks()
            }
        }
    }

    /**
     * Shears all blocks around this block.
     */
    @Suppress("DEPRECATION")
    private fun Block.shearBlocks() {
        forXYZ(-1..1, -1..1, -1..1) { dx, dy, dz ->
            val block = getRelative(dx, dy, dz)
            val originalType = block.type

            if (originalType.isShearable) {
                val itemDrop = ItemStack(originalType, 1, block.itemDataValue().toShort())
                block.centreLocation.dropItem(itemDrop)
                block.type = Material.AIR
            }
        }
    }

    /**
     * Get the data value for the item that is dropped by this block.
     */
    @Suppress("DEPRECATION")
    private fun Block.itemDataValue(): Byte = when (type) {
        Material.LEAVES -> (state.data as Leaves).species.data
        Material.LEAVES_2 -> ((state.data as Leaves).species.data - 4).toByte()
        Material.LONG_GRASS -> (state.data as LongGrass).species.data
        Material.DOUBLE_PLANT -> state.data.data
        else -> 0
    }

    companion object {

        /**
         * The shears item used to break blocks.
         */
        private val SHEARS = ItemStack(Material.SHEARS, 1)
    }
}