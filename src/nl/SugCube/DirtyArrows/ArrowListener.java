package nl.SugCube.DirtyArrows;

import java.util.ArrayList;
import java.util.List;


import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.TreeType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class ArrowListener implements Listener {

	public static DirtyArrows plugin;
	private int newamount;
	
	private String name;
	private List<Player> canExplode = new ArrayList<Player>();
	private List<Player> canStrikeLightning = new ArrayList<Player>();
	private List<Player> canCluck = new ArrayList<Player>();
	private List<Player> canTeleport = new ArrayList<Player>();
	private List<Player> canSpawnOak = new ArrayList<Player>();
	private List<Player> canSpawnBirch = new ArrayList<Player>();
	private List<Player> canSpawnSpruce = new ArrayList<Player>();
	private List<Player> canSpawnJungle = new ArrayList<Player>();
	private List<Player> canSpawnBats = new ArrayList<Player>();
	private List<Player> canNuke = new ArrayList<Player>();
	private List<Player> canLight = new ArrayList<Player>();
	private List<Player> canPoison = new ArrayList<Player>();
	private List<Player> canDisorient = new ArrayList<Player>();
	private List<Player> canDrain = new ArrayList<Player>();
	private List<Player> canSwap = new ArrayList<Player>();
	private List<Player> canFlint = new ArrayList<Player>();
	
	public ArrowListener(DirtyArrows instance) {
		plugin = instance;
	}
	
	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof Projectile) {
			Projectile proj = (Projectile) event.getDamager();
			if (proj.getShooter() instanceof Player) {
				Player player = (Player) proj.getShooter();
				if (event.getEntity() instanceof LivingEntity) {
					LivingEntity entity = (LivingEntity) event.getEntity();
					if (canPoison.contains(player)) {
						entity.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 150, 1));
						if (player.getGameMode().getValue() == 0) {
							player.getInventory().removeItem(new ItemStack(Material.SPIDER_EYE, 1));
						}
						canPoison.remove(player);
					} else if (canDisorient.contains(player)) {
						if (!(event.getEntity() instanceof Player)) {
							Location loc = entity.getLocation();
							loc.setYaw(loc.getYaw() + 180);
							loc.setPitch(loc.getPitch() + 180);
							entity.teleport(loc);
							canDisorient.remove(player);
						} else {
							Location loc = event.getEntity().getLocation();
							loc.setYaw(loc.getYaw() + 180);
							loc.setPitch(loc.getPitch() + 180);
							event.getEntity().teleport(loc);
							canDisorient.remove(player);
						}
					} else if (canDrain.contains(player)) {
						newamount = player.getHealth() + (int) (event.getDamage() / 3);
						if (newamount >= 20) {
							player.setHealth(20);
						} else {
							player.setHealth(newamount);
						}
						canDrain.remove(player);
					} else if (canSwap.contains(player)) {
						if (event.getDamage() > 0) {
							Location locEnt = entity.getLocation();
							Location locDam = player.getLocation();
							player.teleport(locEnt);
							entity.teleport(locDam);
						}
						canSwap.remove(player);
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (player.getInventory().getItemInHand() != null) {
			if (player.getInventory().getItemInHand().getItemMeta().hasDisplayName()) {
				if (player.getInventory().getItemInHand().getType().getId() == 261) {
					if (player.getInventory().getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase("Machine Bastard")) {
						if (player.hasPermission("dirtyarrows.machine") && plugin.getConfig().getBoolean("machine.enabled")) {
							if (plugin.activated.contains(player) && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
								if (player.getInventory().contains(Material.ARROW) || player.getGameMode().getValue() == 1) {
									Location loc = player.getEyeLocation().toVector().add(player.getLocation().getDirection().multiply(3)).toLocation(player.getWorld(), player.getLocation().getYaw(), player.getLocation().getPitch());
									removeSwap(player);
									Arrow arrow = player.getWorld().spawn(loc, Arrow.class);
									if (player.getItemInHand().containsEnchantment(Enchantment.ARROW_FIRE)) {
										arrow.setFireTicks(2000);
									}
									if (player.getGameMode().getValue() == 0) {
										if (!(player.getInventory().getItemInHand().containsEnchantment(Enchantment.ARROW_INFINITE))) {
											player.getInventory().removeItem(new ItemStack(Material.ARROW, 1));
										}
										if (player.getInventory().getItemInHand().getDurability() < (short) 384) {
											player.getInventory().getItemInHand().setDurability((short) (player.getItemInHand().getDurability() + 1));
										} else {
											player.getInventory().remove(player.getInventory().getItemInHand());
											player.playSound(player.getLocation(), Sound.ITEM_BREAK, 10, 10);
										}
									}
									arrow.setShooter(player);
									player.playSound(player.getLocation(), Sound.ARROW_HIT, 5, 5);
									arrow.setVelocity(player.getEyeLocation().getDirection().multiply(3));
									event.setCancelled(true);
								}
							}
						} else {
							Error.noMachine(player);
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onProjectileLaunch(ProjectileLaunchEvent event) {
		if (event.getEntity() instanceof Arrow) {
			if (event.getEntity().getShooter() instanceof Player) {
				Player player = (Player) event.getEntity().getShooter();
				if (player.getInventory().getItemInHand().getItemMeta().hasDisplayName()) {
					name = player.getInventory().getItemInHand().getItemMeta().getDisplayName();
					if (name.equalsIgnoreCase("Exploding Bastard") && plugin.activated.contains(player)) {
						removeSwap(player);
						if (!(canExplode.contains(player))) {
							if (player.hasPermission("dirtyarrows.exploding") && plugin.getConfig().getBoolean("exploding.enabled")) {
								if (player.getGameMode().getValue() == 1) {
									canExplode.add(player);
									return;
								}
								if (player.getInventory().contains(Material.TNT, 1)) {
									canExplode.add(player);
								} else {
									Error.noExploding(player, "item");
								}
							} else {
								Error.noExploding(player, "permissions");
							}
						}
					} else if (name.equalsIgnoreCase("Lightning Bastard") && plugin.activated.contains(player)) {
						removeSwap(player);
						if (!(canStrikeLightning.contains(player))) {
							if (player.hasPermission("dirtyarrows.lightning") && plugin.getConfig().getBoolean("lightning.enabled")) {
								if (player.getGameMode().getValue() == 1) {
									canStrikeLightning.add(player);
									return;
								}
								if (player.getInventory().contains(Material.GLOWSTONE_DUST, 1)) {
									canStrikeLightning.add(player);
								} else {
									Error.noLightning(player, "item");
								}
							} else {
								Error.noLightning(player, "permissions");
							}
						}
					} else if (name.equalsIgnoreCase("Clucky Bastard") && plugin.activated.contains(player)) {
						removeSwap(player);
						if (!(canCluck.contains(player))) {
							if (player.hasPermission("dirtyarrows.clucky") && plugin.getConfig().getBoolean("clucky.enabled")) {
								if (player.getGameMode().getValue() == 1) {
									canCluck.add(player);
									return;
								}
								if (player.getInventory().contains(Material.EGG, 1)) {
									canCluck.add(player);
								} else {
									Error.noClucky(player, "item");
								}
							} else {
								Error.noClucky(player, "permissions");
							}
						}
					} else if (name.equalsIgnoreCase("Ender Bastard") && plugin.activated.contains(player)) {
						removeSwap(player);
						if (!(canTeleport.contains(player))) {
							if (player.hasPermission("dirtyarrows.ender") && plugin.getConfig().getBoolean("ender.enabled")) {
								if (player.getGameMode().getValue() == 1) {
									canTeleport.add(player);
									return;
								}
								if (player.getInventory().contains(Material.ENDER_PEARL, 1)) {
									canTeleport.add(player);
								} else {
									Error.noEnder(player, "item");
								}
							} else {
								Error.noEnder(player, "permissions");
							}
						}
					} else if (name.equalsIgnoreCase("Oak Bastard") && plugin.activated.contains(player)) {
						removeSwap(player);
						if (!(canSpawnOak.contains(player))) {
							if (player.hasPermission("dirtyarrows.oak") && plugin.getConfig().getBoolean("oak.enabled")) {
								if (player.getGameMode().getValue() == 1) {
									canSpawnOak.add(player);
									return;
								}
								if (player.getInventory().containsAtLeast(new ItemStack(Material.SAPLING, (short) 1, (short) 0), 1) &&
										player.getInventory().containsAtLeast(new ItemStack(Material.INK_SACK, (short) 1, (short) 15), 1)) {
									canSpawnOak.add(player);
								} else {
									Error.noOak(player, "item");
								}
							} else {
								Error.noOak(player, "permissions");
							}
						}
					} else if (name.equalsIgnoreCase("Birch Bastard") && plugin.activated.contains(player)) {
						removeSwap(player);
						if (!(canSpawnBirch.contains(player))) {
							if (player.hasPermission("dirtyarrows.birch") && plugin.getConfig().getBoolean("birch.enabled")) {
								if (player.getGameMode().getValue() == 1) {
									canSpawnBirch.add(player);
									return;
								}
								if (player.getInventory().containsAtLeast(new ItemStack(Material.SAPLING, (short) 1, (short) 2), 1) &&
										player.getInventory().containsAtLeast(new ItemStack(Material.INK_SACK, (short) 1, (short) 15), 1)) {
									canSpawnBirch.add(player);
								} else {
									Error.noBirch(player, "item");
								}
							} else {
								Error.noBirch(player, "permissions");
							}
						}
					} else if (name.equalsIgnoreCase("Spruce Bastard") && plugin.activated.contains(player)) {
						removeSwap(player);
						if (!(canSpawnSpruce.contains(player))) {
							if (player.hasPermission("dirtyarrows.spruce") && plugin.getConfig().getBoolean("spruce.enabled")) {
								if (player.getGameMode().getValue() == 1) {
									canSpawnSpruce.add(player);
									return;
								}
								if (player.getInventory().containsAtLeast(new ItemStack(Material.SAPLING, (short) 1, (short) 1), 1) &&
										player.getInventory().containsAtLeast(new ItemStack(Material.INK_SACK, (short) 1, (short) 15), 1)) {
									canSpawnSpruce.add(player);
								} else {
									Error.noSpruce(player, "item");
								}
							} else {
								Error.noSpruce(player, "permissions");
							}
						}
					} else if (name.equalsIgnoreCase("Jungle Bastard") && plugin.activated.contains(player)) {
						removeSwap(player);
						if (!(canSpawnJungle.contains(player))) {
							if (player.hasPermission("dirtyarrows.jungle") && plugin.getConfig().getBoolean("jungle.enabled")) {
								if (player.getGameMode().getValue() == 1) {
									canSpawnJungle.add(player);
									return;
								}
								if (player.getInventory().containsAtLeast(new ItemStack(Material.SAPLING, (short) 1, (short) 3), 1) &&
										player.getInventory().containsAtLeast(new ItemStack(Material.INK_SACK, (short) 1, (short) 15), 1)) {
									canSpawnJungle.add(player);
								} else {
									Error.noJungle(player, "item");
								}
							} else {
								Error.noJungle(player, "permissions");
							}
						}
					} else if (name.equalsIgnoreCase("Batty Bastard") && plugin.activated.contains(player)) {
						removeSwap(player);
						if (!(canSpawnBats.contains(player))) {
							if (player.hasPermission("dirtyarrows.batty") && plugin.getConfig().getBoolean("batty.enabled")) {
								if (player.getGameMode().getValue() == 1) {
									canSpawnBats.add(player);
									return;
								}
								if (player.getInventory().contains(Material.ROTTEN_FLESH, 3)) {
									canSpawnBats.add(player);
								} else {
									Error.noBatty(player, "item");
								}
							} else {
								Error.noBatty(player, "permissions");
							}
						}
					} else if (name.equalsIgnoreCase("Nuclear Bastard") && plugin.activated.contains(player)) {
						removeSwap(player);
						if (!(canNuke.contains(player))) {
							if (player.hasPermission("dirtyarrows.nuclear") && plugin.getConfig().getBoolean("nuclear.enabled")) {
								if (player.getGameMode().getValue() == 1) {
									canNuke.add(player);
									return;
								}
								if (player.getInventory().contains(Material.TNT, 64)) {
									canNuke.add(player);
								} else {
									Error.noNuclear(player, "item");
								}
							} else {
								Error.noNuclear(player, "permissions");
							}
						}
					} else if (name.equalsIgnoreCase("Enlightened Bastard") && plugin.activated.contains(player)) {
						removeSwap(player);
						if (!(canLight.contains(player))) {
							if (player.hasPermission("dirtyarrows.enlightened") && plugin.getConfig().getBoolean("enlightened.enabled")) {
								if (player.getGameMode().getValue() == 1) {
									canLight.add(player);
									return;
								}
								if (player.getInventory().contains(Material.TORCH, 1)) {
									canLight.add(player);
								} else {
									Error.noEnlightened(player, "item");
								}
							} else {
								Error.noEnlightened(player, "permissions");
							}
						}
					} else if (name.equalsIgnoreCase("Ranged Bastard") && plugin.activated.contains(player)) {
						removeSwap(player);
						if (player.hasPermission("dirtyarrows.ranged") && plugin.getConfig().getBoolean("ranged.enabled")) {
							Arrow arrow = (Arrow) event.getEntity();
							arrow.setVelocity(player.getEyeLocation().getDirection().multiply(5));
						} else {
							Error.noRanged(player);
						}
					} else if (name.equalsIgnoreCase("Poisonous Bastard") && plugin.activated.contains(player)) {
						removeSwap(player);
						if (!(canPoison.contains(player))) {
							if (player.hasPermission("dirtyarrows.poisonous") && plugin.getConfig().getBoolean("poisonous.enabled")) {
								if (player.getGameMode().getValue() == 1) {
									canPoison.add(player);
									return;
								}
								if (player.getInventory().contains(Material.SPIDER_EYE, 1)){
									canPoison.add(player);
								} else {
									Error.noPoisonous(player, "item");
								}
							} else {
								Error.noPoisonous(player, "permissions");
							}
						}
					} else if (name.equalsIgnoreCase("Disorienting Bastard") && plugin.activated.contains(player)) {
						removeSwap(player);
						if (!(canDisorient.contains(player))) {
							if (player.hasPermission("dirtyarrows.disorienting") && plugin.getConfig().getBoolean("disorienting.enabled")) {
								canDisorient.add(player);
							} else {
								Error.noDisorienting(player);
							}
						}
					} else if (name.equalsIgnoreCase("Draining Bastard") && plugin.activated.contains(player)) {
						removeSwap(player);
						if (!(canDrain.contains(player))) {
							if (player.hasPermission("dirtyarrows.draining") && plugin.getConfig().getBoolean("draining.enabled")) {
								canDrain.add(player);
							} else {
								Error.noDraining(player);
							}
						}
					} else if (name.equalsIgnoreCase("Swap Bastard") && plugin.activated.contains(player)) {
						removeSwap(player);
						if (!(canSwap.contains(player))) {
							if (player.hasPermission("dirtyarrows.swap") && plugin.getConfig().getBoolean("swap.enabled")) {
								canSwap.add(player);
							} else {
								Error.noDisorienting(player);
							}
						}
					} else if (name.equalsIgnoreCase("Flint and Bastard") && plugin.activated.contains(player)) {
						removeSwap(player);
						if (!(canFlint.contains(player))) {
							if (player.hasPermission("dirtyarrows.flintand") && plugin.getConfig().getBoolean("flintand.enabled")) {
								if (player.getGameMode().getValue() == 1) {
									canFlint.add(player);
									return;
								}
								if (player.getInventory().contains(Material.FLINT_AND_STEEL, 1)){
									canFlint.add(player);
								} else {
									Error.noFlintAnd(player, "item");
								}
							} else {
								Error.noFlintAnd(player, "permissions");
							}
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onProjectileHit(ProjectileHitEvent event) {
		if (event.getEntity() instanceof Arrow) {
			Arrow arrow = (Arrow) event.getEntity();
			if (arrow.getShooter() instanceof Player) {
				Player player = (Player) arrow.getShooter();
				if (plugin.activated.contains(player)) {
					if (canExplode.contains(player)) {
						player.getWorld().createExplosion(arrow.getLocation(), 4F);
						arrow.remove();
						if (player.getGameMode().getValue() == 0)
							player.getInventory().removeItem(new ItemStack(Material.TNT, 1));
						canExplode.remove(player);
					} else if (canStrikeLightning.contains(player)) {
						player.getWorld().strikeLightning(arrow.getLocation());
						arrow.remove();
						if (player.getGameMode().getValue() == 0) {
							player.getInventory().removeItem(new ItemStack(Material.GLOWSTONE_DUST, 1));
						}
						canStrikeLightning.remove(player);
					} else if (canCluck.contains(player)) {
						player.getWorld().spawnEntity(arrow.getLocation(), EntityType.CHICKEN);
						player.playSound(player.getLocation(), Sound.CHICKEN_HURT, 10, 1);
						arrow.remove();
						if (player.getGameMode().getValue() == 0)
							player.getInventory().removeItem(new ItemStack(Material.EGG, 1));
						canCluck.remove(player);
					} else if (canTeleport.contains(player)) {
						player.teleport(arrow.getLocation());
						player.playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 10, 1);
						arrow.remove();
						if (player.getGameMode().getValue() == 0)
							player.getInventory().removeItem(new ItemStack(Material.ENDER_PEARL, 1));
						canTeleport.remove(player);
					} else if (canSpawnOak.contains(player)) {
						if (arrow.getWorld().generateTree(arrow.getLocation(), TreeType.TREE)) {
							arrow.getWorld().generateTree(arrow.getLocation(), TreeType.TREE);
							if (player.getGameMode().getValue() == 0) {
								player.getInventory().removeItem(new ItemStack(Material.SAPLING, 1, (short) 0));
								player.getInventory().removeItem(new ItemStack(Material.INK_SACK, 1, (short) 15));
							}
							arrow.remove();
						}
						canSpawnOak.remove(player);
					} else if (canSpawnBirch.contains(player)) {
						if (arrow.getWorld().generateTree(arrow.getLocation(), TreeType.BIRCH)) {
							arrow.getWorld().generateTree(arrow.getLocation(), TreeType.BIRCH);
							if (player.getGameMode().getValue() == 0) {
								player.getInventory().removeItem(new ItemStack(Material.SAPLING, 1, (short) 2));
								player.getInventory().removeItem(new ItemStack(Material.INK_SACK, 1, (short) 15));
							}
							arrow.remove();
						}
						canSpawnBirch.remove(player);
					} else if (canSpawnSpruce.contains(player)) {
						if (arrow.getWorld().generateTree(arrow.getLocation(), TreeType.REDWOOD)) {
							arrow.getWorld().generateTree(arrow.getLocation(), TreeType.REDWOOD);
							if (player.getGameMode().getValue() == 0) {
								player.getInventory().removeItem(new ItemStack(Material.SAPLING, 1, (short) 1));
								player.getInventory().removeItem(new ItemStack(Material.INK_SACK, 1, (short) 15));
							}
							arrow.remove();
						}
						canSpawnSpruce.remove(player);
					} else if (canSpawnJungle.contains(player)) {
						if (arrow.getWorld().generateTree(arrow.getLocation(), TreeType.SMALL_JUNGLE)) {
							arrow.getWorld().generateTree(arrow.getLocation(), TreeType.SMALL_JUNGLE);
							if (player.getGameMode().getValue() == 0) {
								player.getInventory().removeItem(new ItemStack(Material.SAPLING, 1, (short) 3));
								player.getInventory().removeItem(new ItemStack(Material.INK_SACK, 1, (short) 15));
							}
							arrow.remove();
						}
						canSpawnJungle.remove(player);
					} else if (canSpawnBats.contains(player)) {
						for (int i = 1; i <= 10; i++) {
							arrow.getWorld().spawnEntity(arrow.getLocation(), EntityType.BAT);
						}
						arrow.remove();
						if (player.getGameMode().getValue() == 0) {
							player.getInventory().removeItem(new ItemStack(Material.ROTTEN_FLESH, 3));
						}
						canSpawnBats.remove(player);
					} else if (canNuke.contains(player)) {
						player.getWorld().createExplosion(arrow.getLocation(), 50F);
						arrow.remove();
						if (player.getGameMode().getValue() == 0)
							player.getInventory().removeItem(new ItemStack(Material.TNT, 64));
						canNuke.remove(player);
					} else if (canLight.contains(player)) {
						arrow.getLocation().getBlock().setTypeId(50);
						arrow.remove();
						if (player.getGameMode().getValue() == 0)
							player.getInventory().removeItem(new ItemStack(Material.TORCH, 1));
						canLight.remove(player);
					} else if (canFlint.contains(player)) {
						int fintuses = 0;
						Location loc = arrow.getLocation();
						boolean check = (loc.getBlock().getType() == Material.AIR ||
										 loc.getBlock().getType() == Material.SNOW ||
										 loc.getBlock().getType() == Material.VINE ||
										 loc.getBlock().getType() == Material.DEAD_BUSH);
						if (check) {
							loc.getBlock().setType(Material.FIRE);
							fintuses++;
						}
						loc.add(1, 0, 0);
						if (check) {
							loc.getBlock().setType(Material.FIRE);
							fintuses++;
						}
						loc.add(-2, 0, 0);
						if (check) {
							loc.getBlock().setType(Material.FIRE);
							fintuses++;
						}
						loc.add(1, 0, 1);
						if (check) {
							loc.getBlock().setType(Material.FIRE);
							fintuses++;
						}
						loc.add(0, 0, -2);
						if (check) {
							loc.getBlock().setType(Material.FIRE);
							fintuses++;
						}
						if (player.getGameMode().getValue() == 0) {
							for (ItemStack is : player.getInventory().getContents()) {
								if (is.getType() == Material.FLINT_AND_STEEL) {
									if (is.getDurability() <= 64) {
										is.setDurability((short) (is.getDurability() + fintuses));
									} else {
										player.getInventory().remove(is);
										player.playSound(player.getLocation(), Sound.ITEM_BREAK, 10, 10);
									}
									break;
								}
							}
						}
						arrow.remove();
						canFlint.remove(player);
					}
				}
			}
		}
	}

	private void removeSwap(Player player) {
		if (canSwap.contains(player))
			canSwap.remove(player);
	}
	
}