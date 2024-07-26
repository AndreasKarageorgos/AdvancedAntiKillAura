package plugins.karageorgos.me.advancedAntiKillAura.events;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
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
        entityLocation.setY(playersLocation.getY() + (Math.abs(rand.nextInt())+1) % 2);

        Zombie zombie = (Zombie) player.getWorld().spawnEntity(entityLocation, EntityType.ZOMBIE);
        zombie.setCustomName(playersAndNames.get(player));
        zombie.setAI(false);
        zombie.setSilent(true);
        zombie.setVisualFire(false);
        zombie.setInvulnerable(true);
        zombie.setInvisible(true);
        zombie.setAdult();
        zombie.setMetadata("NoBurn", new FixedMetadataValue(plugin, true));



        Bukkit.getScheduler().runTaskLater(plugin, zombie::remove, 20L); //Removes the entity.

        if(entityDamageByEntityEvent.getEntity() instanceof Zombie){
            if(((Zombie) entityDamageByEntityEvent.getEntity()).getName().equals(playersAndNames.get(player))){
                entityDamageByEntityEvent.getEntity().remove();
                keepAnEye(player);
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {

        if (event.getEntity() instanceof Zombie) {
            Zombie zombie = (Zombie) event.getEntity();
            if (zombie.getCustomName() != null && entityNames.contains(zombie.getCustomName())) {
                event.setCancelled(true); // Cancel any damage to the entity
                entityNames.remove(zombie.getCustomName());
            }
        }
    }

    @EventHandler
    public void onEntityCombust(EntityCombustEvent event) {
        if (event.getEntity() instanceof Zombie) {
            Zombie zombie = (Zombie) event.getEntity();
            if (zombie.hasMetadata("NoBurn")) {
                for (MetadataValue value : zombie.getMetadata("NoBurn")) {
                    if (value.asBoolean()) {
                        event.setCancelled(true);
                        break;
                    }
                }
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
