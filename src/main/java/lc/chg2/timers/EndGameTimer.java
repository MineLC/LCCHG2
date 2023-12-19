package lc.chg2.timers;

import lc.chg2.CHG2;
import lc.chg2.utilities.BGChat;
import lc.chg2.utilities.BGFBattle;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.World;

public class EndGameTimer {
  private static Integer shed_id = null;
  
  public EndGameTimer() {
    shed_id = Integer.valueOf(Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(CHG2.instance, new Runnable() {
      public void run() {
        World w = Bukkit.getWorlds().get(0);
        w.setDifficulty(Difficulty.HARD);
        w.strikeLightning(CHG2.spawn.clone().add(0.0D, 50.0D, 0.0D));
        BGChat.printInfoChat(ChatColor.RED +""+ChatColor.BOLD +"Batalla Final");
        BGChat.printInfoChat("Teletransportando al spawn.");
        CHG2.log.info("Game phase: 4 - Final");
        BGFBattle.teleportGamers(CHG2.getGamers());
      }
    }, 0L, 1200L));
  }
  
  public static void cancel() {
    if (shed_id != null) {
      Bukkit.getServer().getScheduler().cancelTask(shed_id.intValue());
      shed_id = null;
    }
  }
}
