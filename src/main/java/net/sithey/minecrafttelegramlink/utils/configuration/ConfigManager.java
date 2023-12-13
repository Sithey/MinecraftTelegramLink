package net.sithey.minecrafttelegramlink.utils.configuration;

import net.sithey.minecrafttelegramlink.Main;

public class ConfigManager {

    /**
     * Get the config manager instance
     * @return ConfigManager instance
     */

    public static ConfigManager get(){
        return Main.get().getConfigManager();
    }

    private final Config config;
    private final String botToken, chatId;

    public ConfigManager(){

        config = new Config("config.yml");
        config.load();

        botToken = config.addValue("telegram.botToken", "your bot token here").toString();
        chatId = config.addValue("telegram.chatId", "your chat id here").toString();

    }

    /**
     * Get the configuration file
     * @return Config
     */

    public Config getConfig() {
        return config;
    }

    /**
     * Get the bot token
     * @return Bot token
     */

    public String getBotToken() {
        return botToken;
    }

    /**
     * Get the chat id
     * @return Chat id
     */

    public String getChatId() {
        return chatId;
    }
}
