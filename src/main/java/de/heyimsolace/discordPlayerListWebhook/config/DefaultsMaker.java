package de.heyimsolace.discordPlayerListWebhook.config;

import java.util.HashMap;

public class DefaultsMaker {


    public static HashMap<String, String> mainConfig() {
        HashMap<String, String> config = new HashMap<>();
        config.put("webhook", "https://discordapp.com/api/webhooks/id/token/messages/messageid");
        config.put("title", "Players Online {PONLINE}/{PMAX}");
        config.put("message", "{PLAYERS}");
        config.put("footer", "SERVERNAME/IP");
        return config;
    }

    public static HashMap<String, String> playerMap() {
        HashMap<String, String> config = new HashMap<>();
        config.put("MinecraftName", "DiscordID");
        config.put("notch:", "<@012345678901234567>");
        return config;
    }
}
