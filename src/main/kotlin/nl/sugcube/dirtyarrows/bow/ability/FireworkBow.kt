package nl.sugcube.dirtyarrows.bow.ability

import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.bow.BowAbility
import nl.sugcube.dirtyarrows.bow.DefaultBow
import nl.sugcube.dirtyarrows.util.ColourScheme
import nl.sugcube.dirtyarrows.util.fuzz
import nl.sugcube.dirtyarrows.util.showColoredDust
import org.bukkit.*
import org.bukkit.entity.*
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.event.entity.ProjectileLaunchEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.FireworkEffectMeta
import org.bukkit.util.Vector
import kotlin.math.max
import kotlin.math.min

/**
 * Shoots a slow projectile that moves up and splits into 3 different fireworks.
 *
 * @author SugarCaney
 */
open class FireworkBow(plugin: DirtyArrows) : BowAbility(
        plugin = plugin,
        type = DefaultBow.FIREWORK,
        handleEveryNTicks = 1,
        canShootInProtectedRegions = false,
        removeArrow = true,
        description = "Splits into deadly fireworks.",
        costRequirements = listOf(ItemStack(Material.FIREWORK_CHARGE, 2))
) {

    /**
     * Shot arrows mapped to their birth time as unix timestamp.
     */
    private val mainProjectileTime = HashMap<Arrow, Long>()

    /**
     * Burst arrows from a main arrow mapped to their birth time as unix timestamp.
     */
    private val subProjectileTime = HashMap<Arrow, Long>()

    /**
     * The maximum speed the main firework projectiles can move.
     */
    val maximumProjectileSpeed = config.getDouble("$node.main.maximum-speed")

    /**
     * The amount of milliseconds the main projectile flies before splitting into multiple projectiles.
     */
    val mainProjectileFlightTime = config.getLong("$node.main.flight-time")

    /**
     * The maximum deviation from the main projectile flight time in milliseconds.
     */
    val mainProjectileFlightTimeFuzzing = config.getLong("$node.main.flight-time-fuzzing")

    /**
     * The amount of milliseconds the burst arrows fly before splitting into multiple projectiles.
     */
    val subProjectileFlightTime = config.getLong("$node.burst.flight-time")

    /**
     * The maximum deviation from the sub projectile flight time in milliseconds.
     */
    val subProjectileFlightTimeFuzzing = config.getLong("$node.burst.flight-time-fuzzing")

    /**
     * How quickly the sub projectiles move.
     */
    val subProjectileFlightSpeed = config.getDouble("$node.burst.flight-speed")

    /**
     * In how many sub fireworks the arrow should split.
     */
    val burstCount = config.getInt("$node.burst.count")

    /**
     * The gravity that is applied to the projectiles every tick.
     */
    val gravity = Vector(0.0, config.getDouble("$node.gravity"), 0.0)

    /**
     * Accuracy of the bow: the maximum deviation on any axis in blocks from the initial direction.
     */
    val directionFuzzing = config.getDouble("$node.main.direction-fuzzing")

    /**
     * How close to the projectiles entities take damage.
     */
    val damageRange = config.getDouble("$node.damage-range")

    /**
     * How much damage to deal to entities within range of the fireworks.
     */
    val maximumFireworkDamage = config.getDouble("$node.maximum-damage")

    override fun launch(player: Player, arrow: Arrow, event: ProjectileLaunchEvent) {
        val speed = min(arrow.velocity.length() / 2.0, maximumProjectileSpeed)
        arrow.velocity = arrow.velocity.fuzz(directionFuzzing).normalize().multiply(speed)
        arrow.setGravity(false)
        arrow.isCritical = false

        arrow.world.playSound(arrow.location, Sound.ENTITY_FIREWORK_LAUNCH, 1f, 1f)

        mainProjectileTime[arrow] = System.currentTimeMillis().fuzz(mainProjectileFlightTimeFuzzing)
    }

    override fun land(arrow: Arrow, player: Player, event: ProjectileHitEvent) {
        if (mainProjectileTime.remove(arrow) != null) {
            arrow.burst()
        }

        if (subProjectileTime.remove(arrow) != null) {
            arrow.explode()
        }
    }

    override fun effect() {
        val now = System.currentTimeMillis()

        subProjectileTime.entries.removeIf { (subProjectile, birthTime) ->
            subProjectile.applyGravity()

            if (now - birthTime >= subProjectileFlightTime) {
                subProjectile.explode()
                true
            }
            else false
        }

        mainProjectileTime.entries.removeIf { (mainProjectile, birthTime) ->
            mainProjectile.applyGravity()

            if (now - birthTime >= mainProjectileFlightTime) {
                mainProjectile.burst()
                true
            }
            else false
        }
    }

    /**
     * Applies gravity to this arrow (updates velocity).
     */
    private fun Arrow.applyGravity() {
        velocity = velocity.add(gravity)
    }

    /**
     * Creates a firework explosion at the arrow's location.
     * Does not remove the arrow from the [subProjectileTime] map.
     */
    private fun Arrow.explode() {
        val player = shooter as? Player ?: return
        if (location.isInProtectedRegion(player)) return

        val fireworkEffect = fireworkMeta(player)?.effect
        location.createFirework(player, fireworkEffect)
        location.damageEntities(player, damage = maximumFireworkDamage, range = damageRange)

        unregisterArrow(this)
        remove()
    }

    /**
     * Damages all entities around this location.
     * The damage will be scaled to the distance to the firework.
     *
     * @param this
     *          The location from where to search for entities.
     * @param cause
     *          Who the damage should be attributed to.
     * @param damage
     *          The maximum amount of damage to deal.
     * @param range
     *          How far from this location to inflict damage.
     */
    private fun Location.damageEntities(cause: Entity, damage: Double = maximumFireworkDamage, range: Double = damageRange) {
        world.getNearbyEntities(this, damageRange, damageRange, damageRange).asSequence()
                .mapNotNull { it as? LivingEntity }
                .filter { it.location.distance(this) <= range }
                .forEach {
                    val distance = it.location.distance(this)
                    val multiplier = max(0.5, 1.0 - distance / damageRange)
                    it.damage(damage * multiplier, cause)
                }
    }

    /**
     * Creates a firework at this location.
     *
     * @param this
     *          Where to create the firework.
     * @param player
     *          The player that shot the firework.
     * @param fireworkEffect
     *          The base effect the fireworks will have.
     */
    private fun Location.createFirework(player: Player, fireworkEffect: FireworkEffect?) {
        val firework = world.spawnEntity(this, EntityType.FIREWORK) as Firework
        firework.apply {
            fireworkMeta = fireworkMeta.apply {
                power = 3
                val effectBuilder = FireworkEffect.builder()
                        .with(fireworkEffect?.type ?: FireworkEffect.Type.BALL)
                        .withColor(fireworkEffect?.colors ?: fireworkColours(player) ?: ColourScheme.DEFAULT_SCHEMES.random())

                fireworkEffect?.fadeColors?.let { effectBuilder.withFade(it) }
                fireworkEffect?.hasFlicker()?.let { effectBuilder.flicker(it) }
                fireworkEffect?.hasTrail()?.let { effectBuilder.trail(it) }

                addEffect(effectBuilder.build())
            }
            detonate()
        }
    }

    /**
     * Make the arrow burst into several explosive fireworks.
     * Does not remove the arrow from the [mainProjectileTime] map.
     *
     * @param this
     *          Which arrow to burst into sub projectiles.
     */
    private fun Arrow.burst() {
        val player = shooter as? Player ?: return

        if (location.isInProtectedRegion(player, showError = false).not()) {
            repeat(burstCount) {
                location.shootBurstArrow(player, speed = subProjectileFlightSpeed)
            }
        }

        this.explode()
        unregisterArrow(this)
        remove()
    }

    /**
     * Launches a single burst arrow at the given location.
     *
     * @param this
     *          The location to spawn the burst arrow at.
     * @param shooter
     *          The player who shot the arrow.
     * @param speed
     *          The speed of the new burst arrow in blocks/tick.
     */
    private fun Location.shootBurstArrow(shooter: Player, speed: Double) {
        val randomDirection = Vector(
                0.0.fuzz(1.0),
                0.0.fuzz(1.0),
                0.0.fuzz(1.0)
        ).normalize().multiply(speed)

        val arrow = world.spawnEntity(this, EntityType.ARROW) as Arrow
        arrow.apply {
            velocity = randomDirection
            setGravity(false)
            setShooter(shooter)

            world.playSound(location, Sound.ENTITY_FIREWORK_BLAST, 1f, 1f)
            world.playSound(location, Sound.ENTITY_FIREWORK_LAUNCH, 1f, 1f)
        }

        registerArrow(arrow)
        subProjectileTime[arrow] = System.currentTimeMillis().fuzz(subProjectileFlightTimeFuzzing)
    }

    override fun particle(tickNumber: Int) = arrows.forEach { arrow ->
        val shooter = arrow.shooter as? Player
        repeat(3) {
            val colour = shooter?.let { fireworkColours(it)?.random() } ?: Color.WHITE
            arrow.location.fuzz(0.1).showColoredDust(colour, 1)
        }
    }

    /**
     * Determines which colour the fireworks should have.
     *
     * @param player
     *          The player who shot the firework item.
     */
    private fun fireworkColour(player: Player) = fireworkColours(player)?.firstOrNull()

    /**
     * Determines which colours the firework could have.
     *
     * @param player
     *          The player who shot the firework item.
     */
    private fun fireworkColours(player: Player): List<Color>? = fireworkMeta(player)?.effect?.colors

    /**
     * Get the firework meta of the firework charge to use.
     */
    private fun fireworkMeta(player: Player) = player.lastConsumedItems.asSequence()
            .filter { it.type == Material.FIREWORK_CHARGE && it.hasItemMeta() }
            .firstOrNull()
            ?.itemMeta as? FireworkEffectMeta
}