package plugins.karageorgos.me.advancedAntiKillAura.core;

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
import org.bukkit.util.Vector;
import plugins.karageorgos.me.advancedAntiKillAura.AdvancedAntiKillAura;
import plugins.karageorgos.me.advancedAntiKillAura.helpers.Punishment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class HiddenTargetsTrap implements Listener {
    private final Random rand = new Random();
    private final String letters = "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM";
    private HashMap<Player,String> playersAndNames;
    private HashMap<Player,Integer> playerStrikes;
    private ArrayList<String> entityNames;
    private AdvancedAntiKillAura plugin;
    private Location playersLocation;
    private Location entityLocation;
    private boolean lock;


    public HiddenTargetsTrap(AdvancedAntiKillAura plugin){
        this.plugin = plugin;
        playerStrikes = new HashMap<Player, Integer>();
        playersAndNames = plugin.playersAndNames;
        entityNames = plugin.entityNames;
        lock = false;
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
        }else{
            entityNames.add(playersAndNames.get(player));
        }



        playersLocation = player.getLocation();

        Vector playerDirection = playersLocation.getDirection();

        // Calculate the vectors pointing to the left and right of the player
        Vector left = playerDirection.clone().rotateAroundY(-Math.PI / 2);
        Vector right = playerDirection.clone().rotateAroundY(Math.PI / 2);


        // Choose randomly between left and right vectors
        Vector chosenDirection = rand.nextBoolean() ? left : right;

        // Calculate the new location for the entity

        entityLocation = playersLocation.clone().add(playerDirection.multiply(-2)).add(chosenDirection.multiply(2));
        entityLocation.setY(playersLocation.getY() + (Math.abs(rand.nextInt())+1) % 2);

        Zombie zombie = (Zombie) player.getWorld().spawnEntity(entityLocation, EntityType.ZOMBIE);
        zombie.setCustomName(playersAndNames.get(player));
        zombie.setAI(false);
        zombie.setSilent(true);
        zombie.setVisualFire(false);
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
                // Cancel any damage to the entity
                event.setDamage(0);
                event.setCancelled(true);
                //removes the entity from the arrayList.
                safeRemove(zombie.getCustomName());
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

    private void safeRemove(String name){
        int[] taskId = new int[1];
        taskId[0] = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                if(!lock){
                    try{
                        entityNames.remove(name);
                        lock = false;
                        Bukkit.getScheduler().cancelTask(taskId[0]);
                    }catch (Exception e){
                        lock = false;
                    }

                }
            }
        },0,0);
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
            plugin.punishment.punishPlayer(player);
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


}
