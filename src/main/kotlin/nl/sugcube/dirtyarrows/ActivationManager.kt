package nl.sugcube.dirtyarrows

import org.bukkit.entity.Entity
import java.util.*
import kotlin.collections.HashSet

/**
 * @author SugarCaney
 */
open class ActivationManager(

    /**
     * Checks whether minigame mode is enabled.
     */
    private val isMinigamePredicate: () -> Boolean = { false }
) {

    /**
     * Contains the UUIDs of all entities that have DirtyArrows enabled.
     */
    private val activatedFor = HashSet<UUID>()

    /**
     * Checks if DA is activated for the entity with the given UUID.
     */
    fun isActivatedFor(uuid: UUID): Boolean = isMinigamePredicate() || uuid in activatedFor

    /**
     * Checks if DA is activated for the given entity.
     */
    fun isActivatedFor(entity: Entity): Boolean = isActivatedFor(entity.uniqueId)

    /**
     * Activates DA for the entity with the given UUID.
     */
    fun activateFor(uuid: UUID) = activatedFor.add(uuid)

    /**
     * Activates DA for the given entity.
     */
    fun activateFor(entity: Entity) = activateFor(entity.uniqueId)

    /**
     * Deactivates DA for the entity with the given UUID.
     */
    fun deactivateFor(uuid: UUID) = activatedFor.remove(uuid)

    /**
     * Deactivates DA for the given entity.
     */
    fun deactivateFor(entity: Entity) = deactivateFor(entity.uniqueId)

    /**
     * Enables DA when disabled, and disables DA when enabled for the entity with the given UUID.
     *
     * @return `true` if the new state is enabled, `false` if the new state is disabled.
     */
    fun toggleActivation(uuid: UUID): Boolean = if (isActivatedFor(uuid)) {
        deactivateFor(uuid)
        false
    }
    else {
        activateFor(uuid)
        true
    }

    /**
     * Enables DA when disabled, and disables DA when enabled for the given entity.
     *
     * @return `true` if the new state is enabled, `false` if the new state is disabled.
     */
    fun toggleActivation(entity: Entity) = toggleActivation(entity.uniqueId)
}