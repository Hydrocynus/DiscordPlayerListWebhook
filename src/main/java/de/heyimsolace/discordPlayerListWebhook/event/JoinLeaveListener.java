package de.heyimsolace.discordPlayerListWebhook.event;

import de.heyimsolace.discordPlayerListWebhook.rest.RestHandler;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class JoinLeaveListener {
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
