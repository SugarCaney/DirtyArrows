package nl.sugcube.dirtyarrows.util

import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.material.MaterialData

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