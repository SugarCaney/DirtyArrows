package nl.sugcube.dirtyarrows.bow.ability

import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.bow.BowAbility
import nl.sugcube.dirtyarrows.bow.DefaultBow
import nl.sugcube.dirtyarrows.util.scheduleDelayed
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Arrow
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector
import kotlin.random.Random

/**
 * Creates a shower of arrows at the arrows' apex.
 *
 * @author SugarCaney
 */
open class RainBow(plugin: DirtyArrows) : BowAbility(
    plugin = plugin,
    type = DefaultBow.RAIN,
    handleEveryNTicks = 1,
    canShootInProtectedRegions = true,
    removeArrow = false,
    costRequirements = listOf(ItemStack(Material.ARROW, 8)),
    description = "Spawns an arrow rain at the arrow apex."
) {

    /**
     * How many arrows to spawn.
     */
    val arrowCount = config.getInt("$node.arrow-count")

    /**
     * Spread of the rain arrows.
     */
    val spread = config.getDouble("$node.spread")

    /**
     * Vertical spread of the rain arrows.
     */
    val verticalSpread = config.getDouble("$node.vertical-spread")

    /**
     * Multiplier for damage inflicted by arrows shot by the Rain bow.
     */
    val damageMultiplier = config.getDouble("$node.damage-multiplier")

    private val hasSpawnedRain = HashSet<Arrow>()
    private val rainArrows = HashSet<Arrow>()

    init {
        check(arrowCount >= 1) { "$node.arrow-count must be greater than or equal to 1, got <$arrowCount>" }
    }

    override fun land(arrow: Arrow, player: Player, event: ProjectileHitEvent) {
        hasSpawnedRain.remove(arrow)
        plugin.scheduleDelayed(1) { rainArrows.remove(arrow) }
    }

    override fun effect() {
        arrows.asSequence()
            .filter { it !in rainArrows }
            .filter { it.velocity.y <= 0 && it !in hasSpawnedRain }
            .forEach {
                it.spawnRain()
                hasSpawnedRain.add(it)
            }
    }

    override fun Player.consumeBowItems() {
        if (gameMode == GameMode.CREATIVE) return

        val hasInfinity = bowItem()?.enchantments?.containsKey(Enchantment.ARROW_INFINITE) == true
        if (hasInfinity.not()) {
            inventory.removeItem(ItemStack(Material.ARROW, arrowCount))
        }
    }

    override fun meetsResourceRequirements(player: Player, showError: Boolean): Boolean {
        if (player.bowItem()?.containsEnchantment(Enchantment.ARROW_INFINITE) == true) {
            return true
        }
        return super.meetsResourceRequirements(player, showError)
    }

    private fun Arrow.spawnRain() {
        repeat(arrowCount) {
            val rainArrow = world.spawn(location, Arrow::class.java).apply {
                shooter = this@spawnRain.shooter
                velocity = Vector(
                    Random.nextDouble(spread) - spread / 2,
                    Random.nextDouble(verticalSpread),
                    Random.nextDouble(spread) - spread / 2
                )
                pickupStatus = this@spawnRain.pickupStatus
                knockbackStrength = this@spawnRain.knockbackStrength
                fireTicks = this@spawnRain.fireTicks
                isCritical = this@spawnRain.isCritical
            }
            rainArrows.add(rainArrow)
            plugin.scheduleDelayed(0) { registerArrow(rainArrow) }
        }
        location.world?.playSound(location, Sound.ENTITY_FIREWORK_ROCKET_BLAST, 5.0f, 0.0f)
    }

    @EventHandler
    fun increaseDamage(event: EntityDamageByEntityEvent) {
        val arrow = event.damager as? Arrow ?: return
        if (arrow !in rainArrows) return

        event.damage = event.damage * damageMultiplier
    }
}