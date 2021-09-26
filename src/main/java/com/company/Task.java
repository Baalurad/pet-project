package com.company;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.GetUpdates;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.GetUpdatesResponse;
import com.pengrad.telegrambot.response.SendResponse;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

import static com.company.DBLayer.selectId;
import static com.company.PageParser.getName;
import static com.company.PageParser.getPrice;
import static com.company.Starter.dbLayer;

public class Task extends TimerTask {
    static Map<String, Integer> rows = new HashMap<>();
    private static final String path = "links.txt";


    @Override
    public void run() {
        try {
            updateBDFromInputFile();
            //parsePrices();
            Map<String, Integer> needToSend = validatePrices();
            new TeleBot().sendToBot(needToSend);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                dbLayer.shutdown();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }


    private void updateBDFromInputFile() throws IOException, SQLException {
        if (rows.isEmpty())
            rows = new FileLoader().parseFile(path);
        for (Map.Entry<String, Integer> entry : rows.entrySet()) {
            String key = entry.getKey();
            Integer value = entry.getValue();
            dbLayer.addNameIfAbsent(key, value);
        }
    }

    public static void parsePrices() throws IOException, SQLException {
        if (rows.isEmpty())
            rows = new FileLoader().parseFile(path);

        Map<String, Integer> results = new HashMap<>();
        for (String uri : rows.keySet()) {

            Document doc = Jsoup.connect(uri).get();
            results.put(getName(doc), getPrice(doc));
        }
        if (!results.isEmpty()) {
            for (var entry : results.entrySet()) {
                int id = selectId(entry.getKey()).getInt("Id");
                dbLayer.putPrice(id, entry.getValue(), System.currentTimeMillis());
            }
        }
    }

    private static Map<String, Integer> validatePrices() throws SQLException {
        List<Integer> ids = dbLayer.selectAllIds();
        Map<String, Integer> response = new HashMap<>();
        for (var id : ids) {
            var lastPrice = dbLayer.selectPrice(id);
            var name = dbLayer.selectName(id);
            if (lastPrice <= rows.get(name))
                response.put(name, lastPrice);
        }
        return response;
    }
}
