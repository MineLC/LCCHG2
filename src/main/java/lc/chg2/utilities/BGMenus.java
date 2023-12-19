package lc.chg2.utilities;

import lc.chg2.CHG2;
import lc.core2.entities.Database;
import lc.core2.utils.IconMenu;
import lc.core2.utils.ItemUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BGMenus {
    private static final ItemStack stats_item = new ItemUtils(Material.PAPER, (short) 0, 1, ChatColor.GREEN+"TOP Jugadores" , ChatColor.GRAY+"Click derecho para abrir el menu de estadisticas");
    private static final ItemStack kit_item = new ItemUtils(Material.BOW, (short) 0, 1, ChatColor.GREEN+"Selector De Kit" , ChatColor.GRAY+"Click derecho para abrir el menu de kits");
    private static final ItemStack book_item;
    private static ItemStack compass = new ItemUtils(Material.COMPASS, (short) 0, 1, ChatColor.AQUA+"Rastreador"+ChatColor.BOLD+"", ChatColor.GRAY+"Click para rastrar al jugador mas cercano");
    private static final IconMenu invStats_CHG;
    private static final IconMenu invStats_CHG_kills;
    private static final IconMenu invStats_CHG_deaths;
    private static final IconMenu invStats_CHG_part_ganadas;
    private static final IconMenu invStats_CHG_part_jugadas;
    private static final IconMenu invStats_CHG_lvl;

    public static ItemStack getBook_item() {
        return book_item;
    }

    public static ItemStack getKit_item() {
        return kit_item;
    }

    public static ItemStack getCompass() {
        return compass;
    }

    public static ItemStack getStats_item() {
        return stats_item;
    }

    public static IconMenu getInvStats_CHG() {
        return invStats_CHG;
    }

    public static IconMenu getInvStats_CHG_kills() {
        return invStats_CHG_kills;
    }

    public static IconMenu getInvStats_CHG_deaths() {
        return invStats_CHG_deaths;
    }

    public static IconMenu getInvStats_CHG_part_ganadas() {
        return invStats_CHG_part_ganadas;
    }

    public static IconMenu getInvStats_CHG_part_jugadas() {
        return invStats_CHG_part_jugadas;
    }

    public static IconMenu getInvStats_CHG_lvl() {
        return invStats_CHG_lvl;
    }

    //BOOK
    static {
        book_item = new ItemStack(Material.WRITTEN_BOOK);
        List<String> pages = BGFiles.bookconf.getStringList("content");
        List<String> content = new ArrayList<>();
        List<String> page = new ArrayList<>();
        for(String line : pages)  {
            line = line.replace("<server_title>", CHG2.SERVER_TITLE);
            line = line.replace("<space>", ChatColor.RESET + "\n");
            line = ChatColor.translateAlternateColorCodes('&', line);
            if(!line.contains("<newpage>")) {
                page.add(line + "\n");
            } else {
                StringBuilder pagestr = new StringBuilder();
                for(String l : page)
                    pagestr.append(l);
                content.add(pagestr.toString());
                page.clear();
            }
        }
        StringBuilder pagestr = new StringBuilder();
        for(String l : page)
            pagestr.append(l);
        content.add(pagestr.toString());
        page.clear();

        BookMeta im = (BookMeta) book_item.getItemMeta();
        im.setPages(content);
        im.setAuthor(BGFiles.bookconf.getString("author"));
        im.setTitle(BGFiles.bookconf.getString("title"));
        book_item.setItemMeta(im);
    }
    //TOP KILLS
    static {
        invStats_CHG_kills = new IconMenu("TOP Asesinatos - CHG", 45, e -> {
            e.setWillClose(false);
            e.setWillDestroy(false);
            if (e.getPosition() == 31) {
                getInvStats_CHG().open(e.getPlayer());
            }
        }, CHG2.instance);

        LinkedHashMap<String, Integer> top = Database.getTop(18, "stats_kills", "SV_CHG");
        int slot = 0;
        for(Map.Entry<String, Integer> es : top.entrySet()) {
            invStats_CHG_kills.setOption(slot++, new ItemUtils(es.getKey(), 1, ChatColor.GOLD+""+ChatColor.BOLD+"#"+slot +ChatColor.DARK_GRAY+" - "+ChatColor.RED+es.getKey(), ""+ChatColor.GRAY+es.getValue()+" asesinatos"));
        }

        invStats_CHG_kills.setOption(31, new ItemStack(Material.MAP), ChatColor.GRAY+""+ChatColor.BOLD+"Regresar");

    }
    //TOPS
    static {
        invStats_CHG = new IconMenu("TOP Jugadores - CHG", 45, e -> {
            e.setWillClose(false);
            e.setWillDestroy(false);

            switch (e.getPosition()) {
                case 10: getInvStats_CHG_kills().open(e.getPlayer()); break;
                case 12: getInvStats_CHG_part_ganadas().open(e.getPlayer()); break;
                case 14: getInvStats_CHG_part_jugadas().open(e.getPlayer()); break;
                case 16: getInvStats_CHG_deaths().open(e.getPlayer()); break;
                case 28: getInvStats_CHG_lvl().open(e.getPlayer()); break;
                default: break;
            }

            //getInvSettings(e.getPlayer()).open(e.getPlayer());
        }, CHG2.instance);

        invStats_CHG.setOption(10, new ItemStack(Material.SIGN), ChatColor.GREEN+""+ChatColor.BOLD+"Asesinatos   ",
                ChatColor.GRAY+"Click para mostrar a los usuarios con", ChatColor.GRAY+"mas asesinatos");

        invStats_CHG.setOption(12, new ItemStack(Material.SIGN), ChatColor.GREEN+""+ChatColor.BOLD+"Partidas Ganadas",
                ChatColor.GRAY+"Click para mostrar a los usuarios con", ChatColor.GRAY+"mas partidas ganadas");

        invStats_CHG.setOption(14, new ItemStack(Material.SIGN), ChatColor.GREEN+""+ChatColor.BOLD+"Partidas Jugadas",
                ChatColor.GRAY+"Click para mostrar a los usuarios con", ChatColor.GRAY+"mas partidas jugadas");

        invStats_CHG.setOption(16, new ItemStack(Material.SIGN), ChatColor.GREEN+""+ChatColor.BOLD+"Muertes  ",
                ChatColor.GRAY+"Click para mostrar a los usuarios con", ChatColor.GRAY+"mas muertes");

        invStats_CHG.setOption(28, new ItemStack(Material.SIGN), ChatColor.GREEN+""+ChatColor.BOLD+"Nivel  ",
                ChatColor.GRAY+"Click para mostrar a los usuarios con", ChatColor.GRAY+"mas nivel");

    }
    //TOP DEATHS
    static {
        invStats_CHG_deaths = new IconMenu("TOP Muertes - CHG", 45, e -> {
            e.setWillClose(false);
            e.setWillDestroy(false);
            if(e.getPosition() == 31) {
                getInvStats_CHG().open(e.getPlayer());
            }
        }, CHG2.instance);

        LinkedHashMap<String, Integer> top = Database.getTop(18, "stats_deaths", "SV_CHG");
        int slot = 0;
        for(Map.Entry<String, Integer> es : top.entrySet()) {
            invStats_CHG_deaths.setOption(slot++, new ItemUtils(es.getKey(), 1, ChatColor.GOLD+""+ChatColor.BOLD+"#"+slot +ChatColor.DARK_GRAY+" - "+ChatColor.RED+es.getKey(), ""+ChatColor.GRAY+es.getValue()+" muertes"));
        }

        invStats_CHG_deaths.setOption(31, new ItemStack(Material.MAP), ChatColor.GRAY+""+ChatColor.BOLD+"Regresar");
    }
    //TOP WINS
    static {
        invStats_CHG_part_ganadas = new IconMenu("TOP Partidas Ganadas - CHG", 45, e -> {
            e.setWillClose(false);
            e.setWillDestroy(false);
            if(e.getPosition() == 31) {
                getInvStats_CHG().open(e.getPlayer());
            }
        }, CHG2.instance);

        LinkedHashMap<String, Integer> top = Database.getTop(18, "stats_partidas_ganadas", "SV_CHG");
        int slot = 0;
        for(Map.Entry<String, Integer> es : top.entrySet()) {
            invStats_CHG_part_ganadas.setOption(slot++, new ItemUtils(es.getKey(), 1, ChatColor.GOLD+""+ChatColor.BOLD+"#"+slot +ChatColor.DARK_GRAY+" - "+ChatColor.RED+es.getKey(), ""+ChatColor.GRAY+es.getValue()+" partidas ganadas"));
        }

        invStats_CHG_part_ganadas.setOption(31, new ItemStack(Material.MAP), ChatColor.GRAY+""+ChatColor.BOLD+"Regresar");
    }
    //TOP JUGADAS
    static {
        invStats_CHG_part_jugadas = new IconMenu("TOP Partidas Jugadas - CHG", 45, e -> {
            e.setWillClose(false);
            e.setWillDestroy(false);
            if(e.getPosition() == 31) {
                getInvStats_CHG().open(e.getPlayer());
            }
        }, CHG2.instance);

        LinkedHashMap<String, Integer> top = Database.getTop(18, "stats_partidas_jugadas", "SV_CHG");
        int slot = 0;
        for(Map.Entry<String, Integer> es : top.entrySet()) {
            invStats_CHG_part_jugadas.setOption(slot++, new ItemUtils(es.getKey(), 1, ChatColor.GOLD+""+ChatColor.BOLD+"#"+slot +ChatColor.DARK_GRAY+" - "+ChatColor.RED+es.getKey(), ""+ChatColor.GRAY+es.getValue()+" partidas jugadas"));
        }

        invStats_CHG_part_jugadas.setOption(31, new ItemStack(Material.MAP), ChatColor.GRAY+""+ChatColor.BOLD+"Regresar");

    }
    //TOP LvL
    static {
        invStats_CHG_lvl = new IconMenu("TOP Nivel - CHG", 45, e -> {
            e.setWillClose(false);
            e.setWillDestroy(false);
            if(e.getPosition() == 31) {
                getInvStats_CHG().open(e.getPlayer());
            }
        }, CHG2.instance);

        LinkedHashMap<String, Integer> top = Database.getTop(18, "stats_level", "SV_CHG");
        int slot = 0;
        for(Map.Entry<String, Integer> es : top.entrySet()) {
            invStats_CHG_lvl.setOption(slot++, new ItemUtils(es.getKey(), 1, ChatColor.GOLD+""+ChatColor.BOLD+"#"+slot +ChatColor.DARK_GRAY+" - "+ChatColor.RED+es.getKey(), ChatColor.GRAY+"Nivel: "+es.getValue()+" "));
        }

        invStats_CHG_lvl.setOption(31, new ItemStack(Material.MAP), ChatColor.GRAY+""+ChatColor.BOLD+"Regresar");

    }

}
