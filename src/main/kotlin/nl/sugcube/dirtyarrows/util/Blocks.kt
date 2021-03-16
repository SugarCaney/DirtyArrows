package nl.sugcube.dirtyarrows.util

import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.entity.Arrow
import org.bukkit.material.MaterialData
import org.bukkit.util.BlockIterator

/**
 * The materials that are tools that can mine blocks.
 */
val BLOCK_BREAK_TOOLS = setOf(
        Material.DIAMOND_PICKAXE,
        Material.GOLD_PICKAXE,
        Material.IRON_PICKAXE,
        Material.STONE_PICKAXE,
        Material.WOOD_PICKAXE,
)

/**
 * Get a copy of the location that is at the centre of the block.
 */
val Block.centreLocation: Location
    get() {
        val location = location
        return location.copyOf(
                x = location.x + 0.5,
                y = location.y + 0.5,
                z = location.z + 0.5
        )
    }

/**
 * Checks if the block below this block is solid.
 */
fun Block.onSolid() = location.copyOf(y = location.y - 1.0).block.type.isSolid

/**
 * Checks if this block is a water block.
 */
fun Block.isWater() = type == Material.STATIONARY_WATER || type == Material.WATER

/**
 * Updates the block data of this block.
 *
 * @param updater
 *          The function that updates the state of the data.
 */
inline fun <reified Data : MaterialData> Block.updateData(updater: (Data) -> Unit) {
    val state = this.state
    val theData = state.data as Data
    updater(theData)
    state.data = theData
    state.update(true)
}

/**
 * Finds the block that the arrow hits.
 */
fun Arrow.hitBlock(): Block {
    // Find the block that the arrow hits.
    val iterator = BlockIterator(world, location.toVector(), velocity.normalize(), 0.0, 4)
    var hitBlock = iterator.next()
    while (iterator.hasNext()) {
        hitBlock = iterator.next()
        if (hitBlock.type != Material.AIR) break
    }
    return hitBlock
}

/**
 * Get the face of the block that has been hit by the arrow.
 *
 * @param this
 *          The arrow that hit the block face.
 * @param hitBlock
 *          The block that was hit by the arrow.
 * @return The block face that was hit or `null` when no block face could be found.
 */
fun Arrow.hitBlockFace(hitBlock: Block): BlockFace? {
    val iterator = BlockIterator(world, location.toVector(), velocity, 0.0, 3)
    var blockBefore = hitBlock
    var nextBlock = iterator.next()
    while (iterator.hasNext() && nextBlock.type == Material.AIR) {
        blockBefore = nextBlock
        nextBlock = iterator.next()
    }
    return nextBlock.getFace(blockBefore)
}