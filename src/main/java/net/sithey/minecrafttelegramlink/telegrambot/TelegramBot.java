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

public class TelegramBot extends TelegramLongPollingBot {

    public TelegramBot(){
        super(ConfigManager.get().getBotToken());
        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(this);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            handle(update);
        } catch (TelegramApiException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getBotUsername() {
        return "MinecraftTelegramLink";
    }

    /**
     * Handle the update
     * @param update Update
     * @throws TelegramApiException TelegramApiException
     * @throws IOException IOException
     */
    private void handle(Update update) throws TelegramApiException, IOException {

        if (update.hasMessage()) {

            Message message = update.getMessage();

            if (!message.hasText())
                return;

            String text = message.getText();

            if (text.startsWith("/")) {

                text = text.replaceFirst("/", "");

                List<String> listArgs = new ArrayList<>(Arrays.stream(text.split(" ")).toList());

                if (listArgs.isEmpty())
                    return;

                String command = listArgs.remove(0);
                String[] args = listArgs.toArray(new String[0]);

                if (args.length == 0) {

                    if (command.equalsIgnoreCase("stop")) {

                        Bukkit.getServer().shutdown();

                    }

                    if (command.equalsIgnoreCase("playerlist")) {

                        SendMessage sendMessage = new SendMessage();
                        sendMessage.setChatId(message.getChatId());
                        sendMessage.setText("Player list: \n" + String.join("\n", Bukkit.getOnlinePlayers().stream().map(player -> player.getName() + " (" + player.getUniqueId() + ")").toArray(String[]::new)));
                        execute(sendMessage);

                    }

                    if (command.equalsIgnoreCase("chatid")) {

                        SendMessage sendMessage = new SendMessage();
                        sendMessage.setChatId(message.getChatId());
                        sendMessage.setText(message.getChatId().toString());
                        execute(sendMessage);

                    }


                    if (command.equalsIgnoreCase("latestlog")) {

                        File file = new File(Bukkit.getServer().getWorldContainer(), "logs/latest.log");

                        if (!file.exists()) {

                            SendMessage sendMessage = new SendMessage();
                            sendMessage.setChatId(message.getChatId());
                            sendMessage.setText("The latest log file doesn't exist");
                            execute(sendMessage);

                        } else {

                            SendDocument sendDocument = new SendDocument();
                            sendDocument.setChatId(message.getChatId());
                            sendDocument.setDocument(new InputFile(file));
                            sendDocument.setCaption("Latest log file");

                        }


                    }

                } else {

                    if (command.equalsIgnoreCase("console")) {

                        String commandConsole = String.join(" ", args);

                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), commandConsole);
                            }
                        }.runTask(Main.get());



                    }

                }


            } else {

                sendWelcomeMessage();

            }

        }else if (update.hasCallbackQuery()){

            String data = update.getCallbackQuery().getData();

            if (data.equalsIgnoreCase("/console")){

                SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId(update.getCallbackQuery().getMessage().getChatId());
                sendMessage.setText("Use /console <text> to send a command to the console");

                execute(sendMessage);

            }

            if (data.equalsIgnoreCase("/playerlist")){

                SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId(update.getCallbackQuery().getMessage().getChatId());
                sendMessage.setText("Player list: \n" + String.join("\n", Bukkit.getOnlinePlayers().stream().map(player -> player.getName() + " (" + player.getUniqueId() + ")").toArray(String[]::new)));
                execute(sendMessage);

            }

            if (data.equalsIgnoreCase("/latestlog")){

                File file = new File(Bukkit.getServer().getWorldContainer(), "logs/latest.log");

                if (!file.exists()) {

                    SendMessage sendMessage = new SendMessage();
                    sendMessage.setChatId(update.getCallbackQuery().getMessage().getChatId());
                    sendMessage.setText("The latest log file doesn't exist");
                    execute(sendMessage);

                } else {

                    SendDocument sendDocument = new SendDocument();
                    sendDocument.setChatId(update.getCallbackQuery().getMessage().getChatId());
                    sendDocument.setDocument(new InputFile(file));
                    sendDocument.setCaption("Latest log file");

                    execute(sendDocument);

                }

            }

            if (data.equalsIgnoreCase("/stop")){

                Bukkit.getServer().shutdown();

            }

        }

    }

    /**
     * Get the welcome message
     * return the welcome message
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
        consoleButton.setCallbackData("/console");;

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
            throw new RuntimeException(e);
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
            throw new RuntimeException(e);
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
            throw new RuntimeException(e);
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
            throw new RuntimeException(e);
        }

    }
}
