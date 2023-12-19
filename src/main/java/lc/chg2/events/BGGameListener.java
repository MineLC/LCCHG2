package lc.chg2.events;

import java.text.DecimalFormat;
import java.util.Collection;

import lc.chg2.CHG2;
import lc.chg2.timers.PreGameTimer;
import lc.chg2.utilities.*;
import lc.chg2.utilities.enums.GameState;
import lc.chg2.utilities.enums.Translation;
import lc.core2.Core2;
import lc.core2.entities.Database;
import lc.core2.entities.Jugador;
import lc.core2.entities.Ranks;
import lc.core2.utils.KickType;
import lc.core2.utils.Util;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Egg;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.util.Vector;

public class BGGameListener implements Listener {
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player p = event.getPlayer();
		Action a = event.getAction();
		if (a == Action.RIGHT_CLICK_AIR || a == Action.RIGHT_CLICK_BLOCK) {
			if (CHG2.GAMESTATE == GameState.PREGAME) {
				if(p.getItemInHand().getType() == Material.BOW) {
					BGChat.printKitChat(p);
				} else if(p.getItemInHand().getType() == Material.PAPER) {
					BGMenus.getInvStats_CHG().open(p);
				}
				}
		}
		if(CHG2.isSpectator(p)) {
			event.setCancelled(true);
			return;
		}

		if (CHG2.GAMESTATE == GameState.PREGAME) {
			event.setCancelled(true);
			return;
		}
				

