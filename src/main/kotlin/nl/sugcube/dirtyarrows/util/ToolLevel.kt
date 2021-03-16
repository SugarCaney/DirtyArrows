package nl.sugcube.dirtyarrows.util

import org.bukkit.Material
import org.bukkit.Material.*
import org.bukkit.inventory.ItemStack

/**
 * @author SugarCaney
 */
enum class ToolLevel(
        val spade: Material,
        val axe: Material,
        val pickaxe: Material,
        val sword: Material,
        val hoe: Material
) : Comparable<ToolLevel> {

    BARE_HANDS(AIR, AIR, AIR, AIR, AIR),
    WOOD(WOOD_SPADE, WOOD_AXE, WOOD_PICKAXE, WOOD_SWORD, WOOD_HOE),
    STONE(STONE_SPADE, STONE_AXE, STONE_PICKAXE, STONE_SWORD, STONE_HOE),
    IRON(IRON_SPADE, IRON_AXE, IRON_PICKAXE, IRON_SWORD, IRON_HOE),
    GOLD(GOLD_SPADE, GOLD_AXE, GOLD_PICKAXE, GOLD_SWORD, GOLD_HOE),
    DIAMOND(DIAMOND_SPADE, DIAMOND_AXE, DIAMOND_PICKAXE, DIAMOND_SWORD, DIAMOND_HOE);

    companion object {

        /**
         * Maps materials to tool level.
         */
        val MATERIAL_LOOKUP: Map<Material, ToolLevel> = HashMap<Material, ToolLevel>().apply {
            values().forEach {
                this[it.spade] = it
                this[it.axe] = it
                this[it.pickaxe] = it
                this[it.sword] = it
                this[it.hoe] = it
            }
        }

        /**
         * All pickaxe materials.
         */
        val PICKAXES = values().map { it.pickaxe }.toSet()
    }
}

/**
 * The tool level of this material or `null` when there is no tool level for this tool.
 */
val Material.toolLevel: ToolLevel?
    get() = ToolLevel.MATERIAL_LOOKUP[this]

/**
 * Creates the tool used to break the blocks.
 */
fun ToolLevel?.createMineTool(): ItemStack {
    return ItemStack(this?.pickaxe ?: AIR, 1)
}