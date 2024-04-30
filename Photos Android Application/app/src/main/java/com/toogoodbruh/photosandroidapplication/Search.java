package com.toogoodbruh.photosandroidapplication;

import android.content.Intent;
import android.net.Uri;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class Search extends AppCompatActivity {
    private RadioButton loc, person;
    private RadioGroup rg;
    private EditText tagData;
    private Button search, cancel;
    private int type = -1;
    private static final ArrayList<Photo> searched = new ArrayList<>();
    private static final ArrayList<Photo> sList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);


        rg = findViewById(R.id.radiogroup);

        loc = findViewById(R.id.location);
        person = findViewById(R.id.person);

        tagData = findViewById(R.id.data);
        search = findViewById(R.id.search);
        cancel = findViewById(R.id.cancel);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> tags = new ArrayList<>();
                searched.clear();
                sList.clear();
                int i = 0;
                read();

                type = rg.getCheckedRadioButtonId();
                if (!tagData.getText().toString().equals("")) {

                    switch (type) {
                        case 2131165275:
                            for (Photo p : searched) {
                                for (Tag t : p.tags){
                                    if (t.getData().contains(tagData.getText().toString()) &&
                                            t.type.equals("Location")){
                                        sList.add(p);
                                        i++;
                                        break;
                                    }
                                }
                            }
                            write();
                            finish();
                            break;
                        case 2131165294:
                            for (Photo p : searched) {
                                for (Tag t : p.tags){
                                    if (t.getData().contains(tagData.getText().toString()) &&
                                            t.type.equals("Person")){
                                        sList.add(p);
                                        i++;
                                        break;
                                    }
                                }
                            }
                            write();
                            finish();
                            break;
                        default:
                            break;

                    }

                    Intent intent = new Intent(getApplicationContext(), AlbumView.class);
                    HomeScreen.albumName="SearchRes";
                    startActivity(intent);

                }
            }
        });

        read();
    }

    public void read() {
        ArrayList<String> masterList = new ArrayList<>();

        try {
            FileInputStream fileInputStream = openFileInput("albums.albm");

            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            ArrayList<String> list = new ArrayList<String>();
            String lineIn;

            while ((lineIn = bufferedReader.readLine()) != null) {
                list.add(lineIn);
            }

            for (String s : list) {
                try {
                    FileInputStream fileInputStream2 = openFileInput(s + ".list");

                    InputStreamReader inputStreamReader2 = new InputStreamReader(fileInputStream2);
                    BufferedReader bufferedReader2 = new BufferedReader(inputStreamReader2);

                    Photo photo = null; // Initialize a Photo object

                    while ((lineIn = bufferedReader2.readLine()) != null) {
                        if (lineIn.startsWith("TAG:")) {
                            // Ensure a photo object is created before adding tags
                            if (photo != null) {
                                photo.addTag(lineIn.substring(4));
                            }
                        } else {
                            // Create a new Photo object and add it to the searched list
                            Uri uri = Uri.parse(lineIn);
                            photo = new Photo(uri);
                            searched.add(photo);
                        }
                    }
                }
                catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void write(){
        try {

            String str = "";
            new File(getFilesDir() + File.separator + "SearchRes.list").delete();
            FileOutputStream fileOutputStream = openFileOutput("SearchRes.list", MODE_PRIVATE);
            for (int i = 0; i < sList.size();i++) {
                ArrayList<String> tgs = new ArrayList<>();
                Photo u = sList.get(i);
                if (str.equals("")) {
                    str = u.getUri().toString();
                }
                else {
                    str = str + "\n" + u.getUri().toString();
                }
                for (Tag t : u.tags){
                    if (!tgs.contains(t.toString())) {
                        str = str + "\nTAG:" + t;
                        tgs.add(t.toString());
                    }
                }
            }
            fileOutputStream.write(str.getBytes());

        }
        catch(FileNotFoundException e){
            e.printStackTrace();
        }
        catch(ArrayIndexOutOfBoundsException e){
            e.printStackTrace();
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
}

//need to check this