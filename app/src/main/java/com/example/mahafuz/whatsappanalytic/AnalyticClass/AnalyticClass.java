package com.example.mahafuz.whatsappanalytic.AnalyticClass;

import java.util.*;

public class AnalyticClass {
    private HashMap<String, Integer> map;

    public AnalyticClass(HashMap<String, Integer> map) {
        this.map = map;
    }

    public int countWord(String line){
        int countWords = line.split(" ").length;
        //System.out.println("Line Of Word Count: "+(countWords-1));
        return  countWords-1;
    }
     public void commonWords(String line){
         String sentence = line.toLowerCase();

         for(String word : sentence.split(" ")) {
             if(word.isEmpty()) {
                 continue;
             }
             if(map.containsKey(word)) {
                 map.put(word, map.get(word)+1);
             }
             else {
                 map.put(word, 1);
             }
         }
     }
}
