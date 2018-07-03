package com.example.mahafuz.whatsappanalytic.AnalyticClass;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;

import com.example.mahafuz.whatsappanalytic.Home;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AnalyticMain extends Home{
    private  String  filePath;
    private static String line = null;
    private static AnalyticClass analyticClass;
    private static HashMap<String, Integer> map = new HashMap<>();
    private static Map<String, Integer> map2;
    private static String filePathEdit = null;


    public void main(String filePath){
        analyticClass = new AnalyticClass(map);
        formatFile(filePath);
        if (filePathEdit==null){
            emailText(filePath);
        }else
            emailText(filePathEdit);
    }

    private void formatFile(String filePath) {
        Pattern pattern = Pattern.compile("^[0-3]?[0-9]/[0-3]?[0-9]/(?:[0-9]{2})?[0-9]{2}$");
        filePathEdit = filePath.replaceAll(".txt","").concat("edit.txt");

        try {
            InputStream inputStream = openFileInput(filePath);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader reader = new BufferedReader(inputStreamReader);
            FileWriter writer = new FileWriter(filePathEdit);
            Matcher matcher;
            int count = 0;
            String date;
            while ((line=reader.readLine())!=null){
                count++;
                date = line.split(",")[0];
                matcher = pattern.matcher(date);
                if (matcher.matches()){
                    //System.out.println("Line at: "+count+" True");
                    if (count==1)
                        writer.write(line);
                    else
                        writer.write("\n"+line);
                }

                else {
                    //System.out.println("Line at: "+count+" False");
                    writer.write(line);
                }
                writer.flush();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static void emailText(String filePath){
        FileReader fileReader = null;
        System.out.println(filePath);
        try (PrintWriter out = new PrintWriter(new FileWriter("output.txt"))) {
            int totalWords = 0;
            try {
                fileReader = new FileReader(filePath);
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                while ((line = bufferedReader.readLine()) != null) {
                    if (line.equals("\n"))
                        System.out.println("Starts from here");

                    String date = line.split(",")[0];
                    String time = line.split(",")[1].split("-")[0];
                    String name = line.split(",")[1].split("-")[1].split(":")[0];
                    String message = line.split(":")[2];
                    int n;
                    if ((n = line.split(":").length) > 3) {
                        for (int i = 3; i < n; i++) {
                            message = message + ":" + line.split(":")[i];
                        }
                    }

                    System.out.println("Date: " + date + " Time:" + time + " Name:" + name + " Message:" + message);
                    totalWords = totalWords + analyticClass.countWord(message);
                    analyticClass.commonWords(message);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Total Word: " + totalWords);
            map2 = new TreeMap<>(map);
            for (Map.Entry<String, Integer> entry : map2.entrySet()) {
                System.out.println(entry.getKey() + " \t" + entry.getValue());
                out.println(entry.getKey() + " \t" + entry.getValue());
                out.close();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
