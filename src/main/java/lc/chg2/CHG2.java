package lc.chg2;

import com.google.common.collect.Lists;
import lc.chg2.commands.BGConsole;
import lc.chg2.commands.BGPlayer;
import lc.chg2.events.BGAbilitiesListener;
import lc.chg2.events.BGGameListener;
import lc.chg2.events.encantos;
import lc.chg2.timers.GameTimer;
import lc.chg2.timers.InvincibilityTimer;
import lc.chg2.timers.PreGameTimer;
import lc.chg2.utilities.BGChat;
import lc.chg2.utilities.BGFiles;
import lc.chg2.utilities.BGKit;
import lc.chg2.utilities.enums.GameState;
import lc.chg2.utilities.enums.Translation;
import lc.core2.entities.Database;
import lc.core2.entities.Jugador;
import lc.core2.entities.Ranks;
import lc.core2.utils.KickType;
import lc.core2.utils.Util;
import org.bukkit.*;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.io.*;
import java.util.*;
import java.util.logging.Logger;

public final class CHG2 extends JavaPlugin {

    public static GameState GAMESTATE = GameState.PREGAME;
    public static String HELP_MESSAGE = null;
    public static String SERVER_FULL_MSG = "";
    public static String GAME_IN_PROGRESS_MSG = "";
    public static String MOTD_PROGRESS_MSG = "";
    public static String MOTD_COUNTDOWN_MSG = "";
    public static String NO_KIT_MSG = "";
    public static String SERVER_TITLE = null;
    public static Integer COUNTDOWN_SECONDS = 120;
    public static Integer FINAL_COUNTDOWN_SECONDS = 60;
    public static Integer END_GAME_TIME = 1;
    public static Integer MAX_GAME_RUNNING_TIME = 60;
    public static Integer MINIMUM_PLAYERS = 4;
    public static Integer GAME_ENDING_TIME = 50;
    public static Boolean DEFAULT_KIT = false;
    public static Boolean END_GAME = true;
    public static Location spawn;
    public static ArrayList<Player> spectators = new ArrayList<Player>();
    public static Integer COUNTDOWN = 0;
    public static Integer FINAL_COUNTDOWN = 0;
    public static Integer GAME_RUNNING_TIME = 0;
    public static Integer WORLDRADIUS = 250;
    public static CHG2 instance;
    public static HashMap<String, Integer> kills = new HashMap<String, Integer>();
    public static Logger log = Bukkit.getLogger();
    public static HashMap<String, Team> TEAMS = new HashMap<String, Team>();
    public static String mapa = "default";
    public static LinkedList<Player> gamers = new LinkedList<Player>();
    public static String ganador = "nadie";
    public static SplittableRandom random = new SplittableRandom();
    public static HashMap<Player, Integer> Fame = new HashMap<Player, Integer>();

    public static Integer GetFame(Player p){
        return Fame.get(p);
    }
    public static void setFame(Player p, Integer a){
        Fame.remove(p);
        Fame.put(p, a);
    }
    public static void AddFame(Player p){
        if(!Fame.containsKey(p)){
            Fame.put(p, 0);
        }
        Integer fame = Fame.get(p);
        if(fame < 4 ){
            fame = fame + 1;
        } else if(fame < 8){
            fame = fame + 2;
        } else if(fame < 40){
            fame = fame + 4;
        } else {
            fame = fame + 40;
        }
        p.sendMessage(ChatColor.GREEN + "Has recibido " + Integer.toString(fame) + " de Fama.");
        Integer total = Fame.get(p) + fame;
        Fame.remove(p);
        Fame.put(p, total);
        Jugador jug = Jugador.getJugador(p);
        jug.setCHG_Fame(jug.getCHG_Fame() + fame);
        Database.saveCHGFame(jug);
    }

