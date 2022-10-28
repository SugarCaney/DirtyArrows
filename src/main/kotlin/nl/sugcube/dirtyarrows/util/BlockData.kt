@file:JvmName("DaBlockData")

package nl.sugcube.dirtyarrows.util

import org.bukkit.block.data.BlockData
import org.bukkit.block.data.type.CaveVines

/**
 * Checks whether this is a cave vine with berries.
 */
fun BlockData.hasBerries() = (this as? CaveVines)?.isBerries ?: false