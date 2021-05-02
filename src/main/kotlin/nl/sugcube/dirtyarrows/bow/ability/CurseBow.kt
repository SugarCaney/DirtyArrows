package nl.sugcube.dirtyarrows.bow.ability

import nl.sugcube.dirtyarrows.Broadcast
import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.bow.BowAbility
import nl.sugcube.dirtyarrows.bow.DefaultBow
import nl.sugcube.dirtyarrows.util.copyOf
import nl.sugcube.dirtyarrows.util.fuzz
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Arrow
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector
import java.util.concurrent.ConcurrentHashMap
import kotlin.random.Random

/**
 * Curses hit target. Curses are annoying as they randomly do this:
 * - Disorients (changes yaw/pitch).
 * - Boosts upward.
 * - Sets on fire.
 * - Boosts in random direction.
 * - Tiny explosion.
 *
 * @author SugarCaney
 */
open class CurseBow(plugin: DirtyArrows) : BowAbility(
        plugin = plugin,
        type = DefaultBow.CURSE,
        handleEveryNTicks = 18,
        canShootInProtectedRegions = true,
        costRequirements = listOf(ItemStack(Material.FERMENTED_SPIDER_EYE, 1)),
        removeArrow = false,
        description = "Curses the target."
) {

    /**
     * All cursed entities mapped to the unix timestamp where the arrow was launched.
     */
    private val cursed = ConcurrentHashMap<LivingEntity, Long>()

    /**
     * The mean duration of the curse in milliseconds.
     */
    val curseDurationMillis = config.getInt("$node.duration")

    /**
     * Maximum deviation from the curse duration.
     */
    val curseDurationFuzzing = config.getInt("$node.duration-fuzzing")

    /**
     * Chance of any effect happening. 1-this chance of no effect being applied this iteration.
     */
    val effectChance = config.getDouble("$node.effect-chance.global")

    /**
     * Chance of being disoriented each iteration.
     */
    val effectDisorientChance = config.getDouble("$node.effect-chance.disorient")

    /**
     * Chance of being launched upward each iteration.
     */
    val effectLaunchChance = config.getDouble("$node.effect-chance.launch")

    /**
     * Chance of being set on fire each iteration.
     */
    val effectFireChance = config.getDouble("$node.effect-chance.fire")

    /**
     * Chance of being moved in a different direction each iteration.
     */
    val effectMoveChance = config.getDouble("$node.effect-chance.move")

    /**
     * Chance of creating an explosion each iteration.
     */
    val effectExplodeChance = config.getDouble("$node.effect-chance.explode")

    /**
     * The maximum amount of degrees the yaw can change when getting disoriented.
     */
    val disorientYawFuzzing = config.getDouble("$node.disorient.yaw-fuzzing").toFloat()

    /**
     * The maximum amount of degrees the pitch can change when getting disoriented.
     */
    val disorientPitchFuzzing = config.getDouble("$node.disorient.pitch-fuzzing").toFloat()

    /**
     * The mean velocity being applied when the launch effect gets chosen.
     */
    val launchVelocity = config.getDouble("$node.launch.velocity")

    /**
     * The maximum deviation from the launch velocity.
     */
    val launchVelocityFuzzing = config.getDouble("$node.launch.fuzzing")

    /**
     * The mean amount of ticks the target is set on fire.
     */
    val fireTicks = config.getInt("$node.fire.ticks")

    /**
     * The maximum deviation from the fire ticks.
     */
    val fireTickFuzzing = config.getInt("$node.fire.fuzzing")

    /**
     * Mean velocity the target will move on each axis in the horizontal plane when the move effect is chosen.
     */
    val moveVelocityRadius = config.getDouble("$node.move.velocity-radius")

    /**
     * The power of the random explosions. 4f is TNT.
     */
    val explosionPower = config.getDouble("$node.explode.power").toFloat()

    /**
     * If random explosions must create fire.
     */
    val explosionFire = config.getBoolean("$node.explode.set-fire")

    /**
     * If random explosions must be able to break blocks.
     */
    val explosionBreaksBlocks = config.getBoolean("$node.explode.break-blocks")

    init {
        check(curseDurationMillis >= 0) { "$node.duration cannot be negative, got <$curseDurationMillis>" }
        check(effectChance in 0.0..1.0) { "$node.effect-chance.global must be between 0 and 1, got <$effectChance>" }
        check(effectDisorientChance in 0.0..1.0) { "$node.effect-chance.disorient must be between 0 and 1, got <$effectDisorientChance>" }
        check(effectLaunchChance in 0.0..1.0) { "$node.effect-chance.launch must be between 0 and 1, got <$effectLaunchChance>" }
        check(effectFireChance in 0.0..1.0) { "$node.effect-chance.fire must be between 0 and 1, got <$effectFireChance>" }
        check(effectMoveChance in 0.0..1.0) { "$node.effect-chance.move must be between 0 and 1, got <$effectMoveChance>" }
        check(effectExplodeChance in 0.0..1.0) { "$node.effect-chance.explode must be between 0 and 1, got <$effectExplodeChance>" }
        check(fireTicks >= 0) { "$node.fire.ticks cannot be negative, got <$fireTicks>" }
        check(explosionPower >= 0) { "$node.explode.power cannot be negative, got <$explosionPower>" }
    }

    override fun land(arrow: Arrow, player: Player, event: ProjectileHitEvent) {
        val target = event.hitEntity as? LivingEntity ?: return

        cursed[target] = System.currentTimeMillis().fuzz(curseDurationFuzzing.toLong())

        if (target is Player) {
            target.sendMessage(Broadcast.CURSED)
            target.playSound(target.location, Sound.BLOCK_PORTAL_TRAVEL, 4f, 1f)
        }
    }

    override fun effect() {
        cursed.forEach { (target, _) ->
            target.executeCurseEffect()
        }

        // Remove when arrows have their effects applied for the maximum time.
        // Sometimes the hit event is not registered correctly, therefore this must be taken care of seperately.
        cursed.keys.removeAll {
            val mustDie = System.currentTimeMillis() - (cursed[it] ?: System.currentTimeMillis()) >= curseDurationMillis
            mustDie.apply {
                if (this && it is Player) {
                    it.sendMessage(Broadcast.CURSE_LIFTED)
                }
            }
        }
    }

    private fun LivingEntity.executeCurseEffect() {
        if (Random.nextDouble() >= effectChance) return
        if (Random.nextDouble() < effectDisorientChance) disorient()
        if (Random.nextDouble() < effectLaunchChance) launchUpward()
        if (Random.nextDouble() < effectFireChance) setOnFire()
        if (Random.nextDouble() < effectMoveChance) launchHorizontally()
        if (Random.nextDouble() < effectExplodeChance) explode()
    }

    private fun LivingEntity.disorient() = teleport(location.copyOf(
            yaw = location.yaw.fuzz(disorientYawFuzzing),
            pitch = location.pitch.fuzz(disorientPitchFuzzing)
    ))

    private fun LivingEntity.launchUpward() {
        velocity = velocity.copyOf().add(Vector(0.0,launchVelocity.fuzz(launchVelocityFuzzing), 0.0))
    }

    private fun LivingEntity.setOnFire() {
        fireTicks = fireTicks.fuzz(fireTickFuzzing)
    }

    private fun LivingEntity.launchHorizontally() {
        val newX = velocity.x + 0.0.fuzz(moveVelocityRadius)
        val newZ = velocity.z + 0.0.fuzz(moveVelocityRadius)
        velocity = velocity.copyOf(x = newX, y = 0.025.fuzz(0.005), z = newZ)
    }

    private fun LivingEntity.explode() = with(location) {
        world.createExplosion(x, y + 0.5, z, explosionPower, explosionFire, explosionBreaksBlocks)
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun deadUnregisterHandler(event: EntityDeathEvent) {
        if (event.entity is LivingEntity) {
            cursed.remove(event.entity)
        }
    }
}