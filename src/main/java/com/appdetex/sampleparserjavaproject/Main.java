package com.appdetex.sampleparserjavaproject;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;

/**
 * Main Java Class
 *
 * This class will use Jsoup to retrieve a provided URL
 * and parse out certain data, printing that data to
 * stdout in a JSON format.
 */
public class Main {

    public static void main( String[] args ) {
        try {
            Document doc = Jsoup.connect(args[0]).get();
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode node = mapper.createObjectNode();
            node.put("title", getTitle(doc));
            node.put("description", getDescription(doc));
            node.put("publisher", getPublisher(doc));
            node.put("price", getPrice(doc));
            node.put("rating", getRating(doc));

            System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(node));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getTitle(Document doc) {
        return doc.select("div.id-app-title").first().text();
    }

    private static String getPublisher(Document doc) {
        Element publisherElement = doc.select("div.left-info").first();
        String publisherString = publisherElement.toString();
        int index1 = publisherString.indexOf("<span itemprop=\"name\">");
        int index2 = publisherString.indexOf("</span>", index1);
        return publisherString.substring(index1 + 22, index2);
    }

    private static String getPrice(Document doc) {
        Element priceElement = doc.select(".price").first();
        String priceString = priceElement.toString();
        int index1 = priceString.indexOf("\" itemprop=\"price\"");
        int index2 = priceString.indexOf("<meta content=\"", index1-25);
        return priceString.substring(index2 + 15, index1);
    }

    private static String getDescription(Document doc) {
        Element descriptionElement = doc.select(".description").first();
        return descriptionElement.child(0).child(0).child(1).child(0).text();
    }

    private static String getRating(Document doc) {
        Element ratingElement = doc.select("div.score").first();
        String ratingString = ratingElement.text();
        return ratingString;
    }

    @Test
    public void test() {
        try {
            Document doc = Jsoup.connect("https://play.google.com/store/apps/details?id=com.exozet.game.carcassonne")
                .get();
            assert(getTitle(doc).equals("Carcassonne"));
            assert(getDescription(doc).startsWith("The award-winning tile based board game is finally here on Android!"));
            assert(getPublisher(doc).equals("Exozet"));
            assert(getPrice(doc).equals("$0.99"));
            assert(getRating(doc).equals("4.3"));
        } catch (IOException e) {
            assert(e == null);
        }
    }

}
