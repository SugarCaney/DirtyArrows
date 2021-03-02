package nl.sugcube.dirtyarrows.bow

import nl.sugcube.dirtyarrows.Broadcast
import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.util.applyColours
import nl.sugcube.dirtyarrows.util.itemInMainHand
import nl.sugcube.dirtyarrows.util.itemInOffHand
import nl.sugcube.dirtyarrows.util.itemName
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.entity.Arrow
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.event.entity.ProjectileLaunchEvent
import org.bukkit.inventory.ItemStack

/**
 * Base for all bow abilities.
 *
 * * Override [effect] to execute a repeating scheduled effect every [handleEveryNTicks] ticks.
 * * Override [particle] to show a particle on every applicable site.
 * * Override [land] to handle when arrows shot with this bow land.
 * * Override [launch] for behaviour when the player shoots arrows.
 *
 * The implementing class should keep track of relevant entities/other data itself.
 *
 * @author SugarCaney
 */
abstract class BowAbility(

        /**
         * The main DA plugin instance.
         */
        protected val plugin: DirtyArrows,

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
        val costRequirements: List<ItemStack> = emptyList(),

        /**
         * Whether to remove the arrow when it hit.
         */
        val removeArrow: Boolean = true

) : Listener, Runnable {

    /**
     * Keeps track of the amount of ticks that have passed.
     */
    private var tickCounter = 0

    /**
     * Set containing all arrows that have been shot with this bow.
     */
    protected val arrows: MutableSet<Arrow> = HashSet()

    /**
     * Executes a repeating scheduled effect every [handleEveryNTicks] ticks.
     */
    open fun effect() = Unit

    /**
     * Shows a single particle effect on every applicable instance.
     */
    open fun particle(tickNumber: Int) = Unit

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
    open fun launch(player: Player, arrow: Arrow, event: ProjectileLaunchEvent) = Unit

    /**
     * Executes whenever an arrow with this abilities lands.
     * Only arrows that are shot with this bow, or are registered in [arrow] will get a [land] event.
     * The arrow is removed from [arrow] when the arrow landed.
     *
     * @param arrow
     *          The arrow that has landed.
     * @param player
     *          The player that shot the arrow.
     * @param event
     *          The event that fired when the arrow landed.
     */
    open fun land(arrow: Arrow, player: Player, event: ProjectileHitEvent) = Unit

    override fun run() {
        tickCounter++

        // Only execute the effect every [handleEveryNTicks] ticks.
        if (tickCounter % handleEveryNTicks == 0) {
            effect()
        }

        // Show particles every tick.
        if (plugin.config.getBoolean("show-particles")) {
            particle(tickCounter)
        }
    }

    @EventHandler
    fun eventHandlerProjectileHit(event: ProjectileHitEvent) {
        val arrow = event.entity as? Arrow ?: return
        val player = arrow.shooter as? Player ?: return

        // Only hit arrows that are linked to this ability.
        if (arrow in arrows) {
            // Remove arrow beforehand, as per documentation of [land]
            arrows.remove(arrow)

            // Only apply effects when the arrow does not land in a protected region.
            // Give back the items if it is in a protected region as they have been removed upon launch.
            if (arrow.location.isInProtectedRegion(player)) {
                player.reimburseBowItems()
            } else {
                land(arrow, player, event)
                if (removeArrow) {
                    arrow.remove()
                }
            }
        }
    }

    @EventHandler
    fun eventHandlerLaunchProjectile(event: ProjectileLaunchEvent) {
        val arrow = event.entity as? Arrow ?: return
        val player = arrow.shooter as? Player ?: return

        // Check if the bow can be used.
        if (player.isDirtyArrowsActivated().not()) return
        if (player.hasBowInHand().not()) return
        if (player.hasPermission().not()) return
        if (canShootInProtectedRegions.not() && player.location.isInProtectedRegion(player)) return
        if (player.meetsResourceRequirements().not()) return

        // All is fine, handle event.
        arrows.add(arrow)
        player.consumeBowItems()
        launch(player, arrow, event)
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
        return itemInMainHand.itemName == expectedName || itemInOffHand.itemName == expectedName
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
     * Checks if the location is inside a protected region.
     *
     * @param player
     *          The player who shot the arrow.
     * @param showError
     *          Whether to show an error to the player when they are in a protected region.
     * @return `true` when the player is in a protected region, `false` otherwise.
     */
    protected fun Location.isInProtectedRegion(player: Player, showError: Boolean = true): Boolean {
        val inRegion = plugin.regionManager.isWithinARegionMargin(this, protectionRange) != null

        if (showError && inRegion && canShootInProtectedRegions.not()) {
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
        val meetsRequirements = survival.not() || costRequirements.all {
            inventory.contains(it.type, it.amount)
        }

        if (showError && meetsRequirements.not()) {
            sendMessage(Broadcast.NOT_ENOUGH_RESOURCES.format(costRequirements.joinToString(", ") {
                "${it.type.name.toLowerCase()} (x${it.amount})"
            }))
        }

        return meetsRequirements
    }

    /**
     * Get the colour applied bow name of the bow of this ability.
     */
    protected fun bowName() = plugin.config.getString(this@BowAbility.type.nameNode).applyColours()

    /**
     * Registers that the given arrow is shot by this bow.
     *
     * @return `true` if the arrow has been added, `false` if the arrow is already registered.
     */
    protected fun registerArrow(arrow: Arrow) = arrows.add(arrow)

    /**
     * Removes all resources from the player's inventory that are required for 1 use.
     */
    protected open fun Player.consumeBowItems() = costRequirements.forEach {
        inventory.removeItem(it)
    }

    /**
     * Gives back all resources from the player's inventory that are required for 1 use.
     */
    protected open fun Player.reimburseBowItems() {
        if (player.gameMode != GameMode.CREATIVE) {
            costRequirements.forEach {
                inventory.addItem(it)
            }
        }
    }
}