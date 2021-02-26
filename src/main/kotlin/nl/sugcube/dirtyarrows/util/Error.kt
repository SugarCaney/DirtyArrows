package nl.sugcube.dirtyarrows.util

import nl.sugcube.dirtyarrows.DirtyArrows
import org.bukkit.ChatColor
import org.bukkit.entity.Player

/**
 * @author SugarCaney
 */
class Error(val plugin: DirtyArrows) {

    companion object {

        private const val NO_PERMISSION = "You don't have permission to use this bow."
        private const val RESOURCES_REQUIRED = "You don't have enough resources to use this bow. Required:"
    }
    
    fun noFrozen(player: Player, reason: Reason) {
        player.showBowError(reason, "Snowball (x1)")
    }

    fun noCurse(player: Player, reason: Reason) {
        player.showBowError(reason, "Fermented Spider Eye (x1)")
    }

    fun noIron(player: Player, reason: Reason) {
        player.showBowError(reason, "Anvil (x1)")
    }

    fun noAirship(player: Player, reason: Reason) {
        player.showBowError(reason, "Feather (x2)")
    }

    fun noSulphur(player: Player, reason: Reason) {
        player.showBowError(reason, "TNT (x5)")
    }

    fun noDarkOak(player: Player, reason: Reason) {
        player.showBowError(reason, "Dark Oak Sapling (x4)", "Bone Meal (x1)")
    }

    fun noAcacia(player: Player, reason: Reason) {
        player.showBowError(reason, "Acacia Sapling (x1)", "Bone Meal (x1)")
    }

    fun noParalyze(player: Player, reason: Reason) {
        player.showBowError(reason, "Nether Wart (x1)")
    }

    fun noAquatic(player: Player, reason: Reason) {
        player.showBowError(reason, "Water Bucket (x1)")
    }

    fun noMagmatic(player: Player, reason: Reason) {
        player.showBowError(reason, "Lava Bucket (x1)")
    }

    fun noAirstrike(player: Player, reason: Reason) {
        player.showBowError(reason, "TNT")
    }

    fun noMulti(player: Player, reason: Reason) {
        player.showBowError(reason, "Arrow (x8)")
    }

    fun noBomb(player: Player, reason: Reason) {
        player.showBowError(reason, "TNT (x3)")
    }

    fun noWoodsman(player: Player) {
        player.showBowError(Reason.NO_PERMISSION)
    }

    fun noLevel(player: Player) {
        player.showBowError(Reason.NO_PERMISSION)
    }

    fun noSlow(player: Player) {
        player.showBowError(Reason.NO_PERMISSION)
    }

    fun noDisarm(player: Player) {
        player.showBowError(Reason.NO_PERMISSION)
    }

    fun noPull(player: Player) {
        player.showBowError(Reason.NO_PERMISSION)
    }

    fun noDraining(player: Player) {
        player.showBowError(Reason.NO_PERMISSION)
    }

    fun noSwap(player: Player) {
        player.showBowError(Reason.NO_PERMISSION)
    }

    fun noDisorienting(player: Player) {
        player.showBowError(Reason.NO_PERMISSION)
    }

    fun noMachine(player: Player) {
        player.showBowError(Reason.NO_PERMISSION)
    }

    fun noRanged(player: Player) {
        player.showBowError(Reason.NO_PERMISSION)
    }

    fun noUndead(player: Player, reason: Reason) {
        player.showBowError(reason, "Rotten Flesh (x64)")
    }

    fun noFirey(player: Player, reason: Reason) {
        player.showBowError(reason, "Fire Charge (x1)")
    }

    fun noWither(player: Player, reason: Reason) {
        player.showBowError(reason, "Soul Sand (x3)")
    }

    fun noFlintAnd(player: Player, reason: Reason) {
        player.showBowError(reason, "Flint And Steel")
    }

    fun noPoisonous(player: Player, reason: Reason) {
        player.showBowError(reason, "Spider Eye (x1)")
    }

    fun noEnlightened(player: Player, reason: Reason) {
        player.showBowError(reason, "Torch (x1)")
    }

    fun noNuclear(player: Player, reason: Reason) {
        player.showBowError(reason, "TNT (x64)")
    }

    fun noBatty(player: Player, reason: Reason) {
        player.showBowError(reason, "Rotten Flesh (x3)")
    }

    fun noJungle(player: Player, reason: Reason) {
        player.showBowError(reason, "Jungle Sapling (x1)", "Bone Meal (x1)")
    }

    fun noSpruce(player: Player, reason: Reason) {
        player.showBowError(reason, "Spruce Sapling (x1)", "Bone Meal (x1)")
    }

    fun noBirch(player: Player, reason: Reason) {
        player.showBowError(reason, "Birch Sapling (x1)", "Bone Meal (x1)")
    }

    fun noOak(player: Player, reason: Reason) {
        player.showBowError(reason, "Oak Sapling (x1)", "Bone Meal (x1)")
    }

    fun noEnder(player: Player, reason: Reason) {
        player.showBowError(reason, "Ender Pearl (x1)")
    }

    fun noClucky(player: Player, reason: Reason) {
        player.showBowError(reason, "Egg (x1)")
    }

    fun noLightning(player: Player, reason: Reason) {
        player.showBowError(reason, "Glowstone Dust (x1)")
    }

    fun noExploding(player: Player, reason: Reason) {
        player.showBowError(reason, "TNT (x1)")
    }

    /**
     * Shows an error for usage of this bow.
     *
     * Does not show when this is a minigame version.
     */
    private fun Player.showBowError(reason: Reason, vararg resourceMessageLines: String) {
        if (plugin.isMinigameVersion()) return

        when (reason) {
            Reason.NO_PERMISSION -> error(NO_PERMISSION)
            Reason.NOT_ENOUGH_ITEMS -> {
                error(RESOURCES_REQUIRED)
                resourceMessageLines.forEach { error(it) }
            }
        }
    }

    /**
     * Shows the given error message (inclluding with colour and prefix) to the player.
     */
    private fun Player.error(message: String) {
        sendMessage(ChatColor.RED.toString() + "[!!] $message")
    }

    /**
     * @author SugarCaney
     */
    enum class Reason {

        NO_PERMISSION,
        NOT_ENOUGH_ITEMS
    }
}