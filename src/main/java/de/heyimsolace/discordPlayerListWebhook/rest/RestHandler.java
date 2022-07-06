package de.heyimsolace.discordPlayerListWebhook.rest;

import de.heyimsolace.discordPlayerListWebhook.ModGlobals;
import de.heyimsolace.discordPlayerListWebhook.config.FileParser;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;

public class RestHandler {

    private String webhook;
    private String titleraw;
    private String messageraw;
    private String footerraw;


    private static final String jsonTemplate = "{" +
            "\"content\": \"\"," +
            "\"embeds\": [{" +
            "\"title\": \"%s\"," +
            "\"description\": \"%s\"," +
            "\"footer\": {" +
            "\"text\": \"%s\"" +
            "}" +
            "}]" +
            "}";


    public RestHandler() {
        webhook = ModGlobals.getMainConfig().getValue("webhook");
        titleraw = ModGlobals.getMainConfig().getValue("title");
        messageraw = ModGlobals.getMainConfig().getValue("message");
        footerraw = ModGlobals.getMainConfig().getValue("footer");
    }

    /**
     * Sends the Message to the Discord Webhook
     */
    public void sendMessage(List<ServerPlayer> playerList) throws IOException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(webhook))
                .header("Content-Type", "application/json")
                .method("PATCH", HttpRequest.BodyPublishers.ofString(buildMessageJson(playerList)))
                .build();

        HttpClient client = HttpClient.newBuilder()
                .build();
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(System.out::println)
                .exceptionally(t -> {
                    System.out.println("Exception: " + t.getMessage());
                    return null;
                });
    }

    /**
     * Builds the Message Json
     * @return the jsonTemplate with filled values
     */
    private String buildMessageJson(List<ServerPlayer> playerList) {
        String title = titleraw.replace("{PONLINE}", String.valueOf(playerList.size()));
        title = title.replace("{PMAX}", String.valueOf(ServerLifecycleHooks.getCurrentServer().getMaxPlayers()));

        String playerstring = "";
        HashMap<String, String> playerMap = ModGlobals.getPlayerMap().getMap();
        for (ServerPlayer player : playerList) {
            playerstring += playerMap
                    .containsKey(player.getScoreboardName()) ?
                    playerMap.get(player.getScoreboardName()) : player.getScoreboardName();
            playerstring += "\\n";
        }
        String message = messageraw.replace("{PLAYERS}", playerstring);

        return String.format(jsonTemplate, title, message, footerraw);
    }

}
