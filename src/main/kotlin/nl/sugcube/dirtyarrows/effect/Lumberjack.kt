@file:JvmName("Lumberjack")

package nl.sugcube.dirtyarrows.effect

import nl.sugcube.dirtyarrows.util.Worlds
import nl.sugcube.dirtyarrows.util.copyOf
import nl.sugcube.dirtyarrows.util.dropItem
import nl.sugcube.dirtyarrows.util.isLog
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import kotlin.random.Random

/**
 * Drops the woodblock as a result of the Woodman Bow.
 */
@Suppress("DEPRECATION")
fun Location.dropWood() {
    val woodMaterial = block.type
    // Data deprecation will be replaced in later versions (1.13, 1.14, ...)
    val damageValue = block.data.toShort()

    // 80% chance to drop log. 20% chance to drop stick/planks.
    if (Random.nextInt(5) != 0) {
        dropItem(ItemStack(woodMaterial, 1, damageValue.toShort()))
        return
    }

    // 40% of dropping planks, 60% of dropping sticks (8% planks, 12% sticks).
    if (Random.nextDouble() < 0.4) {
        val dataValue = if (woodMaterial == Material.LOG_2) damageValue + 4 else damageValue
        dropItem(ItemStack(Material.WOOD, Random.nextInt(4) + 1, dataValue.toShort()))
    }
    else dropItem(ItemStack(Material.STICK, Random.nextInt(6) + 1))
}

/**
 * Cuts down a tree with a block at this location and drops wood.
 *
 * @return `true` if the tree was cut, `false` otherwise.
 */
fun Location.cutDownTree(): Boolean {
    if (block.isLog().not()) return false
    var dropped = false

    fun scanAndDrop(range: IntProgression) = range.forEach { i ->
        val block = copyOf(y = i.toDouble()).block
        if (block.isLog()) {
            block.location.dropWood()
            block.type = Material.AIR
            dropped = true
        }
    }

    scanAndDrop(blockY..Worlds.MAX_WORLD_Y)
    scanAndDrop((blockY - 1) downTo 1)

    return dropped
}