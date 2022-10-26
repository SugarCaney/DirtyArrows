package nl.sugcube.dirtyarrows.bow

import nl.sugcube.dirtyarrows.Broadcast
import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.util.*
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.Sound
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Arrow
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.event.entity.ProjectileLaunchEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import java.util.concurrent.ConcurrentHashMap

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
        costRequirements: List<ItemStack> = emptyList(),

        /**
         * Whether to remove the arrow when it hit.
         */
        val removeArrow: Boolean = true,

        /**
         * Short human readable description of the bow's effects.
         */
        val description: String = ""

) : Listener, Runnable {

    /**
     * What items are required for a single use.
     */
    var costRequirements: List<ItemStack> = costRequirements
        protected set

    /**
     * Keeps track of the amount of ticks that have passed.
     */
    protected var tickCounter = 0
        private set

    /**
     * Set containing all arrows that have been shot with this bow.
     */
    protected val arrows: MutableSet<Arrow> = HashSet()

    /**
     * The plugin's configuration.
     */
    protected val config: FileConfiguration = plugin.config

    /**
     * The configuration node of the bow.
     */
    protected val node = type.node

    /**
     * How many milliseconds of cooldown the ability has.
     */
    private val cooldownTime = config.getInt("$node.cooldown").toLong()

    /**
     * Whether to play a sound when the recharge time expires.
     */
    private val playSoundWhenRecharged = config.getBoolean("play-sound-when-charged")

    /**
     * Maps each player to the unix time they used this bow ability (millis).
     * When a player is contained in this map, the cooldown period is active.
     * When an ability has no cooldown, this map is always empty.
     * Gets accessed from the scheduler (run) and the event thread (projectilel launch event) hence concurrent.
     */
    private val cooldownMap = ConcurrentHashMap<Player, Long>()

    /**
     * Maps each entity to the items that were consumed latest by the ability.
     */
    private val mostRecentItemsConsumed = HashMap<Entity, List<ItemStack>>()

    /**
     * Get the items that were consumed on last use.
     */
    val Entity.lastConsumedItems: List<ItemStack>
        get() = mostRecentItemsConsumed[this] ?: emptyList()

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

        removeExpiredCooldowns()
    }

    /**
     * Removes all cooldown periods when they have expired.
     */
    private fun removeExpiredCooldowns() {
        val now = System.currentTimeMillis()

        cooldownMap.entries.removeIf { (player, usedTime) ->
            val toRemove = now - usedTime >= cooldownTime
            if (toRemove) {
                player.playSound(player.location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 10f, 1f)
            }
            toRemove
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    fun eventHandlerProjectileHit(event: ProjectileHitEvent) {
        val arrow = event.entity as? Arrow ?: return
        val player = arrow.shooter as? Player ?: return

        // Only hit arrows that are linked to this ability.
        if (arrow in arrows) {
            // Remove arrow beforehand, as per documentation of [land]
            unregisterArrow(arrow)

            // Only apply effects when the arrow does not land in a protected region.
            // Give back the items if it is in a protected region as they have been removed upon launch.
            if (canShootInProtectedRegions.not() && arrow.location.isInProtectedRegion(player)) {
                player.reimburseBowItems()
            } else {
                land(arrow, player, event)
                if (removeArrow) {
                    arrow.remove()
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    fun eventHandlerLaunchProjectile(event: ProjectileLaunchEvent) {
        val arrow = event.entity as? Arrow ?: return
        val player = arrow.shooter as? Player ?: return

        // Check if the bow can be used.
        if (player.isDirtyArrowsActivated().not()) return
        if (player.hasBowInHand().not()) return
        if (player.hasPermission().not()) return
        if (player.inCooldownPeriod()) return
        if (canShootInProtectedRegions.not() && player.location.isInProtectedRegion(player)) return
        if (player.meetsResourceRequirements().not()) return

        // Set cooldown.
        if (cooldownTime > 0) {
            cooldownMap[player] = System.currentTimeMillis()
        }

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
            player?.sendMessage(Broadcast.NO_BOW_PERMISSION.format(bowName()))
        }

        return hasPermission
    }

    /**
     * Checks if the ability is in cooldown.
     *
     * @param showError
     *          Whether to show an error if the ability is in cooldown.
     * @return `true` when the player is in cooldown, `false` otherwise.
     */
    protected fun Player.inCooldownPeriod(showError: Boolean = true): Boolean {
        val cooldown = player?.let { isOnCooldown(it) } ?: false

        if (cooldown && showError) {
            val timeLeft = cooldownTimeLeft() / 1000.0
            val bowName = config.getString(this@BowAbility.type.nameNode)?.applyColours()
            player?.sendMessage(Broadcast.COOLDOWN.format(timeLeft, bowName))
        }

        return cooldown
    }

    /**
     * Checks if the location is inside a protected region.
     *
     * @param entity
     *          The entity that shot the arrow.
     * @param showError
     *          Whether to show an error to the player when they are in a protected region.
     * @return `true` when the player is in a protected region, `false` otherwise.
     */
    protected fun Location.isInProtectedRegion(entity: LivingEntity?, showError: Boolean = true): Boolean {
        val inRegion = plugin.regionManager.isWithinARegionMargin(this, protectionRange) != null

        if (showError && inRegion && canShootInProtectedRegions.not()) {
            entity?.sendMessage(Broadcast.DISABLED_IN_PROTECTED_REGION.format(bowName()))
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
            inventory.checkForItem(it)
        }

        if (showError && meetsRequirements.not()) {
            sendMessage(Broadcast.NOT_ENOUGH_RESOURCES.format(costRequirements.joinToString(", ") {
                "${it.type.name.toLowerCase()} (x${it.amount})"
            }))
        }

        return meetsRequirements
    }

    /**
     * Checks if the inventory contains the required item.
     */
    open fun Inventory.checkForItem(itemStack: ItemStack) = containsAtLeastExcludingData(itemStack)

    /**
     * Get the colour applied bow name of the bow of this ability.
     */
    fun bowName() = plugin.config.getString(this@BowAbility.type.nameNode)?.applyColours()
            ?: error("No bow name found in configuration for ${this@BowAbility.type.nameNode}")

    /**
     * Whether the given player has the ability on cooldown.
     *
     * @param player
     *          The player to check for.
     * @return `true` if the player has the ability on cooldown, `false` otherwise.
     */
    fun isOnCooldown(player: Player) = cooldownMap.containsKey(player)

    /**
     * Looks up how many milliseconds of cooldown the given player has left for this ability.
     */
    protected fun Player.cooldownTimeLeft(): Long {
        if (isOnCooldown(this).not()) return 0
        val usedTime = cooldownMap.getOrDefault(this, 0L)
        return cooldownTime - (System.currentTimeMillis() - usedTime)
    }

    /**
     * Registers that the given arrow is shot by this bow.
     *
     * @return `true` if the arrow has been added, `false` if the arrow is already registered.
     */
    protected open fun registerArrow(arrow: Arrow) = arrows.add(arrow)

    /**
     * Unregisters the arrow.
     *
     * @return `true` if the arrow has been successfully removed; `false` if it was not present.
     */
    protected open fun unregisterArrow(arrow: Arrow) = arrows.remove(arrow)

    /**
     * Removes all resources from the player's inventory that are required for 1 use.
     */
    protected open fun Player.consumeBowItems() {
        val recentlyRemoved = ArrayList<ItemStack>(costRequirements.size)

        costRequirements.forEach { item ->
            // Find the item with the correct material. removeItem won't work when the item has
            // item data.
            val eligibleItem = inventory.asSequence()
                    .filterNotNull()
                    .firstOrNull { it.type == item.type && it.amount >= item.amount }
                    ?: return@forEach

            val toRemove = eligibleItem.clone().apply {
                amount = item.amount
            }
            if (gameMode != GameMode.CREATIVE) {
                inventory.removeIncludingData(toRemove)
            }
            recentlyRemoved.add(toRemove)
        }

        mostRecentItemsConsumed[this] = recentlyRemoved
    }

    /**
     * Removes the given entity from the cost requirements cache.
     * Effectively clearing the consumption cache for the entity.
     */
    fun removeFromCostRequirementsCache(entity: Entity) = mostRecentItemsConsumed.remove(entity)

    /**
     * Gives back all resources from the player's inventory that are required for 1 use.
     */
    protected open fun Player.reimburseBowItems() {
        if (player != null && player!!.gameMode != GameMode.CREATIVE) {
            costRequirements.forEach {
                inventory.addItem(it)
            }
        }
    }

    /**
     * Get the item of the bow that the player is holding (with this ability).
     *
     * @return The applicable bow item, or `null` when the player does not hold a bow.
     */
    protected fun Player.bowItem(): ItemStack? {
        val bowName = bowName()
        return if (itemInMainHand.itemName == bowName) {
            itemInMainHand
        } else if (itemInOffHand.itemName == bowName) {
            itemInOffHand
        } else null
    }
}