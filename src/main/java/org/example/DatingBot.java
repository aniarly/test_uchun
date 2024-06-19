package org.example;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatingBot extends TelegramLongPollingBot {

    private final String botUsername = "mitoyah_tanishuv_bot";
    private final String botToken = "7020603867:AAFbzUtJJs8vqwH1PR9hQS_kjlcGAr5DIzY";
    private final Map<Long, String> userGenders = new HashMap<>();
    private final Map<Long, Long> userConnections = new HashMap<>();
    private final long groupId = -1002200196291L; // Yopiq guruhingizning ID sini bu yerga kiriting

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            Message message = update.getMessage();
            Long chatId = message.getChatId();
            String username = message.getFrom().getUserName();
            if (message.hasText()) {
                String text = message.getText();
                if (text.equals("/start")) {
                    sendGenderPrompt(chatId);
                } else {
                    forwardMessage(chatId, text, username);
                }
            }
        } else if (update.hasCallbackQuery()) {
            Long chatId = update.getCallbackQuery().getMessage().getChatId();
            String gender = update.getCallbackQuery().getData();
            String username = update.getCallbackQuery().getFrom().getUserName();
            saveUserGender(chatId, gender, username);
            connectUser(chatId);
        }
    }

    private void sendGenderPrompt(Long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText("Assalomu alaykum! Iltimos, jinsingizni tanlang:");

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        InlineKeyboardButton maleButton = new InlineKeyboardButton();
        maleButton.setText("Erkak");
        maleButton.setCallbackData("Male");

        InlineKeyboardButton femaleButton = new InlineKeyboardButton();
        femaleButton.setText("Ayol");
        femaleButton.setCallbackData("Female");

        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(maleButton);
        row.add(femaleButton);

        rows.add(row);
        markup.setKeyboard(rows);
        message.setReplyMarkup(markup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void saveUserGender(Long chatId, String gender, String username) {
        userGenders.put(chatId, gender);
        sendMessage(chatId, "Siz " + (gender.equals("Male") ? "Erkak" : "Ayol") + " jinsini tanladingiz.");
        logToGroup(chatId, "Foydalanuvchi: @" + username + "; Jinsi: " + (gender.equals("Male") ? "Erkak" : "Ayol"));
    }

    private void connectUser(Long chatId) {
        Long matchedUser = userConnections.entrySet().stream()
                .filter(entry -> entry.getValue() == null)
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);

        if (matchedUser != null) {
            userConnections.put(chatId, matchedUser);
            userConnections.put(matchedUser, chatId);
            sendMessage(chatId, "Siz boshqa foydalanuvchi bilan bog'landingiz.");
            sendMessage(matchedUser, "Siz boshqa foydalanuvchi bilan bog'landingiz.");
        } else {
            sendMessage(chatId, "Kechirasiz, hozircha foydalanuvchi topa olmadim. Bironta foydalanuvchi qo'shilsa sizga habar beraman.");
            userConnections.put(chatId, null);
        }
    }

    private void forwardMessage(Long chatId, String text, String username) {
        Long matchedUser = userConnections.get(chatId);
        if (matchedUser != null) {
            sendMessage(matchedUser, text);
            logToGroup(chatId, "Foydalanuvchi: @" + username + "; Id: " + chatId + "; Xabar: " + text + ";");
        }
    }

    private void logToGroup(Long chatId, String text) {
        sendMessage(groupId, text);
    }

    private void sendMessage(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new DatingBot());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        System.out.println("Ishladi...");
    }
}





















