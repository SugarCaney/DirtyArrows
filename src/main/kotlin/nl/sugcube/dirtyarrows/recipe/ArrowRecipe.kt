package nl.sugcube.dirtyarrows.recipe

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapedRecipe

/**
 * @author SugarCaney
 */
object ArrowRecipe {

    /**
     * Crafting recipe for arrows in the left column.
     */
    fun left(amount: Int) = recipe(amount, "*  ", "#  ", "%  ")

    /**
     * Crafting recipe for arrows in the middle column.
     */
    fun middle(amount: Int) = recipe(amount, " * ", " # ", " % ")

    /**
     * Crafting recipe for arrows in the right column.
     */
    fun right(amount: Int) = recipe(amount, "  *", "  #", "  %")

    /**
     * Flint: *, Stick: #, Feather: %.
     */
    private fun recipe(amount: Int, flint: String, stick: String, feather: String): ShapedRecipe {
        // TODO: Will remove in 1.13/the flattening.
        @Suppress("DEPRECATION")
        return ShapedRecipe(ItemStack(Material.ARROW, amount))
            .shape(flint, stick, feather)
            .setIngredient('*', Material.FLINT)
            .setIngredient('#', Material.STICK)
            .setIngredient('%', Material.FEATHER)
    }
}