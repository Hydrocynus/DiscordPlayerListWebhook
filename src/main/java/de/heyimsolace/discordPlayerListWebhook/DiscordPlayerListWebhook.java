package de.heyimsolace.discordPlayerListWebhook;

import de.heyimsolace.discordPlayerListWebhook.event.JoinLeaveListener;
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
//@Mod.EventBusSubscriber(modid = DiscordPlayerListWebhook.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.DEDICATED_SERVER)
public class DiscordPlayerListWebhook {
    public static final String MODID = "discordplayerlistwebhook";



    public DiscordPlayerListWebhook() {
        // Register the event listener & Mod
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new JoinLeaveListener());
    }

}
