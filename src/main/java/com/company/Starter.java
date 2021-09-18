package com.company;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static com.company.FileLoader.readFromFile;
import static com.company.PageParser.getName;
import static com.company.PageParser.getPrice;

public class Starter {
    static DBLayer dbLayer;

    public static void main(String[] args) throws IOException, SQLException, ClassNotFoundException {
        dbLayer = new DBLayer();
        parsePrices();

        dbLayer.shutdown();
    }



    private static void parsePrices() throws IOException, SQLException {
        File file = new FileLoader().getFileFromURL("links.txt");
        List<String> uris = readFromFile(file);

        Map<String, Integer> results = new HashMap<>();
        int price;
        String name;
        for (String uri : uris) {
            Document doc = Jsoup.connect(uri).get();
            price = getPrice(doc);
            name = getName(doc);
            results.put(name, price);
        }
        if (!results.isEmpty()) {
            for (var entry : results.entrySet()) {
                int id = dbLayer.addNameIfAbsent(entry.getKey());
                dbLayer.putPrice(id, entry.getValue(), System.currentTimeMillis());
            }
        }
    }
}
