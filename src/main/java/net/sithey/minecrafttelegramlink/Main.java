package net.sithey.minecrafttelegramlink;

import net.sithey.minecrafttelegramlink.listener.ConnectionListener;
import net.sithey.minecrafttelegramlink.telegrambot.TelegramBot;
import net.sithey.minecrafttelegramlink.utils.configuration.Config;
import net.sithey.minecrafttelegramlink.utils.configuration.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class Main extends JavaPlugin {

    private static Main instance;
    private ConfigManager configManager;
    private TelegramBot telegramBot;

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        configManager = new ConfigManager();
        telegramBot = new TelegramBot();
        telegramBot.sendStartMessage();

        Bukkit.getPluginManager().registerEvents(new ConnectionListener(), this);
    }

    @Override
    public void onDisable() {
        telegramBot.sendStopMessage();
    }

    /**
     * Get the JavaPlugin instance
     * @return JavaPlugin instance
     */

    public static Main get() {
        return instance;
    }

    /**
     * Get the config manager
     * @return ConfigManager
     */

    public ConfigManager getConfigManager() {
        return configManager;
    }

    /**
     * Get the TelegramBot
     * @return TelegramBot
     */

    public TelegramBot getTelegramBot() {
        return telegramBot;
    }
}
