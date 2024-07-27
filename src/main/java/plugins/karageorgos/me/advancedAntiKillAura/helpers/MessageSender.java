package plugins.karageorgos.me.advancedAntiKillAura.helpers;

import org.bukkit.entity.Player;
import plugins.karageorgos.me.advancedAntiKillAura.AdvancedAntiKillAura;

public class MessageSender {

    private AdvancedAntiKillAura plugin;
    private final String header = "§b[§aAdvancedAntiKillAura§b] §r";
    public MessageSender(AdvancedAntiKillAura plugin){
        this.plugin = plugin;
    }

    public void sendConsoleMessage(String message){
        plugin.getServer().getConsoleSender().sendMessage(header + message.replace("&", "§"));
    }

    public void sendPlayerMessage(Player player, String message){
        player.sendMessage(header + message.replace("&", "§"));
    }


}
