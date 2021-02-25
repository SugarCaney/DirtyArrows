package nl.sugcube.dirtyarrows.listener;

import nl.sugcube.dirtyarrows.DirtyArrows;
import nl.sugcube.dirtyarrows.ability.Swarm;
import nl.sugcube.dirtyarrows.ability.TreeCut;
import nl.sugcube.dirtyarrows.util.Error;
import nl.sugcube.dirtyarrows.util.Message;
import nl.sugcube.dirtyarrows.util.Message.Type;
import nl.sugcube.dirtyarrows.util.Methods;
import nl.sugcube.dirtyarrows.util.Util;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.BlockIterator;

import java.util.*;

public class ArrowListener implements Listener {

	public static DirtyArrows plugin;
	public static final int CURSE_DURATION = 10;
	public static final int FREEZE_DURATION = 3;
	
	Random ran = new Random();
	int e = 0;
	boolean hasUnbreaking = false;
	
	private String name;
	private final List<Projectile> powerArrows = new ArrayList<>();
	private final List<String> canExplode = new ArrayList<>();
	private final List<String> canStrikeLightning = new ArrayList<>();
	private final List<String> canCluck = new ArrayList<>();
	private final List<String> canTeleport = new ArrayList<>();
	private final List<String> canSpawnOak = new ArrayList<>();
	private final List<String> canSpawnBirch = new ArrayList<>();
	private final List<String> canSpawnSpruce = new ArrayList<>();
	private final List<String> canSpawnJungle = new ArrayList<>();
	private final List<String> canSpawnBats = new ArrayList<>();
	private final List<String> canNuke = new ArrayList<>();
	private final List<String> canLight = new ArrayList<>();
	private final List<String> canPoison = new ArrayList<>();
	private final List<String> canDisorient = new ArrayList<>();
	private final List<String> canDrain = new ArrayList<>();
	private final List<String> canSwap = new ArrayList<>();
	private final List<String> canFlint = new ArrayList<>();
	private final List<String> canDisarm = new ArrayList<>();
	private final List<String> canBrick = new ArrayList<>();
	private final List<String> canLevel = new ArrayList<>();
	private final List<String> canSwarm = new ArrayList<>();
	private final List<String> canwoodman = new ArrayList<>();
	private final List<String> canFood = new ArrayList<>();
	private final List<String> canBomb = new ArrayList<>();
	private final List<String> canDrop = new ArrayList<>();
	private final List<String> canPull = new ArrayList<>();
	private final List<String> canParalyze = new ArrayList<>();
	private final List<String> canSpawnAcacia = new ArrayList<>();
	private final List<String> canSpawnDarkOak = new ArrayList<>();
	private final List<String> canCluster = new ArrayList<>();
	private final List<Integer> canCurse = new ArrayList<>();
	private final List<UUID> round = new ArrayList<>();
	
