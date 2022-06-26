package de.tobias.discordPlayerListWebhook;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerLifecycleEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.jmx.Server;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Mod("discordplayerlistwebhook")
//@Mod.EventBusSubscriber(modid = DiscordPlayerListWebhook.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.DEDICATED_SERVER)
public class DiscordPlayerListWebhook {
    public static final String MODID = "discordplayerlistwebhook";
    private static final String CONFIG_FILE_NAME = "config/DiscordPlayerListWebhook/cfg.config";
    private static final String PLAYER_MAP_NAME = "config/DiscordPlayerListWebhook/playermap.config";
    private static final String CONFIG_DIR_NAME = "config/DiscordPlayerListWebhook";

    private static Logger logger;
    private ConfigFileParser configFileParser;
    private ConfigFileParser playerMapFileParser;
    private static String webhook;
    private static String titleraw;
    private static String messageraw;
    private static String footerraw;

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


    public DiscordPlayerListWebhook() {
        logger = LogManager.getLogger(MODID);

        loadConfigs();

        // Register the event listener & Mod
        MinecraftForge.EVENT_BUS.register(this);

        logger.info("DiscordPlayerListWebhook loaded");
    }

    private void loadConfigs() {
        File confDir = new File(CONFIG_DIR_NAME);
        if (!confDir.exists()) {
            confDir.mkdir();
        }

        //config loading
        File file = new File(CONFIG_FILE_NAME);
        try {
            if (!file.exists()){
                file.createNewFile();
                FileWriter writer = new FileWriter(file);
                writer.write("webhook: https://discordapp.com/api/webhooks/id/token/messages/messageid");
                writer.write("\ntitle: Players Online {PONLINE}/{PMAX}");
                writer.write("\nmessage: {PLAYERS}");
                writer.write("\nfooter: SERVERNAME/IP");
                writer.close();
            }
            configFileParser = new ConfigFileParser(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        webhook = configFileParser.getValues().get("webhook");
        titleraw = configFileParser.getValues().get("title");
        messageraw = configFileParser.getValues().get("message");
        footerraw = configFileParser.getValues().get("footer");

        //player map loading
        File playerfile = new File(PLAYER_MAP_NAME);
        try {
            if (!playerfile.exists()){
                playerfile.createNewFile();
                FileWriter writer = new FileWriter(playerfile);
                writer.write("notch: <@012345678901234567>");
                writer.close();
            }
            playerMapFileParser = new ConfigFileParser(playerfile);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Called whenever a Player joins the server
     * @param event
     */
    @SubscribeEvent
    public void handleLogin(PlayerEvent.PlayerLoggedInEvent event) throws IOException {
        loadConfigs();
        sendMessage(ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers());
    }

    /**
     * Called whenever a Player leaves the server
     * @param event
     */
    @SubscribeEvent
    public void handleLogout(PlayerEvent.PlayerLoggedOutEvent event) throws IOException {
        loadConfigs();
        List<ServerPlayer> plist = ServerLifecycleHooks.getCurrentServer()
                .getPlayerList()
                .getPlayers()
                .stream()
                .filter(p -> !p.getName()
                        .equals(event.getPlayer().getName()))
                .collect(Collectors.toList());
        sendMessage(plist);
    }


    /**
     * Sends the Message to the Discord Webhook
     */
    private void sendMessage(List<ServerPlayer> playerList) throws IOException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(webhook))
                .header("Content-Type", "application/json")
                .method("PATCH", HttpRequest.BodyPublishers.ofString(buildMessageJson(playerList)))
                .build();

        HttpClient client = HttpClient.newBuilder()
                .build();
        logger.info(buildMessageJson(playerList));

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(System.out::println)
                .exceptionally(t -> {
                    System.out.println("Exception: " + t.getMessage());
                    return null;
                });


//        URL url = new URL(webhook);
//        URLConnection con = url.openConnection();
//        HttpsURLConnection http = (HttpsURLConnection)con;
//        allowMethods("PATCH");
//        http.setRequestMethod("PATCH");
//        http.setDoOutput(true);
//        String request = buildMessageJson();
//        byte[] out = request.getBytes(StandardCharsets.UTF_8);
//        http.setRequestProperty("Content-Type", "application/json");
//        http.connect();
//        try(OutputStream os = http.getOutputStream()) {
//            os.write(out);
//        }
//        BufferedReader reader = new BufferedReader(new InputStreamReader(http.getInputStream()));
//        String line;
//        while ((line = reader.readLine()) != null){
//            System.out.println(line);
//        }
    }

    /**
     * Builds the Message Json
     * @return the jsonTemplate with filled values
     */
    private String buildMessageJson(List<ServerPlayer> playerList) {
        String title = titleraw.replace("{PONLINE}", String.valueOf(playerList.size()));
        title = title.replace("{PMAX}", String.valueOf(ServerLifecycleHooks.getCurrentServer().getMaxPlayers()));

        String playerstring = "";
        for (ServerPlayer player : playerList) {
            playerstring += playerMapFileParser.getValues().containsKey(player.getScoreboardName()) ? playerMapFileParser.getValues().get(player.getScoreboardName()) : player.getScoreboardName();
            playerstring += "\\n";
        }
        String message = messageraw.replace("{PLAYERS}", playerstring);

        return String.format(jsonTemplate, title, message, footerraw);
    }


}
