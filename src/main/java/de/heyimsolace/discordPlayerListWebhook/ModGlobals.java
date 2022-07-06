package de.heyimsolace.discordPlayerListWebhook;

import de.heyimsolace.discordPlayerListWebhook.config.DefaultsMaker;
import de.heyimsolace.discordPlayerListWebhook.config.FileParser;

import java.io.File;

public class ModGlobals {

    public static final String CONFIG_FILE_NAME = "config/DiscordPlayerListWebhook/cfg.config";
    public static final String PLAYER_MAP_NAME = "config/DiscordPlayerListWebhook/playermap.config";
    public static final String CONFIG_DIR_NAME = "config/DiscordPlayerListWebhook";

    private static FileParser mainConfig;
    public static FileParser getMainConfig() {
        if (mainConfig == null) {
            mainConfig = new FileParser(CONFIG_FILE_NAME, DefaultsMaker.mainConfig());
        }
        return mainConfig;
    }

    private static FileParser playerMap;
    public static FileParser getPlayerMap() {
        if (playerMap == null) {
            playerMap = new FileParser(PLAYER_MAP_NAME, DefaultsMaker.playerMap());
        }
        return playerMap;
    }

    public static void reloadConfigs() {
        if (mainConfig != null) {
            mainConfig.loadFile();
        }
        if (playerMap != null) {
            playerMap.loadFile();
        }
    }

}
