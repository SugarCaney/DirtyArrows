package nl.sugcube.dirtyarrows.util

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.plugin.java.JavaPlugin
import kotlin.reflect.KClass

/**
 * Schedules a delayed task.
 *
 * @param delayTicks
 *          The amount of ticks to wait before executing the task.
 * @param task
 *          The task to execute.
 */
fun JavaPlugin.scheduleDelayed(delayTicks: Long, task: Runnable) {
    Bukkit.getScheduler().scheduleSyncDelayedTask(this, task, delayTicks)
}

/**
 * Spawns an entity at this location.
 */
fun <T : Entity> Location.spawn(entity: Class<T>): T = world.spawn(this, entity)

/**
 * Spawns an entity at this location.
 */
fun <T : Entity> Location.spawn(entity: KClass<T>): T = world.spawn(this, entity.java)