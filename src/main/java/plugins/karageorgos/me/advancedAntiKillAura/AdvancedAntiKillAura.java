package plugins.karageorgos.me.advancedAntiKillAura;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import plugins.karageorgos.me.advancedAntiKillAura.core.PatternTracker;
import plugins.karageorgos.me.advancedAntiKillAura.core.HiddenTargetsTrap;
import plugins.karageorgos.me.advancedAntiKillAura.helpers.CheckForUpdates;
import plugins.karageorgos.me.advancedAntiKillAura.helpers.MessageSender;
import plugins.karageorgos.me.advancedAntiKillAura.helpers.Punishment;


import java.util.ArrayList;
import java.util.HashMap;

public final class AdvancedAntiKillAura extends JavaPlugin {

    public final String version = "2.2-Alpha";
    public final ArrayList<Player> players = new ArrayList<Player>();
    public final HashMap<Player, String> playersAndNames = new HashMap<Player,String>();
    public final ArrayList<String> entityNames = new ArrayList<String>();
    public final MessageSender messageSender = new MessageSender(this);
    public final Punishment punishment = new Punishment(this);

    private final CheckForUpdates checkForUpdates = new CheckForUpdates(this);

    @Override
    public void onEnable() {
        saveDefaultConfig();
        if(checkForUpdates.hasUpdate()){
            messageSender.sendConsoleMessage("&eThere is a new version available. Download it at: https://www.spigotmc.org/resources/advanced-anti-kill-aura.118365/");
        }
        if(!getConfig().getBoolean("enable")){return;}//Stops the plugin.
        getServer().getPluginManager().registerEvents(new HiddenTargetsTrap(this), this);
        getServer().getPluginManager().registerEvents(new PatternTracker(this), this);
        if(!isPluginEnabled("Essentials")){
            messageSender.sendConsoleMessage("&eEssentials plugin is not found. &a/tempban &emay not work !");
        }

        messageSender.sendConsoleMessage("&eIf you enjoy this plugin. Please consider donating using the following link: &bhttps://bit.ly/4c40uwW");
    }

    @Override
    public void onDisable() {

    }

    private boolean isPluginEnabled(String pluginName) {
        try{
            return getServer().getPluginManager().getPlugin(pluginName).isEnabled();
        }catch (NullPointerException e){
            return false;
        }

    }


}
