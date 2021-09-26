package com.company;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

import static com.company.PageParser.parsePrices;
import static com.company.Starter.dbLayer;

public class Task extends TimerTask {
    private static final String path = "links.txt";

    @Override
    public void run() {
        try {
            Map<String, Integer> fileRows = new FileLoader().parseFile(path);
            updateBDFromInputFile(fileRows);
            parsePrices(fileRows);
            Map<String, Integer> needToSend = validatePrices(fileRows);
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

    private void updateBDFromInputFile(Map<String, Integer> fileRows) throws SQLException {
        for (Map.Entry<String, Integer> entry : fileRows.entrySet()) {
            String key = entry.getKey();
            Integer value = entry.getValue();
            DBLayer.addNameIfAbsent(key, value);
        }
    }

    private Map<String, Integer> validatePrices(Map<String, Integer> fileRows) throws SQLException {
        List<Integer> ids = dbLayer.selectAllIds();
        Map<String, Integer> response = new HashMap<>();
        for (var id : ids) {
            var lastPrice = dbLayer.selectPrice(id);
            var name = dbLayer.selectName(id);
            if (lastPrice <= fileRows.get(name))
                response.put(name, lastPrice);
        }
        return response;
    }
}