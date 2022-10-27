package nl.sugcube.dirtyarrows.util

import org.bukkit.Bukkit
import org.bukkit.DyeColor
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.inventory.CookingRecipe
import org.bukkit.inventory.ItemStack
import kotlin.math.min
import kotlin.random.Random
import kotlin.random.nextInt

/**
 * Maps each material to the item stack obtained when smelting this item.
 */
private val SMELT_RESULTS: Map<Material, ItemStack> = HashMap(Bukkit.recipeIterator().asSequence()
    .mapNotNull { it as? CookingRecipe<*> }
    .map { it.input.type to it.result }
    .toMap()
).apply {
    // Apparently nether gold ore was not in the default list of cooking recipes, so adding it manually.
    this[Material.NETHER_GOLD_ORE] = ItemStack(Material.GOLD_INGOT, 1)
}

/**
 * Maps each DyeColor to a list of (indices in DYE_INDEX_*):
 * - Coloured wool material [DYE_INDEX_WOOL]
 */
private val DYE_COLOURS: Map<DyeColor, List<Material>> = hashMapOf(
    DyeColor.WHITE to listOf(Material.WHITE_WOOL),
    DyeColor.ORANGE to listOf(Material.ORANGE_WOOL),
    DyeColor.MAGENTA to listOf(Material.MAGENTA_WOOL),
    DyeColor.LIGHT_BLUE to listOf(Material.LIGHT_BLUE_WOOL),
    DyeColor.YELLOW to listOf(Material.YELLOW_WOOL),
    DyeColor.LIME to listOf(Material.LIME_WOOL),
    DyeColor.PINK to listOf(Material.PINK_WOOL),
    DyeColor.GRAY to listOf(Material.GRAY_WOOL),
    DyeColor.LIGHT_GRAY to listOf(Material.LIGHT_GRAY_WOOL),
    DyeColor.CYAN to listOf(Material.CYAN_WOOL),
    DyeColor.PURPLE to listOf(Material.PURPLE_WOOL),
    DyeColor.BLUE to listOf(Material.BLUE_WOOL),
    DyeColor.BROWN to listOf(Material.BROWN_WOOL),
    DyeColor.GREEN to listOf(Material.GREEN_WOOL),
    DyeColor.RED to listOf(Material.RED_WOOL),
    DyeColor.BLACK to listOf(Material.BLACK_WOOL),
)

/**
 * Index in [DYE_COLOURS] for the Wool block material.
 */
private const val DYE_INDEX_WOOL = 0

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
        Material.COBWEB,
        Material.DEAD_BUSH,
        Material.GRASS,
        Material.TALL_GRASS,
        Material.LILAC,
        Material.ROSE_BUSH,
        Material.SUNFLOWER,
        Material.LARGE_FERN,
        Material.PEONY,
        Material.OAK_LEAVES,
        Material.SPRUCE_LEAVES,
        Material.BIRCH_LEAVES,
        Material.JUNGLE_LEAVES,
        Material.ACACIA_LEAVES,
        Material.DARK_OAK_LEAVES,
        Material.NETHER_WART_BLOCK,
        Material.WARPED_WART_BLOCK,
        Material.CRIMSON_FUNGUS,
        Material.WARPED_FUNGUS,
        Material.CRIMSON_ROOTS,
        Material.WARPED_ROOTS,
        Material.NETHER_SPROUTS,
        Material.TWISTING_VINES,
        Material.WEEPING_VINES,
        Material.VINE -> true
        else -> false
    }

/**
 * Checks if this material is a log.
 */
fun Material.isLog(): Boolean = when (this) {
    Material.OAK_LOG, Material.SPRUCE_LOG, Material.BIRCH_LOG, Material.JUNGLE_LOG, Material.ACACIA_LOG, Material.DARK_OAK_LOG -> true
    else -> false
}

/**
 * Get the corresponding plank material of this log type
 */
fun Material.plankOfLog(): Material = when (this) {
    Material.OAK_LOG -> Material.OAK_PLANKS
    Material.SPRUCE_LOG -> Material.SPRUCE_PLANKS
    Material.BIRCH_LOG -> Material.BIRCH_PLANKS
    Material.JUNGLE_LOG -> Material.JUNGLE_PLANKS
    Material.ACACIA_LOG -> Material.ACACIA_PLANKS
    Material.DARK_OAK_LOG -> Material.DARK_OAK_PLANKS
    else -> error("Material <$this> is not a supported log material.")
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
        Material.NETHER_QUARTZ_ORE -> oreFortuneCount(fortuneLevel = level, dropAmount = 1..1)
        Material.NETHER_GOLD_ORE -> netherGoldOreFortuneCount(fortuneLevel = level)
        Material.LAPIS_ORE -> oreFortuneCount(fortuneLevel = level, dropAmount = 4..9)
        Material.REDSTONE_ORE -> redstoneFortuneCount(fortuneLevel = level)
        Material.MELON -> melonFortuneCount(fortuneLevel = level)
        else -> 1
    }

    val dropMaterial = when (this) {
        Material.IRON_ORE, Material.GOLD_ORE -> this
        Material.MELON -> Material.MELON_SLICE
        Material.NETHER_GOLD_ORE -> Material.GOLD_NUGGET
        else -> smeltedItem?.type ?: this
    }

    return if (this == Material.LAPIS_ORE) {
        listOf(ItemStack(Material.LAPIS_LAZULI, amount))
    }
    else listOf(ItemStack(dropMaterial, amount))
}

/**
 * Get the wool material corresponding with this dye colour.
 */
fun DyeColor.toWoolMaterial() = DYE_COLOURS[this]!![DYE_INDEX_WOOL]

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

/**
 * Calculates a random amount of melon slices to drop from a melon block.
 *
 * @param fortuneLevel
 *          The level of the fortune enchantment to consider (0 for no fortune).
 */
fun melonFortuneCount(fortuneLevel: Int = 0) = min(9, 3 + Random.nextInt(0, fortuneLevel + 4))

/**
 * Calculates a random amount of drops for nether gold ore, including the effects of fortune.
 *
 * @param fortuneLevel
 *          The level of the fortune enchantment to consider (0 for no fortune).
 */
fun netherGoldOreFortuneCount(fortuneLevel: Int = 0): Int {
    // No fortune: 2-6 drops
    var dropCount = Random.nextInt(2..6)

    when (fortuneLevel) {
        // Fortune 1: 33% chance to double
        1 -> {
            if (Random.nextInt(0, 3) == 0) {
                dropCount *= 2
            }
        }
        // Fortune 2: 25% chance to double, 25% chance to triple
        2 -> {
            when (Random.nextInt(0, 4)) {
                0 -> dropCount *= 2
                1 -> dropCount *= 3
            }
        }
        // Fortune 3: 20% chance to double, 20% chance to triple, 20% chance to quadruple
        3 -> {
            when (Random.nextInt(0, 5)) {
                0 -> dropCount *= 2
                1 -> dropCount *= 3
                2 -> dropCount *= 4
            }
        }
    }

    return dropCount
}