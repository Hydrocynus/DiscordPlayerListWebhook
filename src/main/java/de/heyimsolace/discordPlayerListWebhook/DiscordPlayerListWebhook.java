package de.heyimsolace.discordPlayerListWebhook;

import de.heyimsolace.discordPlayerListWebhook.rest.RestHandler;
import lombok.extern.log4j.Log4j;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.io.*;
import java.util.List;
import java.util.stream.Collectors;


@Mod("discordplayerlistwebhook")
@Log4j
//@Mod.EventBusSubscriber(modid = DiscordPlayerListWebhook.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.DEDICATED_SERVER)
public class DiscordPlayerListWebhook {
    public static final String MODID = "discordplayerlistwebhook";

    private static String webhook;
    private static String titleraw;
    private static String messageraw;
    private static String footerraw;



    public DiscordPlayerListWebhook() {



        // Register the event listener & Mod
        MinecraftForge.EVENT_BUS.register(this);
    }

    /**
     * Called whenever a Player joins the server
     * @param event
     */
    @SubscribeEvent
    public void handleLogin(PlayerEvent.PlayerLoggedInEvent event) throws IOException {
        new RestHandler().sendMessage(ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers());
    }

    /**
     * Called whenever a Player leaves the server
     * @param event
     */
    @SubscribeEvent
    public void handleLogout(PlayerEvent.PlayerLoggedOutEvent event) throws IOException {

        List<ServerPlayer> plist = ServerLifecycleHooks.getCurrentServer()
                .getPlayerList()
                .getPlayers()
                .stream()
                .filter(p -> !p.getName()
                        .equals(event.getPlayer().getName()))
                .collect(Collectors.toList());
        new RestHandler().sendMessage(plist);
    }



}
