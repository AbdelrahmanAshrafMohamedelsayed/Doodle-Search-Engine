package com.company;

import java.net.MalformedURLException;
import java.util.ArrayList;

import static com.company.WebCrawler.visitedLinks;

class RobotRule {
    public String userAgent;
    public ArrayList<String> rules;

    RobotRule() {
        rules = new ArrayList<String>();
    }

//    @Override
//    public String toString() {
//        StringBuffer  result = new StringBuffer();
////        String NEW_LINE = System.getProperty("line.separator");
//        result.append(this.getClass().getName() + " Object {" + "\n");
//        result.append("   userAgent: " + this.userAgent + "\n");
//        result.append("   rule: " + this.rule + "\n");
//        result.append("}");
//        return result.toString();
//    }
}
public class Main {
    public static ArrayList<WebCrawler> bots = new ArrayList<>();

    public Main() throws MalformedURLException {
    }

    public static void main(String[] args) throws Exception {
        bots.add(new WebCrawler("https://www.facebook.com/", 1));
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
//        URL urlRobot = new URL("https://www.facebook.com");
//
//        WebCrawler.robotSafe(urlRobot);
    }

}

