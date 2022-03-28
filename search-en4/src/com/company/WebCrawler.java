package com.company;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;

import static com.company.Main.bots;

class  Pair {
    public String first;
    public String second;

    public Pair(String first, String second) {
        this.first = first;
        this.second = second;
    }
}
public class WebCrawler implements Runnable {

    private static final int MAX_DEPTH = 3;
    private static int NUM_THREADS = 1;
    private static int MAX_THREADS = 5;
    private Thread thread;
    private String first_link;
    private static int currentDoc = 0;
    public static ArrayList<Pair> visitedLinks = new ArrayList<Pair>();
    private int ID;

    public WebCrawler(String link, int num) {
        System.out.println("WebCrawler Created");
        first_link = link;
        ID = num;

        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        crawl(1, first_link);
    }

    private void crawl(int level, String url) {
        if(currentDoc <= 15) {
            Document doc = request(url);

            if(doc != null) {
                for (Element link : doc.select("a[href]")) {
                    String next_link = link.absUrl("href");
                    synchronized (this) {
                        boolean checkRepeat = true;
                        for (Pair visitedLink : visitedLinks) {
                            if(visitedLink.first.equals(next_link) == true){
                                checkRepeat = false;
                                break;
                            }
                        }
                        if (checkRepeat == true) {
                            if(NUM_THREADS < MAX_THREADS){
                                bots.add(new WebCrawler(next_link, ++NUM_THREADS));
                            }else{
                                crawl(++level, next_link);
                            }
                        }
                    }
                }
            }
        }
    }

    private Document request(String url) {
        try {
            Connection con = Jsoup.connect(url);
            Document doc = con.get();

            if(con.response().statusCode() == 200) {
                System.out.println(String.format("\nBot ID: %d Received Webpage at: %s", ID, url));

                String docContent = doc.text();
                String docContentArr[] = docContent.trim().split("\\s");
                int docContentLen = docContentArr.length;
                StringBuffer docContentBuffer = new StringBuffer();
                for (int i = 0; i < (docContentLen < 50 ? docContentLen : 50); i++) {
                    if(docContentArr[i].length() > 0){
                        docContentBuffer.append(docContentArr[i].charAt(0));
                    }
                }
                String docContentFirst50 = docContentBuffer.toString();
                Pair newPair = new Pair(url, docContentFirst50);

                boolean checkRepeat = true;
                for (Pair link : visitedLinks) {
                    if(link.second.equals(docContentFirst50)){
                        checkRepeat = false;
                        break;
                    }
                }
                if (checkRepeat==true) {
                    System.out.println(String.format("\n%d : %s : %s", ID, url, docContentFirst50));
                    synchronized (this){
                        visitedLinks.add(newPair);
                        currentDoc++;
                        System.out.println(currentDoc);
                    }
                }

                return doc;
            }
            return null;
        } catch (IOException e) {
            return null;
        }
    }

    public Thread getThread() {
        return thread;
    }
}
