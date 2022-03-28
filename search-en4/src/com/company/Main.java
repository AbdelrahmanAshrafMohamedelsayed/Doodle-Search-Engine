package com.company;

import java.util.ArrayList;

import static com.company.WebCrawler.visitedLinks;

public class Main {
    public static ArrayList<WebCrawler> bots = new ArrayList<>();
    public static void main(String[] args) throws Exception {
        bots.add(new WebCrawler("https://stackoverflow.com/questions/3184883/concurrentmodificationexception-for-arraylist", 1));
//        bots.add(new WebCrawler("https://www.npr.org", 2));
//        bots.add(new WebCrawler("https://www.nytimes.com", 3));
        int botsSize = 5;
        for (int i = 0; i < botsSize; i++) {
            try {
                bots.get(i).getThread().join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        for (Pair link : visitedLinks) {
            System.out.println(link.first);
            System.out.println(link.second);
        }
    }
}

