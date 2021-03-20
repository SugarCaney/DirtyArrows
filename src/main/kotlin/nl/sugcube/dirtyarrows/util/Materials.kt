package nl.sugcube.dirtyarrows.util

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.inventory.FurnaceRecipe
import org.bukkit.inventory.ItemStack
import kotlin.random.Random

/**
 * Maps each material to the item stack obtained when smelting this item.
 */
private val SMELT_RESULTS: Map<Material, ItemStack> = Bukkit.recipeIterator().asSequence()
        .mapNotNull { it as? FurnaceRecipe }
        .map { it.input.type to it.result }
        .toMap()

/**
 * Get the item that is obtained when smelting this material.
 */
val Material.smeltedItem: ItemStack?
    get() = SMELT_RESULTS[this]

/**
 * Whether the block can be broken by shears an drop their original block.
 * `false` in cases like wool, where the wool block can also be properly broken without shears.
 */
val Material.isShearable: Boolean
    get() = when (this) {
        Material.WEB,
        Material.DEAD_BUSH,
        Material.LONG_GRASS,
        Material.DOUBLE_PLANT,
        Material.LEAVES,
        Material.LEAVES_2,
        Material.TRIPWIRE,
        Material.VINE -> true
        else -> false
    }

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

/**
 * Get the items that should drop from this material, considering the fortune level.
 */
fun Material.fortuneDrops(level: Int = 0): Collection<ItemStack> {
    val amount = when (this) {
        Material.COAL_ORE,
        Material.DIAMOND_ORE,
        Material.EMERALD_ORE,
        Material.QUARTZ_ORE -> oreFortuneCount(fortuneLevel = level, dropAmount = 1..1)
        Material.LAPIS_ORE -> oreFortuneCount(fortuneLevel = level, dropAmount = 4..9)
        Material.REDSTONE_ORE -> redstoneFortuneCount(fortuneLevel = level)
        else -> 1
    }

    val dropMaterial = when (this) {
        Material.IRON_ORE, Material.GOLD_ORE -> this
        else -> smeltedItem?.type ?: return emptyList()
    }

    return if (this == Material.LAPIS_ORE) {
        listOf(ItemStack(Material.INK_SACK, amount, 4))
    }
    else listOf(ItemStack(dropMaterial, amount))
}

/**
 * Calculates a random amount of drops for coal, diamond, emerald, nether quartz, lapis lazuli ore inclding
 * the effects of fortune.
 *
 * @param fortuneLevel
 *          The level of the fortune enchantment to consider (0 for no fortune).
 * @param dropAmount
 *          The range containing the possible amount of base drops.
 */
fun oreFortuneCount(fortuneLevel: Int = 0, dropAmount: IntRange = 1..1): Int {
    val base = dropAmount.random()
    return when (fortuneLevel) {
        1 -> if (Random.nextDouble() > 0.33) base else base * 2
        2 -> if (Random.nextBoolean()) base else base * Random.nextInt(2, 4)
        3 -> if (Random.nextDouble() > 0.6) base else base * Random.nextInt(2, 5)
        else -> base
    }
}

/**
 * Calculates a random amount of drops for redstone ore, including the effects of fortune.
 *
 * @param fortuneLevel
 *          The level of the fortune enchantment to consider (0 for no fortune).
 */
fun redstoneFortuneCount(fortuneLevel: Int = 0) = 4 + Random.nextInt(0, fortuneLevel + 2)