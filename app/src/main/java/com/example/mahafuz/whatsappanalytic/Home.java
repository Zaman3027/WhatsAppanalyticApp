package com.example.mahafuz.whatsappanalytic;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mahafuz.whatsappanalytic.AnalyticClass.AnalyticClass;
import com.obsez.android.lib.filechooser.ChooserDialog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Home extends AppCompatActivity {
    String fileName;
    Button fileChooser;
    TextView textView;
    HashMap<String, Integer> map = new HashMap<>();
    AnalyticClass analyticClass = new AnalyticClass(map);
    Map<String, Integer> map2;
    final ArrayList<String> list = new ArrayList<String>();
    ListView listView;
    EditText editText;
    ArrayAdapter adapter;
    static final Integer READ_EXST = 0x4;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        textView = findViewById(R.id.viewData);
        listView = findViewById(R.id.ListViewData);
        editText = findViewById(R.id.editTextSearch);
        editText.setVisibility(View.INVISIBLE);
        askForPermission(Manifest.permission.READ_EXTERNAL_STORAGE,READ_EXST);
        final ChooserDialog chooserDialog = new ChooserDialog().with(this)
                .withStartFile(Environment.getExternalStorageDirectory().getPath())
                .withFilterRegex(false, false, ".*\\.txt")
                .withChosenListener(new ChooserDialog.Result() {
                    @Override
                    public void onChoosePath(String path, File pathFile) {
                        Log.e("File Name",(fileName=pathFile.getName()));
                        setFileName(pathFile.getName());
                        readFile(path);
                        editText.setVisibility(View.VISIBLE);
                    }
                })
                .build();

        fileChooser = findViewById(R.id.fileChooser);
        fileChooser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooserDialog.show();
            }
        });

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Home.this.adapter.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    void readFile(String path) {
        try {
            FileInputStream inputStream = new FileInputStream(path);
            InputStreamReader isr = new InputStreamReader(inputStream, "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            Pattern pattern = Pattern.compile("^[0-3]?[0-9]/[0-3]?[0-9]/(?:[0-9]{2})?[0-9]{2}$");
            Matcher matcher;
            int count = 0;
            String date;
            while ((line = bufferedReader.readLine()) != null) {
                count++;
                date = line.split(",")[0];
                matcher = pattern.matcher(date);
                if (matcher.matches()){
                    if (count==1)
                        sb.append(line);
                    else
                        sb.append("\n").append(line);
                }

                else {
                    sb.append(line);
                }

            }
            //Log.d("Read Data", sb.toString());
            //textView.setText(sb);
            formatFile(sb);


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void formatFile(StringBuilder stringBuilder) {
        File file = new File(Environment.getExternalStorageDirectory(), "WhtasAppAnalytic");
        Log.d("getFiles",""+getFilesDir());
        if (!file.exists()) {
            file.mkdir();
        }

        File file1 = new File(file,getFileName().replace(".txt",".log"));
        try {
            FileWriter writer = new FileWriter(file1);
            writer.write(stringBuilder.toString());
            writer.flush();
            writer.close();
            Log.d("Success",file1.getPath());
            emailFile(file1.getPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void emailFile(String filePath){
        int totalWords = 0;
        try {
            FileInputStream inputStream = new FileInputStream(filePath);
            InputStreamReader isr = new InputStreamReader(inputStream, "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(isr);
            String line;
            while ((line = bufferedReader.readLine())!=null){
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
                totalWords = totalWords + analyticClass.countWord(message);
                analyticClass.commonWords(message);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        textView.setText(""+totalWords);
        map2 = new TreeMap<>(map);
        for (Map.Entry<String, Integer> entry : map2.entrySet()) {
            //Log.e("Output",entry.getKey() + " \t" + entry.getValue());
            list.add(entry.getKey() + " \t" + entry.getValue());
        }
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(Home.this, permission) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(Home.this, permission)) {

                //This is called if user has denied the permission before
                //In this case I am just asking the permission again
                ActivityCompat.requestPermissions(Home.this, new String[]{permission}, requestCode);

            } else {

                ActivityCompat.requestPermissions(Home.this, new String[]{permission}, requestCode);
            }
        } else {
            //Toast.makeText(this, "" + permission + " is already granted.", Toast.LENGTH_SHORT).show();
        }
    }

}
