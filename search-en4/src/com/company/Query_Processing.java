package com.company;

//import org.tartarus.snowball.ext.PorterStemmer;

import org.tartarus.snowball.ext.PorterStemmer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Query_Processing {

//    @BeforeClass
//    public static void loadStop-words() throws IOException {
//
//
//    }
    public static void main(String[] args) throws IOException {
        PorterStemmer stem = new PorterStemmer();
        List<String> stop_words = Files.readAllLines(Paths.get("s.txt"));
        String X="engineer is my hi step    at,,a, time ,!";
        String[] q=X.split( "[\\s,]+" );
        StringBuilder builder = new StringBuilder();
        for(String word : q) {
            if (!stop_words.contains(word)) {
                //////////////////
                stem.setCurrent(word);
                stem.stem();
                word = stem.getCurrent();
                ////////////////////////
                builder.append(word);
                builder.append(' ');
            }
        }
        String result = builder.toString().trim();
        System.out.println(result);
    }


}
