package nl.sugcube.dirtyarrows.recipe

import nl.sugcube.dirtyarrows.DirtyArrows
import java.util.logging.Level

/**
 * Manages all custom DirtyArrows recipes.
 *
 * @author SugarCaney
 */
open class RecipeManager(private val plugin: DirtyArrows) {

    /**
     * The amount of by DA registered recipes.
     */
    private var registeredRecipes: Int = 0

    /**
     * Registers all DirtyArrows recipes and clears all old DirtyArrows recipes.
     */
    fun reloadRecipes() = with(plugin) {
        // Remove all old versions of the recipes.
        removeRecipes()

        // Add the arrow recipes. Add to [addedRecipes] to keep track of which recipes are added by DA.
        val arrowAmount = config.getInt("arrow-recipe-amount")
        val recipesToAdd = recipes(arrowAmount)
        recipesToAdd.forEach { recipe ->
            server.addRecipe(recipe)
            registeredRecipes++
        }

        logger.log(Level.INFO, "Loaded ${recipesToAdd.size} recipes.")
    }

    /**
     * Removes all recipes that have been registered by DirtyArrows.
     */
    fun removeRecipes() = with(plugin) {
        if (registeredRecipes == 0) return

        server.resetRecipes()

        logger.log(Level.INFO, "Removed $registeredRecipes recipes.")
        registeredRecipes = 0
    }

    /**
     * Produces a list containing all DirtyArrows recipes.
     */
    private fun recipes(amount: Int) = listOf(
        ArrowRecipe.left(amount),
        ArrowRecipe.middle(amount),
        ArrowRecipe.right(amount)
    )
}