//package org.example;
//
//import org.telegram.telegrambots.bots.TelegramLongPollingBot;
//import org.telegram.telegrambots.meta.TelegramBotsApi;
//import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
//import org.telegram.telegrambots.meta.api.objects.Message;
//import org.telegram.telegrambots.meta.api.objects.Update;
//import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
//import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
//
//import java.util.HashMap;
//import java.util.Map;
//
//public class DatingBot extends TelegramLongPollingBot {
//
//    private final String botUsername;
//    private final String botToken;
//    private final Map<Long, String> userGenders = new HashMap<>();
//    private final Map<Long, Long> userConnections = new HashMap<>();
//    private final long groupId = -1002200196291L; // Your private group ID here
//
//    public DatingBot(String botUsername, String botToken) {
//        this.botUsername = botUsername;
//        this.botToken = botToken;
//    }
//
//    @Override
//    public String getBotUsername() {
//        return botUsername;
//    }
//
//    @Override
//    public String getBotToken() {
//        return botToken;
//    }
//
//    @Override
//    public void onUpdateReceived(Update update) {
//        if (update.hasMessage()) {
//            Message message = update.getMessage();
//            Long chatId = message.getChatId();
//            if (message.hasText()) {
//                String text = message.getText();
//                if (text.equals("/start")) {
//                    sendGenderPrompt(chatId);
//                } else if (text.equalsIgnoreCase("Male") || text.equalsIgnoreCase("Female")) {
//                    saveUserGender(chatId, text);
//                    connectUser(chatId);
//                } else {
//                    forwardMessage(chatId, text);
//                }
//            }
//        }
//    }
//
//    private void sendGenderPrompt(Long chatId) {
//        sendMessage(chatId, "Welcome! Please select your gender: Male or Female");
//    }
//
//    private void saveUserGender(Long chatId, String gender) {
//        userGenders.put(chatId, gender);
//        sendMessage(chatId, "You selected: " + gender);
//    }
//
//    private void connectUser(Long chatId) {
//        String userGender = userGenders.get(chatId);
//        Long matchedUser = userConnections.entrySet().stream()
//                .filter(entry -> !entry.getKey().equals(chatId) && !userGenders.get(entry.getKey()).equals(userGender))
//                .map(Map.Entry::getKey)
//                .findFirst()
//                .orElse(null);
//
//        if (matchedUser != null) {
//            userConnections.put(chatId, matchedUser);
//            userConnections.put(matchedUser, chatId);
//            sendMessage(chatId, "Connected with user: " + matchedUser);
//            sendMessage(matchedUser, "Connected with user: " + chatId);
//        } else {
//            userConnections.put(chatId, null);
//            sendMessage(chatId, "Waiting for a match...");
//        }
//    }
//
//    private void forwardMessage(Long chatId, String text) {
//        Long matchedUser = userConnections.get(chatId);
//        if (matchedUser != null) {
//            sendMessage(matchedUser, text);
//            logToGroup(chatId, text);
//        }
//    }
//
//    private void logToGroup(Long chatId, String text) {
//        sendMessage(groupId, "User: " + chatId + " Message: " + text);
//    }
//
//    private void sendMessage(Long chatId, String text) {
//        SendMessage message = new SendMessage();
//        message.setChatId(chatId.toString());
//        message.setText(text);
//        try {
//            execute(message);
//        } catch (TelegramApiException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public static void main(String[] args) {
//        try {
//            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
//            botsApi.registerBot(new DatingBot("mitoyah_tanishuv_bot", "7020603867:AAFbzUtJJs8vqwH1PR9hQS_kjlcGAr5DIzY"));
//        } catch (TelegramApiException e) {
//            e.printStackTrace();
//        }
//        System.out.println("Ishladi...");
//    }
//}
//
//
//




