	public ArrowListener(DirtyArrows instance) {
		plugin = instance;
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if (plugin.slow.contains(event.getDamager())) {
			event.setDamage(event.getDamage() * 200);
		}
		if (powerArrows.contains(event.getDamager())) {
			event.setDamage(event.getDamage() * 2);
		}
		if (event.getCause() == DamageCause.PROJECTILE && plugin.getConfig().getBoolean("headshot")) {
			Projectile proj = (Projectile) event.getDamager();
			if (proj.getShooter() instanceof Player) {
				Entity shot = event.getEntity();
				
				double y = proj.getLocation().getY();
				double shotY = shot.getLocation().getY();
				boolean headshot = y - shotY > 1.35d;
				if (headshot) {
					event.setDamage(event.getDamage() * plugin.getConfig().getDouble("headshot-multiplier"));
					if (shot instanceof Player) {
						Player p = (Player) event.getEntity();
						Player shoot = (Player) proj.getShooter();
						p.sendMessage(Message.getHeadshot(shoot, Type.HEADSHOT_BY));
						shoot.sendMessage(Message.getHeadshot(p, Type.HEADSHOT_ON));
					}
				}
			}
		}
		if (event.getDamager() instanceof Projectile) {
			Projectile proj = (Projectile) event.getDamager();
			if (proj.getShooter() instanceof Player) {
				Player player = (Player) proj.getShooter();
				if (powerArrows.contains(proj)) {
					powerArrows.remove(proj);
					event.setDamage(event.getDamage() * 1.5);
				}
				if (event.getEntity() instanceof LivingEntity) {
					if (canCurse.contains(new Integer(proj.getEntityId()))) {
						canCurse.remove(proj);
						plugin.cursed.put(event.getEntity(), CURSE_DURATION + ran.nextInt(7));
						if (event.getEntity() instanceof Player) {
							((Player) event.getEntity()).sendMessage(Message.getTag() + ChatColor.GRAY + "You have been cursed.");
						}
					}
					if (plugin.ice.contains(new Integer(proj.getEntityId()))) {
						plugin.ice.remove(new Integer(proj.getEntityId()));
						plugin.iceParticle.remove(proj);
						int duration = FREEZE_DURATION + ran.nextInt(5);
						plugin.frozen.put(event.getEntity(), duration);
						if (event.getEntity() instanceof Player) {
							plugin.noInteract.add(((Player) event.getEntity()));
							((Player) event.getEntity()).sendMessage(Message.getTag() + ChatColor.GRAY + "You are frozen solid.");
							((Player) event.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, duration * 20, 6));
						}
						else {
							if (event.getEntity() instanceof LivingEntity) {
								LivingEntity len = (LivingEntity) event.getEntity();
								len.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, duration * 20, 6));
							}
						}
					}
					
					LivingEntity entity = (LivingEntity) event.getEntity();
					if (canLevel.contains(player.getUniqueId().toString()) && entity instanceof Player) {
						Player target = (Player) event.getEntity();
						if (target.getLevel() > 0) {
							target.setLevel(target.getLevel() - 1);
							player.setLevel(player.getLevel() + 1);
							player.getWorld().playEffect(target.getLocation(), Effect.SMOKE, 0);
							player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 10, 0);
						}
					} else if (canPoison.contains(player.getUniqueId().toString())) {
						entity.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 200, 1));
						if (player.getGameMode() == GameMode.SURVIVAL) {
							player.getInventory().removeItem(new ItemStack(Material.SPIDER_EYE, 1));
						}
						canPoison.remove(player.getUniqueId().toString());
					} else if (canDisorient.contains(player.getUniqueId().toString())) {
						if (!(event.getEntity() instanceof Player)) {
							Location loc = entity.getLocation();
							loc.setYaw(loc.getYaw() + 180);
							loc.setPitch(loc.getPitch() + 180);
							entity.teleport(loc);
							canDisorient.remove(player.getUniqueId().toString());
						} else {
							Location loc = event.getEntity().getLocation();
							loc.setYaw(loc.getYaw() + 180);
							loc.setPitch(loc.getPitch() + 180);
							event.getEntity().teleport(loc);
							canDisorient.remove(player.getUniqueId().toString());
						}
					} else if (canFood.contains(player.getUniqueId().toString())) {
						if (entity instanceof Player) {
							Player target = (Player) entity;
							if (event.getDamage() <= target.getFoodLevel()) {
								target.setFoodLevel((int) (target.getFoodLevel() - event.getDamage()));
							} else {
								target.setFoodLevel(0);
							}
						}
						canFood.remove(player.getUniqueId().toString());
					} else if (canDrain.contains(player.getUniqueId().toString())) {
						double newamount = player.getHealth() + 2;
						if (newamount >= 20) {
							player.setHealth(20.0);
						} else {
							player.setHealth(newamount);
						}
						canDrain.remove(player.getUniqueId().toString());
					} else if (canSwap.contains(player.getUniqueId().toString())) {
						if (event.getDamage() > 0) {
							Location locEnt = entity.getLocation();
							Location locDam = player.getLocation();
							player.teleport(locEnt);
							entity.teleport(locDam);
						}
						canSwap.remove(player.getUniqueId().toString());
					} else if (canDisarm.contains(player.getUniqueId().toString())) {
						if (ran.nextInt(3) > 0) {
							if (event.getEntity() instanceof LivingEntity) {
								Location loc = entity.getLocation();
								loc.add(ran.nextInt(5) - 2, 0, ran.nextInt(5) - 2);
								LivingEntity Lentity = (LivingEntity) event.getEntity();
								if (Lentity.getEquipment().getItemInHand().getType() != Material.AIR) {
									Lentity.getWorld().dropItem(loc, Lentity.getEquipment().getItemInHand());
									Lentity.getEquipment().setItemInHand(new ItemStack(Material.AIR));
								}
								
								if (ran.nextInt(2) == 0) {
									if (ran.nextInt(2) == 0) {
										if (ran.nextInt(2) == 0) {
											if (Lentity.getEquipment().getHelmet() != null) {
												ItemStack disarmArmor = Lentity.getEquipment().getHelmet();
												Lentity.getEquipment().setHelmet(null);
												if (disarmArmor.getType() != Material.AIR) 
													player.getWorld().dropItem(loc, disarmArmor);
											}
										} else {
											if (Lentity.getEquipment().getChestplate() != null) {
												ItemStack disarmArmor = Lentity.getEquipment().getChestplate();
												Lentity.getEquipment().setChestplate(null);
												if (disarmArmor.getType() != Material.AIR) 
													Lentity.getWorld().dropItem(loc, disarmArmor);
											}
										}
									} else {
										if (ran.nextInt(2) == 0) {
											if (Lentity.getEquipment().getLeggings() != null) {
												ItemStack disarmArmor = Lentity.getEquipment().getLeggings();
												Lentity.getEquipment().setLeggings(null);
												if (disarmArmor.getType() != Material.AIR) 
													Lentity.getWorld().dropItem(loc, disarmArmor);
											}
										} else {
											if (Lentity.getEquipment().getBoots() != null) {
												ItemStack disarmArmor = Lentity.getEquipment().getBoots();
												Lentity.getEquipment().setBoots(null);
												if (disarmArmor.getType() != Material.AIR) 
													Lentity.getWorld().dropItem(loc, disarmArmor);
											}
										}
									}
								}
							}
						}
						canDisarm.remove(player.getUniqueId().toString());
					} else if (canDrop.contains(player.getUniqueId().toString())) {
						Location loc = entity.getLocation();
						loc.add(0.5, 0.5, 0.5);
						boolean check = true;
						while (check) {
							if (loc.getBlock().getType() == Material.AIR) {
								loc.add(0, 1, 0);
								if (loc.getBlock().getType() == Material.AIR) {
									loc.add(0, -1, 0);
									
									for (int i = 1; i <= 8; i++) {
										if (loc.add(0, 1, 0).getBlock().getType() != Material.AIR) {
											check = true;
											break;
										}
										if (i == 8) {
											entity.teleport(loc);
											check = false;
											break;
										}
									}
									
								} else {
									loc.add(0, -1, 0);
								}
							}
							loc.add(0, 1, 0);
						}
						canDrop.remove(player.getUniqueId().toString());
					} else if (canPull.contains(player.getUniqueId().toString())) {
						entity.setVelocity(player.getLocation().getDirection().multiply(-5));
						canPull.remove(player.getUniqueId().toString());
					} else if (canParalyze.contains(player.getUniqueId().toString())) {
						int time = 100 + ran.nextInt(200);
						if (ran.nextInt(2) == 0) {
							entity.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, time + 100, 0));
						}
						if (ran.nextInt(3) == 0) {
							entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, time, 20));
						}
						if (ran.nextInt(3) == 0) {
							entity.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, time, 20));
						}
						if (ran.nextInt(3) == 0) {
							entity.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, time, 20));
						}
						if (player.getGameMode() == GameMode.SURVIVAL) {
							player.getInventory().removeItem(new ItemStack(Material.NETHER_STALK, 1));
						}
						canParalyze.remove(player.getUniqueId().toString());
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (player.getItemInHand().hasItemMeta()) {
			if (player.getInventory().getItemInHand().getItemMeta().hasDisplayName()) {
				if (player.getInventory().getItemInHand().getType() == Material.BOW) {
					if (player.getInventory().getItemInHand().getItemMeta().getDisplayName()
							.equalsIgnoreCase(Methods.setColours(plugin.getConfig().getString("machine.name")))) {
						if (player.hasPermission("dirtyarrows.machine") && plugin.getConfig().getBoolean("machine.enabled")) {
							if (plugin.isActivated(player) && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
								if (player.getInventory().contains(Material.ARROW) || player.getGameMode() == GameMode.CREATIVE) {
									if (player.getInventory().getItemInHand().containsEnchantment(Enchantment.DURABILITY)) {
										e = player.getInventory().getItemInHand().getEnchantmentLevel(Enchantment.DURABILITY);
										if (e == 1) {
											e = 4;
										} else if (e == 2) {
											e = 3;
										} else {
											e = 2;
										}
										hasUnbreaking = true;
									} else {
										hasUnbreaking = false;
									}
									
									Location loc = player.getEyeLocation().toVector().add(player.getLocation().getDirection().multiply(3)).toLocation(player.getWorld(), player.getLocation().getYaw(), player.getLocation().getPitch());
									removeSwap(player.getUniqueId().toString());
									Arrow arrow = player.getWorld().spawn(loc, Arrow.class);
									if (player.getItemInHand().containsEnchantment(Enchantment.ARROW_FIRE)) {
										arrow.setFireTicks(2000);
									}
									if (player.getGameMode() != GameMode.CREATIVE) {
										if (!(player.getInventory().getItemInHand().containsEnchantment(Enchantment.ARROW_INFINITE))) {
											player.getInventory().removeItem(new ItemStack(Material.ARROW, 1));
										}
										if (player.getInventory().getItemInHand().getDurability() < (short) 384) {
											if (hasUnbreaking && ran.nextInt(e) == 0) { } else {
												player.getInventory().getItemInHand().setDurability((short) (player.getItemInHand().getDurability() + 1));
											}
										} else {
											player.getInventory().remove(player.getInventory().getItemInHand());
											player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 10, 10);
										}
									}
									arrow.setShooter(player);
									player.playEffect(player.getLocation(), Effect.BOW_FIRE, null);
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
				final Player player = (Player) event.getEntity().getShooter();
				
				if (round.contains(player.getUniqueId())) {
					return;
				}
				
				if (player.getInventory().getItemInHand().getItemMeta().hasDisplayName()) {
					name = player.getInventory().getItemInHand().getItemMeta().getDisplayName();
					/*
					 * EXPLODING
					 */
					if (name.equalsIgnoreCase(Methods.setColours(plugin.getConfig().getString("exploding.name"))) && plugin.isActivated(player)) {
						removeSwap(player.getUniqueId().toString());
						if (!(canExplode.contains(player.getUniqueId().toString()))) {
							if (player.hasPermission("dirtyarrows.exploding") && plugin.getConfig().getBoolean("exploding.enabled")) {
								if (plugin.rm.isWithinARegionMargin(player.getLocation(), 1) == null) {
									if (player.getGameMode() == GameMode.CREATIVE) {
										plugin.particleExploding.add(event.getEntity());
										canExplode.add(player.getUniqueId().toString());
										return;
									}
									if (player.getInventory().contains(Material.TNT, 1)) {
										plugin.particleExploding.add(event.getEntity());
										canExplode.add(player.getUniqueId().toString());
									} else {
										Error.noExploding(player, "item");
									}
								} else {
									event.setCancelled(true);
									player.sendMessage(ChatColor.RED + "[!!] You can't shoot exploding arrows in a " +
											"protected region!");
								}
							} else {
								Error.noExploding(player, "permissions");
							}
						}
					}
					/*
					 * LIGHTNING
					 */
					else if (name.equalsIgnoreCase(Methods.setColours(plugin.getConfig().getString("lightning.name"))) && plugin.isActivated(player)) {
						if (!(canStrikeLightning.contains(player.getUniqueId().toString()))) {
							if (player.hasPermission("dirtyarrows.lightning") && plugin.getConfig().getBoolean("lightning.enabled")) {
								if (plugin.rm.isWithinARegionMargin(player.getLocation(), 1) == null) {
									if (player.getGameMode() == GameMode.CREATIVE) {
										canStrikeLightning.add(player.getUniqueId().toString());
										return;
									}
									if (player.getInventory().contains(Material.GLOWSTONE_DUST, 1)) {
										canStrikeLightning.add(player.getUniqueId().toString());
									} else {
										Error.noLightning(player, "item");
									}
								} else {
									event.setCancelled(true);
									player.sendMessage(ChatColor.RED + "[!!] You can't shoot lightning arrows in a " +
											"protected region!");
								}
							} else {
								Error.noLightning(player, "permissions");
							}
						}
					}
					/*
					 * CLUCKY
					 */
					else if (name.equalsIgnoreCase(Methods.setColours(plugin.getConfig().getString("clucky.name"))) && plugin.isActivated(player)) {
						removeSwap(player.getUniqueId().toString());
						if (!(canCluck.contains(player.getUniqueId().toString()))) {
							if (player.hasPermission("dirtyarrows.clucky") && plugin.getConfig().getBoolean("clucky.enabled")) {
								if (plugin.rm.isWithinARegionMargin(player.getLocation(), 1) == null) {
									if (player.getGameMode() == GameMode.CREATIVE) {
										canCluck.add(player.getUniqueId().toString());
										return;
									}
									if (player.getInventory().contains(Material.EGG, 1)) {
										canCluck.add(player.getUniqueId().toString());
									} else {
										Error.noClucky(player, "item");
									}
								} else {
									event.setCancelled(true);
									player.sendMessage(ChatColor.RED + "[!!] You can't shoot chickens in protected regions!");
								}
							} else {
								Error.noClucky(player, "permissions");
							}
						}						
					}
					/*
					 * ENDER
					 */
					else if (name.equalsIgnoreCase(Methods.setColours(plugin.getConfig().getString("ender.name"))) && plugin.isActivated(player)) {
						removeSwap(player.getUniqueId().toString());
						if (!(canTeleport.contains(player.getUniqueId().toString()))) {
							if (player.hasPermission("dirtyarrows.ender") && plugin.getConfig().getBoolean("ender.enabled")) {
								if (player.getGameMode() == GameMode.CREATIVE) {
									canTeleport.add(player.getUniqueId().toString());
									return;
								}
								if (player.getInventory().contains(Material.ENDER_PEARL, 1)) {
									canTeleport.add(player.getUniqueId().toString());
								} else {
									Error.noEnder(player, "item");
								}
							} else {
								Error.noEnder(player, "permissions");
							}
						}
					}
					/*
					 * OAK
					 */
					else if (name.equalsIgnoreCase(Methods.setColours(plugin.getConfig().getString("oak.name"))) && plugin.isActivated(player)) {
						removeSwap(player.getUniqueId().toString());
						if (!(canSpawnOak.contains(player.getUniqueId().toString()))) {
							if (player.hasPermission("dirtyarrows.oak") && plugin.getConfig().getBoolean("oak.enabled")) {
								if (plugin.rm.isWithinARegionMargin(player.getLocation(), 1) == null) {
									if (player.getGameMode() == GameMode.CREATIVE) {
										canSpawnOak.add(player.getUniqueId().toString());
										return;
									}
									if (player.getInventory().containsAtLeast(new ItemStack(Material.SAPLING, (short) 1, (short) 0), 1) &&
											player.getInventory().containsAtLeast(new ItemStack(Material.INK_SACK, (short) 1, (short) 15), 1)) {
										canSpawnOak.add(player.getUniqueId().toString());
									} else {
										Error.noOak(player, "item");
									}
								} else {
									event.setCancelled(true);
									player.sendMessage(ChatColor.RED + "[!!] You can't shoot oak arrows in a protected region!");
								}
							} else {
								Error.noOak(player, "permissions");
							}
						}
					}
					/*
					 * BIRCH
					 */
					else if (name.equalsIgnoreCase(Methods.setColours(plugin.getConfig().getString("birch.name"))) && plugin.isActivated(player)) {
						removeSwap(player.getUniqueId().toString());
						if (!(canSpawnBirch.contains(player.getUniqueId().toString()))) {
							if (player.hasPermission("dirtyarrows.birch") && plugin.getConfig().getBoolean("birch.enabled")) {
								if (plugin.rm.isWithinARegionMargin(player.getLocation(), 1) == null) {
									if (player.getGameMode() == GameMode.CREATIVE) {
										canSpawnBirch.add(player.getUniqueId().toString());
										return;
									}
									if (player.getInventory().containsAtLeast(new ItemStack(Material.SAPLING, (short) 1, (short) 2), 1) &&
											player.getInventory().containsAtLeast(new ItemStack(Material.INK_SACK, (short) 1, (short) 15), 1)) {
										canSpawnBirch.add(player.getUniqueId().toString());
									} else {
										Error.noBirch(player, "item");
									}
								} else {
									event.setCancelled(true);
									player.sendMessage(ChatColor.RED + "[!!] You can't shoot birch arrows in a protected region!");
								}
							} else {
								Error.noBirch(player, "permissions");
							}
						}
					}
					/*
					 * SPRUCE
					 */
					else if (name.equalsIgnoreCase(Methods.setColours(plugin.getConfig().getString("spruce.name"))) && plugin.isActivated(player)) {
						removeSwap(player.getUniqueId().toString());
						if (!(canSpawnSpruce.contains(player.getUniqueId().toString()))) {
							if (player.hasPermission("dirtyarrows.spruce") && plugin.getConfig().getBoolean("spruce.enabled")) {
								if (plugin.rm.isWithinARegionMargin(player.getLocation(), 1) == null) {
									if (player.getGameMode() == GameMode.CREATIVE) {
										canSpawnSpruce.add(player.getUniqueId().toString());
										return;
									}
									if (player.getInventory().containsAtLeast(new ItemStack(Material.SAPLING, (short) 1, (short) 1), 1) &&
											player.getInventory().containsAtLeast(new ItemStack(Material.INK_SACK, (short) 1, (short) 15), 1)) {
										canSpawnSpruce.add(player.getUniqueId().toString());
									} else {
										Error.noSpruce(player, "item");
									}
								} else {
									event.setCancelled(true);
									player.sendMessage(ChatColor.RED + "[!!] You can't shoot spruce arrows in a protected region!");
								}
							} else {
								Error.noSpruce(player, "permissions");
							}
						}
					}
					/*
					 * JUNGLE
					 */
					else if (name.equalsIgnoreCase(Methods.setColours(plugin.getConfig().getString("jungle.name"))) && plugin.isActivated(player)) {
						removeSwap(player.getUniqueId().toString());
						if (!(canSpawnJungle.contains(player.getUniqueId().toString()))) {
							if (player.hasPermission("dirtyarrows.jungle") && plugin.getConfig().getBoolean("jungle.enabled")) {
								if (plugin.rm.isWithinARegionMargin(player.getLocation(), 1) == null) {
									if (player.getGameMode() == GameMode.CREATIVE) {
										canSpawnJungle.add(player.getUniqueId().toString());
										return;
									}
									if (player.getInventory().containsAtLeast(new ItemStack(Material.SAPLING, (short) 1, (short) 3), 1) &&
											player.getInventory().containsAtLeast(new ItemStack(Material.INK_SACK, (short) 1, (short) 15), 1)) {
										canSpawnJungle.add(player.getUniqueId().toString());
									} else {
										Error.noJungle(player, "item");
									}
								} else {
									event.setCancelled(true);
									player.sendMessage(ChatColor.RED + "[!!] You can't shoot jungle arrows in a protected region!");
								}
							} else {
								Error.noJungle(player, "permissions");
							}
						}
					}
					/*
					 * BATTY
					 */
					else if (name.equalsIgnoreCase(Methods.setColours(plugin.getConfig().getString("batty.name"))) && plugin.isActivated(player)) {
						removeSwap(player.getUniqueId().toString());
						if (!(canSpawnBats.contains(player.getUniqueId().toString()))) {
							if (player.hasPermission("dirtyarrows.batty") && plugin.getConfig().getBoolean("batty.enabled")) {
								if (plugin.rm.isWithinARegionMargin(player.getLocation(), 1) == null) {
									if (player.getGameMode() == GameMode.CREATIVE) {
										canSpawnBats.add(player.getUniqueId().toString());
										return;
									}
									if (player.getInventory().contains(Material.ROTTEN_FLESH, 3)) {
										canSpawnBats.add(player.getUniqueId().toString());
									} else {
										Error.noBatty(player, "item");
									}
								} else {
									event.setCancelled(true);
									player.sendMessage(ChatColor.RED + "[!!] You can't shoot bat arrows in protected regions!");
								}
							} else {
								Error.noBatty(player, "permissions");
							}
						}
					}
					/*
					 * NUCLEAR
					 */
					else if (name.equalsIgnoreCase(Methods.setColours(plugin.getConfig().getString("nuclear.name"))) && plugin.isActivated(player)) {
						removeSwap(player.getUniqueId().toString());
						if (!(canNuke.contains(player.getUniqueId().toString()))) {
							if (player.hasPermission("dirtyarrows.nuclear") && plugin.getConfig().getBoolean("nuclear.enabled")) {
								if (plugin.rm.isWithinARegionMargin(player.getLocation(), 1) == null) {
									if (player.getGameMode() == GameMode.CREATIVE) {
										canNuke.add(player.getUniqueId().toString());
										plugin.particleExploding.add(event.getEntity());
										return;
									}
									if (player.getInventory().contains(Material.TNT, 64)) {
										plugin.particleExploding.add(event.getEntity());
										canNuke.add(player.getUniqueId().toString());
									} else {
										Error.noNuclear(player, "item");
									}
								} else {
									event.setCancelled(true);
									player.sendMessage(ChatColor.RED + "[!!] You can't shoot nukes in protected regions!");
								}
							} else {
								Error.noNuclear(player, "permissions");
							}
						}
					}
					/*
					 * ENLIGHTENED
					 */
					else if (name.equalsIgnoreCase(Methods.setColours(plugin.getConfig().getString("enlightened.name"))) && plugin.isActivated(player)) {
						removeSwap(player.getUniqueId().toString());
						if (!(canLight.contains(player.getUniqueId().toString()))) {
							if (player.hasPermission("dirtyarrows.enlightened") && plugin.getConfig().getBoolean("enlightened.enabled")) {
								if (plugin.rm.isWithinARegionMargin(player.getLocation(), 1) == null) {
									if (player.getGameMode() == GameMode.CREATIVE) {
										canLight.add(player.getUniqueId().toString());
										return;
									}
									if (player.getInventory().contains(Material.TORCH, 1)) {
										canLight.add(player.getUniqueId().toString());
									} else {
										Error.noEnlightened(player, "item");
									}
								} else {
									event.setCancelled(true);
									player.sendMessage(ChatColor.RED + "[!!] You can't shoot torches in protected regions!");
								}
							} else {
								Error.noEnlightened(player, "permissions");
							}
						}
					}
					/*
					 * RANGED
					 */
					else if (name.equalsIgnoreCase(Methods.setColours(plugin.getConfig().getString("ranged.name"))) && plugin.isActivated(player)) {
						removeSwap(player.getUniqueId().toString());
						if (player.hasPermission("dirtyarrows.ranged") && plugin.getConfig().getBoolean("ranged.enabled")) {
							Arrow arrow = (Arrow) event.getEntity();
							arrow.setVelocity(player.getEyeLocation().getDirection().multiply(5));
						} else {
							Error.noRanged(player);
						}
					}
					/*
					 * POISONOUS
					 */
					else if (name.equalsIgnoreCase(Methods.setColours(plugin.getConfig().getString("poisonous.name"))) && plugin.isActivated(player)) {
						removeSwap(player.getUniqueId().toString());
						if (!(canPoison.contains(player.getUniqueId().toString()))) {
							if (player.hasPermission("dirtyarrows.poisonous") && plugin.getConfig().getBoolean("poisonous.enabled")) {
								if (player.getGameMode() == GameMode.CREATIVE) {
									canPoison.add(player.getUniqueId().toString());
									return;
								}
								if (player.getInventory().contains(Material.SPIDER_EYE, 1)){
									canPoison.add(player.getUniqueId().toString());
								} else {
									Error.noPoisonous(player, "item");
								}
							} else {
								Error.noPoisonous(player, "permissions");
							}
						}
					}
					/*
					 * DISORIENTING
					 */
					else if (name.equalsIgnoreCase(Methods.setColours(plugin.getConfig().getString("disorienting.name"))) && plugin.isActivated(player)) {
						removeSwap(player.getUniqueId().toString());
						if (!(canDisorient.contains(player.getUniqueId().toString()))) {
							if (player.hasPermission("dirtyarrows.disorienting") && plugin.getConfig().getBoolean("disorienting.enabled")) {
								canDisorient.add(player.getUniqueId().toString());
							} else {
								Error.noDisorienting(player);
							}
						}
					}
					/*
					 * STARVATION
					 */
					else if (name.equalsIgnoreCase(Methods.setColours(plugin.getConfig().getString("starvation.name"))) && plugin.isActivated(player)) {
						removeSwap(player.getUniqueId().toString());
						if (!(canFood.contains(player.getUniqueId().toString()))) {
							if (player.hasPermission("dirtyarrows.starvation") && plugin.getConfig().getBoolean("starvation.enabled")) {
								canFood.add(player.getUniqueId().toString());
							} else {
								Error.noDisorienting(player);
							}
						}
					}
					/*
					 * DRAINING
					 */
					else if (name.equalsIgnoreCase(Methods.setColours(plugin.getConfig().getString("draining.name"))) && plugin.isActivated(player)) {
						removeSwap(player.getUniqueId().toString());
						if (!(canDrain.contains(player.getUniqueId().toString()))) {
							if (player.hasPermission("dirtyarrows.draining") && plugin.getConfig().getBoolean("draining.enabled")) {
								canDrain.add(player.getUniqueId().toString());
							} else {
								Error.noDraining(player);
							}
						}
					}
					/*
					 * WOODMAN
					 */
					else if (name.equalsIgnoreCase(Methods.setColours(plugin.getConfig().getString("woodman.name"))) && plugin.isActivated(player)) {
						removeSwap(player.getUniqueId().toString());
						if (!(canwoodman.contains(player.getUniqueId().toString()))) {
							if (player.hasPermission("dirtyarrows.woodman") && plugin.getConfig().getBoolean("woodman.enabled")) {
								if (plugin.rm.isWithinARegionMargin(player.getLocation(), 1) == null) {
									canwoodman.add(player.getUniqueId().toString());
								} else {
									event.setCancelled(true);
									player.sendMessage(ChatColor.RED + "[!!] You can't tear down trees from protected regions!");
								}
							} else {
								Error.noWoodsman(player);
							}
						}
					}
					/*
					 * SWAP
					 */
					else if (name.equalsIgnoreCase(Methods.setColours(plugin.getConfig().getString("swap.name"))) && plugin.isActivated(player)) {
						removeSwap(player.getUniqueId().toString());
						if (!(canSwap.contains(player.getUniqueId().toString()))) {
							if (player.hasPermission("dirtyarrows.swap") && plugin.getConfig().getBoolean("swap.enabled")) {
								canSwap.add(player.getUniqueId().toString());
							} else {
								Error.noDisorienting(player);
							}
						}
					}
					/*
					 * FLINTAND
					 */
					else if (name.equalsIgnoreCase(Methods.setColours(plugin.getConfig().getString("flintand.name"))) && plugin.isActivated(player)) {
						removeSwap(player.getUniqueId().toString());
						if (!(canFlint.contains(player.getUniqueId().toString()))) {
							if (player.hasPermission("dirtyarrows.flintand") && plugin.getConfig().getBoolean("flintand.enabled")) {
								if (plugin.rm.isWithinARegionMargin(player.getLocation(), 1) == null) {
									if (player.getGameMode() == GameMode.CREATIVE) {
										canFlint.add(player.getUniqueId().toString());
										plugin.particleFire.add(event.getEntity());
										event.getEntity().setFireTicks(1300);
										return;
									}
									if (player.getInventory().contains(Material.FLINT_AND_STEEL, 1)){
										canFlint.add(player.getUniqueId().toString());
										plugin.particleFire.add(event.getEntity());
										event.getEntity().setFireTicks(1300);
									} else {
										Error.noFlintAnd(player, "item");
									}
								} else {
									event.setCancelled(true);
									player.sendMessage(ChatColor.RED + "[!!] You can't shoot fire in protected regions!");
								}
							} else {
								Error.noFlintAnd(player, "permissions");
							}
						}
					}
					/*
					 * DISARMING
					 */
					else if (name.equalsIgnoreCase(Methods.setColours(plugin.getConfig().getString("disarming.name"))) && plugin.isActivated(player)) {
						removeSwap(player.getUniqueId().toString());
						if (!(canDisarm.contains(player.getUniqueId().toString()))) {
							if (player.hasPermission("dirtyarrows.disarming") && plugin.getConfig().getBoolean("disarming.enabled")) {
								if (player.getGameMode() == GameMode.CREATIVE) {
									canDisarm.add(player.getUniqueId().toString());
									return;
								}
							} else {
								Error.noDisarm(player);
							}
						}
					}
					/*
					 * WITHER
					 */
					else if (name.equalsIgnoreCase(Methods.setColours(plugin.getConfig().getString("wither.name"))) && plugin.isActivated(player)) {
						removeSwap(player.getUniqueId().toString());
						if (!(canBrick.contains(player.getUniqueId().toString()))) {
							if (player.hasPermission("dirtyarrows.wither") && plugin.getConfig().getBoolean("wither.enabled")) {
								if (plugin.rm.isWithinARegionMargin(player.getLocation(), 1) == null) {
									if (player.getGameMode() != GameMode.CREATIVE) {
										if (player.getInventory().contains(Material.SOUL_SAND, 3)){
											player.getInventory().removeItem(new ItemStack(Material.SOUL_SAND, 3));
										} else {
											Error.noWither(player, "item");
											return;
										}
									}
									Projectile p = event.getEntity();
									p.remove();
					                player.launchProjectile(WitherSkull.class).setVelocity(p.getVelocity().multiply(0.4));
								} else {
									event.setCancelled(true);
									player.sendMessage(ChatColor.RED + "[!!] You can't shoot wither skulls in protected regions!");
								}
							} else {
								Error.noWither(player, "permissions");
							}
						}
					}
					/*
					 * FIREY
					 */
					else if (name.equalsIgnoreCase(Methods.setColours(plugin.getConfig().getString("firey.name"))) && plugin.isActivated(player)) {
						removeSwap(player.getUniqueId().toString());
						if (!(canBrick.contains(player.getUniqueId().toString()))) {
							if (player.hasPermission("dirtyarrows.firey") && plugin.getConfig().getBoolean("firey.enabled")) {
								if (plugin.rm.isWithinARegionMargin(player.getLocation(), 1) == null) {
									if (player.getGameMode() != GameMode.CREATIVE) {
										if (player.getInventory().contains(Material.FIREBALL, 1)){
											player.getInventory().removeItem(new ItemStack(Material.FIREBALL, 1));
										} else {
											Error.noFirey(player, "item");
											return;
										}
									}
									Projectile p = event.getEntity();
									p.remove();
						            player.launchProjectile(Fireball.class).setVelocity(p.getVelocity().multiply(1.2));
								} else {
									event.setCancelled(true);
									player.sendMessage(ChatColor.RED + "[!!] You can't shoot fireballs in protected regions!");
								}
							} else {
								Error.noFirey(player, "permissions");
							}
						}
					}
					/*
					 * SLOW
					 */
					else if (name.equalsIgnoreCase(Methods.setColours(plugin.getConfig().getString("slow.name"))) && plugin.isActivated(player)) {
						removeSwap(player.getUniqueId().toString());
						if (player.hasPermission("dirtyarrows.slow") && plugin.getConfig().getBoolean("slow.enabled")) {
							if (event.getEntity() instanceof Projectile) {
								Projectile proj = event.getEntity();
								proj.setVelocity(proj.getVelocity().multiply(0.12));
								plugin.slow.add(proj);
								plugin.slowVec.add(proj.getVelocity());
							}
						} else {
							Error.noSlow(player);
						}
					}
					/*
					 * LEVEL
					 */
					else if (name.equalsIgnoreCase(Methods.setColours(plugin.getConfig().getString("level.name"))) && plugin.isActivated(player)) {
						removeSwap(player.getUniqueId().toString());
						if (!(canLevel.contains(player.getUniqueId().toString()))) {
							if (player.hasPermission("dirtyarrows.level") && plugin.getConfig().getBoolean("level.enabled")) {
								canLevel.add(player.getUniqueId().toString());
							} else {
								Error.noLevel(player);
							}
						}
					}
					/*
					 * UNDEAD
					 */
					else if (name.equalsIgnoreCase(Methods.setColours(plugin.getConfig().getString("undead.name"))) && plugin.isActivated(player)) {
						removeSwap(player.getUniqueId().toString());
						if (!(canSwarm.contains(player.getUniqueId().toString()))) {
							if (player.hasPermission("dirtyarrows.undead") && plugin.getConfig().getBoolean("undead.enabled")) {
								if (plugin.rm.isWithinARegionMargin(player.getLocation(), 1) == null) {
									if (player.getGameMode() != GameMode.CREATIVE) {
										if (player.getInventory().contains(Material.ROTTEN_FLESH, 64)){
											player.getInventory().removeItem(new ItemStack(Material.ROTTEN_FLESH, 64));
										} else {
											Error.noUndead(player, "item");
											return;
										}
									}
								} else {
									event.setCancelled(true);
									player.sendMessage(ChatColor.RED + "[!!] You can't shoot the undead in protected regions!");
								}
								canSwarm.add(player.getUniqueId().toString());
							} else {
								Error.noUndead(player, "permissions");
							}
						}
					}
					/*
					 * MULTI
					 */
					else if (name.equalsIgnoreCase(Methods.setColours(plugin.getConfig().getString("multi.name"))) && plugin.isActivated(player)) {
						removeSwap(player.getUniqueId().toString());
						if (player.hasPermission("dirtyarrows.multi")) {
							if (player.getInventory().contains(Material.ARROW, 8) ||
									player.getItemInHand().containsEnchantment(Enchantment.ARROW_INFINITE) ||
									player.getGameMode() == GameMode.CREATIVE) {
								if (player.getGameMode() != GameMode.CREATIVE) {
									if (!(player.getItemInHand().containsEnchantment(Enchantment.ARROW_INFINITE))) {
										player.getInventory().removeItem(new ItemStack(Material.ARROW, 7));
									}
								}
								Projectile proj = event.getEntity();
								for (int i = 1; i <= 7; i++) {
									Arrow arrow = player.getWorld().spawn(proj.getLocation(), Arrow.class);
									powerArrows.add(arrow);
									arrow.setShooter(player);
									arrow.setVelocity(proj.getVelocity()
											.setX(proj.getVelocity().getX() + ran.nextDouble() / 2 - 0.254)
											.setY(proj.getVelocity().getY() + ran.nextDouble() / 2 - 0.254)
											.setZ(proj.getVelocity().getZ() + ran.nextDouble() / 2 - 0.254));
									if (player.getItemInHand().containsEnchantment(Enchantment.ARROW_FIRE)) {
										arrow.setFireTicks(1200);
									}
								}
							} else {
								Error.noMulti(player, "notenough");
							}
						} else {
							Error.noMulti(player, "permissions");
						}
					}
					/*
					 * BOMB
					 */
					else if (name.equalsIgnoreCase(Methods.setColours(plugin.getConfig().getString("bomb.name"))) && plugin.isActivated(player)) {
						removeSwap(player.getUniqueId().toString());
						if (!(canBomb.contains(player.getUniqueId().toString()))) {
							if (player.hasPermission("dirtyarrows.bomb") && plugin.getConfig().getBoolean("bomb.enabled")) {
								if (plugin.rm.isWithinARegionMargin(player.getLocation(), 1) == null) {
									if (player.getGameMode() != GameMode.CREATIVE) {
										if (player.getInventory().contains(Material.TNT, 3)){
											player.getInventory().removeItem(new ItemStack(Material.TNT, 3));
										} else {
											Error.noBomb(player, "item");
											return;
										}
									}
									canBomb.add(player.getUniqueId().toString());
									plugin.particleExploding.add(event.getEntity());
								} else {
									event.setCancelled(true);
									player.sendMessage(ChatColor.RED + "[!!] You can't shoot bombs in protected regions!");
								}
							} else {
								Error.noLevel(player);
							}
						}
					}
					/*
					 * CLUSTER
					 */
					else if (name.equalsIgnoreCase(Methods.setColours(plugin.getConfig().getString("cluster.name"))) && plugin.isActivated(player)) {
						removeSwap(player.getUniqueId().toString());
						if (!(canBomb.contains(player.getUniqueId().toString()))) {
							if (player.hasPermission("dirtyarrows.cluster") && plugin.getConfig().getBoolean("cluster.enabled")) {
								if (plugin.rm.isWithinARegionMargin(player.getLocation(), 24) == null) {
									if (player.getGameMode() != GameMode.CREATIVE) {
										if (player.getInventory().contains(Material.TNT, 5)){
											player.getInventory().removeItem(new ItemStack(Material.TNT, 5));
										} else {
											Error.noSulphur(player, "item");
											return;
										}
									}
									canCluster.add(player.getUniqueId().toString());
									plugin.particleExploding.add(event.getEntity());
								} else {
									event.setCancelled(true);
									player.sendMessage(ChatColor.RED + "[!!] You can't shoot bombs in protected regions!");
								}
							} else {
								Error.noLevel(player);
							}
						}
					}
					/*
					 * DROP
					 */
					else if (name.equalsIgnoreCase(Methods.setColours(plugin.getConfig().getString("drop.name"))) && plugin.isActivated(player)) {
						removeSwap(player.getUniqueId().toString());
						if (!(canDrop.contains(player.getUniqueId().toString()))) {
							if (player.hasPermission("dirtyarrows.drop") && plugin.getConfig().getBoolean("drop.enabled")) {
								canDrop.add(player.getUniqueId().toString());
							} else {
								Error.noDisorienting(player);
							}
						}
					}
					/*
					 * AIRSTRIKE
					 */
					else if (name.equalsIgnoreCase(Methods.setColours(plugin.getConfig().getString("airstrike.name"))) && plugin.isActivated(player)) {
						removeSwap(player.getUniqueId().toString());
						if (player.hasPermission("dirtyarrows.airstrike") && plugin.getConfig().getBoolean("airstrike.enabled")) {
							if (plugin.rm.isWithinARegionMargin(player.getLocation(), 1) == null) {
								if (player.getGameMode() != GameMode.CREATIVE) {
									if (player.getInventory().contains(Material.TNT, 1)){
										plugin.airstrike.add(event.getEntity());
										plugin.particleExploding.add(event.getEntity());
									} else {
										Error.noAirstrike(player, "item");
										return;
									}
								} else {
									plugin.airstrike.add(event.getEntity());
									plugin.particleExploding.add(event.getEntity());
								}
							} else {
								event.setCancelled(true);
								player.sendMessage(ChatColor.RED + "[!!] You can't shoot airstrikes in protected regions!");
							}
						} else {
							Error.noAirstrike(player, "permissions");
						}
					}
					/*
					 * AIRSHIP
					 */
					else if (name.equalsIgnoreCase(Methods.setColours(plugin.getConfig().getString("airship.name"))) && plugin.isActivated(player)) {
						removeSwap(player.getUniqueId().toString());
						if (player.hasPermission("dirtyarrows.airship") && plugin.getConfig().getBoolean("airship.enabled")) {
							if (player.getGameMode() != GameMode.CREATIVE) {
								if (player.getInventory().contains(Material.FEATHER, 2)){
									plugin.airship.add(event.getEntity());
									plugin.noFallDamage.add(player.getUniqueId());
									player.getInventory().removeItem(new ItemStack(Material.FEATHER, 2));
								} else {
									Error.noAirship(player, "item");
									return;
								}
							}
							else {
								plugin.airship.add(event.getEntity());
							}
						}
						else {
							Error.noAirship(player, "permissions");
						}
					}
					/*
					 * MAGMATIC
					 */
					else if (name.equalsIgnoreCase(Methods.setColours(plugin.getConfig().getString("magmatic.name"))) && plugin.isActivated(player)) {
						removeSwap(player.getUniqueId().toString());
						if (player.hasPermission("dirtyarrows.magmatic") && plugin.getConfig().getBoolean("magmatic.enabled")) {
							if (plugin.rm.isWithinARegionMargin(player.getLocation(), 1) == null) {
								if (player.getGameMode() != GameMode.CREATIVE) {
									if (player.getInventory().contains(Material.LAVA_BUCKET, 1)){
										player.getInventory().remove(new ItemStack(Material.LAVA_BUCKET, 1));
										player.getInventory().addItem(new ItemStack(Material.BUCKET, 1));
										FallingBlock fb = (FallingBlock) player.getWorld().spawnFallingBlock(
												player.getLocation().add(0, 1, 0), Material.LAVA, (byte) 0);
										fb.setVelocity(event.getEntity().getVelocity());
										fb.setDropItem(false);
										plugin.particleLava.add(fb);
										event.getEntity().remove();
									} else {
										Error.noMagmatic(player, "item");
										return;
									}
								} else {
									FallingBlock fb = (FallingBlock) player.getWorld().spawnFallingBlock(
											player.getLocation().add(0, 1, 0), Material.LAVA, (byte) 0);
									fb.setVelocity(event.getEntity().getVelocity());
									fb.setDropItem(false);
									plugin.particleLava.add(fb);
									event.getEntity().remove();
								}
							} else {
								event.setCancelled(true);
								player.sendMessage(ChatColor.RED + "[!!] You can't shoot lava in protected regions!");
							}
						} else {
							Error.noMagmatic(player, "permissions");
						}
					}
					/*
					 * AQUATIC
					 */
					else if (name.equalsIgnoreCase(Methods.setColours(plugin.getConfig().getString("aquatic.name"))) && plugin.isActivated(player)) {
						removeSwap(player.getUniqueId().toString());
						if (player.hasPermission("dirtyarrows.aquatic") && plugin.getConfig().getBoolean("aquatic.enabled")) {
							if (plugin.rm.isWithinARegionMargin(player.getLocation(), 1) == null) {
								if (player.getGameMode() != GameMode.CREATIVE) {
									if (player.getInventory().contains(Material.WATER_BUCKET, 1)){
										player.getInventory().remove(new ItemStack(Material.WATER_BUCKET, 1));
										player.getInventory().addItem(new ItemStack(Material.BUCKET, 1));
										FallingBlock fb = (FallingBlock) player.getWorld().spawnFallingBlock(
												player.getLocation().add(0, 1, 0), Material.WATER, (byte) 0);
										fb.setVelocity(event.getEntity().getVelocity());
										fb.setDropItem(false);
										plugin.particleWater.add(fb);
										event.getEntity().remove();
									} else {
										Error.noAquatic(player, "item");
										return;
									}
								} else {
									FallingBlock fb = (FallingBlock) player.getWorld().spawnFallingBlock(
											player.getLocation().add(0, 1, 0), Material.WATER, (byte) 0);
									fb.setVelocity(event.getEntity().getVelocity());
									fb.setDropItem(false);
									plugin.particleWater.add(fb);
									event.getEntity().remove();
								}
							} else {
								event.setCancelled(true);
								player.sendMessage(ChatColor.RED + "[!!] You can't shoot water in protected regions!");
							}
						}
						
						else {
							Error.noAquatic(player, "permissions");
						}
					}
					/*
					 * IRON
					 */
					else if (name.equalsIgnoreCase(Methods.setColours(plugin.getConfig().getString("iron.name"))) && plugin.isActivated(player)) {
						removeSwap(player.getUniqueId().toString());
						if (player.hasPermission("dirtyarrows.iron") && plugin.getConfig().getBoolean("iron.enabled")) {
							if (plugin.rm.isWithinARegionMargin(player.getLocation(), 1) == null) {
								if (player.getGameMode() != GameMode.CREATIVE) {
									if (player.getInventory().contains(Material.ANVIL, 1)){
										player.getInventory().removeItem(new ItemStack(Material.ANVIL, 1));
										FallingBlock fb = (FallingBlock) player.getWorld().spawnFallingBlock(
												player.getLocation().add(0, 1, 0), Material.ANVIL, (byte) 0);
										fb.setVelocity(event.getEntity().getVelocity());
										fb.setDropItem(false);
										plugin.anvils.put(fb, 8 * 4);
										event.getEntity().remove();
									} else {
										Error.noIron(player, "item");
										return;
									}
								} else {
									FallingBlock fb = (FallingBlock) player.getWorld().spawnFallingBlock(
											player.getLocation().add(0, 1, 0), Material.ANVIL, (byte) 0);
									fb.setVelocity(event.getEntity().getVelocity());
									fb.setDropItem(false);
									plugin.anvils.put(fb, 8 * 4);
									event.getEntity().remove();
								}
							} else {
								event.setCancelled(true);
								player.sendMessage(ChatColor.RED + "[!!] You can't shoot iron in protected regions!");
							}
						}
						
						else {
							Error.noAquatic(player, "permissions");
						}
					}
					/*
					 * PULL
					 */
					else if (name.equalsIgnoreCase(Methods.setColours(plugin.getConfig().getString("pull.name"))) && plugin.isActivated(player)) {
						removeSwap(player.getUniqueId().toString());
						if (!(canPull.contains(player.getUniqueId().toString()))) {
							if (player.hasPermission("dirtyarrows.pull") && plugin.getConfig().getBoolean("pull.enabled")) {
								canPull.add(player.getUniqueId().toString());
							} else {
								Error.noPull(player);
							}
						}
					}
					/*
					 * 360
					 */
					else if (name.equalsIgnoreCase(Methods.setColours(plugin.getConfig().getString("round.name"))) && plugin.isActivated(player)) {
						removeSwap(player.getUniqueId().toString());
						if (player.hasPermission("dirtyarrows.round") && plugin.getConfig().getBoolean("round.enabled")) {
							final float arrows = 30;
							boolean infinity = false;
							boolean fire = false;
							
							ItemStack item = player.getItemInHand();
							if (item.hasItemMeta()) {
								ItemMeta im = item.getItemMeta();
								if (im.getEnchants().containsKey(Enchantment.ARROW_INFINITE)) {
									infinity = true;
								}
								if (im.getEnchants().containsKey(Enchantment.ARROW_FIRE)) {
									fire = true;
								}
							}
							
							final boolean hasInfinity = infinity;
							final boolean hasFire = fire;
							
							round.add(player.getUniqueId());
							for (float i = 0; i < arrows; i++) {
								if (player.getGameMode() == GameMode.CREATIVE) {
									Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
										public void run() {
											Location loc = player.getLocation().clone(); 
											loc.setYaw(player.getLocation().getYaw() + (360f / arrows));
											player.teleport(loc);
											Arrow a = player.launchProjectile(Arrow.class);
											a.setVelocity(a.getVelocity().multiply(1.5));
											if (hasInfinity) {
												EntityListener.deadArrows.add(a);
											}
											if (hasFire) {
												a.setFireTicks(1000);
											}
										}
									}, 1L * (long)i);
								}
								else if (player.getInventory().contains(Material.ARROW, 1)) {
									Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
										public void run() {
											if (!hasInfinity) player.getInventory().removeItem(new ItemStack(Material.ARROW, 1));
											Location loc = player.getLocation().clone(); 
											loc.setYaw(player.getLocation().getYaw() + (360f / arrows));
											player.teleport(loc);
											Arrow a = player.launchProjectile(Arrow.class);
											a.setVelocity(a.getVelocity().multiply(1.5));
											if (hasInfinity) {
												EntityListener.deadArrows.add(a);
											}
											if (hasFire) {
												a.setFireTicks(1000);
											}
										}
									}, 1L * (long)i);
								}
							}
							
							Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
								public void run() {
									round.remove(player.getUniqueId());
								}
							}, 1L * (long)arrows);
						} else {
							Error.noPull(player);
						}
					}
					/*
					 * CURSE
					 */
					else if (name.equalsIgnoreCase(Methods.setColours(plugin.getConfig().getString("curse.name"))) && plugin.isActivated(player)) {
						removeSwap(player.getUniqueId().toString());
						if (player.hasPermission("dirtyarrows.curse") && plugin.getConfig().getBoolean("curse.enabled")) {
							if (player.getGameMode() != GameMode.CREATIVE) {
								if (player.getInventory().contains(Material.FERMENTED_SPIDER_EYE, 1)){
									canCurse.add(event.getEntity().getEntityId());
									player.getInventory().removeItem(new ItemStack(Material.FERMENTED_SPIDER_EYE, 1));
								} else {
									Error.noCurse(player, "item");
									return;
								}
							}
							else {
								canCurse.add(event.getEntity().getEntityId());
							}
						} else {
							Error.noPull(player);
						}
					}
					/*
					 * FROZEN
					 */
					else if (name.equalsIgnoreCase(Methods.setColours(plugin.getConfig().getString("frozen.name"))) && plugin.isActivated(player)) {
						removeSwap(player.getUniqueId().toString());
						if (player.hasPermission("dirtyarrows.frozen") && plugin.getConfig().getBoolean("frozen.enabled")) {
							if (player.getGameMode() != GameMode.CREATIVE) {
								if (player.getInventory().contains(Material.SNOW_BALL, 1)){
									plugin.ice.add(event.getEntity().getEntityId());
									plugin.iceParticle.add(event.getEntity());
									player.getInventory().removeItem(new ItemStack(Material.SNOW_BALL, 1));
								} else {
									Error.noFrozen(player, "item");
									return;
								}
							}
							else {
								plugin.ice.add(event.getEntity().getEntityId());
								plugin.iceParticle.add(event.getEntity());
							}
						} else {
							Error.noPull(player);
						}
					}
					/*
					 * PARALYZE
					 */
					else if (name.equalsIgnoreCase(Methods.setColours(plugin.getConfig().getString("paralyze.name"))) && plugin.isActivated(player)) {
						removeSwap(player.getUniqueId().toString());
						if (!(canParalyze.contains(player.getUniqueId().toString()))) {
							if (player.hasPermission("dirtyarrows.paralyze") && plugin.getConfig().getBoolean("paralyze.enabled")) {
								if (player.getGameMode() == GameMode.CREATIVE) {
									canParalyze.add(player.getUniqueId().toString());
									return;
								}
								if (player.getInventory().contains(Material.NETHER_STALK, 1)){
									canParalyze.add(player.getUniqueId().toString());
								} else {
									Error.noParalyze(player, "item");
								}
							} else {
								Error.noParalyze(player, "permissions");
							}
						}
					}
					/*
					 * ACACIA
					 */
					else if (name.equalsIgnoreCase(Methods.setColours(plugin.getConfig().getString("acacia.name"))) && plugin.isActivated(player)) {
						removeSwap(player.getUniqueId().toString());
						if (!(canSpawnAcacia.contains(player.getUniqueId().toString()))) {
							if (player.hasPermission("dirtyarrows.acacia") && plugin.getConfig().getBoolean("acacia.enabled")) {
								if (plugin.rm.isWithinARegionMargin(player.getLocation(), 1) == null) {
									if (player.getGameMode() == GameMode.CREATIVE) {
										canSpawnAcacia.add(player.getUniqueId().toString());
										return;
									}
									if (player.getInventory().containsAtLeast(new ItemStack(Material.SAPLING, (short) 1, (short) 4), 1) &&
											player.getInventory().containsAtLeast(new ItemStack(Material.INK_SACK, (short) 1, (short) 15), 1)) {
										canSpawnAcacia.add(player.getUniqueId().toString());
									} else {
										Error.noAcacia(player, "item");
									}
								} else {
									event.setCancelled(true);
									player.sendMessage(ChatColor.RED + "[!!] You can't shoot acacia arrows in a protected region!");
								}
							} else {
								Error.noAcacia(player, "permissions");
							}
						}
					}
					/*
					 * DARKOAK
					 */
					else if (name.equalsIgnoreCase(Methods.setColours(plugin.getConfig().getString("darkoak.name"))) && plugin.isActivated(player)) {
						removeSwap(player.getUniqueId().toString());
						if (!(canSpawnDarkOak.contains(player.getUniqueId().toString()))) {
							if (player.hasPermission("dirtyarrows.darkoak") && plugin.getConfig().getBoolean("darkoak.enabled")) {
								if (plugin.rm.isWithinARegionMargin(player.getLocation(), 1) == null) {
									if (player.getGameMode() == GameMode.CREATIVE) {
										canSpawnDarkOak.add(player.getUniqueId().toString());
										return;
									}
									if (player.getInventory().containsAtLeast(new ItemStack(Material.SAPLING, (short) 1, (short) 5), 4) &&
											player.getInventory().containsAtLeast(new ItemStack(Material.INK_SACK, (short) 1, (short) 15), 1)) {
										canSpawnDarkOak.add(player.getUniqueId().toString());
									} else {
										Error.noDarkOak(player, "item");
									}
								} else {
									event.setCancelled(true);
									player.sendMessage(ChatColor.RED + "[!!] You can't shoot dark oak arrows in a protected region!");
								}
							} else {
								Error.noDarkOak(player, "permissions");
							}
						}
					} 
				}
			}
		}
	}
	
	private final List<Material> invalidSnow = Arrays.asList(new Material[] {
			Material.LONG_GRASS, Material.YELLOW_FLOWER, Material.RED_ROSE, Material.DEAD_BUSH,
			Material.TORCH, Material.REDSTONE_TORCH_ON, Material.REDSTONE_TORCH_ON, Material.TRAP_DOOR,
			Material.REDSTONE, Material.IRON_TRAPDOOR, Material.SAPLING, Material.RED_MUSHROOM,
			Material.BROWN_MUSHROOM, Material.CARPET, Material.STRING, Material.CROPS, Material.BED_BLOCK,
			Material.MELON_STEM, Material.PUMPKIN_STEM, Material.STONE_PLATE, Material.WOOD_PLATE,
			Material.IRON_PLATE, Material.GOLD_PLATE, Material.VINE, Material.LAVA, Material.RAILS,
			Material.ACTIVATOR_RAIL, Material.POWERED_RAIL, Material.DETECTOR_RAIL, Material.DIODE_BLOCK_ON,
			Material.DIODE_BLOCK_OFF, Material.SNOW, Material.AIR, Material.FLOWER_POT, Material.BANNER,
			Material.DOUBLE_PLANT, Material.FIRE, Material.STATIONARY_LAVA, Material.STATIONARY_WATER,
			Material.WATER
	});
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onProjectileHit(ProjectileHitEvent event) {
		if (event.getEntity() instanceof Arrow) {
			Arrow arrow = (Arrow) event.getEntity();
			
			canCurse.remove(arrow);
			plugin.airstrike.remove(event.getEntity());
			plugin.airship.remove(event.getEntity());
			plugin.particleExploding.remove(event.getEntity());
			plugin.particleFire.remove(event.getEntity());
			
			/*
			 * FROZEN
			 */
			if (plugin.iceParticle.contains(arrow)) {
				plugin.iceParticle.remove(arrow);
				World w = arrow.getWorld();
				int x = (int) arrow.getLocation().getX();
				int y = (int) arrow.getLocation().getY();
				int z = (int) arrow.getLocation().getZ();
				for (int i = -2; i <= 2; i++) {
					for (int j = -2; j <= 2; j++) {
						for (int k = -2; k <= 2; k++) {
							final Location loc = new Location(w, x + i, y + k, z + j);
							if (loc.getBlock().getType() == Material.AIR) {
								Location below = new Location(w, x + i, y + k - 1, z + j);
								if (!invalidSnow.contains(below.getBlock().getType())) {
									if (ran.nextFloat() < 0.74) {
										loc.getBlock().setType(Material.SNOW);
										k = y + 3;
									}
								}
							}
							else if (loc.getBlock().getType() == Material.STATIONARY_WATER) {
								if (ran.nextFloat() < 0.74) {
									loc.getBlock().setType(Material.ICE);
								}
							}
							else if (loc.getBlock().getType() == Material.ICE) {
								if (ran.nextFloat() < 0.18) {
									loc.getBlock().setType(Material.PACKED_ICE);
								}
							}
						}
					}
				}
				
			}
			
			if (arrow.getShooter() instanceof Player) {
				Player player = (Player) arrow.getShooter();
				if (plugin.isActivated(player)) {
					/*
					 * ZOMBIE
					 */
					if (canSwarm.contains(player.getUniqueId().toString())) {
						if (plugin.rm.isWithinARegionMargin(arrow.getLocation(), 7) == null) {
							Swarm.doSwarm(player.getWorld(), arrow.getLocation());
						} else {
							arrow.remove();
							if (player.getGameMode() != GameMode.CREATIVE) {
								player.getInventory().addItem(new ItemStack(Material.ROTTEN_FLESH, 64));
							}
							player.sendMessage(ChatColor.RED + "[!!] Arrows can't summon zombies in protected areas!");
						}
					}
					/*
					 * EXPLODING
					 */
					else if (canExplode.contains(player.getUniqueId().toString())) {
						if (plugin.rm.isWithinARegionMargin(arrow.getLocation(), 5) == null) {
							player.getWorld().createExplosion(arrow.getLocation(), 4F);
							arrow.remove();
							if (player.getGameMode() == GameMode.SURVIVAL)
								player.getInventory().removeItem(new ItemStack(Material.TNT, 1));
							canExplode.remove(player.getUniqueId().toString());
						} else {
							arrow.remove();
							player.sendMessage(ChatColor.RED + "[!!] Arrows can't boom stuff near protected regions!");
						}
					}
					/*
					 * WOODMAN
					 */
					else if (canwoodman.contains(player.getUniqueId().toString())) {
						if (plugin.rm.isWithinARegionMargin(arrow.getLocation(), 4) == null) {
							BlockIterator iterator = new BlockIterator(event.getEntity().getWorld(), event.getEntity().getLocation().toVector(),
				            		event.getEntity().getVelocity().normalize(), 0.0D, 4);
				            Block hitBlock = null;
				            hitBlock = iterator.next();
				            
				            while (iterator.hasNext()) {
				                hitBlock = iterator.next();
				                if (hitBlock.getType() != Material.AIR) {
				                    break;
				                }
				            }
							if (TreeCut.cutDownTree(hitBlock.getLocation(), hitBlock, player)) {
								arrow.remove();
							}
						} else {
							arrow.remove();
							player.sendMessage(ChatColor.RED + "[!!] Arrows can't tear down trees in protected regions!");
						}
			            canwoodman.remove(player.getUniqueId().toString());
					}
					/*
					 * LIGHTNING
					 */
					else if (canStrikeLightning.contains(player.getUniqueId().toString())) {
						if (plugin.rm.isWithinARegionMargin(arrow.getLocation(), 3) == null) {
							player.getWorld().strikeLightning(arrow.getLocation());
							arrow.remove();
							if (player.getGameMode() == GameMode.SURVIVAL)
								player.getInventory().removeItem(new ItemStack(Material.GLOWSTONE_DUST, 1));
							canStrikeLightning.remove(player.getUniqueId().toString());
						} else {
							arrow.remove();
							player.sendMessage(ChatColor.RED + "[!!] Arrows can't summon lightning near protected regions!");
						}
					}
					/*
					 * CLUCKY
					 */
					else if (canCluck.contains(player.getUniqueId().toString())) {
						if (plugin.rm.isWithinAXZMargin(arrow.getLocation(), 2) == null) {
							player.getWorld().spawnEntity(arrow.getLocation(), EntityType.CHICKEN);
							player.playSound(player.getLocation(), Sound.ENTITY_CHICKEN_HURT, 10, 1);
							arrow.remove();
							if (player.getGameMode() == GameMode.SURVIVAL)
								player.getInventory().removeItem(new ItemStack(Material.EGG, 1));
						} else {
							arrow.remove();
							player.sendMessage(ChatColor.RED + "[!!] Arrows can't spawn chicken near protected regions!");
						}
						canCluck.remove(player.getUniqueId().toString());
					}
					/*
					 * ENDER
					 */
					else if (canTeleport.contains(player.getUniqueId().toString())) {
						player.teleport(arrow.getLocation());
						player.playSound(player.getLocation(), Sound.ENTITY_ENDERMEN_TELEPORT, 10, 1);
						arrow.remove();
						if (player.getGameMode() == GameMode.SURVIVAL)
							player.getInventory().removeItem(new ItemStack(Material.ENDER_PEARL, 1));
						canTeleport.remove(player.getUniqueId().toString());
					}
					/*
					 * OAK
					 */
					else if (canSpawnOak.contains(player.getUniqueId().toString())) {
						if (plugin.rm.isWithinARegionMargin(arrow.getLocation(), 5) == null) {
							if (arrow.getWorld().generateTree(arrow.getLocation(), TreeType.TREE)) {
								arrow.getWorld().generateTree(arrow.getLocation(), TreeType.TREE);
								if (player.getGameMode() == GameMode.SURVIVAL) {
									player.getInventory().removeItem(new ItemStack(Material.SAPLING, 1, (short) 0));
									player.getInventory().removeItem(new ItemStack(Material.INK_SACK, 1, (short) 15));
								}
								arrow.remove();
							}
						} else {
							arrow.remove();
							player.sendMessage(ChatColor.RED + "[!!] Arrows can't spawn oaks near protected areas!");
						}
						canSpawnOak.remove(player.getUniqueId().toString());
					}
					/*
					 * BIRCH
					 */
					else if (canSpawnBirch.contains(player.getUniqueId().toString())) {
						if (plugin.rm.isWithinARegionMargin(arrow.getLocation(), 5) == null) {
							if (arrow.getWorld().generateTree(arrow.getLocation(), TreeType.BIRCH)) {
								arrow.getWorld().generateTree(arrow.getLocation(), TreeType.BIRCH);
								if (player.getGameMode() == GameMode.SURVIVAL) {
									player.getInventory().removeItem(new ItemStack(Material.SAPLING, 1, (short) 2));
									player.getInventory().removeItem(new ItemStack(Material.INK_SACK, 1, (short) 15));
								}
								arrow.remove();
							}
						} else {
							arrow.remove();
							player.sendMessage(ChatColor.RED + "[!!] Arrows can't spawn birch trees near protected areas!");
						}
						canSpawnBirch.remove(player.getUniqueId().toString());
					}
					/*
					 * SPRUCE
					 */
					else if (canSpawnSpruce.contains(player.getUniqueId().toString())) {
						if (plugin.rm.isWithinARegionMargin(arrow.getLocation(), 5) == null) {
							if (arrow.getWorld().generateTree(arrow.getLocation(), TreeType.REDWOOD)) {
								arrow.getWorld().generateTree(arrow.getLocation(), TreeType.REDWOOD);
								if (player.getGameMode() == GameMode.SURVIVAL) {
									player.getInventory().removeItem(new ItemStack(Material.SAPLING, 1, (short) 1));
									player.getInventory().removeItem(new ItemStack(Material.INK_SACK, 1, (short) 15));
								}
								arrow.remove();
							}
						} else {
							arrow.remove();
							player.sendMessage(ChatColor.RED + "[!!] Arrows can't spawn spruce trees near protected areas!");
						}
						canSpawnSpruce.remove(player.getUniqueId().toString());
					}
					/*
					 * JUNGLE
					 */
					else if (canSpawnJungle.contains(player.getUniqueId().toString())) {
						if (plugin.rm.isWithinARegionMargin(arrow.getLocation(), 5) == null) {
							if (arrow.getWorld().generateTree(arrow.getLocation(), TreeType.SMALL_JUNGLE)) {
								arrow.getWorld().generateTree(arrow.getLocation(), TreeType.SMALL_JUNGLE);
								if (player.getGameMode() == GameMode.SURVIVAL) {
									player.getInventory().removeItem(new ItemStack(Material.SAPLING, 1, (short) 3));
									player.getInventory().removeItem(new ItemStack(Material.INK_SACK, 1, (short) 15));
								}
								arrow.remove();
							}
						} else {
							arrow.remove();
							player.sendMessage(ChatColor.RED + "[!!] Arrows can't spawn jungle trees near protected areas!");
						}
						canSpawnJungle.remove(player.getUniqueId().toString());
					}
					/*
					 * ACACIA
					 */
					else if (canSpawnAcacia.contains(player.getUniqueId().toString())) {
						if (plugin.rm.isWithinARegionMargin(arrow.getLocation(), 10) == null) {
							if (arrow.getWorld().generateTree(arrow.getLocation(), TreeType.ACACIA)) {
								arrow.getWorld().generateTree(arrow.getLocation(), TreeType.ACACIA);
								if (player.getGameMode() == GameMode.SURVIVAL) {
									player.getInventory().removeItem(new ItemStack(Material.SAPLING, 1, (short) 4));
									player.getInventory().removeItem(new ItemStack(Material.INK_SACK, 1, (short) 15));
								}
								arrow.remove();
							}
						} else {
							arrow.remove();
							player.sendMessage(ChatColor.RED + "[!!] Arrows can't spawn acacias near protected areas!");
						}
						canSpawnAcacia.remove(player.getUniqueId().toString());
					}
					/*
					 * DARKOAK
					 */
					else if (canSpawnDarkOak.contains(player.getUniqueId().toString())) {
						if (plugin.rm.isWithinARegionMargin(arrow.getLocation(), 10) == null) {
							if (arrow.getWorld().generateTree(arrow.getLocation(), TreeType.DARK_OAK)) {
								arrow.getWorld().generateTree(arrow.getLocation(), TreeType.DARK_OAK);
								if (player.getGameMode() == GameMode.SURVIVAL) {
									player.getInventory().removeItem(new ItemStack(Material.SAPLING, 4, (short) 5));
									player.getInventory().removeItem(new ItemStack(Material.INK_SACK, 1, (short) 15));
								}
								arrow.remove();
							}
						} else {
							arrow.remove();
							player.sendMessage(ChatColor.RED + "[!!] Arrows can't spawn dark oaks near protected areas!");
						}
						canSpawnDarkOak.remove(player.getUniqueId().toString());
					}
					/*
					 * BATTY
					 */
					else if (canSpawnBats.contains(player.getUniqueId().toString())) {
						if (plugin.rm.isWithinARegionMargin(arrow.getLocation(), 5) == null) { 
							for (int i = 1; i <= 10; i++) {
								arrow.getWorld().spawnEntity(arrow.getLocation(), EntityType.BAT);
							}
							arrow.remove();
							if (player.getGameMode() == GameMode.SURVIVAL) {
								player.getInventory().removeItem(new ItemStack(Material.ROTTEN_FLESH, 3));
							}
						} else {
							arrow.remove();
							player.sendMessage(ChatColor.RED + "[!!] Arrows can't spawn bats near protected areas!");
						}
						canSpawnBats.remove(player.getUniqueId().toString());
					}
					/*
					 * NUCLEAR
					 */
					else if (canNuke.contains(player.getUniqueId().toString())) {
						if (plugin.rm.isWithinARegionMargin(arrow.getLocation(), 65) == null) {
							player.getWorld().createExplosion(arrow.getLocation(), 50F);
							arrow.remove();
							if (player.getGameMode() != GameMode.CREATIVE)
								player.getInventory().removeItem(new ItemStack(Material.TNT, 64));
						} else {
							arrow.remove();
							player.sendMessage(ChatColor.RED + "[!!] Arrows can't explode near protected areas!");
						}
						canNuke.remove(player.getUniqueId().toString());
					}
					/*
					 * ENLIGHTED
					 */
					else if (canLight.contains(player.getUniqueId().toString())) {
						if (plugin.rm.isWithinARegionMargin(arrow.getLocation(), 2) == null) {
							arrow.getLocation().getBlock().setType(Material.TORCH);
							arrow.remove();
							if (player.getGameMode() == GameMode.SURVIVAL)
								player.getInventory().removeItem(new ItemStack(Material.TORCH, 1));
						} else {
							arrow.remove();
							player.sendMessage(ChatColor.RED + "[!!] Arrows can't place torches in protected areas!");
						}
						canLight.remove(player.getUniqueId().toString());
					}
					/*
					 * FLINTAND
					 */
					else if (canFlint.contains(player.getUniqueId().toString())) {
						if (plugin.rm.isWithinARegionMargin(arrow.getLocation(), 6) == null) {
							int fintuses = 1;
							Location loc = arrow.getLocation();
							if (loc.getBlock().getType() == Material.AIR ||
									 loc.getBlock().getType() == Material.SNOW ||
									 loc.getBlock().getType() == Material.VINE ||
									 loc.getBlock().getType() == Material.DEAD_BUSH) {
								loc.getBlock().setType(Material.FIRE);
								fintuses++;
							} else {
								loc.add(0, 1, 0);
								if (loc.getBlock().getType() == Material.AIR ||
										 loc.getBlock().getType() == Material.SNOW ||
										 loc.getBlock().getType() == Material.VINE ||
										 loc.getBlock().getType() == Material.DEAD_BUSH) {
									loc.getBlock().setType(Material.FIRE);
									fintuses++;
								}
								loc.add(0, -1, 0);
							}
							loc.add(1, 0, 0);
							if (loc.getBlock().getType() == Material.AIR ||
									 loc.getBlock().getType() == Material.SNOW ||
									 loc.getBlock().getType() == Material.VINE ||
									 loc.getBlock().getType() == Material.DEAD_BUSH) {
								loc.getBlock().setType(Material.FIRE);
								fintuses++;
							} else {
								loc.add(0, 1, 0);
								if (loc.getBlock().getType() == Material.AIR ||
										 loc.getBlock().getType() == Material.SNOW ||
										 loc.getBlock().getType() == Material.VINE ||
										 loc.getBlock().getType() == Material.DEAD_BUSH) {
									loc.getBlock().setType(Material.FIRE);
									fintuses++;
								}
								loc.add(0, -1, 0);
							}
							loc.add(-2, 0, 0);
							if (loc.getBlock().getType() == Material.AIR ||
									 loc.getBlock().getType() == Material.SNOW ||
									 loc.getBlock().getType() == Material.VINE ||
									 loc.getBlock().getType() == Material.DEAD_BUSH) {
								loc.getBlock().setType(Material.FIRE);
								fintuses++;
							} else {
								loc.add(0, 1, 0);
								if (loc.getBlock().getType() == Material.AIR ||
										 loc.getBlock().getType() == Material.SNOW ||
										 loc.getBlock().getType() == Material.VINE ||
										 loc.getBlock().getType() == Material.DEAD_BUSH) {
									loc.getBlock().setType(Material.FIRE);
									fintuses++;
								}
								loc.add(0, -1, 0);
							}
							loc.add(1, 0, 1);
							if (loc.getBlock().getType() == Material.AIR ||
									 loc.getBlock().getType() == Material.SNOW ||
									 loc.getBlock().getType() == Material.VINE ||
									 loc.getBlock().getType() == Material.DEAD_BUSH) {
								loc.getBlock().setType(Material.FIRE);
								fintuses++;
							} else {
								loc.add(0, 1, 0);
								if (loc.getBlock().getType() == Material.AIR ||
										 loc.getBlock().getType() == Material.SNOW ||
										 loc.getBlock().getType() == Material.VINE ||
										 loc.getBlock().getType() == Material.DEAD_BUSH) {
									loc.getBlock().setType(Material.FIRE);
									fintuses++;
								}
								loc.add(0, -1, 0);
							}
							loc.add(0, 0, -2);
							if (loc.getBlock().getType() == Material.AIR ||
									 loc.getBlock().getType() == Material.SNOW ||
									 loc.getBlock().getType() == Material.VINE ||
									 loc.getBlock().getType() == Material.DEAD_BUSH) {
								loc.getBlock().setType(Material.FIRE);
								fintuses++;
							} else {
								loc.add(0, 1, 0);
								if (loc.getBlock().getType() == Material.AIR ||
										 loc.getBlock().getType() == Material.SNOW ||
										 loc.getBlock().getType() == Material.VINE ||
										 loc.getBlock().getType() == Material.DEAD_BUSH) {
									loc.getBlock().setType(Material.FIRE);
									fintuses++;
								}
								loc.add(0, -1, 0);
							}
							if (player.getGameMode() == GameMode.SURVIVAL) {
								for (ItemStack is : player.getInventory().getContents()) {
									if (is != null) {
										if (is.getType() == Material.FLINT_AND_STEEL) {
											is.setDurability((short) (is.getDurability() + fintuses));
											if (is.getDurability() > 64) {
												player.getInventory().remove(is);
												player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 10, 10);
											}
											break;
										}
									}
								}
							}
							arrow.remove();
						} else {
							arrow.remove();
							player.sendMessage(ChatColor.RED + "[!!] Arrows can't set stuff on fire near protected areas!");
						}
						canFlint.remove(player.getUniqueId().toString());
					}
					/*
					 * BOMB
					 */
					else if (canBomb.contains(player.getUniqueId().toString())) {
						if (plugin.rm.isWithinAXZMargin(arrow.getLocation(), 16) == null) {
							arrow.remove();
							World world = arrow.getLocation().getWorld();
							for (int i = 0; i < 3; i++) {
								Location loc = arrow.getLocation();
								loc.add(ran.nextInt(9) - 4, 32, ran.nextInt(9) - 4);
								world.spawnEntity(loc, EntityType.PRIMED_TNT);
							}
							canBomb.remove(player.getUniqueId().toString());
						} else {
							arrow.remove();
							if (player.getGameMode() != GameMode.CREATIVE) {
								player.getInventory().addItem(new ItemStack(Material.TNT, 3));
							}
							player.sendMessage(ChatColor.RED + "[!!] Arrows can't boom stuff near protected areas!");
						}
					}
					/*
					 * CLUSTER
					 */
					else if (canCluster.contains(player.getUniqueId().toString())) {
						if (plugin.rm.isWithinAXZMargin(arrow.getLocation(), 24) == null) {
							arrow.remove();
							World world = arrow.getLocation().getWorld();
							Location loc = arrow.getLocation();
							
							for (int i = 0; i < 5; i++) {
								TNTPrimed tnt = world.spawn(loc, TNTPrimed.class);
								tnt.setFuseTicks(40);
								tnt.setVelocity(Util.ranCluster(ran));
							}
							
							canCluster.remove(player.getUniqueId().toString());
						} else {
							arrow.remove();
							if (player.getGameMode() != GameMode.CREATIVE) {
								player.getInventory().addItem(new ItemStack(Material.TNT, 5));
							}
							player.sendMessage(ChatColor.RED + "[!!] Arrows can't boom stuff near protected areas!");
						}
					}
				}
			}
		}
	}

	private void removeSwap(String playerUUID) {
        canParalyze.remove(playerUUID);
        canSwap.remove(playerUUID);
        canDisarm.remove(playerUUID);
        canLevel.remove(playerUUID);
        canSwarm.remove(playerUUID);
        canwoodman.remove(playerUUID);
        canFood.remove(playerUUID);
        canBomb.remove(playerUUID);
        canDrop.remove(playerUUID);
        canPull.remove(playerUUID);
        canCluster.remove(playerUUID);
	}
}