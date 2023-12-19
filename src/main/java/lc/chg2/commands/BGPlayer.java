package lc.chg2.commands;

import lc.chg2.CHG2;
import lc.chg2.utilities.BGChat;
import lc.chg2.utilities.BGKit;
import lc.chg2.utilities.BGTeam;
import lc.chg2.utilities.enums.GameState;
import lc.chg2.utilities.enums.Translation;
import lc.core2.entities.Jugador;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Objects;

@SuppressWarnings("CallToPrintStackTrace")
public class BGPlayer implements CommandExecutor{
		
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (!(sender instanceof Player)) {
			return true;
		}
		final Player p = (Player) sender;
		Jugador jug = Jugador.getJugador(p);
		if (cmd.getName().equalsIgnoreCase("help")) {
			BGChat.printHelpChat(p);
			return true;
		}
		if (cmd.getName().equalsIgnoreCase("rank")) {
			String rango = jug.getHG_Rank();
            if(!CHG2.Fame.containsKey(p)){
                CHG2.Fame.put(p, 0);
            }
			int kills = jug.getCHG_Fame();
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6============================"));
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6Rango: &a" + rango));
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6Fama: &a" + kills));
			int faltante = getFaltante(rango, kills);
			if(!rango.equalsIgnoreCase("Mítico")){
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6RankUP: &a" + faltante + " Fame"));
			} else {
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6RankUP: &a" + "Rango Máximo."));
			}
			p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6============================"));
			return true;
		}
		if (cmd.getName().equalsIgnoreCase("gamemaker")) {
			if (jug.is_Admin())
			{
				if (CHG2.isSpectator(p))
				{
					CHG2.remSpectator(p);
					BGChat.printPlayerChat(p, ChatColor.RED + "Ya no eres Espectador!");
					p.setGameMode(GameMode.CREATIVE);
					return true;
				}
				CHG2.addSpectator(p);
				p.setGameMode(GameMode.SPECTATOR);
				BGChat.printPlayerChat(p, ChatColor.RED + "Ahora eres Espectador!");
				return true;
			}
			BGChat.printPlayerChat(p, ChatColor.RED + Translation.NO_PERMISSION.t());
			return true;
		}
		if (cmd.getName().equalsIgnoreCase("vanish")) {
			if (jug.is_MODERADOR()) {
				if (!CHG2.isSpectator(p)) {
					CHG2.spectators.add(p);
				}
                CHG2.gamers.remove(p);
				if (p.getGameMode() == GameMode.SURVIVAL) {
					p.setGameMode(GameMode.CREATIVE);
					for(Player Online : Bukkit.getOnlinePlayers()) {
						Online.hidePlayer(p);
					}
					BGChat.printPlayerChat(p, ChatColor.RED + "Ahora eres invisible!");
				} else {
					p.setGameMode(GameMode.SURVIVAL);
					p.setAllowFlight(true);
					p.setFlying(true);
					for(Player Online : Bukkit.getOnlinePlayers()) {
						Online.showPlayer(p);
					}
					BGChat.printPlayerChat(p, ChatColor.GREEN + "Ahora eres visible!");
				}
				return true;
			}
			BGChat.printPlayerChat(p, ChatColor.RED + Translation.NO_PERMISSION.t());
			return true;
		}
		if (cmd.getName().equalsIgnoreCase("kitinfo")) {
			if (args.length != 1) {
				return false;
			}
			BGChat.printKitInfo(p, args[0]);
			return true;
		}

		if (cmd.getName().equalsIgnoreCase("kit")) {
			if (CHG2.GAMESTATE != GameState.PREGAME) {
				BGChat.printPlayerChat(p, ChatColor.RED + Translation.GAME_BEGUN.t());
				return true;
			}
			if (args.length != 1) {
				BGChat.printKitChat(p);
				return true;
			}
			BGKit.setKit(p, args[0]);
			return true;
		}

