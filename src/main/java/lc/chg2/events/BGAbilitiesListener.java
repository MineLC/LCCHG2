package lc.chg2.events;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import lc.chg2.CHG2;
import lc.chg2.utilities.*;
import lc.chg2.utilities.enums.GameState;
import lc.chg2.utilities.enums.Translation;
import lc.core2.utils.KickType;
import lc.core2.utils.Util;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.CropState;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.TreeType;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetEvent.TargetReason;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class BGAbilitiesListener implements Listener {

	public static ArrayList<Player> cooldown = new ArrayList<>();
	
	@SuppressWarnings("unlikely-arg-type")
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player p = event.getPlayer();
		Action a = event.getAction();
		if (a == Action.RIGHT_CLICK_AIR || a == Action.RIGHT_CLICK_BLOCK) {
			if ((BGKit.hasAbility(p, 5) & p.getItemInHand()
					.getType() == Material.COOKIE)) {
				p.addPotionEffect(new PotionEffect(
						PotionEffectType.INCREASE_DAMAGE, BGFiles.abconf.getInt("AB.5.Duration") * 20, 0));
				p.getInventory().removeItem(
						new ItemStack[] { new ItemStack(Material.COOKIE, 1) });
				p.playSound(p.getLocation(), Sound.BURP, 1.0F, (byte) 1);
			}
		}
		
		if (a == Action.LEFT_CLICK_BLOCK || a == Action.LEFT_CLICK_AIR || a == Action.RIGHT_CLICK_BLOCK || a == Action.RIGHT_CLICK_AIR) {
			if (BGKit.hasAbility(p, 4) && p.getItemInHand() != null && 
					p.getItemInHand().getType().equals(Material.FIREBALL)) {
				Vector lookat = p.getLocation().getDirection().multiply(10);
				Fireball fire = p.getWorld().spawn(p.getLocation().add(lookat), Fireball.class);
				fire.setShooter(p);
				p.playSound(p.getLocation(), Sound.FIRE, 1.0F, 1.5F);
				p.getInventory().removeItem(new ItemStack[] { new ItemStack(Material.FIREBALL, 1) });
			}
		}
		
		try{
			if (BGKit.hasAbility(p, 11) && a == Action.RIGHT_CLICK_BLOCK && p.getItemInHand()
					.getType() == Material.DIAMOND_AXE) {
				if(!cooldown.contains(p)) {
					cooldown.add(p);
					BGCooldown.thorCooldown(p);
					Block block = event.getClickedBlock();
					Location loc = block.getLocation();
					World world = Bukkit.getServer().getWorlds().get(0);
                    if (event.getClickedBlock().getType() != Material.BEDROCK)
                        event.getClickedBlock().setType(Material.NETHERRACK);
                    event.getClickedBlock().getRelative(BlockFace.UP).setType(Material.FIRE);
					world.strikeLightning(loc);
				}else {
					BGChat.printPlayerChat(p, BGFiles.abconf.getString("AB.11.Expired"));
				}
			}
			
			if (BGKit.hasAbility(p, 16) && p.getItemInHand()
					.getType() == Material.APPLE && (a == Action.RIGHT_CLICK_AIR || a == Action.RIGHT_CLICK_BLOCK)) {
				
				if(!cooldown.contains(p)) {
					cooldown.add(p);
					BGCooldown.ghostCooldown(p);
										
					p.getInventory().removeItem(new ItemStack[] { new ItemStack(Material.APPLE, 1) });
					p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, BGFiles.abconf.getInt("AB.16.Duration") * 20, 1));
					p.playSound(p.getLocation(), Sound.PORTAL_TRIGGER, 1.0F, (byte) 1);
					BGChat.printPlayerChat(p, BGFiles.abconf.getString("AB.16.invisible"));
				} else {
					BGChat.printPlayerChat(p, BGFiles.abconf.getString("AB.16.Expired"));
				}
			}
			
			if (BGKit.hasAbility(p, 21) && p.getItemInHand()
					.getType() == Material.POTATO && (a == Action.RIGHT_CLICK_AIR || a == Action.RIGHT_CLICK_BLOCK)) {
				
				p.getInventory().removeItem(new ItemStack[] { new ItemStack(Material.POTATO, 1) });
				p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, BGFiles.abconf.getInt("AB.21.Duration") * 20, 1));
				p.playSound(p.getLocation(), Sound.ENDERMAN_STARE, 1.0F, (byte) 1);
			}
			
			if(BGKit.hasAbility(p, 22) && CHG2.GAMESTATE == GameState.GAME && p.getItemInHand().getType() == Material.WATCH &&
					(a == Action.RIGHT_CLICK_AIR || a == Action.RIGHT_CLICK_BLOCK)) {
				
				if(!cooldown.contains(p)) {
					cooldown.add(p);
					BGCooldown.timeCooldown(p);
					
					p.getInventory().removeItem(new ItemStack[] {new ItemStack(Material.WATCH,1)});
					
					int radius = BGFiles.abconf.getInt("AB.22.radius");
					p.playSound(p.getLocation(), Sound.AMBIENCE_CAVE, 1.0F, (byte) 1);
					List<Entity> entities = p.getNearbyEntities(radius+30, radius+30, radius+30);
					for(Entity e : entities) {
						
						if(!e.getType().equals(EntityType.PLAYER) || CHG2.isSpectator((Player)e))
							continue;
						Player target = (Player) e;
						if(BGTeam.isInTeam(p, target.getName()))
							continue;
						if(p.getLocation().distance(target.getLocation()) < radius) {
							target.setWalkSpeed(0.0F);
							if(!target.getActivePotionEffects().contains(PotionEffectType.JUMP))
								target.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 100, -1));
							BGCooldown.freezeCooldown(target);
							String text = BGFiles.abconf.getString("AB.22.target");
							text = text.replace("<player>", p.getName());
							BGChat.printPlayerChat(target, text);
							BGCooldown.freezeCooldown(target);
							p.playSound(p.getLocation(), Sound.AMBIENCE_CAVE, 1F, -1);
							p.playSound(p.getLocation(), Sound.AMBIENCE_THUNDER, 1F, 2);
						}	
					}
					BGChat.printPlayerChat(p, BGFiles.abconf.getString("AB.22.success"));
				}else {
					BGChat.printPlayerChat(p, BGFiles.abconf.getString("AB.22.Expired"));
				}
			}
		} catch(NullPointerException e) {
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	//Snowballs
	  @EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
	  public void Snowballs(ProjectileHitEvent e)
	  {
	    Projectile proj = e.getEntity();
	    if ((proj instanceof Snowball))
	    {
	    	Snowball snow = (Snowball)proj;
	      LivingEntity shooter = (LivingEntity)snow.getShooter();
	      if ((shooter instanceof Player))
	      {
	        Player p = (Player)shooter;
	        if (BGKit.hasAbility(p, 37)) {
	          Location loc = snow.getLocation();
	          String world = snow.getWorld().getName();
	          Bukkit.getServer().getWorld(world).getBlockAt(loc).setType(Material.WEB);
	        }
	      }
	    }
	  }
	/*  @EventHandler
	     public void onEntityDamage ( EntityDamageByEntityEvent event )
	     {
	        Entity damager = event.getDamager();
	        Entity damaged = event.getEntity();
	      
	        if (damager instanceof Snowball && damaged instanceof Player) {
	            Player p = (Player) damaged;
	            if(!(BGKit.hasAbility(p, 37))) {
	            	event.setCancelled(true);
	            }
	        }
	     } */
	@EventHandler(priority = EventPriority.NORMAL)
	public void onProjectileHit(ProjectileHitEvent event) {
		Projectile entity =  event.getEntity();

		if (entity.getType() == EntityType.ARROW) {
			Arrow arrow = (Arrow) entity;
			LivingEntity shooter = (LivingEntity) arrow.getShooter();
			if (shooter.getType() == EntityType.PLAYER) {
				Player player = (Player) shooter;
				if(CHG2.isSpectator(player)) {
					return;
				}
				if (BGKit.hasAbility(player, 1)) {
					Bukkit.getServer().getWorlds().get(0).createExplosion(arrow.getLocation(), 2.0F, false);
					arrow.remove();
				} else {
					return;
				}

			}
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityDeath(EntityDeathEvent e) {
		if(e.getEntity().getKiller() == null) return;
		Player p = e.getEntity().getKiller();
		if (BGKit.hasAbility(p, 7)) {
			if (e.getEntityType() == EntityType.PIG) {
				e.getDrops().clear();
				e.getDrops().add(new ItemStack(Material.PORK, BGFiles.abconf.getInt("AB.7.Amount")));
				p.playSound(p.getLocation(), Sound.ORB_PICKUP, 1.0F, (byte) 1);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerDeath(PlayerDeathEvent event) {
		Player p = event.getEntity(); //getEntity() returns the player...
		if(BGKit.hasAbility(p, 23)) {
			Bukkit.getServer().getWorlds().get(0).createExplosion(p.getLocation(), 2.0F, BGFiles.abconf.getBoolean("AB.23.Burn"));
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityDamage(EntityDamageEvent event) {
		if(event.getEntity() instanceof Player && event.getCause() == DamageCause.FALL) {
			Player p = (Player) event.getEntity();
			if(BGKit.hasAbility(p, 37)) {
				event.setCancelled(true);
			}
		}
		else if (event.getEntity() instanceof Player) {
			Player p = (Player) event.getEntity();
			if(CHG2.isSpectator(p))
				return;
			if (event.getCause() == DamageCause.FALL) {
				if(event.isCancelled()) return;
				if (BGKit.hasAbility(p, 8)) {
					if (event.getDamage() > 4) {
						event.setCancelled(true);
						p.damage(4);
					}
					p.playSound(p.getLocation(), Sound.ORB_PICKUP, 1.0F, (byte) 1);
					List<Entity> nearbyEntities = event.getEntity().getNearbyEntities(5, 5, 5);
					for (Entity target : nearbyEntities) {
						if (target instanceof Player) {
							Player t = (Player) target;
							if(CHG2.isSpectator(t))
								continue;
							if(BGTeam.isInTeam(p, t.getName()))
								continue;
							if(t.getName() == p.getName())
								continue;
							
							if (t.isSneaking())
								t.damage(event.getDamage());
							else
								t.damage(event.getDamage());
						}
					}
				}
			} else if (event.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK) {
				if ((BGKit.hasAbility(p, 30) & !p.hasPotionEffect(PotionEffectType.FIRE_RESISTANCE))) {
					p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 300, 1));
					p.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 320, 1));
					p.playSound(p.getLocation(), Sound.ORB_PICKUP, 1.0F, 1.2F);
				} else if ((BGKit.hasAbility(p, 6) & !p.hasPotionEffect(PotionEffectType.FIRE_RESISTANCE))) {
					p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 260, 0));
					p.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 280, 1));
					p.playSound(p.getLocation(), Sound.ORB_PICKUP, 1.0F, 1.2F);
				}
			}

			if(BGKit.hasAbility(p, 18)) {
				if(event.getDamage() > 2) {
				event.setDamage(event.getDamage() - 2);
				} else {
					event.setDamage(event.getDamage() / 2);
				}
			}
			
		}
		
	}


	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockBreak(BlockBreakEvent event) {
		Player p = event.getPlayer();
		Block b = event.getBlock();
		if (BGKit.hasAbility(p, 2) && b.getType() == Material.LOG) {
			World w = Bukkit.getServer().getWorlds().get(0);
			Double y = b.getLocation().getY() + 1;
			Location l = new Location(w, b.getLocation().getX(), y, b
					.getLocation().getZ());
			while (l.getBlock().getType() == Material.LOG) {
				l.getBlock().breakNaturally();
				y++;
				l.setY(y);
			}
			p.playSound(p.getLocation(), Sound.ORB_PICKUP, 1.0F, (byte) 1);
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockPlace(BlockPlaceEvent event) {
		Block block = event.getBlockPlaced();
		Player p = event.getPlayer();
		
		if(BGKit.hasAbility(p, 10)) {
		if (block.getType() == Material.CROPS) {
			p.playSound(p.getLocation(), Sound.ORB_PICKUP, 1.0F, (byte) 1);
			block.setData(CropState.RIPE.getData());
		}
		if (block.getType() == Material.MELON_SEEDS) {
			p.playSound(p.getLocation(), Sound.ORB_PICKUP, 1.0F, (byte) 1);
			block.setData(CropState.RIPE.getData());
		}
		if (block.getType() == Material.PUMPKIN_SEEDS) {
			p.playSound(p.getLocation(), Sound.ORB_PICKUP, 1.0F, (byte) 1);
			block.setData(CropState.RIPE.getData());
		}
		if (block.getType() == Material.SAPLING) {
			p.playSound(p.getLocation(), Sound.ORB_PICKUP, 1.0F, (byte) 1);
			TreeType t = getTree(block.getData());
			Bukkit.getServer().getWorlds().get(0).generateTree(block.getLocation(), t);
		}
		}
	}
	
    public TreeType getTree(int data) {
        TreeType tretyp = TreeType.TREE;
        switch(data) {
        case 0:
            tretyp = TreeType.TREE;
            break;
        case 1:
            tretyp = TreeType.REDWOOD;
            break;
        case 2:
            tretyp = TreeType.BIRCH;
            break;
        case 3:
            tretyp = TreeType.JUNGLE;
            break;
        default:
            tretyp = TreeType.TREE;
        }
        return tretyp;
    }
	  @EventHandler
	  public void onFlash(PlayerInteractEvent e) {
	    final Player p = e.getPlayer();
	    if ((BGKit.hasAbility(p, 31) && p.getInventory().getItemInHand().getType() == Material.REDSTONE_TORCH_ON) && ((e.getAction() == Action.RIGHT_CLICK_AIR) || (e.getAction() == Action.RIGHT_CLICK_BLOCK))) {
	      e.setCancelled(true);
	      if (cooldown.contains(p)) {
	        p.sendMessage(ChatColor.RED + "Necesitas esperar para volver a usarlo!");
	      }
	      else {
		        @SuppressWarnings("deprecation")
				Location loc = e.getPlayer().getTargetBlock((HashSet<Byte>) null, 500).getLocation();
		        if (loc.getBlock().getType() == Material.AIR)
		        {
		          p.sendMessage(ChatColor.RED + "Necesitas mirar un bloque para teletransportarte");
		          return;
		        }
		        if (loc.distance(e.getPlayer().getLocation()) > BGFiles.abconf.getInt("AB.31.Distance"))
		        {
		          p.sendMessage(ChatColor.RED + "No puedes teletransportarte tan lejos");
		         
		          return;
		        }
		        cooldown.add(p);
				BGCooldown.flashCooldown(p);
	            p.setFallDistance(0);
		        loc.add(0.0, 1.0, 0.0);
		        p.teleport(loc);
	            p.setFallDistance(0);
		        int distance = (int)(p.getLocation().distance(loc) / 2);
		        p.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, distance, 0));
				p.playSound(p.getLocation(), Sound.ENDERMAN_TELEPORT, 1, -1);
	      }
	  } else if ((BGKit.hasAbility(p, 32) && e.getPlayer().getItemInHand().getType() == Material.FIREWORK)) {
          e.setCancelled(true);
          if (((e.getAction() == Action.RIGHT_CLICK_AIR) || (e.getAction() == Action.RIGHT_CLICK_BLOCK) || (e.getAction() == Action.LEFT_CLICK_AIR)) && (e.getMaterial() == Material.FIREWORK) && (!p.isSneaking())) {
	          Block b = p.getLocation().getBlock();
	          if ((b.getType() != Material.AIR) || (b.getRelative(BlockFace.DOWN).getType() != Material.AIR))
	          {
	            p.setFallDistance(-5.0F);
	            Vector vector = p.getEyeLocation().getDirection();
	            vector.multiply(0.6F);
	            vector.setY(1);
	            p.setVelocity(vector);
	          }
	        }
      }
	  }


	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		Entity damager = event.getDamager();
		Entity defender = event.getEntity();
		
		if (CHG2.GAMESTATE == GameState.PREGAME && !(event.getEntity() instanceof Player)) {
			return;
		}
		if (CHG2.GAMESTATE != GameState.GAME && event.getEntity() instanceof Player) {
			return;
		}
		
		if (event.getEntity().isDead()) {
			return;
		}

		if (event.getDamager() instanceof Arrow) {
			Arrow arrow = (Arrow) event.getDamager();
			if (arrow.getShooter() instanceof Player) {
				Player p = (Player) arrow.getShooter();
				if (!BGKit.hasAbility(p, 9)) {
					return;
				}
				if (p.getLocation().distance(event.getEntity().getLocation()) >= BGFiles.abconf.getInt("AB.9.Distance")) {
					if (event.getEntity() instanceof LivingEntity) {
						LivingEntity victom = (LivingEntity) event.getEntity();
						if (victom instanceof Player) {
							Player v = (Player) victom;
							ItemStack helmet = v.getInventory().getHelmet();
							if (helmet == null) {
								BGChat.printDeathChat(Translation.HEADSHOT_DEATH.t().replace("<victom>", v.getName()).replace("<player>", p.getName()));
								p.playSound(p.getLocation(), Sound.ORB_PICKUP, 1.0F, (byte) 1);
									BGChat.printDeathChat(Translation.PLAYERS_REMAIN.t().replace("<amount>", (CHG2.getGamers().size() - 1) + ""));
									BGChat.printDeathChat("");
								Location light = v.getLocation();
								Bukkit.getServer()
										.getWorlds().get(0)
										.strikeLightningEffect(
												light.add(0.0D, 100.0D, 0.0D));
								v.setHealth(0);
								Util.kick(v, KickType.BY_SERVER, null, Translation.HEADSHOT_DEATH.t().replace("<victom>", v.getName()).replace("<player>", p.getName()));
							} else {
								helmet.setDurability((short) (helmet.getDurability() + 20));
								v.getInventory().setHelmet(helmet);
							}
						} else {
							BGChat.printPlayerChat(p, Translation.HEADSHOT.t());
							victom.setHealth(0);
						}
					}
				}
			}
		}
		
		if (damager instanceof Player) {
			
			Player dam = (Player)damager;
			if(BGKit.hasAbility(dam, 12)) {
				
				if (dam.getHealth() == dam.getMaxHealth()) {
					return;
				}
				BGCooldown.monkCooldown(dam);
				dam.setHealth(dam.getHealth()+1);
			}
			if(BGKit.hasAbility(dam, 38)) {
				if(dam.getItemInHand().getType() == null||dam.getItemInHand().getType() == (Material.AIR)) {
					if(defender instanceof Player) {
						Player def = (Player)defender;
						int Healt = (int) def.getHealth();
						if(Healt >3) {
							def.setHealth(Healt-2);
						}
					}
				}
			}
			
			if (defender.getType() == EntityType.PLAYER) {
				
				Player def = (Player)defender;
				
				if(BGKit.hasAbility(dam, 13) && dam.getItemInHand().getType() == Material.STICK && def.getItemInHand() != null) {
					
					if(!cooldown.contains(dam)) {
						int random = (int) (Math.random() * (BGFiles.abconf.getInt("AB.13.Chance")-1)+1);
						if(random == 1) {
							cooldown.add(dam);
							BGCooldown.thiefCooldown(dam);
							dam.getInventory().clear(dam.getInventory().getHeldItemSlot());
							dam.getInventory().addItem(def.getItemInHand());
							def.getInventory().clear(def.getInventory().getHeldItemSlot());
							BGChat.printPlayerChat(dam, BGFiles.abconf.getString("AB.13.Success"));
							BGChat.printPlayerChat(def, BGFiles.abconf.getString("AB.13.Success"));
							dam.playSound(dam.getLocation(), Sound.ORB_PICKUP, 1.0F, (byte) 1);
						}
					} else {
						BGChat.printPlayerChat(dam, BGFiles.abconf.getString("AB.15.Expired"));
					}
				}
				
				if(BGKit.hasAbility(dam, 15) && dam.getItemInHand().getType() == Material.STICK && def.getItemInHand() != null) {
					
					if(!cooldown.contains(dam)) {
						int random = (int) (Math.random()* (BGFiles.abconf.getInt("AB.15.Chance")-1)+1);
						if(random == 1) {
							cooldown.add(dam);
							BGCooldown.thiefCooldown(dam);
							dam.getInventory().clear(dam.getInventory().getHeldItemSlot());
							dam.getInventory().addItem(def.getItemInHand());
							def.getInventory().clear(def.getInventory().getHeldItemSlot());
							BGChat.printPlayerChat(dam, BGFiles.abconf.getString("AB.15.Success"));
							BGChat.printPlayerChat(def, BGFiles.abconf.getString("AB.15.Success"));
							dam.playSound(dam.getLocation(), Sound.ORB_PICKUP, 1.0F, (byte) 1);
						}
					}else {
						
						BGChat.printPlayerChat(dam, BGFiles.abconf.getString("AB.15.Expired"));
					}
				}
				
				if (BGKit.hasAbility(dam, 19)) {
					
					int random = (int) (Math.random()* (BGFiles.abconf.getInt("AB.19.Chance")-1)+1);
					if(random == 1 && !cooldown.contains(def)) {
						
						def.addPotionEffect(new PotionEffect(PotionEffectType.POISON, BGFiles.abconf.getInt("AB.19.Duration")*20, 1));
						cooldown.add(def);
						BGChat.printPlayerChat(dam, BGFiles.abconf.getString("AB.19.Damager"));
						BGChat.printPlayerChat(def, BGFiles.abconf.getString("AB.19.Defender"));
						BGCooldown.viperCooldown(def);
						dam.playSound(dam.getLocation(), Sound.ORB_PICKUP, 1.0F, (byte) 1);
					}
				}
			
				
			if (BGKit.hasAbility(dam, 35)) {
				
				int random = (int) (Math.random()* (BGFiles.abconf.getInt("AB.35.Chance")-1)+1);
				if(random == 1 && !cooldown.contains(def)) {
					
					def.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, BGFiles.abconf.getInt("AB.35.Duration")*20, 0));
					cooldown.add(def);
					BGChat.printPlayerChat(dam, BGFiles.abconf.getString("AB.35.Damager"));
					BGChat.printPlayerChat(def, BGFiles.abconf.getString("AB.35.Defender"));
					BGCooldown.orcoCooldown(def);
					dam.playSound(dam.getLocation(), Sound.ORB_PICKUP, 1.0F, (byte) 1);
				}
			}
			if (BGKit.hasAbility(dam, 36)) {
				
				int random = (int) (Math.random()* (BGFiles.abconf.getInt("AB.36.Chance")-1)+1);
				if(random == 1 && !cooldown.contains(def)) {
					
					def.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, BGFiles.abconf.getInt("AB.36.Duration")*20, 0));
					def.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, BGFiles.abconf.getInt("AB.36.Duration")*20, 0));
					def.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, BGFiles.abconf.getInt("AB.36.Duration")*20, 0));
					cooldown.add(def);
					BGChat.printPlayerChat(dam, BGFiles.abconf.getString("AB.36.Damager"));
					BGChat.printPlayerChat(def, BGFiles.abconf.getString("AB.36.Defender"));
					BGCooldown.trollCooldown(def);
					dam.playSound(dam.getLocation(), Sound.ORB_PICKUP, 1, 1);
				}
			}
			if (BGKit.hasAbility(dam, 18) && dam.getItemInHand().getType() == Material.AIR) {
				event.setDamage(event.getDamage()+ 5);
			}
		}
	}	
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityTarget(EntityTargetEvent event) {
		Entity entity = event.getTarget();
		if (entity != null) {
			if (entity instanceof Player) {
				Player player = (Player)entity;
				if(BGKit.hasAbility(player, 20) && event.getReason() == TargetReason.CLOSEST_PLAYER) {
					event.setCancelled(true);
				}
			}
		}
	}


	  @EventHandler
	  public void sopasCurandero(PlayerInteractEvent event)
	  {
			Player p = event.getPlayer();
	    if ((event.getAction() == Action.RIGHT_CLICK_AIR) || (event.getAction() == Action.RIGHT_CLICK_BLOCK))
	    {
	      int heal = 6;
	      int feed = 6;
	      if ((event.getPlayer().getItemInHand().getType() == Material.MUSHROOM_SOUP) && 
	        (BGKit.hasAbility(p,34)))
	      {
	        ItemStack bowl = new ItemStack(Material.BOWL, 1);
	        ItemMeta meta = bowl.getItemMeta();
	        if (event.getPlayer().getHealth() < event.getPlayer().getMaxHealth() - 1)
	        {
	          if (event.getPlayer().getHealth() < event.getPlayer().getMaxHealth() - heal + 1)
	          {
	            event.getPlayer().getItemInHand().setType(Material.BOWL);
	            event.getPlayer().getItemInHand().setItemMeta(meta);
	            event.getPlayer().setItemInHand(bowl);
	            event.getPlayer().setHealth(event.getPlayer().getHealth() + heal);
	          }
	          else if ((event.getPlayer().getHealth() < event.getPlayer().getMaxHealth()) && (event.getPlayer().getHealth() > event.getPlayer().getMaxHealth() - heal))
	          {
	            event.getPlayer().setHealth(event.getPlayer().getMaxHealth());
	            event.getPlayer().getItemInHand().setType(Material.BOWL);
	            event.getPlayer().getItemInHand().setItemMeta(meta);
	            event.getPlayer().setItemInHand(bowl);
	          }
	        }
	        else if ((event.getPlayer().getHealth() == event.getPlayer().getMaxHealth()) && (event.getPlayer().getFoodLevel() < 20)) {
	          if (event.getPlayer().getFoodLevel() < 20 - feed + 1)
	          {
	            event.getPlayer().setFoodLevel(event.getPlayer().getFoodLevel() + feed);
	            event.getPlayer().getItemInHand().setType(Material.BOWL);
	            event.getPlayer().getItemInHand().setItemMeta(meta);
	            event.getPlayer().setItemInHand(bowl);
	          }
	          else if ((event.getPlayer().getFoodLevel() < 20) && (event.getPlayer().getFoodLevel() > 20 - feed))
	          {
	            event.getPlayer().setFoodLevel(20);
	            event.getPlayer().getItemInHand().setType(Material.BOWL);
	            event.getPlayer().getItemInHand().setItemMeta(meta);
	            event.getPlayer().setItemInHand(bowl);
	          }
	        }
	      }
	    }
	  }
}
