package nl.sugcube.dirtyarrows.recipe

import nl.sugcube.dirtyarrows.DirtyArrows
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.PrepareItemCraftEvent

/**
 * Manages all custom DirtyArrows recipes.
 *
 * @author SugarCaney
 */
open class RecipeManager(private val plugin: DirtyArrows) : Listener {

    /**
     * The amount of arrows that must be crafted per recipe instance.
     */
    private var arrowAmount: Int = 4

    init {
        reloadConfig()
    }

    /**
     * Reloads configuration values.
     */
    fun reloadConfig() {
        arrowAmount = plugin.config.getInt("arrow-recipe-amount")
    }

    @EventHandler
    fun craftArrow(event: PrepareItemCraftEvent) {
        val resultItem = event.inventory.contents.firstOrNull() ?: return
        if (resultItem.type == Material.ARROW) {
            resultItem.amount = arrowAmount
        }
    }
}