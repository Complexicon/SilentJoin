package tk.complexicon.silentjoin;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

public class Main extends JavaPlugin implements Listener {

    Logger l = getLogger();
    FileConfiguration cfg = getConfig();

    String prefix;
    List<String> log = new ArrayList<>();
    int logSize;
    boolean informAdmins;

    private SimpleDateFormat time = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

    @Override
    public void onEnable() {
        l.info("Registering Events...");
        Bukkit.getPluginManager().registerEvents(this, this);
        l.info("Loading Config...");
        cfg.addDefault("prefix", "&d[&6SilentJoin&d]");
        cfg.addDefault("logSize", 8);
        cfg.addDefault("informAdmins", true);
        cfg.options().copyDefaults(true);
        saveConfig();
        prefix = cfg.getString("prefix").replaceAll("&", "§");
        logSize = cfg.getInt("logSize");
        informAdmins = cfg.getBoolean("informAdmins");
        l.info("Loaded! SilentJoin v0.1 by Complexicon");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        if(sender.hasPermission("silentjoin.viewlog")){
            sender.sendMessage(prefix + "§aLast Logins and Logouts: ");
            sender.sendMessage(log.toArray(new String[log.size()]));
        }
        return true;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onJoin(PlayerJoinEvent e){
        e.setJoinMessage("");
        String msg = "§a" + e.getPlayer().getName()+ " Joined.";
        informAdmins(msg);
        addToLog(msg);
        Bukkit.getConsoleSender().sendMessage(msg);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onLeave(PlayerQuitEvent e){
        e.setQuitMessage("");
        String msg = "§c" + e.getPlayer().getName() + " Left.";
        informAdmins(msg);
        addToLog(msg);
        Bukkit.getConsoleSender().sendMessage(msg);
    }

    private void addToLog(String msg){
        log.add(prefix + "§7[" + time.format(new Date(System.currentTimeMillis())) + "]" + msg);
        if(log.size() > logSize){
            log.remove(0);
        }
    }

    private void informAdmins(String msg){
        if(informAdmins){
            for(Player p : Bukkit.getOnlinePlayers()){
                if(p.hasPermission("silentjoin.view")){
                    p.sendMessage(prefix + msg);
                }
            }
        }
    }

}
