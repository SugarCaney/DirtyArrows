@file:JvmName("Lumberjack")

package nl.sugcube.dirtyarrows.effect

import nl.sugcube.dirtyarrows.util.*
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import kotlin.random.Random

/**
 * Drops the woodblock as a result of the Woodman Bow.
 * Chances are in range [0,1]. When both rolls don't proc, a regular log will be dropped.
 */
fun Location.dropWood(plankChance: Double = 0.12, stickChance: Double = 0.08) {
    val woodMaterial = block.type

    // Drop sticks
    if (Random.nextDouble() < stickChance) {
        dropItem(ItemStack(Material.STICK, Random.nextInt(6) + 1))
        return
    }

    // Drop planks
    val modifiedPlankChance = plankChance / (1 - stickChance)
    if (Random.nextDouble() < modifiedPlankChance) {
        dropItem(ItemStack(woodMaterial.plankOfLog(), Random.nextInt(4) + 1))
        return
    }

    dropItem(ItemStack(woodMaterial, 1))
}

/**
 * Cuts down a tree with a block at this location and drops wood.
 * Chances are in range [0,1]. When both rolls don't proc, a regular log will be dropped.
 *
 * @return `true` if the tree was cut, `false` otherwise.
 */
fun Location.cutDownTree(plankChance: Double = 0.12, stickChance: Double = 0.08): Boolean {
    if (block.isLog().not()) return false
    var dropped = false

    fun scanAndDrop(range: IntProgression) = range.forEach { i ->
        val block = copyOf(y = i.toDouble()).block
        if (block.isLog()) {
            block.location.dropWood(plankChance, stickChance)
            block.type = Material.AIR
            dropped = true
        }
        else return
    }

    scanAndDrop(blockY..Worlds.MAX_WORLD_Y)
    scanAndDrop((blockY - 1) downTo 1)

    return dropped
}