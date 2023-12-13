package net.sithey.minecrafttelegramlink.telegrambot;

import net.sithey.minecrafttelegramlink.Main;
import net.sithey.minecrafttelegramlink.utils.configuration.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The TelegramBot class is responsible for integrating the Telegram bot with the Minecraft server.
 * It extends the TelegramLongPollingBot class from the TelegramBots Java library.
 */
public class TelegramBot extends TelegramLongPollingBot {

    /**
     * Constructor of the class. It initializes the bot using the Telegram bot token
     * extracted from the configuration file.
     */
    public TelegramBot() {
        super(ConfigManager.get().getBotToken());
        try {
            // Registers the bot with the TelegramBots API.
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(this);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles updates received by the bot.
     *
     * @param update The Update object representing the update.
     * @throws TelegramApiException Exception related to the Telegram API.
     * @throws IOException Exception related to IO operations.
     */
    @Override
    public void onUpdateReceived(Update update) {
        try {
            handle(update);
        } catch (TelegramApiException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the bot's username as defined in the configuration.
     *
     * @return The bot's username.
     */
    @Override
    public String getBotUsername() {
        return "MinecraftTelegramLink";
    }

    /**
     * Handles the update by calling the appropriate methods based on the update type.
     *
     * @param update The Update object representing the update.
     * @throws TelegramApiException Exception related to the Telegram API.
     * @throws IOException Exception related to IO operations.
     */
    private void handle(Update update) throws TelegramApiException, IOException {
        if (update.hasMessage()) {
            Message message = update.getMessage();
            if (message.hasText()) {
                String text = message.getText();
                if (text.startsWith("/")) {
                    handleCommand(message);
                } else {
                    sendWelcomeMessage();
                }
            }
        } else if (update.hasCallbackQuery()) {
            handleCallbackQuery(update.getCallbackQuery());
        }
    }

    /**
     * Handles commands received in the message.
     *
     * @param message The Message object representing the received message.
     * @throws TelegramApiException Exception related to the Telegram API.
     * @throws IOException Exception related to IO operations.
     */
    private void handleCommand(Message message) throws TelegramApiException, IOException {
        String text = message.getText().replaceFirst("/", "");
        List<String> listArgs = new ArrayList<>(Arrays.asList(text.split(" ")));
        if (listArgs.isEmpty()) return;

        String command = listArgs.remove(0);
        String[] args = listArgs.toArray(new String[0]);

        if (args.length == 0) {
            handleSimpleCommand(command, message);
        } else {
            handleComplexCommand(command, args);
        }
    }

    /**
     * Handles simple commands with no arguments.
     *
     * @param command The command to be processed.
     * @param message The Message object representing the received message.
     * @throws TelegramApiException Exception related to the Telegram API.
     * @throws IOException Exception related to IO operations.
     */
    private void handleSimpleCommand(String command, Message message) throws TelegramApiException, IOException {
        switch (command.toLowerCase()) {
            case "stop":
                Bukkit.getServer().shutdown();
                break;
            case "playerlist":
                sendPlayerList(message.getChatId());
                break;
            case "chatid":
                sendChatId(message.getChatId());
                break;
            case "latestlog":
                sendLatestLog(message.getChatId());
                break;
            default:
                break;
        }
    }

    /**
     * Handles complex commands with arguments.
     *
     * @param command The command to be processed.
     * @param args    The arguments associated with the command.
     */
    private void handleComplexCommand(String command, String[] args) {
        if (command.equalsIgnoreCase("console")) {
            String commandConsole = String.join(" ", args);
            executeConsoleCommand(commandConsole);
        }
    }

    /**
     * Executes a command in the console.
     *
     * @param commandConsole The command to be executed in the console.
     */
    private void executeConsoleCommand(String commandConsole) {
        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), commandConsole);
            }
        }.runTask(Main.get());
    }

    /**
     * Handles callback queries received by the bot.
     *
     * @param callbackQuery The CallbackQuery object representing the received query.
     * @throws TelegramApiException Exception related to the Telegram API.
     */
    private void handleCallbackQuery(CallbackQuery callbackQuery) throws TelegramApiException {
        String data = callbackQuery.getData();
        long chatId = callbackQuery.getMessage().getChatId();
        handleCallbackQuery(data, chatId);
    }

    /**
     * Handles callback queries based on the provided data and chat ID.
     *
     * @param data   The data associated with the callback query.
     * @param chatId The chat ID where the query was received.
     * @throws TelegramApiException Exception related to the Telegram API.
     */
    private void handleCallbackQuery(String data, long chatId) throws TelegramApiException {
        switch (data.toLowerCase()) {
            case "/console":
                sendConsoleInfo(chatId);
                break;
            case "/playerlist":
                sendPlayerList(chatId);
                break;
            case "/latestlog":
                sendLatestLog(chatId);
                break;
            case "/stop":
                Bukkit.getServer().shutdown();
                break;
            default:
                break;
        }
    }

