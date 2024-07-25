package plugins.karageorgos.me.advancedAntiKillAura;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import plugins.karageorgos.me.advancedAntiKillAura.events.HitEntityEvent;
import plugins.karageorgos.me.advancedAntiKillAura.mutex.Mutex;

import java.util.ArrayList;
import java.util.HashMap;

public final class AdvancedAntiKillAura extends JavaPlugin {

    public final ArrayList<Player> players = new ArrayList<Player>();
    public final Mutex mutex = new Mutex();
    public final HashMap<Player, String> playersAndNames = new HashMap<Player,String>();
    public final ArrayList<String> entityNames = new ArrayList<String>();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        if(!getConfig().getBoolean("enable")){return;} //Stops the plugin.
        getServer().getPluginManager().registerEvents(new HitEntityEvent(this), this);
    }

    @Override
    public void onDisable() {

    }
}
