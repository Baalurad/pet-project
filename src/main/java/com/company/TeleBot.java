package com.company;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class TeleBot {
    TelegramBot bot;
    private static final long myChatId = 659027810;
    private static final long sunnyChatId = 659027810;

    TeleBot() throws IOException {
        File file = new FileLoader().getFileFromURL("teleToken.txt");
        List<String> rows = FileLoader.readFromFile(file);
        bot = new TelegramBot(rows.get(0));
    }

    void sendToBot(String text) throws IOException {
        /*
        GetUpdates getUpdates = new GetUpdates().limit(100).offset(0).timeout(0);
        GetUpdatesResponse updatesResponse = bot.execute(getUpdates);
        List<Update> updates = updatesResponse.updates();
         */
        SendResponse sendResponse = bot.execute(new SendMessage(myChatId, text));
    }

    void sendToBot(Map<String, Integer> map) throws IOException {
        if (!map.isEmpty())
            for (Map.Entry<String, Integer> entry : map.entrySet()) {
                sendToBot(entry.getKey() + " " + entry.getValue() + "руб!");
            }
    }
}