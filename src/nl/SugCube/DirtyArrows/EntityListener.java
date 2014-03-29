package nl.SugCube.DirtyArrows;

import java.util.List;
import java.util.Random;

import org.bukkit.Effect;
import org.bukkit.EntityEffect;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.WitherSkull;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class EntityListener implements Listener {
	
	public static DirtyArrows plugin;
	
	public EntityListener(DirtyArrows instance) {
		plugin = instance;
	}
	
	Random ran = new Random();
	boolean extra = false;
	Player player;
	
	@EventHandler
	public void onEntityExplode(EntityExplodeEvent event) {
		Entity entity = event.getEntity();
		if (entity instanceof WitherSkull) {
			event.setCancelled(true);
			if (((WitherSkull) entity).getShooter() instanceof Player) {
				Player player = (Player) ((WitherSkull) entity).getShooter();
				if (player.getGameMode() != GameMode.CREATIVE)
					player.getInventory().addItem(new ItemStack(Material.SOUL_SAND, 3));
			}
		} else if (entity instanceof Fireball) {
			event.setCancelled(true);
			if (((Fireball) entity).getShooter() instanceof Player) {
				Player player = (Player) ((Fireball) entity).getShooter();
				if (player.getGameMode() != GameMode.CREATIVE)
					player.getInventory().addItem(new ItemStack(Material.FIREBALL, 1));
			}
		}
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		if (plugin.getConfig().getBoolean("blood") && event.getEntity() instanceof LivingEntity) {
			event.getEntity().getWorld().playEffect(event.getEntity().getLocation().add(0, 1.5, 0), Effect.STEP_SOUND, Material.REDSTONE_BLOCK);
		}
	}
	
	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof Arrow) {
			Arrow arrow = (Arrow) event.getDamager();
			if (arrow.getShooter() instanceof Player) {
				player = (Player) arrow.getShooter();
			}
		}
		if (event.getDamager().getType().equals(EntityType.WITHER_SKULL)) {
			if (event.getEntity() instanceof LivingEntity) {
				LivingEntity lentity = (LivingEntity) event.getEntity();
				lentity.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 180, 1));
			}
		}
	}
	
	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		try {
			if (event.getEntity() instanceof Zombie) {
				if (plugin.getConfig().getBoolean("zombie-flint")) {
					if (ran.nextInt(10) == 0) {
						event.getEntity().getWorld().dropItem(event.getEntity().getLocation(),
								new ItemStack(Material.FLINT, ran.nextInt(2) + 1));
					}
				}
			}
			if (event.getEntity() instanceof LivingEntity) {
				event.getEntity().playEffect(EntityEffect.WOLF_SMOKE);
			}
			if (player.getItemInHand().containsEnchantment(Enchantment.LOOT_BONUS_MOBS) && event.getDrops() != null) {
				ItemStack i = player.getItemInHand();
				if (i.getEnchantmentLevel(Enchantment.LOOT_BONUS_MOBS) == 1) {
					if (ran.nextInt(9) == 0) {
						extra = true;
					}
				} else if (i.getEnchantmentLevel(Enchantment.LOOT_BONUS_MOBS) == 2) {
					if (ran.nextInt(8) == 0) {
						extra = true;
					}
				} else {
					if (ran.nextInt(7) == 0) {
						extra = true;
					}
				}
				
				List<ItemStack> drops = event.getDrops();
				if (ran.nextInt(3) == 0) {
					for (ItemStack drop : drops) {
						int amount;
						if (ran.nextInt(3) != 0) {
							amount = i.getEnchantmentLevel(Enchantment.LOOT_BONUS_MOBS) - 1;
						} else if (ran.nextInt(3) != 0) {
							amount = i.getEnchantmentLevel(Enchantment.LOOT_BONUS_MOBS);
						} else {
							amount = i.getEnchantmentLevel(Enchantment.LOOT_BONUS_MOBS) + 1;
						}
						drop.setAmount(drop.getAmount() + amount);
					}
				}
				if (extra) {
					for (ItemStack drop : drops) {
						drop.setAmount(drop.getAmount() + 1);
					}
				}
				if (ran.nextInt(i.getEnchantmentLevel(Enchantment.LOOT_BONUS_MOBS)) != 0) {
					event.setDroppedExp(event.getDroppedExp() + i.getEnchantmentLevel(Enchantment.LOOT_BONUS_MOBS));
				}
			}
		} catch (Exception e) { }
	}

}
