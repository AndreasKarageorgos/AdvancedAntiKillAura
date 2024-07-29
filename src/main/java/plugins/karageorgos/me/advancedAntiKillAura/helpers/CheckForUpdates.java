package plugins.karageorgos.me.advancedAntiKillAura.helpers;

import plugins.karageorgos.me.advancedAntiKillAura.AdvancedAntiKillAura;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class CheckForUpdates {

    private AdvancedAntiKillAura plugin;
    private final String urlString = "https://raw.githubusercontent.com/AndreasKarageorgos/AdvancedAntiKillAura/main/README.md";
    private String version;

    public CheckForUpdates(AdvancedAntiKillAura plugin){
        this.plugin = plugin;
        version = plugin.version;
    }

    public boolean hasUpdate(){
        StringBuilder content = null;
        try{
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            content = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
                content.append(System.lineSeparator());
            }

            // Close connections
            in.close();
            connection.disconnect();

        } catch (Exception e) {
            return false;
        }
        return !content.toString().contains(version);
    }




}
