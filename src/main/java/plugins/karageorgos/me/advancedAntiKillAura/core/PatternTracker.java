package plugins.karageorgos.me.advancedAntiKillAura.core;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import plugins.karageorgos.me.advancedAntiKillAura.AdvancedAntiKillAura;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class PatternTracker implements Listener {

    private final HashMap<Player, ArrayList<Long>> data = new HashMap<Player,ArrayList<Long>>();
    private boolean locked;
    private AdvancedAntiKillAura plugin;

    public PatternTracker(AdvancedAntiKillAura plugin){
        this.plugin = plugin;
        locked = false;
    }

    @EventHandler
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event){

        if(!(event.getDamager() instanceof Player)){return;}
        if(!(event.getEntity() instanceof Player)){return;}

        Player player = (Player) event.getDamager();

        if(player.hasPermission("antikillaura.ignore")){return;}

        if(!data.containsKey(player)){
            data.put(player, new ArrayList<Long>());
        }

        safeAdd(player, System.currentTimeMillis());
        int size = data.get(player).size();
        if((size>=14) && (size%2==0)){
            calculateData(player);
        }


    }


    private void calculateData(Player player){
        int[] taskId = new int[1];
        taskId[0] = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                if(!locked){
                    locked = true;

                    Long[] time = new Long[data.get(player).size()];

                    for(int i=0; i<time.length; i++){
                        time[i] = data.get(player).get(i);
                    }

                    int failSafe = time.length%2;
                    if(failSafe!=0){
                        locked = false;
                        Bukkit.getScheduler().cancelTask(taskId[0]);
                    }

                    Double[] dataSeconds = new Double[time.length/2];

                    int repeats = 0;
                    int next = 0;
                    String roundNumber;
                    double precentage = 0;
                    HashMap<Double, Integer> commons = new HashMap<Double, Integer>();

                    for(int i=0; i<time.length-1; i=i+2){
                        dataSeconds[next] = (Double) ((time[i+1] - time[i])/1000.0);
                        roundNumber = String.format("%.2f", dataSeconds[next]);
                        dataSeconds[next] = Double.valueOf(roundNumber);
                        next++;
                    }

                    //Finding most common
                    for(Double d : dataSeconds){
                        if(commons.containsKey(d)){
                            commons.replace(d, commons.get(d)+1);
                        }else{
                            commons.put(d,1);
                        }
                    }

                    Double mostCommon = dataSeconds[0];
                    int shows = commons.get(mostCommon);

                    for(Double s : commons.keySet()){
                        if(commons.get(s)>shows){
                            shows = commons.get(s);
                            mostCommon = s;
                        }
                    }


                    int repeat = 0;
                    for(int i=0; i<dataSeconds.length-1; i++){
                        if(Objects.equals(dataSeconds[i], mostCommon) && Objects.equals(dataSeconds[i], dataSeconds[i + 1])){
                            repeat++;
                        }
                    }

                    precentage = 1.0 * repeat/dataSeconds.length * 100;

                    if(precentage>=80.0){
                        precentage = 1.0 * shows/dataSeconds.length * 100;

                        if(precentage>=75.0){
                            plugin.punishment.punishPlayer(player);
                            data.remove(player);
                        }

                        for(Double d : dataSeconds){
                            System.out.println(d);
                        }
                    }else if(time.length>50){
                        data.get(player).clear();
                    }


                    locked = false;
                    Bukkit.getScheduler().cancelTask(taskId[0]);

                }
            }
        },0,0);

    }

    private void safeAdd(Player player, Long time){
        int[] taskId = new int[1];
        taskId[0] = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                if(!locked){
                    locked = true;
                    data.get(player).add(time);
                    locked = false;
                    Bukkit.getScheduler().cancelTask(taskId[0]);
                }
            }
        },0,0);
    }



}