//package org.example;
//
//import org.telegram.telegrambots.bots.TelegramLongPollingBot;
//import org.telegram.telegrambots.meta.TelegramBotsApi;
//import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
//import org.telegram.telegrambots.meta.api.objects.Message;
//import org.telegram.telegrambots.meta.api.objects.Update;
//import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
//import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
//
//import java.util.HashMap;
//import java.util.Map;
//
//public class DatingBot extends TelegramLongPollingBot {
//
//    private final String botUsername;
//    private final String botToken;
//    private final Map<Long, String> userGenders = new HashMap<>();
//    private final Map<Long, Long> waitingUsers = new HashMap<>();
//    private final long groupId = -1002200196291L; // Your private group ID here
//
//    public DatingBot(String botUsername, String botToken) {
//        this.botUsername = botUsername;
//        this.botToken = botToken;
//    }
//
//    @Override
//    public String getBotUsername() {
//        return botUsername;
//    }
//
//    @Override
//    public String getBotToken() {
//        return botToken;
//    }
//
//    @Override
//    public void onUpdateReceived(Update update) {
//        if (update.hasMessage()) {
//            Message message = update.getMessage();
//            Long chatId = message.getChatId();
//            if (message.hasText()) {
//                String text = message.getText();
//                if (text.equals("/start")) {
//                    sendGenderPrompt(chatId);
//                } else if (text.equalsIgnoreCase("Male") || text.equalsIgnoreCase("Female")) {
//                    saveUserGender(chatId, text);
//                    connectUser(chatId);
//                } else {
//                    forwardMessage(chatId, text);
//                }
//            }
//        }
//    }
//
//    private void sendGenderPrompt(Long chatId) {
//        sendMessage(chatId, "Welcome! Please select your gender: Male or Female");
//    }
//
//    private void saveUserGender(Long chatId, String gender) {
//        userGenders.put(chatId, gender);
//        sendMessage(chatId, "You selected: " + gender);
//    }
//
//    private void connectUser(Long chatId) {
//        String userGender = userGenders.get(chatId);
//        Long matchedUser = waitingUsers.entrySet().stream()
//                .filter(entry -> !entry.getKey().equals(chatId) && !userGenders.get(entry.getKey()).equals(userGender))
//                .map(Map.Entry::getKey)
//                .findFirst()
//                .orElse(null);
//
//        if (matchedUser != null) {
//            waitingUsers.remove(matchedUser);
//            sendMessage(chatId, "Connected with user: " + matchedUser);
//            sendMessage(matchedUser, "Connected with user: " + chatId);
//        } else {
//            waitingUsers.put(chatId, null);
//            sendMessage(chatId, "Waiting for a match...");
//        }
//    }
//
//    private void forwardMessage(Long chatId, String text) {
//        Long matchedUser = waitingUsers.entrySet().stream()
//                .filter(entry -> entry.getKey().equals(chatId))
//                .map(Map.Entry::getKey)
//                .findFirst()
//                .orElse(null);
//
//        if (matchedUser != null) {
//            sendMessage(matchedUser, text);
//            logToGroup(chatId, text);
//        }
//    }
//
//    private void logToGroup(Long chatId, String text) {
//        sendMessage(groupId, "User: " + chatId + " Message: " + text);
//    }
//
//    private void sendMessage(Long chatId, String text) {
//        SendMessage message = new SendMessage();
//        message.setChatId(chatId.toString());
//        message.setText(text);
//        try {
//            execute(message);
//        } catch (TelegramApiException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public static void main(String[] args) {
//        try {
//            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
//            botsApi.registerBot(new DatingBot("mitoyah_tanishuv_bot", "7020603867:AAFbzUtJJs8vqwH1PR9hQS_kjlcGAr5DIzY"));
//        } catch (TelegramApiException e) {
//            e.printStackTrace();
//        }
//        System.out.println("Ishladi...");
//    }
//}
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
////package org.example;
////
////import org.telegram.telegrambots.bots.TelegramLongPollingBot;
////import org.telegram.telegrambots.meta.TelegramBotsApi;
////import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
////import org.telegram.telegrambots.meta.api.objects.Message;
////import org.telegram.telegrambots.meta.api.objects.Update;
////import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
////import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
////
////import java.util.HashMap;
////import java.util.Map;
////
////public class DatingBot extends TelegramLongPollingBot {
////
////    private final String botUsername;
////    private final String botToken;
////    private final Map<Long, String> userGenders = new HashMap<>();
////    private final Map<Long, Long> waitingUsers = new HashMap<>();
////    private final long groupId = -1002200196291L; // Your private group ID here
////
////    public DatingBot(String botUsername, String botToken) {
////        this.botUsername = botUsername;
////        this.botToken = botToken;
////    }
////
////    @Override
////    public String getBotUsername() {
////        return botUsername;
////    }
////
////    @Override
////    public String getBotToken() {
////        return botToken;
////    }
////
////    @Override
////    public void onUpdateReceived(Update update) {
////        if (update.hasMessage()) {
////            Message message = update.getMessage();
////            Long chatId = message.getChatId();
////            if (message.hasText()) {
////                String text = message.getText();
////                if (text.equals("/start")) {
////                    sendGenderPrompt(chatId);
////                } else if (text.equalsIgnoreCase("Male") || text.equalsIgnoreCase("Female")) {
////                    saveUserGender(chatId, text);
////                    connectUser(chatId);
////                } else {
////                    forwardMessage(chatId, text);
////                }
////            }
////        }
////    }
////
////    private void sendGenderPrompt(Long chatId) {
////        sendMessage(chatId, "Welcome! Please select your gender: Male or Female");
////    }
////
////    private void saveUserGender(Long chatId, String gender) {
////        userGenders.put(chatId, gender);
////        sendMessage(chatId, "You selected: " + gender);
////    }
////
////    private void connectUser(Long chatId) {
////        String userGender = userGenders.get(chatId);
////        Long matchedUser = waitingUsers.entrySet().stream()
////                .filter(entry -> !entry.getKey().equals(chatId) && !entry.getValue().equals(userGender))
////                .map(Map.Entry::getKey)
////                .findFirst()
////                .orElse(null);
////
////        if (matchedUser != null) {
////            waitingUsers.remove(matchedUser);
////            waitingUsers.remove(chatId);
////            sendMessage(chatId, "Connected with user: " + matchedUser);
////            sendMessage(matchedUser, "Connected with user: " + chatId);
////        } else {
////            waitingUsers.put(chatId, Long.valueOf(userGender));
////            sendMessage(chatId, "Waiting for a match...");
////        }
////    }
////
////    private void forwardMessage(Long chatId, String text) {
////        Long matchedUser = waitingUsers.get(chatId);
////        if (matchedUser != null) {
////            sendMessage(matchedUser, text);
////            logToGroup(chatId, text);
////        }
////    }
////
////    private void logToGroup(Long chatId, String text) {
////        sendMessage(groupId, "User: " + chatId + " Message: " + text);
////    }
////
////    private void sendMessage(Long chatId, String text) {
////        SendMessage message = new SendMessage();
////        message.setChatId(chatId.toString());
////        message.setText(text);
////        try {
////            execute(message);
////        } catch (TelegramApiException e) {
////            e.printStackTrace();
////        }
////    }
////
////    public static void main(String[] args) {
////        try {
////            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
////            botsApi.registerBot(new DatingBot("mitoyah_tanishuv_bot", "7020603867:AAFbzUtJJs8vqwH1PR9hQS_kjlcGAr5DIzY"));
////        } catch (TelegramApiException e) {
////            e.printStackTrace();
////        }
////        System.out.println("Ishladi...");
////    }
////}
