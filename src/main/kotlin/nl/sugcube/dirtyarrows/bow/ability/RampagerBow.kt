package nl.sugcube.dirtyarrows.bow.ability

import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.bow.BowAbility
import nl.sugcube.dirtyarrows.bow.DefaultBow
import nl.sugcube.dirtyarrows.util.scheduleDelayed
import org.bukkit.ChatColor
import org.bukkit.entity.Arrow
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.event.entity.ProjectileLaunchEvent
import org.bukkit.event.player.PlayerQuitEvent
import kotlin.math.floor
import kotlin.math.min

/**
 * Each hit increases the damage output of the bow up to a certain amount.
 * Not hitting an entity reverses this effect.
 *
 * Each hit increases the amount of 'rampager stacks' by 1 up to a maximum.
 * Each stack increases the damage output by a certain amount.
 * Not hitting an enitity for a certain amount of time reverses this effect and stacks will be lost every so often.
 *
 * @author SugarCaney
 */
open class RampagerBow(plugin: DirtyArrows) : BowAbility(
        plugin = plugin,
        type = DefaultBow.RAMPAGER,
        handleEveryNTicks = 1,
        canShootInProtectedRegions = true,
        removeArrow = false,
        description = "Consecutive hits increase damage output."
) {

    /**
     * The maximum amount of rampager stacks.
     * You gain a rampager stack for every hit with this bow.
     */
    val maxStacks: Int = config.getInt("$node.max-stacks")

    /**
     * How many game ticks it takes for rampager stacks to start disappearing.
     */
    val ticksBeforeDecay: Int = config.getInt("$node.ticks-before-decay")

    /**
     * How quickly the rampager stacks disappear in stack per N amount of ticks.
     */
    val decayTicks: Int = config.getInt("$node.decay-ticks")

    /**
     * The damage multiplier when the shooter has racked up `max-stacks` amount of rampager stacks.
     * No stacks have a multiplier of 1.0, the rest is interpolated linearly.
     */
    val maximumDamageMultiplier: Double = config.getDouble("$node.maximum-damage-multiplier")

    /**
     * Whether to show the amount of stacks as title to the players.
     */
    val showStepCountAsTitle: Boolean = config.getBoolean("$node.show-stack-count-as-title")

    /**
     * Whether to show the amount of stacks in the title in colour.
     */
    val coloursInTitle: Boolean = config.getBoolean("$node.colours-in-title")

    /**
     * How much each stack increases the damage multiplier
     */
    val damageStep: Double = (maximumDamageMultiplier - 1.0) / maxStacks

    private val stacks = HashMap<LivingEntity, Int>()
    private val lastDamageTimestamp = HashMap<LivingEntity, Int>()

    private var currentTime = 0

    init {
        check(maxStacks > 0) { "$node.max-stacks must be greater than 0, got <$maxStacks>" }
        check(ticksBeforeDecay >= 0) { "$node.ticks-before-delay cannot be negative, got <$ticksBeforeDecay>" }
        check(decayTicks >= 0) { "$node.decay-ticks cannot be negative, got <$decayTicks>" }
    }

    override fun launch(player: Player, arrow: Arrow, event: ProjectileLaunchEvent) {
        val stackCount = stacks.getOrDefault(player, 0)
        val damageMultiplier = 1.0 + stackCount * damageStep

        arrow.damage = arrow.damage * damageMultiplier
    }

    override fun land(arrow: Arrow, player: Player, event: ProjectileHitEvent) {
        event.hitEntity ?: return

        stacks[player] = min(stacks.getOrDefault(player, 0) + 1, maxStacks)
        lastDamageTimestamp[player] = currentTime

        player.notifyStacks()
    }

    override fun effect() {
        currentTime++

        if (currentTime % decayTicks == 0) {
            removeStacks()

            stacks.keys.forEach { it.notifyStacks() }
        }
    }

    private fun LivingEntity.notifyStacks() {
        if (showStepCountAsTitle.not()) return
        val player = this as? Player ?: return


        player.sendTitle(
            "",
            stacksColour() + stacks[player].toString(),
            0, decayTicks * 2, 0
        )
    }

    private fun LivingEntity.stacksColour(): String {
        if (coloursInTitle.not()) return ""

        val stacks = stacks.getOrDefault(this, 0)
        if (stacks <= 0) return ""

        val step = maxStacks / COLOURS.size
        val index = floor((stacks - 1.0) / step).toInt()
        return COLOURS[index].toString()
    }

    private fun removeStacks() {
        stacks.keys.forEach {
            // Only remove stacks after the decay period ended.
            if (currentTime - lastDamageTimestamp.getOrDefault(it, 0) >= ticksBeforeDecay) {
                stacks[it] = stacks.getOrDefault(it, 0) - 1
            }
        }

        stacks.entries.removeIf { (it, _) -> stacks.getOrDefault(it, 0) <= 0 }
    }

    @EventHandler
    fun cleanupHashMapsOnLeave(event: PlayerQuitEvent) {
        plugin.scheduleDelayed(0) {
            stacks.remove(event.player)
            lastDamageTimestamp.remove(event.player)
        }
    }

    companion object {

        private val COLOURS = listOf(
            ChatColor.GREEN, ChatColor.YELLOW, ChatColor.GOLD, ChatColor.RED, ChatColor.DARK_RED
        )
    }
}