		if (cmd.getName().equalsIgnoreCase("spawn")) {
			if (CHG2.GAMESTATE != GameState.PREGAME && !CHG2.isSpectator(p)) {
				BGChat.printPlayerChat(p, Translation.GAME_BEGUN.t());
            } else {
				p.teleport(CHG2.getSpawn());
				BGChat.printPlayerChat(p, ChatColor.GREEN + Translation.TELEPORTED_SPAWN.t());
            }
            return true;
        }

		if (cmd.getName().equalsIgnoreCase("desbug")) {
			if (CHG2.GAMESTATE != GameState.INVINCIBILITY) {
				BGChat.printPlayerChat(p, Translation.GAME_BEGUN.t());
            } else {
				p.teleport(p);
				BGChat.printPlayerChat(p, ChatColor.GOLD + "Desbugeado!");
            }
            return true;
        }

		if(cmd.getName().equalsIgnoreCase("team")) {
			if(args.length > 2) {
				return false;
			}
			
			if (args.length == 0) {
				BGChat.printPlayerChat(p, ChatColor.YELLOW + Translation.TEAM_FUNC_CMDS.t());
				return true;
			}
			
			if (args[0].equalsIgnoreCase("add")) {
				if(args.length < 2) {
					return false;
				}
				
				if(Bukkit.getServer().getPlayer(args[1]) == null) {
					BGChat.printPlayerChat(p, ChatColor.RED + Translation.PLAYER_NOT_ONLINE.t());
					return true;
				}
				
				Player player = Bukkit.getServer().getPlayer(args[1]);
				
				if(BGTeam.isInTeam(p, player.getName())){
					BGChat.printPlayerChat(p, ChatColor.RED + Translation.TEAM_FUNC_PLAYER_ALREADY_TEAM.t());
					return true;
				}
					
				BGTeam.addMember(p, player.getName());
				BGChat.printPlayerChat(p, ChatColor.GREEN + Translation.TEAM_FUNC_ADDED_PLAYER.t());
				
				return true;
			}
			
			if (args[0].equalsIgnoreCase("remove")) {
				if(args.length < 2) {
					return false;
				}
				
				if(!BGTeam.isInTeam(p, args[1])) {
					BGChat.printPlayerChat(p, ChatColor.RED + Translation.TEAM_FUNC_PLAYER_ALREADY_TEAM.t());
					return true;
				}
				
				BGTeam.removeMember(p, args[1]);
				BGChat.printPlayerChat(p, ChatColor.GREEN + Translation.TEAM_FUNC_REMOVED_PLAYER.t());
				return true;
			}
			
			if (args[0].equalsIgnoreCase("list")) {
				if(args.length != 1) {
					return false;
				}
								
				StringBuilder text = new StringBuilder(ChatColor.YELLOW + Translation.TEAM_FUNC_YOUR_TEAM.t());
				
				if(Objects.requireNonNull(BGTeam.getTeamList(p)).isEmpty()) {
					text.append(" Nobody");
					BGChat.printPlayerChat(p, text.toString());
					return true;
				}
				
				for(String t : Objects.requireNonNull(BGTeam.getTeamList(p))) {
					text.append(" ").append(t);
				}
				BGChat.printPlayerChat(p, text.toString());
				return true;
			}
		}
		
		
		if(cmd.getName().equalsIgnoreCase("hack")) {
			if(CHG2.isSpectator(p)) {	
				try {
				p.setGameMode(GameMode.SURVIVAL);
				p.setAllowFlight(true);
				p.setFlying(true);
				p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 40, 1));
				p.sendMessage(ChatColor.GREEN+"Ahora eres visible durante un momento..");
				if(jug.is_MODERADOR()) {
				Bukkit.getScheduler().runTaskLater(CHG2.instance, new Runnable() {
					@Override
					public void run() {
						p.setGameMode(GameMode.SPECTATOR);
						p.setAllowFlight(true);
						p.setFlying(true);
						p.setFlySpeed(0.2f);
						p.sendMessage(ChatColor.YELLOW+"Ahora vuelves a ser invisible.");
					}
				}, 4L);
				} else {
					Bukkit.getScheduler().runTaskLater(CHG2.instance, new Runnable() {
						@Override
						public void run() {
							p.setGameMode(GameMode.SPECTATOR);
							p.setAllowFlight(true);
							p.setFlying(true);
							p.setFlySpeed(0.2f);
							p.sendMessage(ChatColor.YELLOW+"Ahora vuelves a ser invisible.");
						}
					}, 2L);
				}
				} catch(Exception ex) {
					p.setGameMode(GameMode.SPECTATOR);
					ex.printStackTrace();
				}
			} else {
			p.sendMessage(ChatColor.RED + "Comando solo para espectadores");
			}
		}
		

		if(cmd.getName().equalsIgnoreCase("teleport")) {
			if(CHG2.isSpectator(p)) {
				if(args.length > 2) {
					return false;
				}
				
				if(args.length == 0) {
					BGChat.printPlayerChat(p, ChatColor.YELLOW + Translation.TELEPORT_FUNC_CMDS.t());
					return true;
				}
				
				if(args.length == 1) {
					
					if(Bukkit.getServer().getPlayer(args[0]) == null) {
						BGChat.printPlayerChat(p, ChatColor.RED + Translation.PLAYER_NOT_ONLINE.t());
						return true;
					}
					
					Player target = Bukkit.getServer().getPlayer(args[0]);
					BGChat.printPlayerChat(p, ChatColor.GREEN + Translation.TELEPORT_FUNC_TELEPORTED_PLAYER.t().replace("<player>", target.getName()));
					p.teleport(target);
					return true;
				}

                int x = 0;
                int z = 0;

                try {
                    x = Integer.parseInt(args[0]);
                    z = Integer.parseInt(args[1]);
                } catch(NumberFormatException e) {
                    BGChat.printPlayerChat(p, ChatColor.RED + Translation.TELEPORT_FUNC_COORDS_NOT_VALID.t());
                    return true;
                }

                Location loc = new Location(Bukkit.getServer().getWorlds().get(0), x, Bukkit.getServer().getWorlds().get(0).getHighestBlockYAt(x, z)+1.5, z);
                BGChat.printPlayerChat(p, ChatColor.GREEN + Translation.TELEPORT_FUNC_TELEPORTED_COORDS.t().replace("<x>", x + "").replace("<z>", z + ""));
                p.teleport(loc);
            } else {
				BGChat.printPlayerChat(p, ChatColor.RED + Translation.NO_PERMISSION.t());
            }
            return true;
        }
		
		return true;
	}

	private static int getFaltante(String rango, int kills) {
		int faltante = 0;
		if(rango.equalsIgnoreCase("Nuevo")) {
			faltante = 100 - kills;
		} else if(rango.equalsIgnoreCase("Aprendiz")) {
			faltante = 500 - kills;

		} else if(rango.equalsIgnoreCase("12 héroe")) {
			 faltante = 1000 - kills;

		} else if(rango.equalsIgnoreCase("11 héroe Feroz")) {
			 faltante = 2000 - kills;

		}else if(rango.equalsIgnoreCase("10 Héroe Poderoso")) {
			 faltante = 3000 - kills;

		}else if(rango.equalsIgnoreCase("9 Héroe Mortal")) {
			 faltante = 4000 - kills;

		}else if(rango.equalsIgnoreCase("8 Héroe Terrorífico")) {
			 faltante = 5000 - kills;

		}else if(rango.equalsIgnoreCase("7 Héroe Conquistador")) {
			 faltante = 6000 - kills;

		}else if(rango.equalsIgnoreCase("6 Heroe Renombrado")) {
			 faltante = 7000 - kills;

		}else if(rango.equalsIgnoreCase("5 Héroe ilustre")) {
			 faltante = 8000 - kills;

		}else if(rango.equalsIgnoreCase("4 Héroe Eminente")) {
			 faltante = 9000 - kills;

		}else if(rango.equalsIgnoreCase("3 Rey Héroe")) {
			 faltante = 10000 - kills;

		}else if(rango.equalsIgnoreCase("2 Emperador")) {
			 faltante = 15000 - kills;

		}else if(rango.equalsIgnoreCase("1 Legendario")) {
			 faltante = 20000 - kills;

		}
		return faltante;
	}
}
