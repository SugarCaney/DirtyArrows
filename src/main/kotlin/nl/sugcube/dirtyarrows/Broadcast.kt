package nl.sugcube.dirtyarrows

import nl.sugcube.dirtyarrows.effect.HeadshotType
import org.bukkit.ChatColor
import org.bukkit.entity.Player

/**
 * @author SugarCaney
 */
object Broadcast {

    val TAG = "${Colour.SECONDARY}DA${Colour.TERTIARY}>${Colour.PRIMARY}"
    val TAG_HELP = "${Colour.SECONDARY}DA-?${Colour.TERTIARY}>${Colour.PRIMARY}"
    val TAG_ERROR = "${Colour.ERROR}!!>"
    val TAG_MINIGAME = "${Colour.TERTIARY}[${Colour.SECONDARY}->${Colour.TERTIARY}]${Colour.PRIMARY}"

    /**
     * New version notification message.
     *
     * `%s` = New version number.
     */
    val NEW_VERSION_AVAILABLE = "$TAG A new version of ${Colour.SECONDARY}DirtyArrows${Colour.PRIMARY} is available: ${Colour.SECONDARY}v%s${Colour.PRIMARY}"

    /**
     * Shows that the plugin.yml has been reloaded.
     */
    val RELOADED_CONFIG = "$TAG Reloaded ${Colour.SECONDARY}config.yml"

    /**
     * DirtyArrows enable message.
     */
    val ENABLED = "$TAG ${ChatColor.GREEN}Enabled!"

    /**
     * DirtyArrows disable message.
     */
    val DISABLED = "$TAG ${ChatColor.RED}Disabled!"

    /**
     * Error when not enough xp levels are available.
     *
     * `%d` = Amount of levels required.
     */
    val LEVELS_REQUIRED = "$TAG_ERROR You need ${Colour.ERROR_SECONDARY}%d${Colour.ERROR} levels to craft this bow!"

    /**
     * Confirmation message for giving a bow to someone.
     *
     * %s = Bow name.
     * %s = Receiver name.
     */
    val GAVE_BOW = "$TAG Gave ${Colour.SECONDARY}%s${Colour.PRIMARY} to ${Colour.SECONDARY}%s${Colour.PRIMARY}."

    /**
     * Set position # to coordinate.
     *
     * %d = Position number.
     * %s = Set coordinate.
     */
    val POSITION_SET = "$TAG Set position ${Colour.SECONDARY}%d${Colour.PRIMARY} to ${Colour.SECONDARY}%s${Colour.PRIMARY}."

    /**
     * List all region names.
     *
     * %d = Amount of regions.
     * %s = List of region names.
     */
    val REGIONS_LIST = "$TAG Regions [%d]: ${Colour.SECONDARY}%s${Colour.PRIMARY}."

    /**
     * Show in which region the player is.
     *
     * %s = Region name.
     */
    val IN_REGION = "$TAG You are in region: ${Colour.SECONDARY}%s${Colour.PRIMARY}"

    /**
     * You are not in a region.
     */
    val NOT_IN_REGION = "$TAG You are not in a DirtyArrows region."

    /**
     * Removed a region.
     *
     * %s = Removed region name.
     */
    val REGION_REMOVED = "$TAG Removed region ${Colour.SECONDARY}%s${Colour.PRIMARY}."

    /**
     * Created a region.
     *
     * %s = Created region name.
     */
    val REGION_CREATED = "$TAG Created region ${Colour.SECONDARY}%s${Colour.PRIMARY}."

    /**
     * Teleported to a certain region.
     *
     * %s = Region name.
     */
    val REGION_TELEPORTED = "$TAG Teleported you to region ${Colour.SECONDARY}%s${Colour.PRIMARY}."

    /**
     * Got impacted by a headshot from a different player.
     *
     * %s = Name of player who made the headshot.
     */
    val HEADSHOT_BY = "$TAG Headshot by ${Colour.SECONDARY}%s${Colour.PRIMARY}."

    /**
     * Made a headshot on a different player. For minigames see [MINIGAME_HEADSHOT_ON].
     *
     * %s = Name of player who was the target of the headshot.
     */
    val HEADSHOT_ON = "$TAG You made a headshot on ${Colour.SECONDARY}%s${Colour.PRIMARY}."

    /**
     * Got impacted by a headshot from a different player (minigame).
     *
     * %s = Name of player who made the headshot.
     */
    val MINIGAME_HEADSHOT_BY = "$TAG_MINIGAME Headshot by ${Colour.SECONDARY}%s${Colour.PRIMARY}."