		if (p.getItemInHand().getType() == Material.COMPASS) {
			//new
	    	  Player cplayer = null;
	    	  double cdistance = 1000D;
	    	  for(Player gamers : CHG2.getGamers()) {
    			  if(p == gamers)
    				  continue;
    			  
					if(BGTeam.isInTeam(p, gamers.getName()))
						continue;
    			  
    			  double distance = gamers.getLocation().distance(p.getLocation());
    			  if(distance < cdistance) {
    				  cplayer = gamers;
    				  cdistance = distance;
    			  }
    	  }
	    	  if(cplayer != null) {
	    		  DecimalFormat df = new DecimalFormat("##.#");
	    		  p.sendMessage(ChatColor.GOLD+"La brújula apunta al jugador " + ChatColor.YELLOW +cplayer.getName()+ ChatColor.GREEN+ " ("+df.format(cdistance)+" bloques)!");
	    		  p.setCompassTarget(cplayer.getLocation());
	    	  } else {
	    		  p.sendMessage(ChatColor.GRAY+"La brújula apunta al spawn!");
	    		  p.setCompassTarget(CHG2.getSpawn());
	    	  }
		}
	}
	
	@EventHandler
    public void onPing(ServerListPingEvent e) {
		if (CHG2.GAMESTATE != GameState.PREGAME) {
    		e.setMotd(ChatColor.GOLD+"Estado: "+ChatColor.AQUA+"Progreso"+"=="+ChatColor.GOLD+"Mapa: "+ChatColor.AQUA+WordUtils.capitalize(CHG2.mapa));            		
		} else {
			e.setMotd(ChatColor.GOLD+"Estado: "+ChatColor.AQUA+"Esperando"+"=="+ChatColor.GOLD+"Mapa: "+ChatColor.AQUA+WordUtils.capitalize(CHG2.mapa));
		}
    }

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityShootArrow(EntityShootBowEvent event) {
		if(event.getEntity() instanceof Player)
			if(CHG2.isSpectator((Player) event.getEntity())) {
				event.setCancelled(true);
				return;
			}
		if (event.getEntity() instanceof Player && CHG2.GAMESTATE == GameState.PREGAME) {
			event.getBow().setDurability((short) 0);
			event.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		if(CHG2.isSpectator(event.getPlayer())) {
			event.setCancelled(true);
			return;
		}
		if (CHG2.GAMESTATE == GameState.PREGAME)
			event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityExplode(EntityExplodeEvent event) {
		if (CHG2.GAMESTATE != GameState.GAME) {
			event.setCancelled(true);
			return;
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerPickupItem(PlayerPickupItemEvent event) {
		if(CHG2.isSpectator(event.getPlayer())) {
			event.setCancelled(true);
			return;
		}
		if (CHG2.GAMESTATE == GameState.PREGAME)
			event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerKick(PlayerKickEvent event) {
		event.setLeaveMessage(null);
	}
	
	@EventHandler
	public void onLogin(AsyncPlayerPreLoginEvent e) {
		Jugador jug = Jugador.getJugador(e.getName());
		Player p = jug.getBukkitPlayer();
		try {
			Database.loadPlayerRank_SYNC(jug);
			Database.loadPlayerSV_CHG_SYNC(jug);
			Database.loadPlayerCoins_SYNC(jug);
			Database.loadPlayerSV_CHG_KITS(jug);
		} catch(Exception a) {
			a.printStackTrace();
			Util.kick(p, KickType.BY_SERVER, null, "&c¡Error al cargar tus datos!", "&eIntenta ingresar nuevamente.");
		}
	}
	
	@EventHandler
	public void onLogin(PlayerLoginEvent e) {
		if(e.getResult() == Result.KICK_FULL) {
			if(CHG2.GAMESTATE == GameState.PREGAME) {
				if(Jugador.getJugador(e.getPlayer()).is_VIP()) {
					e.allow();
				} else {
					e.disallow(Result.KICK_OTHER, Util.getKickMessage(KickType.BY_SERVER, null, "&c¡El servidor esta lleno!"));
				}
			} else {
				e.disallow(Result.KICK_FULL, Util.getKickMessage(KickType.BY_SERVER, null, "&c¡El servidor esta lleno y ademas estan ya jugando!"));
			}
		}
	}

	@EventHandler
	public void onPickUp(PlayerPickupItemEvent e) {
		Material mat = e.getItem().getItemStack().getType();
		
		if(mat == Material.DIAMOND_BLOCK || mat == Material.GOLD_ORE || mat == Material.IRON_ORE) {
			e.setCancelled(true);
			e.getItem().remove();
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerJoin(PlayerJoinEvent event) {
		event.setJoinMessage(null);
		Player p = event.getPlayer();
		String title = "title " +p.getName()+ " title [{\"text\":\"CHG\",\"color\":\"gold\"}]";
		String subtitle = "title " +p.getName()+ " subtitle [{\"text\":\"www.minelc.net\",\"color\":\"green\"}]";
		p.getInventory().clear();
		p.updateInventory();
		Jugador jug = Jugador.getJugador(p);
		p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
		jug.setBukkitPlayer(p);
		if(jug.getCHG_Rank() == null) {
			jug.setCHG_Rank("Nuevo");
			Database.savePlayerSV_CHG(jug);
		} else if(jug.getCHG_Rank().isEmpty()) {
			Database.loadPlayerRank_SYNC(jug);
			Database.loadPlayerSV_CHG_SYNC(jug);
			Database.loadPlayerCoins_SYNC(jug);
			Database.loadPlayerSV_CHG_KITS(jug);
		}
		p.setGameMode(GameMode.SURVIVAL);
		p.setMaxHealth(24);
		p.setHealth(24);
		p.setAllowFlight(true);
		CHG2.kills.put(p.getName(), 0);
		
		if(CHG2.GAMESTATE == GameState.PREGAME) {
			CHG2.gamers.add(p);
			p.getInventory().addItem(BGMenus.getKit_item());
			p.getInventory().addItem(BGMenus.getBook_item());
			p.getInventory().setItem(8, BGMenus.getStats_item());
			
			if(!PreGameTimer.started && Bukkit.getOnlinePlayers().size() > 2) {
				new PreGameTimer();
			}
			} else {
				CHG2.addSpectator(p);
			}
		
		addPermissionDEFAULT(p);
		
		if(jug.is_Admin()) {
			addPermissiosADM(p);
			p.setOp(true);
		} else if(jug.is_AYUDANTE()) {
			addPermissionMOD(p);
		}
		CHG2.updateScoreboard(p);
		String command = "minecraft:gamerule sendCommandFeedback false";
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), title);
		Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), subtitle);
	}
	
	public void addPermissionDEFAULT(Player p) {
	    p.addAttachment(Core2.getInstance(), "nocheatplus.command.info", true);
	}
	
	public void addPermissionMOD(Player p) {
	    p.addAttachment(Core2.getInstance(), "nocheatplus.command.*", true);
	    p.addAttachment(Core2.getInstance(), "nocheatplus.notify", true);
	}
	
	public void addPermissiosADM(Player p) {
		p.addAttachment(Core2.getInstance(), "minecraft.*", true);
		p.addAttachment(Core2.getInstance(), "bukkit.*", true);
	    p.addAttachment(Core2.getInstance(), "*.*", true);
	    p.addAttachment(Core2.getInstance(), "*", true);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockBreak(BlockBreakEvent event) {
		if (CHG2.GAMESTATE == GameState.PREGAME) {
			event.setCancelled(true);
			return;
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void FoodCe(FoodLevelChangeEvent e) {

		if (!(CHG2.GAMESTATE == GameState.GAME)) {
			e.setCancelled(true);
		}
	} 
		
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockPlace(BlockPlaceEvent event) {
		if (CHG2.GAMESTATE == GameState.PREGAME) {
			event.setCancelled(true);
			return;
		}
	}
	
	@EventHandler(ignoreCancelled=true)
	public void onChat(AsyncPlayerChatEvent e) {
		Player p = e.getPlayer();
		Jugador j = Jugador.getJugador(p);
		
		String msg = e.getMessage().replace("%", "");
		
		if(j.isHideRank()) {
			e.setFormat(ChatColor.YELLOW + p.getName() + ChatColor.DARK_GRAY+" » " + ChatColor.GRAY+msg);
		} else if(j.is_Owner())
			e.setFormat(ChatColor.DARK_RED+""+ChatColor.BOLD+Ranks.OWNER.name()+" "+ChatColor.DARK_GRAY+" "+j.getNameTagColor() + p.getName() + ChatColor.DARK_GRAY+" » " + ChatColor.GRAY+ChatColor.translateAlternateColorCodes('&', msg));
		else if(j.is_Admin())
			e.setFormat(ChatColor.RED+""+ChatColor.BOLD+Ranks.ADMIN.name()+" "+j.getNameTagColor() + p.getName() + ChatColor.DARK_GRAY+" » " + ChatColor.GRAY+ChatColor.translateAlternateColorCodes('&', msg));
		else if(j.is_MODERADOR())
			e.setFormat(ChatColor.DARK_PURPLE+""+ChatColor.BOLD+Ranks.MOD.name()+" "+j.getNameTagColor() + p.getName() + ChatColor.DARK_GRAY+" » " + ChatColor.GRAY+ChatColor.translateAlternateColorCodes('&', msg));
		else if(j.is_AYUDANTE())
			e.setFormat(ChatColor.DARK_PURPLE+""+ChatColor.BOLD+Ranks.AYUDANTE.name()+" "+j.getNameTagColor() + p.getName() + ChatColor.DARK_GRAY+" » " + ChatColor.GRAY+ChatColor.translateAlternateColorCodes('&', msg));
		else if(j.is_YOUTUBER())
			e.setFormat(ChatColor.RED+""+ChatColor.BOLD+"You"+ChatColor.WHITE+""+ChatColor.BOLD+"Tuber "+j.getNameTagColor() + p.getName() + ChatColor.DARK_GRAY+" » " + ChatColor.GRAY+ChatColor.translateAlternateColorCodes('&', msg));
		else if(j.is_BUILDER())
			e.setFormat(ChatColor.LIGHT_PURPLE+""+ChatColor.BOLD+Ranks.BUILDER.name()+" "+j.getNameTagColor() + p.getName() +ChatColor.DARK_GRAY+" » " + ChatColor.GRAY+ChatColor.translateAlternateColorCodes('&', msg));
		else if(j.is_RUBY())
			e.setFormat(ChatColor.AQUA + "★ " + ChatColor.RED+""+ChatColor.BOLD+Ranks.RUBY.name()+" "+j.getNameTagColor() + p.getName() + ChatColor.DARK_GRAY+" » " + ChatColor.GRAY+ChatColor.translateAlternateColorCodes('&', msg));
		else if(j.is_ELITE())
			e.setFormat(ChatColor.GOLD+""+ChatColor.BOLD+ Ranks.ELITE.name()+" "+j.getNameTagColor() + p.getName() + ChatColor.DARK_GRAY+" » " + ChatColor.GRAY+ChatColor.translateAlternateColorCodes('&', msg));
		else if(j.is_RUBY())
			e.setFormat(ChatColor.AQUA + "★ " + ChatColor.RED+""+ChatColor.BOLD+Ranks.RUBY.name()+" "+j.getNameTagColor() + p.getName() + ChatColor.DARK_GRAY+" » " + ChatColor.GRAY+ChatColor.translateAlternateColorCodes('&', msg));
		else if(j.is_SVIP())
			e.setFormat(ChatColor.GREEN+""+ChatColor.BOLD+Ranks.SVIP.name()+" "+j.getNameTagColor() + p.getName() + ChatColor.DARK_GRAY+" » " + ChatColor.GRAY+ChatColor.translateAlternateColorCodes('&', msg));
		else if(j.is_VIP())
			e.setFormat(ChatColor.AQUA+""+ChatColor.BOLD+Ranks.VIP.name()+" "+j.getNameTagColor() + p.getName() + ChatColor.DARK_GRAY+" » " + ChatColor.GRAY+ChatColor.translateAlternateColorCodes('&', msg));
		else if(j.is_Premium())
			e.setFormat(ChatColor.BLUE+""+ChatColor.BOLD+Ranks.PREMIUM.name()+" "+j.getNameTagColor() + p.getName() + ChatColor.DARK_GRAY+" » " + ChatColor.GRAY+msg);
		else
			e.setFormat(ChatColor.YELLOW + p.getName() + ChatColor.DARK_GRAY+" » " + ChatColor.GRAY+msg);
		
		e.setFormat(ChatColor.translateAlternateColorCodes('&', "&8[&a" + j.getHG_Rank() + "&8]")+" "+e.getFormat());
		
		if(CHG2.isSpectator(p)) {
			e.setFormat(ChatColor.GRAY+""+ChatColor.BOLD+"Espectador "+e.getFormat());
			
			for(Player Rec : Bukkit.getOnlinePlayers()) {
				try {
				if(!CHG2.isSpectator(Rec) && !Jugador.getJugador(Rec).is_MODERADOR()) {
					e.getRecipients().remove(Rec);
				}
				} catch(Exception ex) {
					ex.printStackTrace();
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player p = event.getPlayer();
		event.setQuitMessage(null);
		Jugador jug = Jugador.getJugador(p);

        CHG2.gamers.remove(p);
		
		if (CHG2.isSpectator(p)) {
			CHG2.remSpectator(p);
		} else if(CHG2.GAMESTATE == GameState.GAME) {
			long ttime = Tagged.getTime(jug);
			Jugador killer = Tagged.getKiller(jug);
			if ((System.currentTimeMillis() - ttime) < 10000 && killer != jug) {
				if (killer != null) {
					killer.addCHG_Stats_kills(1);
					CHG2.kills.put(killer.getBukkitPlayer().getName(), CHG2.kills.get(killer.getBukkitPlayer().getName()) + 1);
					jug.addCHG_Stats_deaths(1);
					int killTotal = 1;
					if (killer.is_RUBY()) {
						killTotal = 5;
					} else if (killer.is_ELITE()) {
						killTotal = 4;
					} else if (killer.is_SVIP()) {
						killTotal = 3;
					} else if (killer.is_VIP()) {
						killTotal = 2;
					}
					CHG2.addBalance(killer, killTotal);
					sendGameMessage(ChatColor.GRAY+jug.getBukkitPlayer().getName() +ChatColor.YELLOW+" se desconecto pero fue asesinado por "+
					ChatColor.GRAY+killer.getBukkitPlayer().getName()+ChatColor.YELLOW+"!");
					
					//save
					Database.savePlayerSV_CHG(jug);
					Database.savePlayerSV_CHG(killer);
					
					Location loc = p.getLocation();
					for(ItemStack is : p.getInventory().getArmorContents()) {
						try {
							if(is != null) {
								loc.getWorld().dropItem(loc, is);
							}
						} catch(Exception ex){
							ex.printStackTrace();
						}
					}
					for(ItemStack is : p.getInventory().getContents()) {
						try {
							if(is != null) {
								loc.getWorld().dropItem(loc, is);
							}
						} catch(Exception ex){
							ex.printStackTrace();
						}
					}
				}
			} else {
				sendGameMessage(ChatColor.GRAY+jug.getBukkitPlayer().getName()+ChatColor.YELLOW+" ha muerto.");
				}
			CHG2.checkwinner();
		}
		
		Tagged.removeTagged(jug);
		
		if(CHG2.TEAMS.containsKey("kills"+p.getName())) {
			CHG2.TEAMS.remove("kills"+p.getName()).unregister();;
		}
		if(CHG2.TEAMS.containsKey("deaths"+p.getName())) {
			CHG2.TEAMS.remove("deaths"+p.getName()).unregister();;
		}
		if(CHG2.TEAMS.containsKey("kdr"+p.getName())) {
			CHG2.TEAMS.remove("kdr"+p.getName()).unregister();;
		}
		if(CHG2.TEAMS.containsKey("lvl"+p.getName())) {
			CHG2.TEAMS.remove("lvl"+p.getName()).unregister();;
		}
		
		p.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
		p.getScoreboard().clearSlot(DisplaySlot.BELOW_NAME);
		
		Jugador.removeJugador(p.getName());
	}
	     
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		Entity entityDamager = event.getDamager();
	    Entity entityDamaged = event.getEntity();
	   
	    if(entityDamager instanceof Arrow) {
	        if(entityDamaged instanceof Player && ((Arrow) entityDamager).getShooter() instanceof Player) {
	            Arrow arrow = (Arrow) entityDamager;
	 
	            Vector velocity = arrow.getVelocity();
	 
	            Player shooter = (Player) arrow.getShooter();
	            Player damaged = (Player) entityDamaged;
	 
	            if(CHG2.isSpectator(damaged)) {
	        		  double x = damaged.getLocation().getBlockX() + 2;
	        		  double y = damaged.getLocation().getBlockY() + 10;
	        		  double z = damaged.getLocation().getBlockZ() + 2;
	        		  Location loc = new Location(damaged.getWorld(), x, y, z);

		                damaged.teleport(loc);

	                BGChat.printPlayerChat(damaged, ChatColor.RED + Translation.SPECTATOR_IN_THE_WAY.t());
	               
	                Arrow newArrow = shooter.launchProjectile(Arrow.class);
	                newArrow.setShooter(shooter);
	                newArrow.setVelocity(velocity);
	                newArrow.setBounce(false);
	               
	                event.setCancelled(true);
	                arrow.remove();
	            }
	        }
	    } else if(entityDamager instanceof Player) {
	    	Player player = (Player) event.getDamager();
			if(CHG2.isSpectator((Player) entityDamager)) {
				event.setCancelled(true);
				return;
			}
			if(player.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE)){
	            Collection<PotionEffect> pe = player.getActivePotionEffects();
	            for(PotionEffect effect : pe)
	            {
	                if(effect.getType().equals(PotionEffectType.INCREASE_DAMAGE))
	                {
	                    if(effect.getAmplifier() == 0)
	                    {
	                    	//fuerza 1
	        				event.setDamage(event.getDamage() - 9);
	                    } else {
	                    	//fuerza 2
	        				event.setDamage(event.getDamage() - 11.5);
	                    }
	                }
	            }

				}
			}

	    }
	
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
		if(event.getRightClicked() instanceof Player && CHG2.isSpectator((Player) event.getRightClicked())) {
			if(!CHG2.isSpectator(event.getPlayer())) {
				event.getRightClicked().teleport(CHG2.getSpawn());
				BGChat.printPlayerChat((Player) event.getRightClicked(), ChatColor.RED + Translation.SPECTATOR_IN_THE_WAY.t());
				
				event.setCancelled(true);
				return;
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityDamage(EntityDamageEvent event) {
		if(event.getEntity() instanceof Player) {
			Player p = (Player) event.getEntity();
			if (event.getCause() == DamageCause.VOID) {
			  	  if (CHG2.isSpectator(p) || CHG2.GAMESTATE == GameState.INVINCIBILITY || CHG2.GAMESTATE == GameState.PREGAME) {
			  		  event.setCancelled(true);
			  		  p.setFallDistance(0.0F);
			          p.teleport(p.getWorld().getSpawnLocation());
			          p.playSound(p.getLocation(), Sound.HURT_FLESH, 1F, 1.3F);
			  	  }
			} else if(CHG2.isSpectator(p)) {
				event.setCancelled(true);
				return;
			} 
		}
		
		if (CHG2.GAMESTATE != GameState.GAME && event.getEntity() instanceof Player) {
			event.setCancelled(true);
			return;
		}

		if (CHG2.GAMESTATE == GameState.PREGAME && !(event.getEntity() instanceof Player)) {
			event.setCancelled(true);
			return;
			
		}
	}
	
	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent e) {
		final Player p = e.getPlayer();
		e.setRespawnLocation(p.getLocation());
		CHG2.addSpectator(p);
		p.sendMessage(ChatColor.AQUA+"Ahora eres espectador. Para salir usa el comando /lobby!");
		
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onDeath(PlayerDeathEvent e) {
		e.setDeathMessage(null);
		final Player p = e.getEntity();
		final Jugador target = Jugador.getJugador(p);
		Entity ent = e.getEntity();
		CHG2.setFame(p, 0);
		DamageCause damageCause = DamageCause.CUSTOM;
		if (ent.getLastDamageCause() != null) {
			damageCause = ent.getLastDamageCause().getCause();
		}
		if(CHG2.gamers.contains(p)) {
			CHG2.gamers.remove(p);
		}
		CHG2.spectators.add(p);
		BGChat.printPlayerChat(p, ChatColor.YELLOW + Translation.NOW_SPECTATOR.t());
		final DamageCause dCause = damageCause;
		
		target.addCHG_Stats_deaths(1);
		if ((System.currentTimeMillis() - Tagged.getTime(target)) < 10000) {
			Jugador killer = Tagged.getKiller(target);
			if (killer != null) {
				CHG2.AddFame(killer.getBukkitPlayer());
				Database.saveCHGFame(killer);
				killer.addCHG_Stats_kills(1);
				Database.savePlayerSV_CHG(killer);
				/* rangos */

				String rank = killer.getHG_Rank();
				int kills = killer.getCHG_Fame();
				if(kills >=100 && kills <500) {
					if(!(rank.equalsIgnoreCase("Aprendiz"))) {
						killer.setHG_Rank("Aprendiz");
						killer.getBukkitPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&aFelicidades, ahora eres &2Aprendiz!!"));
					}
				
				}else if(kills >= 500 && kills <1000) {
					if(!(rank.equalsIgnoreCase("12 héroe"))) {
						killer.setHG_Rank("12 héroe");
						killer.getBukkitPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&aFelicidades, ahora eres &212 héroe!!"));
					}
				}else if(kills >=1000 && kills <2000) {
					if(!(rank.equalsIgnoreCase("11 héroe Feroz"))) {
						killer.setHG_Rank("11 héroe Feroz");
						killer.getBukkitPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&aFelicidades, ahora eres &211 héroe Feroz!!"));
					}
				}else if(kills >=2000 && kills <3000) {
					if(!(rank.equalsIgnoreCase("10 Héroe Poderoso"))) {
						killer.setHG_Rank("10 Héroe Poderoso");
						killer.getBukkitPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&aFelicidades, ahora eres &210 Héroe Poderoso!!"));
					}
				}else if(kills >=3000 && kills <4000) {
					if(!(rank.equalsIgnoreCase("9 Héroe Mortal"))) {
						killer.setHG_Rank("9 Héroe Mortal");
						killer.getBukkitPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&aFelicidades, ahora eres &29 Héroe Mortal!!"));
					}
				}else if(kills >=4000 && kills <5000) {
					if(!(rank.equalsIgnoreCase("8 Héroe Terrorífico"))) {
						killer.setHG_Rank("8 Héroe Terrorífico");
						killer.getBukkitPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&a&aFelicidades, ahora eres &28 Héroe Terrorífico!!"));
					}
				}else if(kills >=5000 && kills <6000) {
					if(!(rank.equalsIgnoreCase("7 Héroe Conquistador"))) {
						killer.setHG_Rank("7 Héroe Conquistador");
						killer.getBukkitPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&a&aFelicidades, ahora eres &27 Héroe Conquistador!!"));
					}
				}else if(kills >=6000 && kills <7000) {
					if(!(rank.equalsIgnoreCase("6 Heroe Renombrado"))) {
						killer.setHG_Rank("6 Heroe Renombrado");
						killer.getBukkitPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&a&aFelicidades, ahora eres &26 Heroe Renombrado!!"));
					}
				}else if(kills >=7000 && kills <8000) {
					if(!(rank.equalsIgnoreCase("5 Héroe ilustre"))) {
						killer.setHG_Rank("6 Heroe Renombrado");
						killer.getBukkitPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&a&aFelicidades, ahora eres &25 Héroe ilustre!!"));
					}
				}else if(kills >=8000 && kills <9000) {
					if(!(rank.equalsIgnoreCase("4 Héroe Eminente"))) {
						killer.setHG_Rank("4 Héroe Eminente");
						killer.getBukkitPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&a&aFelicidades, ahora eres &24 Héroe Eminente!!"));
					}
				}else if(kills >=9000 && kills <10000) {
					if(!(rank.equalsIgnoreCase("3 Rey Héroe"))) {
						killer.setHG_Rank("3 Rey Héroe");
						killer.getBukkitPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&a&aFelicidades, ahora eres &23 Rey Héroe!!"));
					}
				}else if(kills >=10000 && kills <15000) {
					if(!(rank.equalsIgnoreCase("2 Emperador"))) {
						killer.setHG_Rank("2 Emperador");
						killer.getBukkitPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&a&aFelicidades, ahora eres &22 Emperador!!"));
					}
				}else if(kills >=15000 && kills <20000) {
					if(!(rank.equalsIgnoreCase("1 Legendario"))) {
						killer.setHG_Rank("1 Legendario");
						killer.getBukkitPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&a&aFelicidades, ahora eres &21 Legendario!!"));
					}
				}else if(kills >= 20000) {
					if(!(rank.equalsIgnoreCase("Mítico"))) {
						killer.setHG_Rank("Mítico");
						killer.getBukkitPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&a&aFelicidades, ahora eres &2Mítico!!"));
					}
				}
				
				CHG2.kills.put(killer.getBukkitPlayer().getName(), CHG2.kills.get(killer.getBukkitPlayer().getName()) + 1);
				int killTotal = 1;
				if (killer.is_RUBY()) {
					killTotal = 5;
				} else if (killer.is_ELITE()) {
					killTotal = 4;
				} else if (killer.is_SVIP()) {
					killTotal = 3;
				} else if (killer.is_VIP()) {
					killTotal = 2;
				}
				CHG2.addBalance(killer, killTotal);
				if (target.getBukkitPlayer() != null) {
					if(dCause == DamageCause.PROJECTILE)
						target.getBukkitPlayer().playSound(target.getBukkitPlayer().getLocation(), Sound.ENDERMAN_SCREAM, 1F, 2F);
					else
						target.getBukkitPlayer().playSound(target.getBukkitPlayer().getLocation(), Sound.ENDERMAN_DEATH, 1F, 2F);
				}
				sendGameMessage(getDeathMessage(dCause, true, target, killer));
				
				//save
				CHG2.updateScoreboard(killer.getBukkitPlayer());
				Database.savePlayerSV_CHG(killer);
			}
		} else {
			sendGameMessage(getDeathMessage(dCause, false, target, target));
		}
		target.addCHG_Stats_Partidas_jugadas(1);
		//save
	//	CHG2.updateScoreboard(p);
		Database.savePlayerSV_CHG(target);

		
		Bukkit.getScheduler().runTaskLater(CHG2.instance, new Runnable() {
			
			@Override
			public void run() {
				if( p.isDead())
				p.sendMessage("");
			}
		}, 8L);
		
		Location loc = p.getLocation();
		String fl = ChatColor.WHITE + "" + ChatColor.BOLD + BGFiles.dsign.getString("FIRST_LINE");
		String sl = ChatColor.DARK_RED + BGFiles.dsign.getString("SECOND_LINE");
		//String fl = BGFiles.dsign.getString("FIRST_LINE");
		//String fl = BGFiles.dsign.getString("FIRST_LINE");
		
		if(fl != null)	
			fl = fl.replace("[name]", p.getName());
		if(sl != null)
			sl = sl.replace("[name]", p.getName());

		
		BGSign.createSign(loc, fl, sl, "","");

		if(p.getKiller() != null && p.getKiller() instanceof Player) {
			Player killer = p.getKiller();
			if(BGKit.hasAbility(killer, 14)) {
				if(killer.getFoodLevel() <= 14) {
					killer.setFoodLevel(killer.getFoodLevel()+ 6);
				} else {
					killer.setFoodLevel(20);
				}
			}
		}
		
		Bukkit.getServer().getWorlds().get(0).strikeLightningEffect(p.getLocation().clone().add(0, 50, 0));
		CHG2.checkwinner();
	}
	
	private void sendGameMessage(String message) {
		for(Player Online : Bukkit.getOnlinePlayers()) {
			Online.sendMessage(message);
		}
	}
	
	private String getDeathMessage(DamageCause dCause, boolean withHelp, Jugador target, Jugador killer) {
		String first = "";
		String second = ChatColor.RED+" por "+ChatColor.RED+killer.getBukkitPlayer().getName();
		//SoundDeathMsg();
		
		try {
		if (dCause.equals(DamageCause.BLOCK_EXPLOSION) || dCause.equals(DamageCause.ENTITY_EXPLOSION)) {
			first = ChatColor.RED+target.getBukkitPlayer().getName()+ChatColor.RED+" exploto";
		} else if (dCause.equals(DamageCause.DROWNING)) {
			first = ChatColor.RED+target.getBukkitPlayer().getName()+ChatColor.RED+" se ahogo";
		} else if (dCause.equals(DamageCause.FIRE) || dCause.equals(DamageCause.FIRE_TICK)) {
			first = ChatColor.RED+target.getBukkitPlayer().getName()+ChatColor.RED+" murio rostizado";
		} else if (dCause.equals(DamageCause.ENTITY_ATTACK)) {
			if(killer.getBukkitPlayer().getItemInHand().getType() == null){
				first = ChatColor.RED+target.getBukkitPlayer().getName()+ChatColor.RED+" fue asesinado por "+ChatColor.RED+killer.getBukkitPlayer().getName();
				second = "";

			}else {
				String item = killer.getBukkitPlayer().getItemInHand().getItemMeta().getDisplayName();
				if(item == null) {
					first = ChatColor.RED+target.getBukkitPlayer().getName()+ChatColor.RED+" fue asesinado por "+ChatColor.RED+killer.getBukkitPlayer().getName();
					second = "";
				} else {
					first = ChatColor.RED+target.getBukkitPlayer().getName()+ChatColor.RED+" fue asesinado por "+ChatColor.RED+killer.getBukkitPlayer().getName() + " usando " +ChatColor.RED + killer.getBukkitPlayer().getItemInHand().getItemMeta().getDisplayName();
					second = "";
				}
			}

			
		} else if (dCause.equals(DamageCause.FALLING_BLOCK)) {
			first = ChatColor.RED+target.getBukkitPlayer().getName()+ChatColor.RED+" fue aplastado";
		} else if (dCause.equals(DamageCause.LAVA)) {
			first = ChatColor.RED+target.getBukkitPlayer().getName()+ChatColor.RED+" trato de nadar en lava y murio";
		} else if (dCause.equals(DamageCause.PROJECTILE)) {
			first = ChatColor.RED+target.getBukkitPlayer().getName()+ChatColor.RED+" fue disparado por "+ChatColor.RED+killer.getBukkitPlayer().getName();
			second = "";
		} else if (dCause.equals(DamageCause.SUFFOCATION)) {
			first = ChatColor.RED+target.getBukkitPlayer().getName()+ChatColor.RED+" murio sofocado";
		} else if (dCause.equals(DamageCause.VOID)) {
			first = ChatColor.RED+target.getBukkitPlayer().getName()+ChatColor.RED+" cayo al vacio";
		} else {
			first = ChatColor.RED+target.getBukkitPlayer().getName()+ChatColor.RED+" murio";
		}
		} catch(Exception ex) {
			ex.printStackTrace();
			return ChatColor.RED+target.getBukkitPlayer().getName()+ChatColor.RED+" murio.";
		}
		if (withHelp) {
			return first + second+ChatColor.RED+"!";
		} else {
			return first +ChatColor.RED+ ".";
		}
	}
	
	
	@EventHandler
	public void onEntityDamageEntity(EntityDamageByEntityEvent e) {
		 Entity ent = e.getEntity();
		 if(ent instanceof Player) {
			 Jugador target = Jugador.getJugador(((Player) ent));
				 Entity damager = e.getDamager();
				 if (e.getCause().equals(DamageCause.PROJECTILE)) {
					 if (damager instanceof Snowball) {
						 Snowball snowball = (Snowball) damager;
						 if(snowball.getShooter() instanceof Player) {
							 Jugador killer = Jugador.getJugador(((Player) snowball.getShooter()));
								 Tagged.addTagged(target, killer, System.currentTimeMillis());
								 return;
							 }
					 	} else if (damager instanceof Egg) {
							 Egg egg = (Egg) damager;
							 if(egg.getShooter() instanceof Player) {
								 Jugador killer = Jugador.getJugador(((Player) egg.getShooter()));
									 Tagged.addTagged(target, killer, System.currentTimeMillis());
									 return;
								 }
					 	} else if (damager instanceof Arrow) {
							 Arrow arrow = (Arrow) damager;
							 if(arrow.getShooter() instanceof Player) {
								 Jugador killer = Jugador.getJugador(((Player) arrow.getShooter()));
									 Tagged.addTagged(target, killer, System.currentTimeMillis());
									 return;
							 }
					 	} else if (damager instanceof EnderPearl) {
						 EnderPearl ePearl = (EnderPearl) damager;
						 if(ePearl.getShooter() instanceof Player) {
							 Jugador killer = Jugador.getJugador(((Player) ePearl.getShooter()));
								 Tagged.addTagged(target, killer, System.currentTimeMillis());
								 return;
							 }
						 } else if (damager instanceof ThrownPotion) {
							 ThrownPotion potion = (ThrownPotion) damager;
							 if(potion.getShooter() instanceof Player) {
								 Jugador killer = Jugador.getJugador(((Player) potion.getShooter()));
									 Tagged.addTagged(target, killer, System.currentTimeMillis());
									 return;
								 }
						 }
				 } else if (damager instanceof Player) {
					 Jugador killer = Jugador.getJugador(((Player) damager));
						 Tagged.addTagged(target, killer, System.currentTimeMillis());
						 return;
					}
		 }
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		if(event.getMessage().toLowerCase().startsWith("/me ") || event.getMessage().toLowerCase().startsWith("/kil") || event.getMessage().toLowerCase().contains(":me ")) {
			event.setCancelled(true);
			return;
		}
			
		if(event.getMessage().toLowerCase().startsWith("/say ")) {
			if(event.getPlayer().hasPermission("bg.admin.*")) {
				String say = event.getMessage().substring(5);
				BGChat.printInfoChat(say);
			}
			event.setCancelled(true);
			return;
		}
	}
	
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onVehicleEntityCollision(VehicleEntityCollisionEvent event) {
        if ((event.getEntity() instanceof Player) && CHG2.isSpectator((Player) event.getEntity())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onVehicleDestroy(VehicleDestroyEvent event) {
        Entity entity = event.getAttacker();
        if (entity instanceof Player && CHG2.isSpectator((Player) entity)) {
        	event.setCancelled(true);
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onVehicleEnter(VehicleEnterEvent event) {
        Entity entity = event.getEntered();
        if (entity instanceof Player && CHG2.isSpectator((Player) entity)) {
        	event.setCancelled(true);
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onVehicleDamage(VehicleDamageEvent event) {
        Entity entity = event.getAttacker();
        if (entity instanceof Player && CHG2.isSpectator((Player) entity)) {
        	event.setCancelled(true);
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerEntityShear(PlayerShearEntityEvent event) {
        if (CHG2.isSpectator(event.getPlayer())) {
            event.setCancelled(true);
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onProjectileHit(ProjectileHitEvent event) {
    	if(event.getEntity() instanceof Arrow && event.getEntity().getShooter() instanceof Player) {
    		if(CHG2.isSpectator((Player) event.getEntity().getShooter())) {
    			event.getEntity().remove();
    			return;
    		}
    	}
    }
}
