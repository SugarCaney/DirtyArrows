package nl.sugcube.dirtyarrows.util

import org.bukkit.Bukkit
import org.bukkit.DyeColor
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.inventory.CookingRecipe
import org.bukkit.inventory.ItemStack
import kotlin.math.min
import kotlin.random.Random

/**
 * Maps each material to the item stack obtained when smelting this item.
 */
private val SMELT_RESULTS: Map<Material, ItemStack> = Bukkit.recipeIterator().asSequence()
    .mapNotNull { it as? CookingRecipe<*> }
    .map { it.input.type to it.result }
    .toMap()

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
        Material.AZALEA_LEAVES,
        Material.FLOWERING_AZALEA_LEAVES,
        Material.AZALEA,
        Material.FLOWERING_AZALEA,
        Material.BIG_DRIPLEAF,
        Material.SMALL_DRIPLEAF,
        Material.CAVE_VINES,
        Material.CAVE_VINES_PLANT,
        Material.GLOW_LICHEN,
        Material.HANGING_ROOTS,
        Material.SPORE_BLOSSOM,
        Material.VINE -> true
        else -> false
    }

/**
 * Checks if this material is a log.
 */
fun Material.isLog(): Boolean = when (this) {
    Material.OAK_LOG,
    Material.SPRUCE_LOG,
    Material.BIRCH_LOG,
    Material.JUNGLE_LOG,
    Material.ACACIA_LOG,
    Material.DARK_OAK_LOG,
    Material.CRIMSON_STEM,
    Material.WARPED_STEM -> true
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
    Material.CRIMSON_STEM -> Material.CRIMSON_PLANKS
    Material.WARPED_STEM -> Material.WARPED_PLANKS
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
    val amount = fortuneDropCount(level)
    val dropMaterial = when (this) {
        Material.LAPIS_ORE, Material.DEEPSLATE_LAPIS_ORE -> Material.LAPIS_LAZULI
        Material.IRON_ORE, Material.DEEPSLATE_IRON_ORE -> Material.RAW_IRON
        Material.COPPER_ORE, Material.DEEPSLATE_COPPER_ORE -> Material.RAW_COPPER
        Material.GOLD_ORE, Material.DEEPSLATE_GOLD_ORE -> Material.RAW_GOLD
        Material.NETHER_GOLD_ORE -> Material.GOLD_NUGGET
        Material.AMETHYST_CLUSTER -> Material.AMETHYST_SHARD
        Material.MELON -> Material.MELON_SLICE
        else -> smeltedItem?.type ?: this
    }

    return listOf(ItemStack(dropMaterial, amount))
}

/**
 * Calculates how many items must be dropped, considering the fortune level.
 */
fun Material.fortuneDropCount(level: Int = 0) = when (this) {
    Material.COAL_ORE, Material.DEEPSLATE_COAL_ORE,
    Material.DIAMOND_ORE, Material.DEEPSLATE_DIAMOND_ORE,
    Material.EMERALD_ORE, Material.DEEPSLATE_EMERALD_ORE,
    Material.IRON_ORE, Material.DEEPSLATE_IRON_ORE,
    Material.GOLD_ORE, Material.DEEPSLATE_GOLD_ORE,
    Material.NETHER_QUARTZ_ORE -> oreFortuneCount(fortuneLevel = level, dropAmount = 1..1)
    Material.COPPER_ORE, Material.DEEPSLATE_COPPER_ORE -> oreFortuneCount(fortuneLevel = level, dropAmount = 2..5)
    Material.NETHER_GOLD_ORE -> oreFortuneCount(fortuneLevel = level, dropAmount = 2..6)
    Material.LAPIS_ORE, Material.DEEPSLATE_LAPIS_ORE -> oreFortuneCount(fortuneLevel = level, dropAmount = 4..9)
    Material.REDSTONE_ORE, Material.DEEPSLATE_REDSTONE_ORE -> redstoneFortuneCount(fortuneLevel = level)
    Material.AMETHYST_CLUSTER -> oreFortuneCount(fortuneLevel = level, dropAmount = 4..4)
    Material.MELON -> melonFortuneCount(fortuneLevel = level)
    else -> 1
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