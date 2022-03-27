package nl.sugcube.dirtyarrows.bow.ability

import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.bow.BowAbility
import nl.sugcube.dirtyarrows.bow.BowType
import nl.sugcube.dirtyarrows.bow.DefaultBow
import nl.sugcube.dirtyarrows.util.isWater
import nl.sugcube.dirtyarrows.util.showGrowthParticle
import org.bukkit.Material
import org.bukkit.TreeType
import org.bukkit.entity.Arrow
import org.bukkit.entity.Player
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.inventory.ItemStack
import kotlin.random.Random

/**
 * Shoots arrows that summons a tree on impact.
 *
 * @author SugarCaney
 */
open class TreeBow(plugin: DirtyArrows, val tree: Tree) : BowAbility(
        plugin = plugin,
        type = tree.bowType,
        canShootInProtectedRegions = false,
        protectionRange = 8.0,
        costRequirements = tree.requiredItems,
        description = "Spawns ${tree.treeName}."
) {

    override fun land(arrow: Arrow, player: Player, event: ProjectileHitEvent) {
        // Try a random tree type.
        val variant = if (arrow.location.block.isWater()) TreeType.SWAMP else tree.randomTreeType()
        if (arrow.world.generateTree(arrow.location, variant)) {
            arrow.location.showGrowthParticle()
            return
        }

        // Try a block lower.
        if (arrow.world.generateTree(arrow.location.clone().add(0.0, -1.0, 0.0), variant)) {
            arrow.location.showGrowthParticle()
            return
        }

        // If it didn't work, fallback on the default.
        if (arrow.world.generateTree(arrow.location, tree.defaultType)) {
            arrow.location.showGrowthParticle()
            return
        }

        // Try a block lower.
        if (arrow.world.generateTree(arrow.location.clone().add(0.0, -1.0, 0.0), tree.defaultType)) {
            arrow.location.showGrowthParticle()
            return
        }

        // If that didn't work, reimburse items.
        player.reimburseBowItems()
    }

    /**
     * @author SugarCaney
     */
    enum class Tree(

            /**
             * Bow type that generates this tree.
             */
            val bowType: BowType,

            /**
             * The 'normal' variant of the tree.
             */
            val defaultType: TreeType,

            /**
             * The log material.
             */
            val material: Material,

            /**
             * The damage value of the log material (for items).
             */
            val damageValue: Short,

            /**
             * The amount of saplings required to spawn the default tree.
             */
            val saplingCount: Int,

            /**
             * Contains a map of each tree type supported by this kind of tree.
             * Maps to the frequency of how many times it should generate.
             */
            val treeTypes: Map<TreeType, Int>,

            /**
             * The name of the tree w/ article.
             */
            val treeName: String
    ) {

        OAK(DefaultBow.OAK, TreeType.TREE, Material.OAK_LOG, 0, 1, mapOf(
                TreeType.TREE to 7,
                TreeType.BIG_TREE to 1,
                TreeType.SWAMP to 1
        ), "an oak"),

        SPRUCE(DefaultBow.SPRUCE, TreeType.REDWOOD, Material.SPRUCE_LOG, 0, 1, mapOf(
                TreeType.REDWOOD to 7,
                TreeType.TALL_REDWOOD to 3,
                TreeType.MEGA_REDWOOD to 1
        ), "a spruce tree"),

        BIRCH(DefaultBow.BIRCH, TreeType.BIRCH, Material.BIRCH_LOG, 0, 1, mapOf(
                TreeType.BIRCH to 3,
                TreeType.TALL_BIRCH to 1
        ), "a birch"),

        JUNGLE(DefaultBow.JUNGLE, TreeType.JUNGLE, Material.JUNGLE_LOG, 0, 1, mapOf(
                TreeType.JUNGLE to 1,
                TreeType.SMALL_JUNGLE to 7,
                TreeType.JUNGLE_BUSH to 3
        ), "a jungle tree"),

        ACACIA(DefaultBow.ACACIA, TreeType.ACACIA, Material.ACACIA_LOG, 0, 1, mapOf(
                TreeType.ACACIA to 1
        ), "an acacia tree"),

        DARK_OAK(DefaultBow.DARK_OAK, TreeType.DARK_OAK, Material.DARK_OAK_LOG, 0, 4, mapOf(
                TreeType.DARK_OAK to 1
        ), "a dark oak");

        /**
         * The items required to use a bow of this tree type.
         */
        val requiredItems = listOf(
//                ItemStack(Material.SAPLING, saplingCount, (damageValue + if (material == Material.LOG_2) 4 else 0).toShort()),
                ItemStack(Material.BONE_MEAL, 1)
            // TODO: Tree Bow required materials
        )

        /**
         * Get a random TreeType for this tree.
         */
        fun randomTreeType(): TreeType {
            var total = treeTypes.values.sum()

            treeTypes.forEach { (type, frequency) ->
                if (Random.nextInt(total) < frequency) return type
                total -= frequency
            }

            error("Problem in algorthm (total: '$total')")
        }
    }
}