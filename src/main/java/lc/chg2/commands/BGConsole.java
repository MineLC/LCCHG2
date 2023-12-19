package lc.chg2.commands;

import lc.chg2.CHG2;
import lc.chg2.timers.EndGameTimer;
import lc.chg2.utilities.BGChat;
import lc.chg2.utilities.BGFBattle;
import lc.chg2.utilities.enums.GameState;
import lc.chg2.utilities.enums.Translation;
import lc.core2.entities.Jugador;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BGConsole implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player)) {
			return true;
		}
		Player p = (Player) sender;
		Jugador jug = Jugador.getJugador(p);

		if (cmd.getName().equalsIgnoreCase("start")) {
			if (jug.is_Admin()) {
				if (CHG2.GAMESTATE != GameState.PREGAME)
					msg(p, sender, ChatColor.RED + Translation.GAME_BEGUN.t());
				else
					CHG2.startgame();
			} else {
				msg(p, sender, ChatColor.RED + Translation.NO_PERMISSION.t());
			}
			return true;
		}
	    if (cmd.getName().equalsIgnoreCase("fbattle")) {
			if (jug.is_Admin()) {
	        	if (CHG2.GAMESTATE == GameState.GAME) {
	          		if (CHG2.END_GAME) {
						CHG2.END_GAME = false;
						BGFBattle.createBattle();
						new EndGameTimer();

						return true;
	          		}
					BGChat.printPlayerChat(p, ChatColor.RED + "No tienes permiso!");
				    return true;
				}
				BGChat.printPlayerChat(p, "El juego no ha comenzado!");
				return true;
	      }
	      BGChat.printPlayerChat(p, ChatColor.RED + Translation.NO_PERMISSION.t());
	      return true;
	    }
	    return true;
	}
	
	private static void msg(Player p, CommandSender s, String msg) {
		if(p == null)
			s.sendMessage(msg);
		else
			BGChat.printPlayerChat(p, msg);
	}
}