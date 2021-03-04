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
) {

    /**
     * All cursed entities mapped to the unix timestamp where the arrow was launched.
     */
    private val cursed = HashMap<LivingEntity, Long>()

    override fun land(arrow: Arrow, player: Player, event: ProjectileHitEvent) {
        val target = event.hitEntity as? LivingEntity ?: return

        cursed[target] = System.currentTimeMillis().fuzz(CURSE_DURATION_FUZZING.toLong())

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
            val mustDie = System.currentTimeMillis() - (cursed[it] ?: System.currentTimeMillis()) >= CURSE_DURATION
            mustDie.apply {
                if (this && it is Player) {
                    it.sendMessage(Broadcast.CURSE_LIFTED)
                }
            }
        }
    }

    private fun LivingEntity.executeCurseEffect() {
        if (Random.nextDouble() >= EFFECT_CHANCE) return
        if (Random.nextDouble() < EFFECT_DISORIENT_CHANCE) disorient()
        if (Random.nextDouble() < EFFECT_LAUNCH_CHANCE) launchUpward()
        if (Random.nextDouble() < EFFECT_FIRE_CHANCE) setOnFire()
        if (Random.nextDouble() < EFFECT_MOVE_CHANCE) launchHorizontally()
        if (Random.nextDouble() < EFFECT_EXPLOSION_CHANCE) explode()
    }

    private fun LivingEntity.disorient() = teleport(location.copyOf(
            yaw = location.yaw.fuzz(60f),
            pitch = location.pitch.fuzz(60f)
    ))

    private fun LivingEntity.launchUpward() {
        velocity = velocity.copyOf().add(Vector(0.0, 0.94.fuzz(0.08), 0.0))
    }

    private fun LivingEntity.setOnFire() {
        fireTicks = 60.fuzz(30)
    }

    private fun LivingEntity.launchHorizontally() {
        val newX = velocity.x + 0.0.fuzz(0.25)
        val newZ = velocity.z + 0.0.fuzz(0.25)
        velocity = velocity.copyOf(x = newX, y = 0.025.fuzz(0.005), z = newZ)
    }

    private fun LivingEntity.explode() = with(location) {
        world.createExplosion(x, y + 0.5, z, 1f, false, false)
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun deadUnregisterHandler(event: EntityDeathEvent) {
        if (event.entity is LivingEntity) {
            cursed.remove(event.entity)
        }
    }

    companion object {

        /**
         * The duration of the curse in milliseconds.
         */
        private const val CURSE_DURATION = 14000

        /**
         * Variance in curse duration.
         */
        private const val CURSE_DURATION_FUZZING = 4500

        /**
         * Chance of any effect happening.
         */
        private const val EFFECT_CHANCE = 0.6

        /**
         * Chance of being disoriented.
         */
        private const val EFFECT_DISORIENT_CHANCE = 0.25

        /**
         * Chance of being launched upward
         */
        private const val EFFECT_LAUNCH_CHANCE = 0.25

        /**
         * Chance of being set on fire.
         */
        private const val EFFECT_FIRE_CHANCE = 0.4

        /**
         * Chance of being moved in a different direction.
         */
        private const val EFFECT_MOVE_CHANCE = 0.2

        /**
         * Chance of creating an explosion.
         */
        private const val EFFECT_EXPLOSION_CHANCE = 0.3
    }
}