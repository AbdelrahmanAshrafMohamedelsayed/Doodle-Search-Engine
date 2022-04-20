package com.company;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
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
    
    private static Object lockObj1 = new Object();
    private static Object userAgentLockObj = new Object();
    private static final int MAX_DEPTH = 3;
    private static int NUM_THREADS = 1;
    private static int MAX_THREADS = 5;
    private Thread thread;
    private String first_link;
    private static int currentDoc = 0;
    public static ArrayList<Pair> visitedLinks = new ArrayList<Pair>();
    public static ArrayList<Document> docs = new ArrayList<Document>();
    private int ID;
    public static ArrayList<RobotRule> robotSafe(URL url)
    {
        String strHost = url.getHost();
//        System.out.println(url.getPath());
        String strRobot = "http://" + strHost + "/robots.txt";
        URL urlRobot;
        try { urlRobot = new URL(strRobot);
        } catch (MalformedURLException e) {
            // something weird is happening, so don't trust it
            return null;
        }
//        System.out.println("okay");

        String strCommands;
        try
        {
//            System.out.println("okay2");

            InputStream urlRobotStream = urlRobot.openStream();
            byte b[] = new byte[1000];
            int numRead = ((InputStream) urlRobotStream).read(b);
//            System.out.println(b);
//            System.out.println("okay3");
            strCommands = new String(b, 0, numRead);
            while (numRead != -1) {
                numRead = urlRobotStream.read(b);
                if (numRead != -1)
                {
                    String newCommands = new String(b, 0, numRead);
                    strCommands += newCommands;
                }
            }
//            System.out.println(strCommands);
            urlRobotStream.close();
        }
        catch (IOException e)
        {
            return null; // if there is no robots.txt file, it is OK to search
        }
//        System.out.println(strCommands);

        ArrayList<RobotRule> robotRules = new ArrayList<>();
        if (strCommands.contains("Disallow")) // if there are no "disallow" values, then they are not blocking anything.
        {
//            System.out.println("kkk");
            String[] split = strCommands.split("\n");
            RobotRule r2=new RobotRule();
            String mostRecentUserAgent = null;
            RobotRule r = null;
            for (int i = 0; i < split.length; i++)
            {
                String line = split[i].trim();
                if (line.toLowerCase().startsWith("user-agent"))
                {
                    int start = line.indexOf(":") + 1;
                    int end   = line.length();
                    r = new RobotRule();
                    robotRules.add(r);
                    mostRecentUserAgent = line.substring(start, end).trim();
                }
                else if (line.startsWith("Disallow")) {
                    if (mostRecentUserAgent != null && r != null) {

                        r.userAgent = mostRecentUserAgent;
//                        System.out.println(r.userAgent);
                        int start = line.indexOf(":") + 1;
                        int end   = line.length();
                        String path = url.getHost();
                        String newRule = path + line.substring(start, end).trim();
                        r.rules.add(newRule);
                    }
                }
            }
//            System.out.println(robotRules.size());
            /*for (RobotRule robotRule : robotRules)
            {
                String path = url.getHost();
                if (robotRule.rule.length() == 0) return true; // allows everything if BLANK
                if (robotRule.rule == "/") return false;       // allows nothing if /
//                System.out.println(url.getPath());
//                System.out.println(url.getHost());
//                if (robotRule.rule.length() <= path.length())
//                {
//                    String pathCompare = path.substring(0, robotRule.rule.length());
//                    if (pathCompare.equals(robotRule.rule)) return false;
//                }
                System.out.println(path + robotRule.rule);
            }*/
            for (RobotRule robotRule : robotRules)
            {
                System.out.println(robotRule.userAgent);
                for (String rule : robotRule.rules) {
                    System.out.println(rule);
                }
            }
        }
        return robotRules;
    }
    public WebCrawler(String link, int num) {
        System.out.println("WebCrawler Created");
        first_link = link;
        ID = num;

        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        try {
            crawl(1, first_link);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private void crawl(int level, String url) throws MalformedURLException {
        if(currentDoc <= 5) {
            Document doc = request(url);
            URL urlRobot = new URL(url);
            ArrayList<RobotRule> notAllowedList=robotSafe(urlRobot);
            if(doc != null && ((notAllowedList != null && !notAllowedList.contains(url)) || notAllowedList == null)) {
                for (Element link : doc.select("a[href]")) {
                    String next_link = link.absUrl("href");
                    synchronized (this) {
                        boolean checkRepeat = true;
                        URL url2;
                        url2=new URL(next_link);


                        if(notAllowedList != null){

                        }
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

                for (Pair link : visitedLinks) {
                    if(link.second.equals(docContentFirst50)){
                        return  null;
                    }
                }

                Pair newPair = new Pair(url, docContentFirst50);
                synchronized (lockObj1){
                    System.out.println(String.format("\n%d : %s : %s", ID, url, docContentFirst50));
                    visitedLinks.add(newPair);
                    docs.add(doc);
                    currentDoc++;
                    System.out.println(currentDoc);
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
