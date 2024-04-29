package com.toogoodbruh.photosandroidapplication;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/*public class HomeScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}*/
public class HomeScreen extends AppCompatActivity {
    private static final int NEW_ALBUM_REQUEST_CODE = 1;

    public static GridView gridView;
    Button newButton, delete, rename, open, search;
    public static ArrayList<String> albums;
    public static String albumName;
    private static int index;
    public static Photo copy;
    public static boolean isCopy;
    Album stockAlbum = new Album();

    /**
     * Sets data
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        albums = new ArrayList<String>();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);



        gridView = (GridView) findViewById(R.id.gridView1);
        search = (Button) findViewById(R.id.search);
        newButton = (Button) findViewById(R.id.newButton);
        open = (Button) findViewById(R.id.open);
        delete = (Button) findViewById(R.id.delete);
        rename = (Button) findViewById(R.id.rename);
        open.setVisibility(View.INVISIBLE);
        delete.setVisibility(View.INVISIBLE);
        rename.setVisibility(View.INVISIBLE);

        isCopy = false;

        for (String s:read()) {
            albums.add(s);
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, albums);

        gridView.setAdapter(arrayAdapter);

        gridView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                index = position;
                open.setVisibility(View.VISIBLE);
                delete.setVisibility(View.VISIBLE);
                rename.setVisibility(View.VISIBLE);
            }
        });

        delete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (index>=0 && index<albums.size()) {
                    getApplicationContext().deleteFile(new File(albums.get(index)+".list").getName());
                    albums.remove(index);
                    index = -1;

                    write();
                    albums.clear();
                    for (String s : read()) {
                        albums.add(s);
                    }

                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getApplicationContext(),
                            android.R.layout.simple_list_item_1, albums);

                    gridView.setAdapter(arrayAdapter);

                    open.setVisibility(View.INVISIBLE);
                    delete.setVisibility(View.INVISIBLE);
                    rename.setVisibility(View.INVISIBLE);
                } else {

                    Toast.makeText(getApplicationContext(),
                            "Failed to delete\nIndex = "+index+"\nSize = "+albums.size(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        open.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                albumName=albums.get(index);
                showAlbum();
            }
        });

        search.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                showSearch();
            }
        });

        newButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                showNewAlbum();
                refreshAlbums();
            }
        });


        rename.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                showEditAlbum();
            }
        });

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs.getBoolean("firstTime", true)) {
            try {
                String stockAlbumName = "stock";
                FileOutputStream fileOutputStream = openFileOutput(stockAlbumName + ".list", MODE_PRIVATE);
                HomeScreen.albums.add(stockAlbumName);
                String str = "android.resource://com.toogoodbruh.photosandroidapplication/raw/stock1";
                addPhoto(str, fileOutputStream);
                str = "android.resource://com.toogoodbruh.photosandroidapplication/raw/stock2";
                addPhoto(str, fileOutputStream);
                str = "android.resource://com.toogoodbruh.photosandroidapplication/raw/stock3";
                addPhoto(str, fileOutputStream);
                str = "android.resource://com.toogoodbruh.photosandroidapplication/raw/stock4";
                addPhoto(str, fileOutputStream);
                str = "android.resource://com.toogoodbruh.photosandroidapplication/raw/stock5";
                addPhoto(str, fileOutputStream);
            } catch (FileNotFoundException e){
                e.printStackTrace();
            }
            // mark first time has ran.
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("firstTime", false);
            editor.commit();
        }

        write();
    }
    /*added

     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == NEW_ALBUM_REQUEST_CODE && resultCode == RESULT_OK) {
            // Refresh the list of albums
            refreshAlbums();
        }
    }


    private void refreshAlbums() {
        // Clear the current list of albums
        albums.clear();
        // Read the updated list of albums from storage
        String[] newAlbums = read();
        // Add the new albums to the list
        if (newAlbums != null) {
            for (String album : newAlbums) {
                albums.add(album);
            }
        }
        // Update the GridView adapter
        ((ArrayAdapter<String>) gridView.getAdapter()).notifyDataSetChanged();
    }

    /*
    end added
     */
    /**
     * Starts NewAlbum
     */
    private void showNewAlbum(){
        Intent intent = new Intent(this, NewAlbum.class);
        startActivity(intent);
    }

    /**
     * Starts AlbumView
     */
    private void showAlbum(){
        Intent intent = new Intent(this, AlbumView.class);
        startActivity(intent);
    }

    /**
     * Starts Search
     */
    private void showSearch(){
        Intent intent = new Intent(this, Search.class);
        startActivity(intent);
    }

    /**
     * Starts EditAlbum
     */
    private void showEditAlbum(){
        Intent intent = new Intent(this, EditAlbum.class);
        startActivity(intent);
    }

    /**
     * Reads and stores list of Albums
     */
    public String[] read() {
        String[] strings = {};

        try {
            FileInputStream fileInputStream = openFileInput("albums.albm");

            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            ArrayList<String> list = new ArrayList<String>();
            String lineIn;

            while ((lineIn = bufferedReader.readLine()) != null) {
                list.add(lineIn);
            }

            strings = new String[list.size()];

            for (int i = 0; i < list.size(); i++){
                strings[i] = list.get(i);
            }

            return strings;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return strings;
    }

    /**
     * Writes the list of album names and returns it to "albums.albm"
     */
    public void write(){
        try {
            String str = "";
            if (albums.size() > 0) {
                str = albums.get(0);
            }

            FileOutputStream fileOutputStream = openFileOutput("albums.albm", MODE_PRIVATE);
            for (int i = 1; i < albums.size(); i++) {
                str = str.concat("\n" + albums.get(i));
            }

            fileOutputStream.write(str.getBytes());
        }catch(FileNotFoundException e){
            e.printStackTrace();
        } catch(ArrayIndexOutOfBoundsException e){
            e.printStackTrace();
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Returns index of the most recent album what was selected
     */
    public static int getIndex(){
        return index;
    }

    /**
     * Adds photos to the stock Album
     */
    private void addPhoto(String photoFilePath, FileOutputStream fos){
        File file = new File(photoFilePath);

        /**/

        try {
            fos.write((Uri.parse(photoFilePath).toString()+'\n').getBytes());
        } catch (IOException e){
            Toast.makeText(this, "FAILED", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
}
