package nl.sugcube.dirtyarrows.bow.ability

import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.bow.BowAbility
import nl.sugcube.dirtyarrows.bow.DefaultBow
import nl.sugcube.dirtyarrows.util.*
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Arrow
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.inventory.ItemStack
import kotlin.math.max

/**
 * Meteors will strike on the location of impact. Amount depends on bow power level.
 *
 * @author SugarCaney
 */
open class MeteorBow(plugin: DirtyArrows) : BowAbility(
        plugin = plugin,
        type = DefaultBow.METEOR,
        canShootInProtectedRegions = false,
        protectionRange = 10.0,
        costRequirements = listOf(ItemStack(Material.FIREBALL, 1)),
        description = "Meteors will strike on impact."
) {

    /**
     * Contains all spawned meteor arrows. Use arrows because fire charges are a pain in the butthole to work with.
     * Their speed is neigh impossible to adjust. Their direction changes. They explode too soon.
     * Just use arrows with some particles/fire and it should be fine.
     */
    private val meteors = HashSet<Arrow>()

    /**
     * The power of the meteor explosions. TNT is 4.0 for reference.
     */
    val explosionPower = config.getFloat("$node.explosion-power")

    /**
     * Whether the meteor explosions can set blocks on fire.
     */
    val setOnFire = config.getBoolean("$node.set-on-fire")

    /**
     * Whether meteor explosions can break the broken blocks.
     */
    val breakBlocks = config.getBoolean("$node.break-blocks")

    /**
     * How many blocks from the location of impact the meteor will randomly spawn when there is only 1 meteor.
     */
    val minimumRange = config.getDouble("$node.minimum-range")

    /**
     * How many blocks from the location of impact the meteors will randomly spawn when there are 5 meteors.
     */
    val maximumRange = config.getDouble("$node.maximum-range")

    /**
     * The speed of the meteor in blocks/second.
     */
    val meteorSpeed = config.getDouble("$node.meteor-speed")

    /**
     * Maximum deviation from the meteor speed.
     */
    val meteorSpeedFuzzing = config.getDouble("$node.meteor-speed-fuzzing")

    init {
        check(explosionPower >= 0.0) { "$node.explosion-power cannot be negative, got <$explosionPower>" }
    }

    override fun land(arrow: Arrow, player: Player, event: ProjectileHitEvent) {
        if (arrow in meteors) {
            arrow.explode()
        }
        else {
            val deviation = minimumRange + (maximumRange - minimumRange) * player.powerLevel() / 5.0
            val cost = costRequirements.first()

            repeat(player.powerLevel()) {
                if (it != 0 && player.meetsResourceRequirements(showError = false) && player.gameMode != GameMode.CREATIVE) return@repeat

                arrow.spawnMeteor(deviation)

                // Don't remove the item for the first arrow: that will be deducted on launch by the base class.
                if (it != 0) {
                    player.inventory.removeItem(cost)
                }
            }
        }
    }

    /**
     * Summons a meteor that will strike on the location of the arrow.
     * Has a deviation in [range] blocks.
     * Does not spawn the meteor when the spawn location is within a protected region.
     *
     * @param deviation
     *          Amount of blocks the meteor can spawn from the arrow location.
     */
    private fun Arrow.spawnMeteor(deviation: Double) {
        val baseLocation = location
        val impactLocation = baseLocation.copyOf(
                x = baseLocation.x.fuzz(deviation),
                z = baseLocation.z.fuzz(deviation)
        )
        val spawnLocation = Location(world,
                baseLocation.x.fuzz(35.0),
                world.maxHeight.toDouble(),
                baseLocation.z.fuzz(35.0)
        )

        if (spawnLocation.isInProtectedRegion(shooter as? LivingEntity, showError = false)) return

        val speed = meteorSpeed.fuzz(meteorSpeedFuzzing)
        val direction = impactLocation.subtract(spawnLocation).toVector().normalize().multiply(speed)
        spawnLocation.spawn(Arrow::class).apply {
            shooter = this@spawnMeteor.shooter
            velocity = direction
            fireTicks = 100000
            setGravity(false)
            registerArrow(this)
            meteors.add(this)
        }

        showFlameParticle()
    }

    /**
     * The bow's power level.
     */
    private fun Player.powerLevel() = max(1, bowItem()?.getEnchantmentLevel(Enchantment.ARROW_DAMAGE) ?: 1)

    private fun Arrow.explode() {
        meteors.remove(this)
        location.createExplosion(power = explosionPower, setFire = setOnFire, breakBlocks = breakBlocks)
    }

    override fun particle(tickNumber: Int) = arrows.forEach {
        it.showSmokeParticle()
    }
}