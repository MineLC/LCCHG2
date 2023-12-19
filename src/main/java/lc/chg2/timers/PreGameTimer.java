package lc.chg2.timers;

import lc.chg2.utilities.BGChat;
import org.bukkit.Bukkit;
import lc.chg2.CHG2;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class PreGameTimer {

	private static Integer shed_id = null;
	public static boolean started = false;
	
	public PreGameTimer() {
		started = true;
		shed_id = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(CHG2.instance, new Runnable() {
			
			public void run() {
				if (CHG2.COUNTDOWN.intValue() > 0) {
					if (CHG2.COUNTDOWN >= 30 & CHG2.COUNTDOWN % 10 == 0) {
						BGChat.printTimeChat("El juego comienza en "
								+ CHG2.TIME(CHG2.COUNTDOWN) + ".");
						for (Player pl : CHG2.getGamers()) {
							if (CHG2.getGamers().size() >= Bukkit.getServer().getMaxPlayers()){
								CHG2.COUNTDOWN = 26;
								pl.playSound(pl.getLocation(), Sound.LEVEL_UP, 1, 2);
							}
						}
						} else if (CHG2.COUNTDOWN <= 25 && CHG2.COUNTDOWN > 10 & CHG2.COUNTDOWN % 5 == 0) {
							BGChat.printTimeChat("El juego comienza en "
									+ CHG2.TIME(CHG2.COUNTDOWN) + ".");
						}
						else if (CHG2.COUNTDOWN <= 10 && CHG2.COUNTDOWN > 3) {
							BGChat.printTimeChat("El juego comienza en "
									+ CHG2.TIME(CHG2.COUNTDOWN) + ".");
						} else if (CHG2.COUNTDOWN <= 3) {
						BGChat.printTimeChat("El juego comienza en "
								+ CHG2.TIME(CHG2.COUNTDOWN) + ".");
						for (Player pl : CHG2.getGamers()) {
							pl.playSound(pl.getLocation(), Sound.NOTE_PLING, 1, -1);
						}
					}

					CHG2.COUNTDOWN--;
				} else if (CHG2.getGamers().size() < CHG2.MINIMUM_PLAYERS.intValue()) {
					BGChat.printTimeChat("Esperando mas jugadores..");
					CHG2.COUNTDOWN = CHG2.COUNTDOWN_SECONDS;
				} else {
					CHG2.startgame();
				}
			}
		}, 0, 20);
	}
	
	public static void cancel() {
		if(shed_id != null) {
			Bukkit.getServer().getScheduler().cancelTask(shed_id);
			shed_id = null;
		}
	}
	
}
