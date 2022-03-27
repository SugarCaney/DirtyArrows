package nl.sugcube.dirtyarrows.bow

import nl.sugcube.dirtyarrows.util.applyColours
import org.bukkit.Material
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

/**
 * @author SugarCaney
 */
open class BowDistributor(

    /**
     * The DirtyArrows configuration.
     */
    val config: FileConfiguration,

    /**
     * The players to give the bow to.
     */
    val players: Iterable<Player>
) {

    /**
     * Gives the bow to all players.
     *
     * @param bowType
     *          The bow type to give.
     * @param enchanted
     *          `true` if the bow must be enchanted, `false` otherwise.
     */
    fun giveBow(bowType: DefaultBow, enchanted: Boolean) {
        val itemStack = ItemStack(Material.BOW, 1)
        val itemMeta = itemStack.itemMeta ?: return
        val bowName = config.getString(bowType.nameNode)?.applyColours() ?: error("No bow name configured with node ${bowType.nameNode}")
        itemMeta.setDisplayName(bowName)
        itemStack.itemMeta = itemMeta

        if (enchanted) {
            itemStack.addUnsafeEnchantment(Enchantment.DURABILITY, 10)
            itemStack.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 1)
        }

        players.forEach {
            it.inventory.addItem(itemStack)
        }
    }
}