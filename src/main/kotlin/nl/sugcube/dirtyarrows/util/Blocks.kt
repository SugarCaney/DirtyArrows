package nl.sugcube.dirtyarrows.util

import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.Arrow
import org.bukkit.material.MaterialData
import org.bukkit.util.BlockIterator

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