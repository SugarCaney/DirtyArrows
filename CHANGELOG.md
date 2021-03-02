Changelog
==================

## Legend

* **`+`** Added feature
* **`!`** Fixed bug
* **`~`** Tweaked
* **`~`** Removed feature

## Version 4.0 (Upcoming)

* **`i`** Updated for Minecraft 1.11.2.
* **`i`** Refactored the whole code base.
* **`+`** Added \da visualize to visualize regions and position selection.
* **`+`** Added option to hide the enabled/disabled message.
* **`+`** Added option to run in minigame/silent mode.
* **`+`** Added option to disable arrow particles.
* **`!`** Fixed anvil costs not updating correctly (and crash).
* **`!`** Fixed cursed bastard's move-effect not being calculated properly.
* **`!`** Fixed curse not going away after death.
* **`!`** Fixed frozen not going away after death.
* **`!`** Fixed perpetual frozen when you die when you're frozen.
* **`~`** Undead bow spawns zombies on the first available Y instead of the highest Y level.
* **`~`** Decreased the amount of zombies spawned by the undead bow.
* **`~`** Undead bow spawns zombies in a circle instead of a square.
* **`~`** Buffed cursed bastard.
* **`~`** Tweaked woodman bow drops (12% 1-6 planks, 8% 1-4 planks, 80% log).
* **`~`** Increased the power of the nuclear bow.
* **`~`** Effects are now tied to the arrow instead of the player.
* **`~`** Increased cost of batty bow to 6 rotten flesh (was 3).
* **`~`** Tree bows spawn trees more easily.
* **`~`** Tree bows now spawn all possible tree variants (chosen randomly).
* **`~`** Items get reimbursed when an effect (like the oak bow) fails.
* **`~`** Improved robustness.
* **`-`** Removed metrics.

## Version 3.0

* **`+`** Added update checker.
* **`+`** Added metrics.
* **`+`** Added Cluster Bastard.
* **`+`** Added Airship Bastard.
* **`+`** Added Iron Bastard.
* **`+`** Added Curse Bastard.
* **`+`** Added 360 Bastard.
* **`+`** Added Frozen Bastard.
* **`+`** Added Anvil level-cost option.
* **`!`** Fixed stack trace when the data.yml is empty.
* **`~`** Refactored code.
* **`~`** Removed extra Unbreaking Enchants on bows (not needed any longer).

## Version 2.8

* **`!`** Lightning Bastard didn't use up glowstone dust.
* **`!`** Flint and Bastard didn't deplete flint and steel sometimes.
* **`~`** Compacted an error message concerning the saving of regions.

## Version 2.7

* **`+`** Added region support. Protect your areas from being destructed
* **`+`** Added Acacia Bastard
* **`+`** Added Dark Oak Bastard
* **`+`** Added support for new woods for Woodman's Bastard
* **`+`** Added admin help-page (/da help admin)
* **`!`** Fixed getting back 3 soul sand if the Wither Bastard hits
* **`!`** Fixed getting back 1 firecharge if the Firey Bastard hits
* **`!`** Fixed Woodman's Bastard not showing up in /da help
* **`!`** Fixed Paralyze Bastard not showing up in /da help
* **`~`** Flint and Bastard now shoots firearrows (like the Flame-ench)
* **`~`** 'Not enough resource'-messages now all use the "x1" format (instead of 1x)
* **`~`** Data storage now depends on UUID's instead of Player-objects.

## Version 2.6

* **`+`** Added Paralyze Bastard
* **`!`** Fixed zombies dropping flint when they shouldn't.
* **`!`** Multi bastard now works when infinity is applied and there are less than 8 arrows in your inventory.
* **`~`** Turned down the chances of getting flint from zombies.
* **`~`** Improved the woodman's bastard's algorythm.
* **`~`** Beefed up multi bastard.
* **`~`** Beefed up disarming bastard.

## Version 2.4

* **`+`** Added Colour support
* **`+`** Added /da give command
* **`~`** /da help now only shows available bastards
* **`~`** /da reload now requires dirtyarrows.admin permission
* **`~`** Poison from poisonous bastard lasts a little longer
* **`~`** Blood now shows on all damage types instead of only entity dmg by entity
* **`~`** Some minor GameMode-related tweaks
* **`~`** Some minor BlockID-related tweaks
* **`~`** dirtyarrows.woodsman permission has changed to dirtyarrows.woodman

## Version 2.3

* **`+`** Added Bomb Bastard
* **`+`** Added Drop Bastard
* **`+`** Added Airstrike Bastard
* **`+`** Added Magmatic Bastard
* **`+`** Added Aquatic Bastard
* **`+`** Added Pull Bastard
* **`+`** Option to enable DirtyArrows on login (default: false)
* **`+`** Added CraftBukkit 1.6.2-R0.1 support
* **`+`** Added message when you make a headshot on someone
* **`+`** Added particles for exploding-type bastards
* **`+`** Added particles for Aquatic/Magmatic Bastard
* **`+`** Added particles for Flint and Bastard
* **`+`** Added option for blood (default: true)
* **`!`** Fixed Flint and Bastard
* **`!`** Fixed Multi Bastard
* **`!`** Lightning Bastard wás spelled as Ligtning Bastard
* **`~`** Tweaked Flint and Bastard
* **`~`** Default headshot multiplier is now 1.5

## Version 2.2

* **`i`** Updated to CraftBukkit 1.5.2-R1.0

## Version 2.1

* **`+`** Added Woodman's Bastard
* **`+`** Added Bastard of Starvation
* **`+`** Added Multi Bastard
* **`+`** Zombies can drop flint
* **`+`** Smoke particles whilst fighting mobs
* **`+`** Looting enchantment on bows will slightly increase the xp dropped
* **`~`** Slowed down Slow Bastard
* **`~`** Slow Bastard's arrows now go straight
* **`~`** Particles added using Undead Bastard
* **`~`** Changed the sound of Machine Bastard

## Version 2

* **`+`** Added Disarming Bastard
* **`+`** Added Wither Bastard
* **`+`** Added Firey Bastard
* **`+`** Added Level Bastard
* **`+`** Added Undead Bastard
* **`+`** Added Slow Bastard
* **`+`** Added Headshots
* **`+`** Added support for custom names
* **`+`** You can now get Looting & Unbreaking on a bow for normal enchanting
* **`+`** Unbreaking support Machine Bastard

## Version 1.2

* **`!`** Fixed removal of all crafting recipes by using /da reload

## Version 1.1

* **`+`** Added reload command /da reload
* **`!`** Fixed console-error showing up while attacking with nothing in hand

## Version 1

* **`i`** First Release of DirtyArrows
* **`+`** Added Exploding Bastard
* **`+`** Added Lightning Bastard
* **`+`** Added Clucky Bastard
* **`+`** Added Ender Bastard
* **`+`** Added Oak Bastard
* **`+`** Added Birch Bastard
* **`+`** Added Spruce Bastard
* **`+`** Added Jungle Bastard
* **`+`** Added Batty Bastard
* **`+`** Added Nuclear Bastard
* **`+`** Added Enlightened Bastard
* **`+`** Added Ranged Bastard
* **`+`** Added Machine Bastard
* **`+`** Added Poisonous Bastard
* **`+`** Added Swap Bastard
* **`+`** Added Draining Bastard
* **`+`** Added Flint and Bastard
* **`+`** Added recipe to create more arrows