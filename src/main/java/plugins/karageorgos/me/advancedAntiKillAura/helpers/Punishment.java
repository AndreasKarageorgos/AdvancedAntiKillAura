package plugins.karageorgos.me.advancedAntiKillAura.helpers;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import plugins.karageorgos.me.advancedAntiKillAura.AdvancedAntiKillAura;

public class Punishment {

    private AdvancedAntiKillAura plugin;

    public Punishment(AdvancedAntiKillAura plugin){
        this.plugin = plugin;
    }

    public void punishPlayer(Player player){
        plugin.messageSender.sendConsoleMessage(String.format("&eThe player &4%s &egot caught using kill aura.", player.getName()));
        FileConfiguration config = plugin.getConfig();
        if(config.getBoolean("kick-player")){
            player.kickPlayer(config.getString("kick-message"));
        }else if(config.getBoolean("ban-player")){
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), String.format("ban %s %s",player.getName(), config.getString("ban-message").replace("&","§")));
        }else if(config.getBoolean("tempban-player")){
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), String.format("tempban %s %ds %s",player.getName(), config.getInt("seconds"), config.getString("tempban-message").replace("&","§")));
        }

    }



}
