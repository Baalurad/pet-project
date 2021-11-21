package com.company;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

import static com.company.PageParser.parsePrices;

public class Task extends TimerTask {
    private static final String path = "links.txt";

    @Override
    public void run() {
        System.out.println("New task started at: " + + System.currentTimeMillis());
        try {
            DBLayer.connectToDb();
            Map<String, Integer> fileRows = new FileLoader().parseFile(path);
            updateBDFromInputFile(fileRows);
            parsePrices(fileRows);
            Map<String, Integer> needToSend = validatePrices(fileRows);
            new TeleBot().sendToBot(needToSend);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                DBLayer.shutdown();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        System.out.println("Task completed at: " + System.currentTimeMillis());
    }

    private void updateBDFromInputFile(Map<String, Integer> fileRows) throws SQLException {
        for (Map.Entry<String, Integer> entry : fileRows.entrySet()) {
            String key = entry.getKey();
            Integer value = entry.getValue();
            DBLayer.addNameIfAbsent(key, value);
        }
    }

    private Map<String, Integer> validatePrices(Map<String, Integer> fileRows) throws SQLException {
        List<Integer> ids = DBLayer.selectAllIds();
        Map<String, Integer> response = new HashMap<>();
        for (var id : ids) {
            var lastPrice = DBLayer.selectPrice(id);
            var name = DBLayer.selectName(id);
            if (lastPrice <= fileRows.get(name) && lastPrice != 0)
                response.put(name, lastPrice);
        }
        return response;
    }
}