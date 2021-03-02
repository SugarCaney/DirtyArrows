package nl.sugcube.dirtyarrows.util

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Entity
import org.bukkit.entity.HumanEntity
import org.bukkit.entity.Item
import org.bukkit.inventory.ItemStack
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

/**
 * Drops the given item on this location.
 */
fun Location.dropItem(item: ItemStack): Item = world.dropItem(this, item)

/**
 * Makes a copy of this location.
 */
fun Location.copyOf(
    world: World = this.world,
    x: Double = this.x,
    y: Double = this.y,
    z: Double = this.z,
    yaw: Float = this.yaw,
    pitch: Float = this.pitch
): Location {
    return Location(world, x, y, z, yaw, pitch)
}

/**
 * @see org.bukkit.inventory.PlayerInventory.getItemInMainHand
 */
val HumanEntity.itemInMainHand: ItemStack
    get() = inventory.itemInMainHand

/**
 * @see org.bukkit.inventory.PlayerInventory.getItemInOffHand
 */
val HumanEntity.itemInOffHand: ItemStack
    get() = inventory.itemInOffHand

/**
 * The display name of the item.
 */
val ItemStack.itemName: String
    get() = itemMeta.displayName