    public void LoadScoreboard(){

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            int count = 0;
            @Override
            public void run() {
                count++;
                if (count < 30) {
                    for (Player all : Bukkit.getOnlinePlayers()) {
                        updateScoreboard(all);
                    }
                }else if(count == 30){
                    for (Player all : Bukkit.getOnlinePlayers()) {
                        setTopScoreboard(all);
                    }
                }else {
                    //scoreboard top
                    for (Player all : Bukkit.getOnlinePlayers()) {
                        updateTopScoreboard(all);
                    }
                }

                if(count >= 40){
                    for (Player all : Bukkit.getOnlinePlayers()) {
                        setScoreboard(all);
                    }
                    count = 0;
                }

            }
        }, 0, 20 );
    }

    public void onLoad() {
        instance = this;
        try {
            new BGFiles();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Bukkit.getServer().unloadWorld("world", false);
        deleteDir(new File("world"));

        File[] maps = new File("CHG/mapas").listFiles();

        File map = maps[random.nextInt(maps.length)];

        log.info("Copiando mapa. ("+map.getName()+")");
        mapa = map.getName();
        try {
            copyDirectory(map, new File("world"));
        } catch (IOException e) {
            log.warning("Error: " + e.toString());
        }
        CHG2.WORLDRADIUS = 300;

    }

    private void registerEvents() {
        BGGameListener gl = new BGGameListener();
        getLogger().info("gl");
        encantos gg = new encantos();
        getLogger().info("gg");
        BGAbilitiesListener al = new BGAbilitiesListener();
        getLogger().info("al");
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(gl, this);
        getLogger().info("glr");
        pm.registerEvents(gg, this);
        getLogger().info("gfr");
        pm.registerEvents(al, this);
        getLogger().info("alr");
    }


    public void registerCommands() {
        ConsoleCommandSender console = Bukkit.getConsoleSender();
        if (getCommand("help") != null)
            getCommand("help").setExecutor(new BGPlayer());
        else
            console.sendMessage(ChatColor.RED+"getCommand help returns null");

        if (getCommand("kit") != null)
            getCommand("kit").setExecutor(new BGPlayer());
        else
            console.sendMessage(ChatColor.RED+"getCommand kit returns null");

        if (getCommand("rank") != null)
            getCommand("rank").setExecutor(new BGPlayer());
        else
            console.sendMessage(ChatColor.RED+"getCommand rank returns null");

        if (getCommand("kitinfo") != null)
            getCommand("kitinfo").setExecutor(new BGPlayer());
        else
            console.sendMessage(ChatColor.RED+"getCommand kitinfo returns null");

        if (getCommand("start") != null)
            getCommand("start").setExecutor(new BGConsole());
        else
            console.sendMessage(ChatColor.RED+"getCommand start returns null");

        if (getCommand("spawn") != null)
            getCommand("spawn").setExecutor(new BGPlayer());
        else
            console.sendMessage(ChatColor.RED+"getCommand spawn returns null");

        if (getCommand("desbug") != null)
            getCommand("desbug").setExecutor(new BGPlayer());
        else
            console.sendMessage(ChatColor.RED+"getCommand desbug returns null");

        if (getCommand("hack") != null)
            getCommand("hack").setExecutor(new BGPlayer());
        else
            console.sendMessage("");

        if (getCommand("fbattle") != null) {
            getCommand("fbattle").setExecutor(new BGConsole());
        } else {
            console.sendMessage(ChatColor.RED + "getCommand fbattle returns null");
        }
        if(getCommand("team") != null)
            getCommand("team").setExecutor(new BGPlayer());
        else
            console.sendMessage(ChatColor.RED+"getCommand team returns null");
        if (getCommand("gamemaker") != null) {
            getCommand("gamemaker").setExecutor(new BGPlayer());
        } else {
            console.sendMessage(ChatColor.RED + "getCommand gamemaker returns null");
        }
        if (getCommand("vanish") != null) {
            getCommand("vanish").setExecutor(new BGPlayer());
        } else {
            console.sendMessage(ChatColor.RED + "getCommand vanish returns null");
        }
        if(getCommand("teleport") != null)
            getCommand("teleport").setExecutor(new BGPlayer());
        else
            console.sendMessage(ChatColor.RED+"getCommand teleport returns null");
    }

    @SuppressWarnings("InstantiationOfUtilityClass")
    public void onEnable() {
        instance = this;
        Bukkit.getServer().getWorlds().get(0).setDifficulty(Difficulty.PEACEFUL);
        log = Bukkit.getLogger();

        log.info("Loading configuration options.");
        SERVER_TITLE = getConfig().getString("MESSAGE.SERVER_TITLE");
        HELP_MESSAGE = getConfig().getString("MESSAGE.HELP_MESSAGE");
        DEFAULT_KIT = Boolean.valueOf(getConfig().getBoolean("DEFAULT_KIT"));
        NO_KIT_MSG = getConfig().getString("MESSAGE.NO_KIT_PERMISSION");
        GAME_IN_PROGRESS_MSG = getConfig().getString("MESSAGE.GAME_PROGRESS");
        SERVER_FULL_MSG = getConfig().getString("MESSAGE.SERVER_FULL");
        MOTD_PROGRESS_MSG = getConfig().getString("MESSAGE.MOTD_PROGRESS");
        MOTD_COUNTDOWN_MSG = getConfig().getString("MESSAGE.MOTD_COUNTDOWN");
        MINIMUM_PLAYERS = Integer.valueOf(getConfig().getInt("MINIMUM_PLAYERS_START"));
        MAX_GAME_RUNNING_TIME = Integer.valueOf(getConfig().getInt("TIME.MAX_GAME-MIN"));
        FINAL_COUNTDOWN_SECONDS = Integer.valueOf(getConfig().getInt("TIME.FINAL_COUNTDOWN-SEC"));
        END_GAME_TIME = Integer.valueOf(getConfig().getInt("TIME.INCREASE_DIFFICULTY-MIN"));

        CHG2.copy(CHG2.instance.getResource("en.yml"), new File(CHG2.instance.getDataFolder(), "lang.yml"));
        Translation.e = YamlConfiguration.loadConfiguration(new File(CHG2.instance.getDataFolder(), "lang.yml"));

        if(CHG2.WORLDRADIUS < 60) {
            log.warning("Worldborder radius has to be 60 or higher!");
            CHG2.WORLDRADIUS = 100;
        }

        registerEvents();
        registerCommands();
        getLogger().info("cmd");
        new BGKit();
        getLogger().info("kit");
        new BGChat();
        getLogger().info("chat");

        World world = Bukkit.getServer().getWorlds().get(0);
        spawn = world.getSpawnLocation();
        world.setAutoSave(true);
        WorldBorder wb = world.getWorldBorder();
        wb.setCenter(spawn);
        wb.setWarningDistance(15);
        wb.setSize(250);
        world.setTime(6000);
        world.setGameRuleValue("doDaylightCycle", "false");
        world.setGameRuleValue("spectatorsGenerateChunks", "false");
        world.setGameRuleValue("KeepInventory", "false");
        getLogger().info("world");
        COUNTDOWN = COUNTDOWN_SECONDS;
        FINAL_COUNTDOWN = FINAL_COUNTDOWN_SECONDS;
        GAME_RUNNING_TIME = 0;
        CHG2.GAMESTATE = GameState.PREGAME;
        log.info("Fase De Juego: 1 - Esperando");
        String command = "minecraft:gamerule sendCommandFeedback false";
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
        getLogger().info("start");
        LoadScoreboard();
        getLogger().info("scorreload");
    }
    public void onDisable() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if(!ganador.equals("nadie"))
                Util.kick(p, KickType.BY_SERVER, null, "&e¡El juego ha terminado!", "&eEl ganador es: &f"+ganador+"&e.", " ", "&9Reiniciando el servidor...");
            else Util.kick(p, KickType.BY_SERVER, null, "&f¡El juego ha terminado sin un ganador!", "&9Reiniciando el servidor...");
        }

        Bukkit.getServer().getScheduler().cancelAllTasks();
    }

    public static void startgame() {
        Fame.clear();
        log.info("Fase de juego: 2 - Comenzando");
        PreGameTimer.cancel();
        new InvincibilityTimer();

        CHG2.GAMESTATE = GameState.INVINCIBILITY;
        World world = Bukkit.getWorlds().get(0);
        world.setAutoSave(false);
        world.setDifficulty(Difficulty.NORMAL);
        world.setTime(0L);
        List<Location> randomlocs = Lists.newArrayList();
        randomlocs.add(getSpawn().clone().add(0,0,1));
        randomlocs.add(getSpawn().clone().add(0,0,2));
        randomlocs.add(getSpawn().clone().add(0,0,3));
        randomlocs.add(getSpawn().clone().add(0,0,4));

        randomlocs.add(getSpawn().clone().add(0,0,-1));
        randomlocs.add(getSpawn().clone().add(0,0,-2));
        randomlocs.add(getSpawn().clone().add(0,0,-3));
        randomlocs.add(getSpawn().clone().add(0,0,-4));

        randomlocs.add(getSpawn().clone().add(1,0,0));
        randomlocs.add(getSpawn().clone().add(2,0,0));
        randomlocs.add(getSpawn().clone().add(3,0,0));
        randomlocs.add(getSpawn().clone().add(4,0,0));

        randomlocs.add(getSpawn().clone().add(-1,0,0));
        randomlocs.add(getSpawn().clone().add(-2,0,0));
        randomlocs.add(getSpawn().clone().add(-3,0,0));
        randomlocs.add(getSpawn().clone().add(-4,0,0));

        for (Player p : Bukkit.getOnlinePlayers()) {
            if(isSpectator(p))
                continue;
            if(p.isInsideVehicle())
                p.getVehicle().eject();
            p.teleport(randomlocs.get(random.nextInt(16)));

            p.setFlying(false);
            p.setFireTicks(0);
            p.setAllowFlight(false);
            BGKit.giveKit(p);
        }

        BGChat.printTimeChat(Translation.GAMES_HAVE_BEGUN.t());
        BGChat.printTimeChat(Translation.INVINCIBLE_FOR.t().replace("<time>", TIME(FINAL_COUNTDOWN_SECONDS)));
        world.getWorldBorder().setSize(550);
        world.getWorldBorder().setSize(100, 600);
    }

    public static void addBalance(Jugador jug, int x) {
        jug.getBukkitPlayer().playSound(jug.getBukkitPlayer().getLocation(), Sound.NOTE_PLING, 1f, 1.3f);
        jug.getBukkitPlayer().sendMessage(ChatColor.GOLD+"+"+x+" LCoins");
        jug.addLcoins(x);
        Database.savePlayerCoins(jug);
    }

    private void copyDirectory(File sourceLocation, File targetLocation)
            throws IOException {

        if (sourceLocation.isDirectory()) {
            if (!targetLocation.exists()) {
                targetLocation.mkdir();
            }

            String[] children = sourceLocation.list();
            for (int i = 0; i < children.length; i++) {
                copyDirectory(new File(sourceLocation, children[i]), new File(
                        targetLocation, children[i]));
            }
        } else {

            InputStream in = new FileInputStream(sourceLocation);
            OutputStream out = new FileOutputStream(targetLocation);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
        }
    }

    public static void copy(InputStream in, File file) {
        try {
            OutputStream out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            out.close();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Location getSpawn() {
        return spawn;
    }

    public static LinkedList<Player> getGamers() {
        return gamers;
    }

    public static void checkwinner() {
        if (getGamers().size() <= 1) {
            if (getGamers().size() == 0) {
                GameTimer.cancel();
                Bukkit.getServer().shutdown();
            } else {
                GameTimer.cancel();
                final Jugador jug = Jugador.getJugador(getGamers().get(0));
                ganador = jug.getPlayerName();
                jug.addCHG_Stats_Partidas_ganadas(1);
                jug.addCHG_Stats_Partidas_jugadas(1);
                jug.setCHG_Winner(true);
                Database.savePlayerSV_CHG(jug);
                String title = "title " +jug.getBukkitPlayer().getName()+ " title [{\"text\":\"Ganaste!\",\"color\":\"gold\"}]";
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), title);
                Location loc = jug.getBukkitPlayer().getEyeLocation();
                spawnRandomFirework(loc.clone().add(1, 3, 1));
                spawnRandomFirework(loc.clone().add(-1, 3, -1));
                spawnRandomFirework(loc.clone().add(1, 3, -1));
                spawnRandomFirework(loc.clone().add(-1, 3, 1));
                //	Util.sendTitle(jug.getBukkitPlayer(), 20, 50, 20, ChatColor.GREEN+"Ganador", ChatColor.AQUA+"Eres el ganador del juego!");
                jug.getBukkitPlayer().playSound(loc, Sound.LEVEL_UP, 1F, 2.5F);
                int winTotal = 10;
                if (jug.is_RUBY()) {
                    winTotal = 50;
                } else if (jug.is_ELITE()) {
                    winTotal = 40;
                } else if (jug.is_SVIP()) {
                    winTotal = 30;
                } else if (jug.is_VIP()) {
                    winTotal = 20;
                }
                addBalance(jug, winTotal);
                Bukkit.getScheduler().scheduleSyncDelayedTask(instance, new Runnable() {
                    public void run() {
                        Bukkit.shutdown();
                    }
                },  200L);
                for(Player Online : Bukkit.getOnlinePlayers()) {
                    Online.sendMessage(ChatColor.GREEN+""+ChatColor.BOLD+ChatColor.STRIKETHROUGH+"----------------------------------");
                    Online.sendMessage(ChatColor.GOLD+""+ChatColor.BOLD+"                      HG");
                    Online.sendMessage("");
                    Online.sendMessage(ChatColor.YELLOW+"                   Ganador: "+ChatColor.GRAY+jug.getBukkitPlayer().getName());
                    Online.sendMessage("");
                    SayKillWinners(Online);
                    Online.sendMessage("");
                    Online.sendMessage(ChatColor.GREEN+""+ChatColor.BOLD+ChatColor.STRIKETHROUGH+"----------------------------------");
                }
            }
        }
    }

    public static void SayKillWinners(Player p) {
        Map<String, Integer> asesinatos = sortByValue(kills);

        int st = 0;
        ChatColor color = ChatColor.YELLOW;
        for(Map.Entry<String, Integer> pk : asesinatos.entrySet()) {
            st++;
            if(st == 2) {
                color = ChatColor.GOLD;
            } else if(st == 3) {
                color = ChatColor.RED;
            }
            p.sendMessage(color+"             Asesino #"+st+": "+ChatColor.GRAY+pk.getKey() + " - "+pk.getValue());
            if(st >= 3) {
                break;
            }
        }
        //sendGameMessage(ChatColor.BLUE+"            Asesino #1: "+ChatColor.GRAY+jug.getBukkitPlayer().getName());
    }
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static Map<String, Integer> sortByValue(Map<String, Integer> unsortMap) {

        List<Integer> list = new LinkedList(unsortMap.entrySet());

        Collections.sort(list, new Comparator() {
            public int compare(Object o1, Object o2) {
                return ((Comparable) ((Map.Entry) (o1)).getValue())
                        .compareTo(((Map.Entry) (o2)).getValue());
            }
        });
        Collections.reverse(list);

        Map<String, Integer> sortedMap = new LinkedHashMap();
        for (Iterator it = list.iterator(); it.hasNext();) {
            Map.Entry<String, Integer> entry = (Map.Entry<String, Integer>) it.next();
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }


    public static void spawnRandomFirework(Location loc) {
        Firework fw = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
        FireworkMeta fwm = fw.getFireworkMeta();

        int rt = random.nextInt(2) + 1;
        FireworkEffect.Type type = FireworkEffect.Type.STAR;
        if (rt == 1) type = FireworkEffect.Type.STAR;
        if (rt == 2) type = FireworkEffect.Type.STAR;

        Color c1 = Color.RED;
        Color c2 = Color.YELLOW;
        Color c3 = Color.ORANGE;

        FireworkEffect effect = FireworkEffect.builder().flicker(random.nextBoolean()).withColor(c1).withColor(c2).withFade(c3).with(type).trail(random.nextBoolean()).build();
        fwm.addEffect(effect);

        int rp = random.nextInt(3) + 1;
        fwm.setPower(rp);

        fw.setFireworkMeta(fwm);
    }

    public static void deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                deleteDir(new File(dir, children[i]));
            }
        }
        dir.delete();
    }

    public static String TIME(Integer i) {
        if (i.intValue() >= 60) {
            Integer time = Integer.valueOf(i.intValue() / 60);
            String add = "";
            if (time > 1) {
                add = "s";
            }
            return time + ChatColor.GREEN.toString()+" minuto" + add;
        }
        Integer time = i;
        String add = "";
        if (time > 1) {
            add = "s";
        }
        return time + ChatColor.GREEN.toString()+" segundo" + add;
    }


    public static Boolean isSpectator(Player p) {
        return spectators.contains(p);
    }

    public static ArrayList<Player> getSpectators() {
        return spectators;
    }

    public static void addSpectator(final Player p) {
        spectators.add(p);
        Bukkit.getScheduler().runTaskLater(instance, new Runnable() {

            @Override
            public void run() {
                p.setGameMode(GameMode.SPECTATOR);
                p.setAllowFlight(true);
                p.setFlying(true);
            }
        }, 2L);
    }

    public static void remSpectator(Player p) {
        spectators.remove(p);
        p.getInventory().clear();
    }
    @SuppressWarnings("UnnecessaryUnicodeEscape")
    public static void updateTopScoreboard(Player Online) {
        try {
            Jugador jugOnline = Jugador.getJugador(Online);
            Scoreboard sb = Online.getScoreboard();
            if (sb == null || sb.getEntries().isEmpty()) {
                sb = Bukkit.getScoreboardManager().getNewScoreboard();
                jugOnline.getBukkitPlayer().setScoreboard(sb);
                if (TEAMS.containsKey("kills" + Online.getName()))
                    TEAMS.remove("kills" + Online.getName());
                if (TEAMS.containsKey("deaths" + Online.getName()))
                    TEAMS.remove("deaths" + Online.getName());
                if (TEAMS.containsKey("kdr" + Online.getName()))
                    TEAMS.remove("kdr" + Online.getName());
                if (TEAMS.containsKey("jugadores" + Online.getName()))
                    TEAMS.remove("jugadores" + Online.getName());
            }
            if (!TEAMS.containsKey("kills" + Online.getName())) {
                Team tm = sb.registerNewTeam(ChatColor.YELLOW + " ");
                tm.addEntry(ChatColor.YELLOW + " ");
                TEAMS.put("kills" + Online.getName(), tm);
            }
            if (!TEAMS.containsKey("deaths" + Online.getName())) {
                Team tm = sb.registerNewTeam(ChatColor.RED + " ");
                tm.addEntry(ChatColor.RED + " ");
                TEAMS.put("deaths" + Online.getName(), tm);
            }
            if (!TEAMS.containsKey("kdr" + Online.getName())) {
                Team tm = sb.registerNewTeam(ChatColor.GREEN + " ");
                tm.addEntry(ChatColor.GREEN + " ");
                TEAMS.put("kdr" + Online.getName(), tm);
            }
            if (!TEAMS.containsKey("lvl" + Online.getName())) {
                Team tm = sb.registerNewTeam(ChatColor.AQUA + " ");
                tm.addEntry(ChatColor.AQUA + " ");
                TEAMS.put("lvl" + Online.getName(), tm);
            }
            Objective objHealth = sb.getObjective("ShowHealth");
            if (objHealth == null) {
                objHealth = sb.registerNewObjective("ShowHealth", "health");
                objHealth.setDisplaySlot(DisplaySlot.BELOW_NAME);
                objHealth.setDisplayName(ChatColor.RED + "\u2764");
            }
            Objective objGame = sb.getObjective("Game");
            if (objGame == null) {
                // cargar top
                HashMap<String, Integer> top = Database.getTop(5, "stats_kills" , "SV_CHG");

                objGame = sb.registerNewObjective("Game", "dummy");

                objGame.setDisplaySlot(DisplaySlot.SIDEBAR);

                objGame.setDisplayName(ChatColor.GOLD+""+ChatColor.BOLD+"Top Kills");
                int x = top.size()+3;
                for(String str : top.keySet()){
                    objGame.getScore(ChatColor.GREEN+ str + ChatColor.GRAY +": "+ ChatColor.RED + top.get(str) + "").setScore(x--);
                }
                objGame.getScore("   ").setScore(2);
                objGame.getScore(ChatColor.YELLOW + "play.minelc.net").setScore(1);
            }
            ((Team)TEAMS.get("kills" + Online.getName())).setPrefix(ChatColor.YELLOW + ""+jugOnline.getCHG_Stats_kills());
            ((Team)TEAMS.get("deaths" + Online.getName())).setPrefix(ChatColor.RED + ""+jugOnline.getCHG_Stats_deaths());
            ((Team)TEAMS.get("kdr" + Online.getName())).setPrefix(ChatColor.GREEN + ""+jugOnline.getCHG_Stats_kdr());
            ((Team)TEAMS.get("lvl" + Online.getName())).setPrefix(ChatColor.AQUA +""+ jugOnline.getCHG_Stats_Level());
            for (Player tmOnline : Bukkit.getOnlinePlayers()) {
                Jugador jugTM = Jugador.getJugador(tmOnline);
                try {
                    Team tm = sb.getTeam(jugTM.getBukkitPlayer().getName());
                    if (tm != null)
                        continue;
                    tm = sb.registerNewTeam(jugTM.getBukkitPlayer().getName());
                    if (jugTM.isHideRank()) {
                        tm.setPrefix(ChatColor.GRAY.toString());
                    } else if (jugTM.is_Owner()) {
                        tm.setPrefix(ChatColor.DARK_RED + ""+ChatColor.BOLD + Ranks.OWNER.name() + " " + jugTM.getNameTagColor());
                    } else if (jugTM.is_Admin()) {
                        tm.setPrefix(ChatColor.RED + ""+ChatColor.BOLD + Ranks.ADMIN.name() + " " + jugTM.getNameTagColor());
                    } else if (jugTM.is_MODERADOR()) {
                        tm.setPrefix(ChatColor.DARK_PURPLE + ""+ChatColor.BOLD + Ranks.MOD.name() + " " + jugTM.getNameTagColor());
                    } else if (jugTM.is_AYUDANTE()) {
                        tm.setPrefix(ChatColor.DARK_PURPLE +""+ChatColor.BOLD + Ranks.AYUDANTE.name() + " " + jugTM.getNameTagColor());
                    } else if (jugTM.is_YOUTUBER()) {
                        String youtuber = ChatColor.RED + ""+ChatColor.BOLD + "YouTuber ";
                        tm.setPrefix(String.valueOf(youtuber) + jugTM.getNameTagColor());
                    } else if (jugTM.is_BUILDER()) {
                        tm.setPrefix(ChatColor.LIGHT_PURPLE + ""+ChatColor.BOLD + Ranks.BUILDER.name() + " " + jugTM.getNameTagColor());
                    } else if (jugTM.is_RUBY()) {
                        tm.setPrefix(ChatColor.RED +""+ChatColor.BOLD + Ranks.RUBY.name() + " " + jugTM.getNameTagColor());
                    } else if (jugTM.is_ELITE()) {
                        tm.setPrefix(ChatColor.GOLD +""+ ChatColor.BOLD + Ranks.ELITE.name() + " " + jugTM.getNameTagColor());
                    } else if (jugTM.is_SVIP()) {
                        tm.setPrefix(ChatColor.GREEN + ""+ChatColor.BOLD + Ranks.SVIP.name() + " " + jugTM.getNameTagColor());
                    } else if (jugTM.is_VIP()) {
                        tm.setPrefix(ChatColor.AQUA + ""+ChatColor.BOLD + Ranks.VIP.name() + " " + jugTM.getNameTagColor());
                    } else if (jugTM.is_Premium()) {
                        tm.setPrefix(ChatColor.YELLOW.toString());
                    } else {
                        tm.setPrefix(ChatColor.GRAY.toString());
                    }
                    tm.addPlayer(tmOnline);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            for (Player tmOnline : Bukkit.getOnlinePlayers()) {
                Scoreboard sbTM = tmOnline.getScoreboard();
                try {
                    if (sbTM == null)
                        continue;
                    Team tm = sbTM.getTeam(jugOnline.getBukkitPlayer().getName());
                    if (tm != null)
                        continue;
                    tm = sbTM.registerNewTeam(jugOnline.getBukkitPlayer().getName());
                    if (jugOnline.isHideRank()) {
                        tm.setPrefix(ChatColor.GRAY.toString());
                    } else if (jugOnline.is_Owner()) {
                        tm.setPrefix(ChatColor.DARK_RED + ""+ChatColor.BOLD + Ranks.OWNER.name() + " " + jugOnline.getNameTagColor());
                    } else if (jugOnline.is_Admin()) {
                        tm.setPrefix(ChatColor.RED + ""+ChatColor.BOLD + Ranks.ADMIN.name() + " " + jugOnline.getNameTagColor());
                    } else if (jugOnline.is_MODERADOR()) {
                        tm.setPrefix(ChatColor.DARK_PURPLE + ""+ChatColor.BOLD + Ranks.MOD.name() + " " + jugOnline.getNameTagColor());
                    } else if (jugOnline.is_AYUDANTE()) {
                        tm.setPrefix(ChatColor.DARK_PURPLE + ""+ChatColor.BOLD + Ranks.AYUDANTE.name() + " " + jugOnline.getNameTagColor());
                    } else if (jugOnline.is_YOUTUBER()) {
                        String youtuber = ChatColor.RED + ""+ChatColor.BOLD + "YouTuber ";
                        tm.setPrefix(String.valueOf(youtuber) + jugOnline.getNameTagColor());
                    } else if (jugOnline.is_BUILDER()) {
                        tm.setPrefix(ChatColor.LIGHT_PURPLE + ""+ChatColor.BOLD + Ranks.BUILDER.name() + " " + jugOnline.getNameTagColor());
                    } else if (jugOnline.is_RUBY()) {
                        tm.setPrefix(ChatColor.RED + ""+ChatColor.BOLD + Ranks.RUBY.name() + " " + jugOnline.getNameTagColor());
                    } else if (jugOnline.is_ELITE()) {
                        tm.setPrefix(ChatColor.GOLD + ""+ChatColor.BOLD + Ranks.ELITE.name() + " " + jugOnline.getNameTagColor());
                    } else if (jugOnline.is_SVIP()) {
                        tm.setPrefix(ChatColor.GREEN + ""+ChatColor.BOLD + Ranks.SVIP.name() + " " + jugOnline.getNameTagColor());
                    } else if (jugOnline.is_VIP()) {
                        tm.setPrefix(ChatColor.AQUA +""+ChatColor.BOLD + Ranks.VIP.name() + " " + jugOnline.getNameTagColor());
                    } else if (jugOnline.is_Premium()) {
                        tm.setPrefix(ChatColor.YELLOW.toString());
                    } else {
                        tm.setPrefix(ChatColor.GRAY.toString());
                    }
                    tm.addPlayer(jugOnline.getBukkitPlayer());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    public static void updateScoreboard(Player Online) {
        try {
            Jugador jugOnline = Jugador.getJugador(Online);
            Scoreboard sb = Online.getScoreboard();
            if (sb == null || sb.getEntries().isEmpty()) {
                sb = Bukkit.getScoreboardManager().getNewScoreboard();
                jugOnline.getBukkitPlayer().setScoreboard(sb);
                if (TEAMS.containsKey("kills" + Online.getName()))
                    TEAMS.remove("kills" + Online.getName());
                if (TEAMS.containsKey("deaths" + Online.getName()))
                    TEAMS.remove("deaths" + Online.getName());
                if (TEAMS.containsKey("kdr" + Online.getName()))
                    TEAMS.remove("kdr" + Online.getName());
                if (TEAMS.containsKey("jugadores" + Online.getName()))
                    TEAMS.remove("jugadores" + Online.getName());
            }
            if (!TEAMS.containsKey("kills" + Online.getName())) {
                Team tm = sb.registerNewTeam(ChatColor.YELLOW + " ");
                tm.addEntry(ChatColor.YELLOW + " ");
                TEAMS.put("kills" + Online.getName(), tm);
            }
            if (!TEAMS.containsKey("deaths" + Online.getName())) {
                Team tm = sb.registerNewTeam(ChatColor.RED + " ");
                tm.addEntry(ChatColor.RED + " ");
                TEAMS.put("deaths" + Online.getName(), tm);
            }
            if (!TEAMS.containsKey("kdr" + Online.getName())) {
                Team tm = sb.registerNewTeam(ChatColor.GREEN + " ");
                tm.addEntry(ChatColor.GREEN + " ");
                TEAMS.put("kdr" + Online.getName(), tm);
            }
            if (!TEAMS.containsKey("lvl" + Online.getName())) {
                Team tm = sb.registerNewTeam(ChatColor.AQUA + " ");
                tm.addEntry(ChatColor.AQUA + " ");
                TEAMS.put("lvl" + Online.getName(), tm);
            }
            Objective objHealth = sb.getObjective("ShowHealth");
            if (objHealth == null) {
                objHealth = sb.registerNewObjective("ShowHealth", "health");
                objHealth.setDisplaySlot(DisplaySlot.BELOW_NAME);
                objHealth.setDisplayName(ChatColor.RED + "\u2764");
            }
            Objective objGame = sb.getObjective("Game");
            if (objGame == null) {
                objGame = sb.registerNewObjective("Game", "dummy");

                objGame.setDisplaySlot(DisplaySlot.SIDEBAR);

                objGame.setDisplayName(ChatColor.GOLD+""+ChatColor.BOLD+"CHG");

                //kills
                objGame.getScore(" ").setScore(14);
                objGame.getScore(ChatColor.YELLOW+""+ChatColor.BOLD+"Asesinatos").setScore(13);
                objGame.getScore(TEAMS.get("kills"+Online.getName()).getName()).setScore(12);
                //kills
                objGame.getScore("  ").setScore(11);
                objGame.getScore(ChatColor.RED+""+ChatColor.BOLD+"Muertes").setScore(10);
                objGame.getScore(TEAMS.get("deaths"+Online.getName()).getName()).setScore(9);
                //players
                objGame.getScore("   ").setScore(8);

                objGame.getScore("   ").setScore(7);
                objGame.getScore(ChatColor.DARK_PURPLE+""+ChatColor.BOLD+"KDR").setScore(4);
                objGame.getScore(TEAMS.get("kdr"+Online.getName()).getName()).setScore(3);
                objGame.getScore("     ").setScore(2);
                objGame.getScore(ChatColor.YELLOW + "play.minelc.net").setScore(1);
            }
            ((Team)TEAMS.get("kills" + Online.getName())).setPrefix(ChatColor.YELLOW + ""+jugOnline.getCHG_Stats_kills());
            ((Team)TEAMS.get("deaths" + Online.getName())).setPrefix(ChatColor.RED + ""+jugOnline.getCHG_Stats_deaths());
            ((Team)TEAMS.get("kdr" + Online.getName())).setPrefix(ChatColor.GREEN + ""+jugOnline.getCHG_Stats_kdr());
            ((Team)TEAMS.get("lvl" + Online.getName())).setPrefix(ChatColor.AQUA +""+ jugOnline.getCHG_Stats_Level());
            for (Player tmOnline : Bukkit.getOnlinePlayers()) {
                Jugador jugTM = Jugador.getJugador(tmOnline);
                try {
                    Team tm = sb.getTeam(jugTM.getBukkitPlayer().getName());
                    if (tm != null)
                        continue;
                    tm = sb.registerNewTeam(jugTM.getBukkitPlayer().getName());
                    if (jugTM.isHideRank()) {
                        tm.setPrefix(ChatColor.GRAY.toString());
                    } else if (jugTM.is_Owner()) {
                        tm.setPrefix(ChatColor.DARK_RED + ""+ChatColor.BOLD + Ranks.OWNER.name() + " " + jugTM.getNameTagColor());
                    } else if (jugTM.is_Admin()) {
                        tm.setPrefix(ChatColor.RED + ""+ChatColor.BOLD + Ranks.ADMIN.name() + " " + jugTM.getNameTagColor());
                    } else if (jugTM.is_MODERADOR()) {
                        tm.setPrefix(ChatColor.DARK_PURPLE + ""+ChatColor.BOLD + Ranks.MOD.name() + " " + jugTM.getNameTagColor());
                    } else if (jugTM.is_AYUDANTE()) {
                        tm.setPrefix(ChatColor.DARK_PURPLE +""+ChatColor.BOLD + Ranks.AYUDANTE.name() + " " + jugTM.getNameTagColor());
                    } else if (jugTM.is_YOUTUBER()) {
                        String youtuber = ChatColor.RED + ""+ChatColor.BOLD + "YouTuber ";
                        tm.setPrefix(String.valueOf(youtuber) + jugTM.getNameTagColor());
                    } else if (jugTM.is_BUILDER()) {
                        tm.setPrefix(ChatColor.LIGHT_PURPLE + ""+ChatColor.BOLD + Ranks.BUILDER.name() + " " + jugTM.getNameTagColor());
                    } else if (jugTM.is_RUBY()) {
                        tm.setPrefix(ChatColor.RED +""+ChatColor.BOLD + Ranks.RUBY.name() + " " + jugTM.getNameTagColor());
                    } else if (jugTM.is_ELITE()) {
                        tm.setPrefix(ChatColor.GOLD +""+ ChatColor.BOLD + Ranks.ELITE.name() + " " + jugTM.getNameTagColor());
                    } else if (jugTM.is_SVIP()) {
                        tm.setPrefix(ChatColor.GREEN + ""+ChatColor.BOLD + Ranks.SVIP.name() + " " + jugTM.getNameTagColor());
                    } else if (jugTM.is_VIP()) {
                        tm.setPrefix(ChatColor.AQUA + ""+ChatColor.BOLD + Ranks.VIP.name() + " " + jugTM.getNameTagColor());
                    } else if (jugTM.is_Premium()) {
                        tm.setPrefix(ChatColor.YELLOW.toString());
                    } else {
                        tm.setPrefix(ChatColor.GRAY.toString());
                    }
                    tm.addPlayer(tmOnline);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            for (Player tmOnline : Bukkit.getOnlinePlayers()) {
                Scoreboard sbTM = tmOnline.getScoreboard();
                try {
                    if (sbTM == null)
                        continue;
                    Team tm = sbTM.getTeam(jugOnline.getBukkitPlayer().getName());
                    if (tm != null)
                        continue;
                    tm = sbTM.registerNewTeam(jugOnline.getBukkitPlayer().getName());
                    if (jugOnline.isHideRank()) {
                        tm.setPrefix(ChatColor.GRAY.toString());
                    } else if (jugOnline.is_Owner()) {
                        tm.setPrefix(ChatColor.DARK_RED + ""+ChatColor.BOLD + Ranks.OWNER.name() + " " + jugOnline.getNameTagColor());
                    } else if (jugOnline.is_Admin()) {
                        tm.setPrefix(ChatColor.RED + ""+ChatColor.BOLD + Ranks.ADMIN.name() + " " + jugOnline.getNameTagColor());
                    } else if (jugOnline.is_MODERADOR()) {
                        tm.setPrefix(ChatColor.DARK_PURPLE + ""+ChatColor.BOLD + Ranks.MOD.name() + " " + jugOnline.getNameTagColor());
                    } else if (jugOnline.is_AYUDANTE()) {
                        tm.setPrefix(ChatColor.DARK_PURPLE + ""+ChatColor.BOLD + Ranks.AYUDANTE.name() + " " + jugOnline.getNameTagColor());
                    } else if (jugOnline.is_YOUTUBER()) {
                        String youtuber = ChatColor.RED + ""+ChatColor.BOLD + "YouTuber ";
                        tm.setPrefix(String.valueOf(youtuber) + jugOnline.getNameTagColor());
                    } else if (jugOnline.is_BUILDER()) {
                        tm.setPrefix(ChatColor.LIGHT_PURPLE + ""+ChatColor.BOLD + Ranks.BUILDER.name() + " " + jugOnline.getNameTagColor());
                    } else if (jugOnline.is_RUBY()) {
                        tm.setPrefix(ChatColor.RED + ""+ChatColor.BOLD + Ranks.RUBY.name() + " " + jugOnline.getNameTagColor());
                    } else if (jugOnline.is_ELITE()) {
                        tm.setPrefix(ChatColor.GOLD + ""+ChatColor.BOLD + Ranks.ELITE.name() + " " + jugOnline.getNameTagColor());
                    } else if (jugOnline.is_SVIP()) {
                        tm.setPrefix(ChatColor.GREEN + ""+ChatColor.BOLD + Ranks.SVIP.name() + " " + jugOnline.getNameTagColor());
                    } else if (jugOnline.is_VIP()) {
                        tm.setPrefix(ChatColor.AQUA +""+ChatColor.BOLD + Ranks.VIP.name() + " " + jugOnline.getNameTagColor());
                    } else if (jugOnline.is_Premium()) {
                        tm.setPrefix(ChatColor.YELLOW.toString());
                    } else {
                        tm.setPrefix(ChatColor.GRAY.toString());
                    }
                    tm.addPlayer(jugOnline.getBukkitPlayer());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    @SuppressWarnings("deprecation")
    public static void setScoreboard(Player Online) {
        try {
            Jugador jugOnline = Jugador.getJugador(Online);
            Scoreboard sb = Bukkit.getScoreboardManager().getNewScoreboard();


            jugOnline.getBukkitPlayer().setScoreboard(sb);

            if(TEAMS.containsKey("kills"+Online.getName())) {
                TEAMS.remove("kills"+Online.getName());
            }
            if(TEAMS.containsKey("deaths"+Online.getName())) {
                TEAMS.remove("deaths"+Online.getName());
            }
            if(TEAMS.containsKey("kdr"+Online.getName())) {
                TEAMS.remove("kdr"+Online.getName());
            }
            if(TEAMS.containsKey("jugadores"+Online.getName())) {
                TEAMS.remove("jugadores"+Online.getName());
            }

            if(!TEAMS.containsKey("kills"+Online.getName())) {
                Team tm = sb.registerNewTeam(ChatColor.YELLOW+" ");
                tm.addEntry(ChatColor.YELLOW+" ");
                TEAMS.put("kills"+Online.getName(), tm);
            }
            if(!TEAMS.containsKey("deaths"+Online.getName())) {
                Team tm = sb.registerNewTeam(ChatColor.RED+" ");
                tm.addEntry(ChatColor.RED+" ");
                TEAMS.put("deaths"+Online.getName(), tm);
            }
            if(!TEAMS.containsKey("kdr"+Online.getName())) {
                Team tm = sb.registerNewTeam(ChatColor.GREEN+" ");
                tm.addEntry(ChatColor.GREEN+" ");
                TEAMS.put("kdr"+Online.getName(), tm);
            }

            if(!TEAMS.containsKey("lvl"+Online.getName())) {
                Team tm = sb.registerNewTeam(ChatColor.AQUA+" ");
                tm.addEntry(ChatColor.AQUA+" ");
                TEAMS.put("lvl"+Online.getName(), tm);
            }

            Objective objHealth = sb.getObjective("ShowHealth");
            if(objHealth == null) {
                objHealth = sb.registerNewObjective("ShowHealth", "health");
                objHealth.setDisplaySlot(DisplaySlot.BELOW_NAME);
                objHealth.setDisplayName(ChatColor.RED+"\u2764");
            }

            Objective objGame = sb.getObjective("Game");

            if(objGame == null) {
                objGame = sb.registerNewObjective("Game", "dummy");

                objGame.setDisplaySlot(DisplaySlot.SIDEBAR);

                objGame.setDisplayName(ChatColor.GOLD+""+ChatColor.BOLD+"CHG");

                //kills
                objGame.getScore(" ").setScore(14);
                objGame.getScore(ChatColor.YELLOW+""+ChatColor.BOLD+"Asesinatos").setScore(13);
                objGame.getScore(TEAMS.get("kills"+Online.getName()).getName()).setScore(12);
                //kills
                objGame.getScore("  ").setScore(11);
                objGame.getScore(ChatColor.RED+""+ChatColor.BOLD+"Muertes").setScore(10);
                objGame.getScore(TEAMS.get("deaths"+Online.getName()).getName()).setScore(9);
                //players
                objGame.getScore("   ").setScore(8);
                //objGame.getScore(ChatColor.GREEN+""+ChatColor.BOLD+"KDR").setScore(7);
                //objGame.getScore(TEAMS.get("kdr"+Online.getName()).getName()).setScore(6);
                //mapa
                objGame.getScore("   ").setScore(7);
                objGame.getScore(ChatColor.DARK_PURPLE+""+ChatColor.BOLD+"KDR").setScore(4);
                objGame.getScore(TEAMS.get("kdr"+Online.getName()).getName()).setScore(3);
                objGame.getScore("     ").setScore(2);
                objGame.getScore(ChatColor.YELLOW + "play.minelc.net").setScore(1);
            }

            TEAMS.get("kills"+Online.getName()).setPrefix(ChatColor.YELLOW+""+jugOnline.getCHG_Stats_kills());
            TEAMS.get("deaths"+Online.getName()).setPrefix(ChatColor.RED+""+jugOnline.getCHG_Stats_deaths());
            TEAMS.get("kdr"+Online.getName()).setPrefix(ChatColor.GREEN+""+jugOnline.getCHG_Stats_kdr());
            TEAMS.get("lvl"+Online.getName()).setPrefix(ChatColor.AQUA+""+jugOnline.getCHG_Stats_Level());

            for(Player tmOnline : Bukkit.getOnlinePlayers()) {
                Jugador jugTM = Jugador.getJugador(tmOnline);

                try {
                    Team tm = sb.getTeam(jugTM.getBukkitPlayer().getName());

                    if(tm != null) {
                        continue;
                    }

                    tm = sb.registerNewTeam(jugTM.getBukkitPlayer().getName());

                    if(jugTM.isHideRank())
                        tm.setPrefix(ChatColor.GRAY.toString());
                    else if(jugTM.is_Owner())
                        tm.setPrefix(ChatColor.DARK_RED+""+ChatColor.BOLD+Ranks.OWNER.name()+" "+jugTM.getNameTagColor());
                    else if(jugOnline.getBukkitPlayer().getName()=="obed_007")
                        tm.setPrefix(ChatColor.DARK_GRAY+";"+ChatColor.AQUA+"DEV"+ChatColor.DARK_GRAY+";" + jugOnline.getNameTagColor());

                    else if(jugTM.is_Admin())
                        tm.setPrefix(ChatColor.RED+""+ChatColor.BOLD+Ranks.ADMIN.name()+" "+jugTM.getNameTagColor());
                    else if(jugTM.is_MODERADOR())
                        tm.setPrefix(ChatColor.DARK_PURPLE+""+ChatColor.BOLD+Ranks.MOD.name()+" "+jugTM.getNameTagColor());
                    else if(jugTM.is_AYUDANTE())
                        tm.setPrefix(ChatColor.DARK_PURPLE+""+ChatColor.BOLD+Ranks.AYUDANTE.name()+" "+jugTM.getNameTagColor());
                    else if(jugTM.is_YOUTUBER()) {
                        String youtuber = ChatColor.RED+""+ChatColor.BOLD+"YouTuber ";
                        tm.setPrefix(youtuber+jugTM.getNameTagColor());
                    }
                    else if(jugTM.is_BUILDER())
                        tm.setPrefix(ChatColor.LIGHT_PURPLE+""+ChatColor.BOLD+Ranks.BUILDER.name()+" "+jugTM.getNameTagColor());
                    else if(jugTM.is_RUBY())
                        tm.setPrefix(ChatColor.RED+""+ChatColor.BOLD+Ranks.RUBY.name()+" "+jugTM.getNameTagColor());
                    else if(jugTM.is_ELITE())
                        tm.setPrefix(ChatColor.GOLD+""+ChatColor.BOLD+Ranks.ELITE.name()+" "+jugTM.getNameTagColor());
                    else if(jugTM.is_RUBY())
                        tm.setPrefix(ChatColor.RED+""+ChatColor.BOLD+Ranks.RUBY.name()+" " + jugTM.getNameTagColor());
                    else if(jugTM.is_SVIP())
                        tm.setPrefix(ChatColor.GREEN+""+ChatColor.BOLD+Ranks.SVIP.name()+" "+jugTM.getNameTagColor());
                    else if(jugTM.is_VIP())
                        tm.setPrefix(ChatColor.AQUA+""+ChatColor.BOLD+Ranks.VIP.name()+" "+jugTM.getNameTagColor());
                    else if(jugTM.is_Premium())
                        tm.setPrefix(ChatColor.YELLOW.toString());
                    else
                        tm.setPrefix(ChatColor.GRAY.toString());

                    tm.addPlayer(tmOnline);
                } catch(Exception ex) {
                    ex.printStackTrace();
                }
            }

            //quitar?
            for(Player tmOnline : Bukkit.getOnlinePlayers()) {
                Scoreboard sbTM = tmOnline.getScoreboard();
                try {

                    if(sbTM == null) {
                        continue;
                    }
                    Team tm = sbTM.getTeam(jugOnline.getBukkitPlayer().getName());

                    if(tm != null) {
                        continue;
                    }

                    tm = sbTM.registerNewTeam(jugOnline.getBukkitPlayer().getName());

                    if(jugOnline.isHideRank())
                        tm.setPrefix(ChatColor.GRAY.toString());
                    else if(jugOnline.is_Owner())
                        tm.setPrefix(ChatColor.DARK_RED+""+ChatColor.BOLD+Ranks.OWNER.name()+" "+jugOnline.getNameTagColor());
                    else if(jugOnline.getBukkitPlayer().getName()=="obed_007")
                        tm.setPrefix(ChatColor.DARK_GRAY+";"+ChatColor.AQUA+"DEV"+ChatColor.DARK_GRAY+";" + jugOnline.getNameTagColor());

                    else if(jugOnline.is_Admin())
                        tm.setPrefix(ChatColor.RED+""+ChatColor.BOLD+Ranks.ADMIN.name()+" "+jugOnline.getNameTagColor());
                    else if(jugOnline.is_MODERADOR())
                        tm.setPrefix(ChatColor.DARK_PURPLE+""+ChatColor.BOLD+Ranks.MOD.name()+" "+jugOnline.getNameTagColor());
                    else if(jugOnline.is_AYUDANTE())
                        tm.setPrefix(ChatColor.DARK_PURPLE+""+ChatColor.BOLD+Ranks.AYUDANTE.name()+" "+jugOnline.getNameTagColor());
                    else if(jugOnline.is_YOUTUBER()) {
                        String youtuber = ChatColor.RED+""+ChatColor.BOLD+"YouTuber ";
                        tm.setPrefix(youtuber+jugOnline.getNameTagColor());
                    }else if(jugOnline.is_BUILDER())
                        tm.setPrefix(ChatColor.LIGHT_PURPLE+""+ChatColor.BOLD+Ranks.BUILDER.name()+" "+jugOnline.getNameTagColor());
                    else if(jugOnline.is_RUBY())
                        tm.setPrefix(ChatColor.RED+""+ChatColor.BOLD+Ranks.RUBY.name()+" "+jugOnline.getNameTagColor());
                    else if(jugOnline.is_ELITE())
                        tm.setPrefix(ChatColor.GOLD+""+ChatColor.BOLD+Ranks.ELITE.name()+" "+jugOnline.getNameTagColor());
                    else if(jugOnline.is_SVIP())
                        tm.setPrefix(ChatColor.GREEN+""+ChatColor.BOLD+Ranks.SVIP.name()+" "+jugOnline.getNameTagColor());
                    else if(jugOnline.is_VIP())
                        tm.setPrefix(ChatColor.AQUA+""+ChatColor.BOLD+Ranks.VIP.name()+" "+jugOnline.getNameTagColor());
                    else if(jugOnline.is_Premium())
                        tm.setPrefix(ChatColor.YELLOW.toString());
                    else
                        tm.setPrefix(ChatColor.GRAY.toString());

                    tm.addPlayer(jugOnline.getBukkitPlayer());
                } catch(Exception ex) {
                    ex.printStackTrace();
                }
            }

        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    //top scoreboard
    public static void setTopScoreboard(Player Online) {
        try {
            Jugador jugOnline = Jugador.getJugador(Online);
            Scoreboard sb = Bukkit.getScoreboardManager().getNewScoreboard();
            jugOnline.getBukkitPlayer().setScoreboard(sb);

            Objective objHealth = sb.getObjective("ShowHealth");
            objHealth = sb.registerNewObjective("ShowHealth", "health");
            objHealth.setDisplaySlot(DisplaySlot.BELOW_NAME);
            objHealth.setDisplayName(ChatColor.RED+"\u2764");

            Objective objGame = sb.getObjective("Game");

            if(objGame == null) {
                // cargar top
                HashMap<String, Integer> top = Database.getTop(5, "stats_kills" , "SV_CHG");

                objGame = sb.registerNewObjective("Game", "dummy");

                objGame.setDisplaySlot(DisplaySlot.SIDEBAR);

                objGame.setDisplayName(ChatColor.GOLD+""+ChatColor.BOLD+"Top Kills");
                int x = top.size()+3;
                for(String str : top.keySet()){
                    objGame.getScore(ChatColor.GREEN+ str + ChatColor.GRAY +": "+ ChatColor.RED + top.get(str) + "").setScore(x--);
                }
                objGame.getScore("   ").setScore(2);
                objGame.getScore(ChatColor.YELLOW + "play.minelc.net").setScore(1);
            }



            for(Player tmOnline : Bukkit.getOnlinePlayers()) {
                Jugador jugTM = Jugador.getJugador(tmOnline);

                try {
                    Team tm = sb.getTeam(jugTM.getBukkitPlayer().getName());

                    if(tm != null) {
                        continue;
                    }

                    tm = sb.registerNewTeam(jugTM.getBukkitPlayer().getName());

                    if(jugTM.isHideRank())
                        tm.setPrefix(ChatColor.GRAY.toString());
                    else if(jugTM.is_Owner())
                        tm.setPrefix(ChatColor.DARK_RED+""+ChatColor.BOLD+Ranks.OWNER.name()+" "+jugTM.getNameTagColor());
                    else if(jugOnline.getBukkitPlayer().getName()=="obed_007")
                        tm.setPrefix(ChatColor.DARK_GRAY+";"+ChatColor.AQUA+"DEV"+ChatColor.DARK_GRAY+";" + jugOnline.getNameTagColor());

                    else if(jugTM.is_Admin())
                        tm.setPrefix(ChatColor.RED+""+ChatColor.BOLD+Ranks.ADMIN.name()+" "+jugTM.getNameTagColor());
                    else if(jugTM.is_MODERADOR())
                        tm.setPrefix(ChatColor.DARK_PURPLE+""+ChatColor.BOLD+Ranks.MOD.name()+" "+jugTM.getNameTagColor());
                    else if(jugTM.is_AYUDANTE())
                        tm.setPrefix(ChatColor.DARK_PURPLE+""+ChatColor.BOLD+Ranks.AYUDANTE.name()+" "+jugTM.getNameTagColor());
                    else if(jugTM.is_YOUTUBER()) {
                        String youtuber = ChatColor.RED+""+ChatColor.BOLD+"YouTuber ";
                        tm.setPrefix(youtuber+jugTM.getNameTagColor());
                    }
                    else if(jugTM.is_BUILDER())
                        tm.setPrefix(ChatColor.LIGHT_PURPLE+""+ChatColor.BOLD+Ranks.BUILDER.name()+" "+jugTM.getNameTagColor());
                    else if(jugTM.is_RUBY())
                        tm.setPrefix(ChatColor.RED+""+ChatColor.BOLD+Ranks.RUBY.name()+" "+jugTM.getNameTagColor());
                    else if(jugTM.is_ELITE())
                        tm.setPrefix(ChatColor.GOLD+""+ChatColor.BOLD+Ranks.ELITE.name()+" "+jugTM.getNameTagColor());
                    else if(jugTM.is_RUBY())
                        tm.setPrefix(ChatColor.RED+""+ChatColor.BOLD+Ranks.RUBY.name()+" " + jugTM.getNameTagColor());
                    else if(jugTM.is_SVIP())
                        tm.setPrefix(ChatColor.GREEN+""+ChatColor.BOLD+Ranks.SVIP.name()+" "+jugTM.getNameTagColor());
                    else if(jugTM.is_VIP())
                        tm.setPrefix(ChatColor.AQUA+""+ChatColor.BOLD+Ranks.VIP.name()+" "+jugTM.getNameTagColor());
                    else if(jugTM.is_Premium())
                        tm.setPrefix(ChatColor.YELLOW.toString());
                    else
                        tm.setPrefix(ChatColor.GRAY.toString());

                    tm.addPlayer(tmOnline);
                } catch(Exception ex) {
                    ex.printStackTrace();
                }
            }

            //quitar?
            for(Player tmOnline : Bukkit.getOnlinePlayers()) {
                Scoreboard sbTM = tmOnline.getScoreboard();
                try {

                    if(sbTM == null) {
                        continue;
                    }
                    Team tm = sbTM.getTeam(jugOnline.getBukkitPlayer().getName());

                    if(tm != null) {
                        continue;
                    }

                    tm = sbTM.registerNewTeam(jugOnline.getBukkitPlayer().getName());

                    if(jugOnline.isHideRank())
                        tm.setPrefix(ChatColor.GRAY.toString());
                    else if(jugOnline.is_Owner())
                        tm.setPrefix(ChatColor.DARK_RED+""+ChatColor.BOLD+Ranks.OWNER.name()+" "+jugOnline.getNameTagColor());
                    else if(jugOnline.getBukkitPlayer().getName()=="obed_007")
                        tm.setPrefix(ChatColor.DARK_GRAY+";"+ChatColor.AQUA+"DEV"+ChatColor.DARK_GRAY+";" + jugOnline.getNameTagColor());

                    else if(jugOnline.is_Admin())
                        tm.setPrefix(ChatColor.RED+""+ChatColor.BOLD+Ranks.ADMIN.name()+" "+jugOnline.getNameTagColor());
                    else if(jugOnline.is_MODERADOR())
                        tm.setPrefix(ChatColor.DARK_PURPLE+""+ChatColor.BOLD+Ranks.MOD.name()+" "+jugOnline.getNameTagColor());
                    else if(jugOnline.is_AYUDANTE())
                        tm.setPrefix(ChatColor.DARK_PURPLE+""+ChatColor.BOLD+Ranks.AYUDANTE.name()+" "+jugOnline.getNameTagColor());
                    else if(jugOnline.is_YOUTUBER()) {
                        String youtuber = ChatColor.RED+""+ChatColor.BOLD+"YouTuber ";
                        tm.setPrefix(youtuber+jugOnline.getNameTagColor());
                    }else if(jugOnline.is_BUILDER())
                        tm.setPrefix(ChatColor.LIGHT_PURPLE+""+ChatColor.BOLD+Ranks.BUILDER.name()+" "+jugOnline.getNameTagColor());
                    else if(jugOnline.is_RUBY())
                        tm.setPrefix(ChatColor.RED+""+ChatColor.BOLD+Ranks.RUBY.name()+" "+jugOnline.getNameTagColor());
                    else if(jugOnline.is_ELITE())
                        tm.setPrefix(ChatColor.GOLD+""+ChatColor.BOLD+Ranks.ELITE.name()+" "+jugOnline.getNameTagColor());
                    else if(jugOnline.is_SVIP())
                        tm.setPrefix(ChatColor.GREEN+""+ChatColor.BOLD+Ranks.SVIP.name()+" "+jugOnline.getNameTagColor());
                    else if(jugOnline.is_VIP())
                        tm.setPrefix(ChatColor.AQUA+""+ChatColor.BOLD+Ranks.VIP.name()+" "+jugOnline.getNameTagColor());
                    else if(jugOnline.is_Premium())
                        tm.setPrefix(ChatColor.YELLOW.toString());
                    else
                        tm.setPrefix(ChatColor.GRAY.toString());

                    tm.addPlayer(jugOnline.getBukkitPlayer());
                } catch(Exception ex) {
                    ex.printStackTrace();
                }
            }

        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

}
