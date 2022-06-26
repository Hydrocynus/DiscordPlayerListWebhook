package de.tobias.discordPlayerListWebhook;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerLifecycleEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;


@Mod.EventBusSubscriber(modid = DiscordPlayerListWebhook.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.DEDICATED_SERVER)
public class DiscordPlayerListWebhook {
    public static final String MODID = "discordplayerlistwebhook";
    private static final String CONFIG_FILE_NAME = "config/DiscordPlayerListWebhook.config";

    private static Logger logger;
    private ConfigFileParser configFileParser;
    private static String webhook;
    private static String titleraw;
    private static String messageraw;
    private static String footerraw;

    private static final String jsonTemplate = "{" +
            "content: \"\"," +
            "embeds: [{" +
            "title: \"%s\"," +
            "description: \"%s\"," +
            "footer: {" +
            "text: \"%s\"" +
            "}" +
            "}]" +
            "}";

    public DiscordPlayerListWebhook() {
        logger = LogManager.getLogger(MODID);
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


        // Register the event listener & Mod
        MinecraftForge.EVENT_BUS.register(this);
    }

    /**
     * Called whenever a Player joins the server
     * @param event
     */
    @SubscribeEvent
    public void handleLogin(ClientPlayerNetworkEvent.LoggedInEvent event) throws IOException {
        sendMessage();
        //TODO Login Implementieren
    }

    /**
     * Called whenever a Player leaves the server
     * @param event
     */
    @SubscribeEvent
    public void handleLogout(ClientPlayerNetworkEvent.LoggedOutEvent event) throws IOException {
        //TODO Logout Implementieren
        sendMessage();
    }


    /**
     * Sends the Message to the Discord Webhook
     */
    private void sendMessage() throws IOException {
        URL url = new URL(webhook);
        URLConnection con = url.openConnection();
        HttpsURLConnection http = (HttpsURLConnection)con;
        http.setRequestMethod("PATCH");
        http.setDoOutput(true);
        String request = buildMessageJson();
        byte[] out = request.getBytes(StandardCharsets.UTF_8);
        http.setRequestProperty("Content-Type", "application/json");
        http.connect();
        try(OutputStream os = http.getOutputStream()) {
            os.write(out);
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(http.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null){
            System.out.println(line);
        }
    }

    /**
     * Builds the Message Json
     * @return the jsonTemplate with filled values
     */
    private String buildMessageJson() {
        String title = titleraw.replace("{PONLINE}", String.valueOf(ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers().size()));
        title = titleraw.replace("{PMAX}", String.valueOf(ServerLifecycleHooks.getCurrentServer().getMaxPlayers()));
        String playerstring = "";
        for (ServerPlayer player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
            playerstring += player.getScoreboardName() + "\n";
        }
        String message = messageraw.replace("{PLAYERS}", playerstring);

        return String.format(jsonTemplate, title, message, footerraw);
    }

}
