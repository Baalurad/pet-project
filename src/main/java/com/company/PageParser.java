package com.company;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

import static com.company.DBLayer.selectId;

public class PageParser {
    public static String getName(Document doc) {
        Element element = doc.selectFirst(".same-part-kt__header");
        if (element == null)
            return null;
        StringBuilder result = new StringBuilder();
        for (Node child : element.childNodes()) {
            if (child instanceof Element) {
                result.append(((Element) child).text()).append(" ");
            }
        }
        return result.toString();
    }

    public static int getPrice(Document doc) {
        Elements elements = doc.select(".price-block__final-price");
        Node child = elements.get(0).childNode(0);
        String result;
        if (child instanceof TextNode) {
            result = (((TextNode) child).text());
            return Integer.parseInt(result.replaceAll("[^\\d.]+", ""));
        } else
            return 0;
    }

    public static void parsePrices(Map<String, Integer> fileRows) throws IOException, SQLException {
        for (var uri : fileRows.keySet()) {
            Document doc = Jsoup.connect(uri).get();
            var price = getPrice(doc);
            var id = selectId(uri).getInt("Id");

            DBLayer.putPrice(id, price, System.currentTimeMillis());
        }
    }
}