    /**
     * Sends information about using the "/console" command to the specified chat.
     *
     * @param chatId The chat ID where the message should be sent.
     * @throws TelegramApiException Exception related to the Telegram API.
     */
    private void sendConsoleInfo(long chatId) throws TelegramApiException {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("Use /console <text> to send a command to the console");
        execute(sendMessage);
    }

    /**
     * Sends the list of online players to the specified chat.
     *
     * @param chatId The chat ID where the message should be sent.
     * @throws TelegramApiException Exception related to the Telegram API.
     */
    private void sendPlayerList(long chatId) throws TelegramApiException {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("Player list: \n" + String.join("\n", Bukkit.getOnlinePlayers().stream().map(player -> player.getName() + " (" + player.getUniqueId() + ")").toArray(String[]::new)));
        execute(sendMessage);
    }

    /**
     * Sends the chat ID to the specified chat.
     *
     * @param chatId The chat ID where the message should be sent.
     * @throws TelegramApiException Exception related to the Telegram API.
     */
    private void sendChatId(long chatId) throws TelegramApiException {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(String.valueOf(chatId));
        execute(sendMessage);
    }

    /**
     * Sends the latest log file to the specified chat.
     *
     * @param chatId The chat ID where the message should be sent.
     * @throws TelegramApiException Exception related to the Telegram API.
     */
    private void sendLatestLog(long chatId) throws TelegramApiException {
        File file = new File(Bukkit.getServer().getWorldContainer(), "logs/latest.log");
        if (!file.exists()) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            sendMessage.setText("The latest log file doesn't exist");
            execute(sendMessage);
        } else {
            SendDocument sendDocument = new SendDocument();
            sendDocument.setChatId(chatId);
            sendDocument.setDocument(new InputFile(file));
            sendDocument.setCaption("Latest log file");
            execute(sendDocument);
        }
    }

    /**
     * Sends a welcome message to the chat with inline keyboard buttons for various commands.
     *
     * @throws TelegramApiException Exception related to the Telegram API.
     */
    public void sendWelcomeMessage() throws TelegramApiException {

        SendMessage message = new SendMessage();
        message.setText("Welcome to the Minecraft server \n" +
                "IP: " + Bukkit.getIp() + ":" + Bukkit.getPort() + "\n" +
                "Control your server with this bot");
        message.setChatId(ConfigManager.get().getChatId());

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        List<InlineKeyboardButton> rowInline1 = new ArrayList<>(), rowInline2 = new ArrayList<>();

        InlineKeyboardButton consoleButton = new InlineKeyboardButton();
        consoleButton.setText("Console");
        consoleButton.setCallbackData("/console");

        InlineKeyboardButton playerListButton = new InlineKeyboardButton();
        playerListButton.setText("Player List");
        playerListButton.setCallbackData("/playerlist");

        InlineKeyboardButton latestLogButton = new InlineKeyboardButton();
        latestLogButton.setText("Latest Log");
        latestLogButton.setCallbackData("/latestlog");

        InlineKeyboardButton stopButton = new InlineKeyboardButton();
        stopButton.setText("Stop Server");
        stopButton.setCallbackData("/stop");

        rowInline1.add(consoleButton);
        rowInline1.add(playerListButton);
        rowInline2.add(latestLogButton);
        rowInline2.add(stopButton);

        rowsInline.add(rowInline1);
        rowsInline.add(rowInline2);

        inlineKeyboardMarkup.setKeyboard(rowsInline);
        message.setReplyMarkup(inlineKeyboardMarkup);

        execute(message);
    }

    /**
     * Send the join message
     * @param player Player
     */

    public void sendJoinMessage(Player player){

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(ConfigManager.get().getChatId());
        sendMessage.setText("Player " + player.getName() + " joined the server");

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            Bukkit.getConsoleSender().sendMessage("§c[TelegramBot] §7The bot token or the chat id is invalid");
        }

    }

    /**
     * Send the quit message
     * @param player Player
     */

    public void sendQuitMessage(Player player){

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(ConfigManager.get().getChatId());
        sendMessage.setText("Player " + player.getName() + " leaved the server");

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            Bukkit.getConsoleSender().sendMessage("§c[TelegramBot] §7The bot token or the chat id is invalid");
        }

    }

    /**
     * Send the start message
     */

    public void sendStartMessage(){

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(ConfigManager.get().getChatId());
        sendMessage.setText("The server is up.");

        try {
            sendWelcomeMessage();
            execute(sendMessage);
        } catch (TelegramApiException e) {
            Bukkit.getConsoleSender().sendMessage("§c[TelegramBot] §7The bot token or the chat id is invalid");
        }

    }

    /**
     * Send the stop message
     */

    public void sendStopMessage(){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(ConfigManager.get().getChatId());
        sendMessage.setText("The server is down.");

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            Bukkit.getConsoleSender().sendMessage("§c[TelegramBot] §7The bot token or the chat id is invalid");
        }

    }
}