    /**
     * Made a headshot on a different player (minigame).
     *
     * %s = Name of player who was the target of the headshot.
     */
    val MINIGAME_HEADSHOT_ON = "$TAG_MINIGAME You made a headshot on ${Colour.SECONDARY}%s${Colour.PRIMARY}."

    /**
     * No permission to use the bow.
     *
     * %s = Name of the bow.
     */
    val NO_BOW_PERMISSION = "$TAG_ERROR You don't have permission to use ${Colour.PRIMARY}%s{${Colour.ERROR}."

    /**
     * Cannot use the bow in a protected region.
     *
     * %s = Name of the bow.
     */
    val DISABLED_IN_PROTECTED_REGION = "$TAG_ERROR ${Colour.PRIMARY}%s${Colour.ERROR} is disabled in protected regions."

    /**
     * Message to list all required resources.
     *
     * %s = List of required resources.
     */
    val NOT_ENOUGH_RESOURCES = "$TAG_ERROR You don't have enough resources, required: ${Colour.PRIMARY}%s${Colour.ERROR}."

    /**
     * Message to let the player know they defrosted.
     */
    val DEFROSTED = "$TAG You defrosted."

    /**
     * Message to let the player know they are frozen.
     */
    val FROZEN = "$TAG You have been frozen."

    /**
     * Message to let the player know they were cursed.
     */
    val CURSED = "$TAG You have been cursed."

    /**
     * Let know that the curse was lifted.
     */
    val CURSE_LIFTED = "$TAG The curse has been lifted."

    /**
     * Lets know that the bow does not requires ammonution.
     *
     * %s = bow name.
     */
    val NO_AMMO_REQUIRED = "$TAG ${Colour.SECONDARY}%s${Colour.PRIMARY} does not require ammonution."

    /**
     * Give ammo confirmation.
     *
     * %s = Item list.
     * %s = Target player.
     */
    val GAVE_AMMO = "$TAG Gave ${Colour.TERTIARY}%s${Colour.PRIMARY} to ${Colour.SECONDARY}%s${Colour.PRIMARY}."

    /**
     * You have to wait X seconds to use the bow again.
     *
     * %.1f = The amount of seconds left.
     * %s = Bow name.
     */
    val COOLDOWN = "$TAG_ERROR Wait ${Colour.PRIMARY}%.1fs${Colour.ERROR} before you can use ${Colour.SECONDARY}%s${Colour.ERROR} again."

    /**
     * Notifies that they player misses a music disc in their inventory.
     */
    val NO_RECORD = "$TAG_ERROR You need a ${Colour.PRIMARY}music disc${Colour.ERROR} in your inventory to play music."

    /**
     * Get the message that shows whether DA is enabled or disabled.
     *
     * @param plugin
     *          The DirtyArrows plugin.
     * @param enabled
     *          Whether DA is enabled (`true`) or disabled (`false`).
     */
    fun enabledMessage(plugin: DirtyArrows, enabled: Boolean = true): String {
        if (plugin.isMinigameVersion()) return ""

        return if (enabled) ENABLED else DISABLED
    }

    /**
     * Get the regular tag for messages.
     */
    fun tag(plugin: DirtyArrows) = if (plugin.isMinigameVersion()) TAG_MINIGAME else TAG

    /**
     * Get the message to show when a player makes or receives a headshot.
     *
     * @param playerInMessage
     *          The applicable player.
     * @param headshotType
     *          Whether the headshot was made by, or on `playerInMessage`.
     * @param plugin
     *          The DirtyArrows plugin.
     */
    fun headshot(playerInMessage: Player, headshotType: HeadshotType, plugin: DirtyArrows): String {
        return if (plugin.isMinigameVersion()) {
            headshotType.minigameMessage.format(playerInMessage.displayName)
        }
        else headshotType.message.format(playerInMessage.displayName)
    }

    /**
     * @author SugarCaney
     */
    object Colour {

        val PRIMARY = ChatColor.YELLOW
        val SECONDARY = ChatColor.GREEN
        val TERTIARY = ChatColor.WHITE
        val ERROR = ChatColor.RED
        val ERROR_SECONDARY = ChatColor.GRAY
    }
}

/**
 * @see [Broadcast.tag]
 */
fun DirtyArrows.tag() = Broadcast.tag(this)

/**
 * @see [Broadcast.headshot]
 */
fun DirtyArrows.headshot(playerInMessage: Player, headshotType: HeadshotType): String {
    return Broadcast.headshot(playerInMessage, headshotType, this)
}