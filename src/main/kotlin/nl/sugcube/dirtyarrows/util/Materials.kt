package nl.sugcube.dirtyarrows.util

import org.bukkit.Material
import org.bukkit.block.Block

/**
 * Checks if this material is a log.
 */
fun Material.isLog(): Boolean = when (this) {
    Material.LOG, Material.LOG_2 -> true
    else -> false
}

/**
 * Checks if this block is a log.
 */
fun Block.isLog() = type.isLog()