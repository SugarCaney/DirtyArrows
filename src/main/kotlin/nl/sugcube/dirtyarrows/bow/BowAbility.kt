package nl.sugcube.dirtyarrows.bow

import nl.sugcube.dirtyarrows.Broadcast
import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.util.applyColours
import nl.sugcube.dirtyarrows.util.itemInMainHand
import nl.sugcube.dirtyarrows.util.itemInOffHand
import nl.sugcube.dirtyarrows.util.itemName
import org.bukkit.GameMode
import org.bukkit.entity.Arrow
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.ProjectileLaunchEvent
import org.bukkit.inventory.ItemStack

/**
 * Base for all bow abilities.
 *
 * * Override [effect] to execute a repeating scheduled effect every [handleEveryNTicks] ticks.
 * * Override [particle] to show a particle on every applicable site.
 * * Override [onPlayerShoot] for behaviour when the player shoots arrows.
 *
 * The implementing class should keep track of relevant entities/other data itself.
 *
 * @author SugarCaney
 */
abstract class BowAbility(

        /**
         * The main DA plugin instance.
         */
        val plugin: DirtyArrows,

        /**
         * Corresponding [DefaultBow] enum type.
         */
        val type: BowType,

        /**
         * Must handle the effects of this ability every `handleEveryNTicks` ticks.
         */
        val handleEveryNTicks: Int = 20,

        /**
         * Whether the bow can be used in protected regions.
         */
        val canShootInProtectedRegions: Boolean = false,

        /**
         * How many blocks around the region the protection should still count.
         * Example: bigger for the nuclear bow than for the clucky bow.
         */
        val protectionRange: Double = 5.0,

        /**
         * What items are required for a single use.
         */
        val costRequirements: List<ItemStack> = emptyList()

) : Listener, Runnable {

    /**
     * Keeps track of the amount of ticks that have passed.
     */
    private var tickCounter = 0

    /**
     * Executes a repeating scheduled effect every [handleEveryNTicks] ticks.
     */
    open fun effect() = Unit

    /**
     * Shows a single particle effect on every applicable instance.
     */
    open fun particle() = Unit

    /**
     * Handles when a player shoots an arrow with this bow.
     *
     * @param player
     *          The player that launched the arrow.
     * @param arrow
     *          The launched arrow.
     * @param event
     *          The corresponding [ProjectileLaunchEvent].
     */
    open fun onPlayerShoot(player: Player, arrow: Arrow, event: ProjectileLaunchEvent) = Unit

    override fun run() {
        tickCounter++

        // Only execute the effect every [handleEveryNTicks] ticks.
        if (tickCounter >= handleEveryNTicks) {
            effect()
            tickCounter = 0
        }

        // Show particles every tick.
        particle()
    }

    @EventHandler
    fun eventHandlerLaunchProjectile(event: ProjectileLaunchEvent) {
        val arrow = event.entity as? Arrow ?: return
        val player = arrow.shooter as? Player ?: return

        // Check if the bow can be used.
        if (player.isDirtyArrowsActivated().not()) return
        if (player.hasBowInHand().not()) return
        if (player.hasPermission().not()) return
        if (player.isInProtectedRegion()) return
        if (player.meetsResourceRequirements().not()) return

        // All is fine, handle event.
        onPlayerShoot(player, arrow, event)
    }

    /**
     * Checks if Dirty Arrows is activated for this player.
     *
     * @return `true` if DA is activated, `false` if not.
     */
    protected fun Player.isDirtyArrowsActivated(): Boolean {
        return plugin.activationManager.isActivatedFor(this)
    }

    /**
     * Checks if the player has the correct bow in their hand.
     *
     * @return `true` if the player has a bow in their hand, `false` otherwise.
     */
    protected fun Player.hasBowInHand(): Boolean {
        val expectedName = bowName()
        return itemInMainHand.itemName != expectedName && itemInOffHand.itemName != expectedName
    }

    /**
     * Checks if the player has the right permissions to use this bow.
     *
     * @param showError
     *          Whether to show an error if the player has no permission.
     * @return `true` when the player has permission to use this bow, `false` otherwise.
     */
    protected fun Player.hasPermission(showError: Boolean = true): Boolean {
        val hasPermission = hasPermission(this@BowAbility.type.permission)

        if (showError && hasPermission.not()) {
            player.sendMessage(Broadcast.NO_BOW_PERMISSION.format(bowName()))
        }

        return hasPermission
    }

    /**
     * Checks if the player is inside a protected region.
     *
     * @param showError
     *          Whether to show an error to the player when they are in a protected region.
     * @return `true` when the player is in a protected region, `false` otherwise.
     */
    protected fun Player.isInProtectedRegion(showError: Boolean = true): Boolean {
        val inRegion = plugin.regionManager.isWithinARegionMargin(location, protectionRange) != null

        if (showError && inRegion) {
            player.sendMessage(Broadcast.DISABLED_IN_PROTECTED_REGION.format(bowName()))
        }

        return inRegion
    }

    /**
     * Checks whether the given player has all resources required to use this bow.
     *
     * @param showError
     *          Whether to show an error to the player when they don't meet
     * @return `true` if the player meets the cost requirements, `false` if they don't.
     */
    protected fun Player.meetsResourceRequirements(showError: Boolean = true): Boolean {
        val survival = gameMode == GameMode.SURVIVAL || gameMode == GameMode.ADVENTURE
        val meetsRequirements = survival && costRequirements.any { inventory.contains(it).not() }

        if (showError && meetsRequirements.not()) {
            sendMessage(Broadcast.NOT_ENOUGH_RESOURCES.format(costRequirements.joinToString(", ") {
                "${it.itemName} (x${it.amount})"
            }))
        }

        return meetsRequirements
    }

    /**
     * Get the colour applied bow name of the bow of this ability.
     */
    protected fun bowName() = plugin.config.getString(this@BowAbility.type.nameNode).applyColours()
}