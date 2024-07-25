package plugins.karageorgos.me.advancedAntiKillAura.helpers;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import plugins.karageorgos.me.advancedAntiKillAura.AdvancedAntiKillAura;

public class MutexWaitToUnlock {

    private AdvancedAntiKillAura plugin;

    public MutexWaitToUnlock(AdvancedAntiKillAura plugin){
        this.plugin = plugin;
    }

    public void waitToUnlock(){
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                if(!plugin.mutex.isLocked()){
                    return;
                }
            }
        },0,0);
    }


}
