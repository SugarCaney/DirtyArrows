package nl.sugcube.dirtyarrows.bow.ability

import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.bow.BowAbility
import nl.sugcube.dirtyarrows.bow.DefaultBow
import nl.sugcube.dirtyarrows.util.*
import org.bukkit.Effect
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Arrow
import org.bukkit.entity.Player
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.inventory.ItemStack

/**
 * Breaks a whole vein of a certain ore type instantly.
 * Requires a pickaxe in inventory.
 * Will take looting into account.
 * Auto-smelts when the bow has a flame enchantment.
 * Durability is subtracted per block, with unbreaking taken into account.
 *
 * @author SugarCaney
 */
open class MiningBow(plugin: DirtyArrows) : BowAbility(
        plugin = plugin,
        type = DefaultBow.MINING,
        canShootInProtectedRegions = false,
        protectionRange = 5.0,
        removeArrow = true,
        description = "Mines ore veins instantly. Requires pickaxe."
) {

    /**
     * Keeps track of how many ores the arrow has mined.
     */
    private val oreCounts = HashMap<Arrow, Int>()

    /**
     * The maximum amount of blocks in a vein that can be mined.
     */
    val maxVeinSize = config.getInt("$node.max-vein-size")

    init {
        if (maxVeinSize > 100) {
            plugin.logger.warning("$node.max-vein-size is too high ($maxVeinSize) choose a smaller number.")
        }
    }

    override fun land(arrow: Arrow, player: Player, event: ProjectileHitEvent) {
        val startBlock = event.hitBlock ?: return

        try {
            breakOres(arrow, player, startBlock)
            oreCounts.remove(arrow)
        }
        catch (ignored: StackOverflowError) {
            plugin.logger.warning("Tried to mine a very large ore vein. Consider lowering $node.max-vein-size")
        }
    }

    /**
     * Breaks this vein of ores.
     *
     * @param source
     *          The arrow that was shot on the ore vein.
     * @param player
     *          Who shot the arrow.
     * @param startBlock
     *          The ore block that was hit.
     * @param veinMaterial
     *          The material of the vein to destroy.
     */
    private fun breakOres(source: Arrow, player: Player, startBlock: Block, veinMaterial: Material = startBlock.type) {
        if (ELIGIBLE_ORES.containsKey(veinMaterial).not()) return

        val world = startBlock.world

        forXYZ(-1..1, -1..1, -1..1) { dx, dy, dz ->
            val currentBlock = world.getBlockAt(startBlock.x + dx, startBlock.y + dy, startBlock.z + dz)
            if (currentBlock.type == veinMaterial) {
                // Enforce the maximum vein size, helps to prevent a stack overflow error.
                val count = oreCounts.getOrDefault(source, 0)
                if (count < maxVeinSize) {
                    val broken = currentBlock.breakOre(player)
                    oreCounts[source] = count + 1

                    if (broken) {
                        // Recursive call for each broken ore block.
                        breakOres(source, player, currentBlock, veinMaterial)
                    }
                }
            }
        }
    }

    /**
     * Breaks this ore block.
     *
     * @param this
     *          The ore block to break.
     * @param player
     *          The player that breaks the ore block.
     * @return `true` when the block was succesfully broken, `false` otherwise.
     */
    private fun Block.breakOre(player: Player): Boolean {
        if (location.isInProtectedRegion(player, showError = false)) return false

        val bow = player.bowItem() ?: return false
        val toolLevel = player.maxToolLevel()
        val oreMaterial = type

        // Break block.
        type = Material.AIR
        bow.subtractDurability(player)
        world.playEffect(centreLocation, Effect.STEP_SOUND, oreMaterial)

        // Don't drop items when the tool level requirements aren't met.
        if (toolLevel < ELIGIBLE_ORES[oreMaterial]!!) return true

        // Drop the same ore if the player has silk touch.
        if (player.hasSilkTouchOnPickaxe()) {
            world.dropItem(centreLocation, ItemStack(oreMaterial, 1))
            return true
        }

        // On FLAME enchantment, drop the smelted item.
        if (oreMaterial in SMELT_ORES && bow.containsEnchantment(Enchantment.ARROW_FIRE)) {
            oreMaterial.smeltedItem?.let { world.dropItem(centreLocation, it) }
            return true
        }

        // Otherwise, figure out how many items to drop. Can be influenced by the fortune level of the pickaxe.
        val fortune = player.fortuneLevel()
        oreMaterial.fortuneDrops(fortune).forEach { world.dropItem(centreLocation, it) }

        return true
    }

    companion object {

        /**
         * All materials that can be mined mapped to their required tool level.
         */
        private val ELIGIBLE_ORES = mapOf(
                Material.COAL_ORE to ToolLevel.WOOD,
                Material.IRON_ORE to  ToolLevel.STONE,
                Material.LAPIS_ORE to ToolLevel.STONE,
                Material.REDSTONE_ORE to ToolLevel.IRON,
                Material.GOLD_ORE to ToolLevel.IRON,
                Material.DIAMOND_ORE to ToolLevel.IRON,
                Material.EMERALD_ORE to ToolLevel.IRON,
                Material.NETHER_QUARTZ_ORE to ToolLevel.WOOD
        )

        /**
         * All ores that can be smelted.
         */
        private val SMELT_ORES = setOf(
                Material.IRON_ORE,
                Material.GOLD_ORE
        )
    }
}