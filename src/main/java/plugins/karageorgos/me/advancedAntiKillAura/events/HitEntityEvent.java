package plugins.karageorgos.me.advancedAntiKillAura.events;

import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import plugins.karageorgos.me.advancedAntiKillAura.AdvancedAntiKillAura;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class HitEntityEvent implements Listener {
    private final Random rand = new Random();
    private final String letters = "qwertyuiopasdfghjklzxcvbnm";
    private HashMap<Player,String> playersAndNames;
    private HashMap<Player,Integer> playerStrikes;
    private ArrayList<String> entityNames;
    private AdvancedAntiKillAura plugin;
    private Location playersLocation;
    private Location entityLocation;
    private Sheep sheep;
    private final PotionEffect invisibility = new PotionEffect(PotionEffectType.INVISIBILITY, 1 ,10);

    public HitEntityEvent(AdvancedAntiKillAura plugin){
        this.plugin = plugin;
        playerStrikes = new HashMap<Player, Integer>();
        playersAndNames = plugin.playersAndNames;
        entityNames = plugin.entityNames;
    }


    @EventHandler
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent entityDamageByEntityEvent){

        if(!(entityDamageByEntityEvent.getDamager() instanceof Player)){return;} //Terminate if it is not a player.

        Player player = (Player) entityDamageByEntityEvent.getDamager();

        if(player.hasPermission("anikillaura.ignore")){return;} //Terminate is the player has the permission or has op status on.

        if(!playersAndNames.containsKey(player)){
            String randomName = generateRandomName();
            playersAndNames.put(player, randomName);
            entityNames.add(randomName);
        }

        playersLocation = player.getLocation();
        entityLocation = playersLocation.clone().add(playersLocation.getDirection().multiply(-2));
        entityLocation.setY(playersLocation.getY());

        sheep = (Sheep) player.getWorld().spawnEntity(entityLocation, EntityType.SHEEP);
        sheep.addPotionEffect(invisibility);
        sheep.setCustomName(playersAndNames.get(player));
        sheep.setAI(false);
        sheep.setSilent(true);
        //Bukkit.getScheduler().runTaskLater(plugin, sheep::remove, 20L); //Removes the sheep after 1 second.

        if(entityDamageByEntityEvent.getEntity() instanceof Sheep){
            if(((Sheep) entityDamageByEntityEvent.getEntity()).getName().equals(playersAndNames.get(player))){
                sheep.remove();
                keepAnEye(player);
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {

        if (event.getEntity() instanceof Sheep) {
            Sheep sheep = (Sheep) event.getEntity();
            if (sheep.getCustomName() != null && entityNames.contains(sheep.getCustomName())) {
                event.setCancelled(true); // Cancel any damage to the sheep
                entityNames.remove(sheep.getCustomName());
            }
        }
    }


    private String generateRandomName(){
        String randomName = "";
        for(int i=0; i < 12; i++){
            randomName += String.valueOf(letters.charAt(rand.nextInt(letters.length())));
        }
        return randomName;
    }

    private void keepAnEye(Player player) {

        if (playerStrikes.containsKey(player)) {
            playerStrikes.replace(player, playerStrikes.get(player) + 1);
        } else {
            playerStrikes.put(player, 1);
        }

        if(playerStrikes.get(player)>=2){
            punishPlayer(player);
        }
        Bukkit.getScheduler().runTaskLater(plugin, () -> resetPlayersStrikes(player), 40L); //Resets the timer after 2 seconds.
    }

    private void resetPlayersStrikes(Player player){
        if(plugin.getServer().getOnlinePlayers().contains(player) && playerStrikes.containsKey(player)){
            playerStrikes.replace(player,0);
        }else{
            playerStrikes.remove(player);
        }
    }

    private void punishPlayer(Player player){
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
