package com.toogoodbruh.photosandroidapplication;

import android.content.Intent;
import android.net.Uri;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.view.View.OnClickListener;
import android.widget.Toast;
import android.Manifest;
import androidx.core.content.ContextCompat;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import android.util.Log;


import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import androidx.annotation.NonNull;


public class AlbumView extends AppCompatActivity {
    private static final int READ_REQUEST_CODE = 42;
    public static ImageAdapter imgAdapter;
    public static GridView gridView;
    Button add, copy, paste, display, delete, move;

    public static int index = 0;

    private static Photo photoCopy;

    public static Album album = new Album();
    private static final int REQUEST_PERMISSIONS = 100;

    @Override
    protected void onResume() {
        super.onResume();
        read();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_view);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            // Request the permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS);
        } else {
            // Permission has already been granted
            // Proceed with the operation that requires this permission
        }

        // Initialize Toolbar
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Enable the back button

        gridView = findViewById(R.id.GridView);
        add = findViewById(R.id.add);
        if (HomeScreen.albumName.equals("SearchRes")){
            add.setVisibility(View.INVISIBLE);
        }
        copy = findViewById(R.id.Copy);
        copy.setVisibility(View.INVISIBLE);
        paste = findViewById(R.id.paste);
        paste.setVisibility(HomeScreen.isCopy ? View.VISIBLE : View.INVISIBLE);
        display = findViewById(R.id.display);
        display.setVisibility(View.INVISIBLE);
        delete = findViewById(R.id.delete);
        delete.setVisibility(View.INVISIBLE);
        move = findViewById(R.id.move);
        move.setVisibility(View.INVISIBLE);

        imgAdapter = new ImageAdapter(this);
        final GridView gridview = findViewById(R.id.GridView);

        read();

        gridview.setAdapter(imgAdapter);


        add.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(intent, READ_REQUEST_CODE);

            }

        });

        delete.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View view){
                if (index>=0 && index<imgAdapter.getCount()) {
                    imgAdapter.remove(index);
                    index = -1;

                    createVisibility(false);

                    write();
                    gridView.setAdapter(imgAdapter);
                } else {

                    Toast.makeText(getApplicationContext(),
                            "Failed to delete image "+index, Toast.LENGTH_SHORT).show();
                }

            }
        });

        paste.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view){
                imgAdapter.add(HomeScreen.copy);
                gridView.setAdapter(imgAdapter);
                write();
            }
        });

        display.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view){

                Intent intent = new Intent(getApplicationContext(), SlideShowView.class);
                startActivity(intent);
            }
        });

        copy.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                HomeScreen.isCopy = true;
                paste.setVisibility(View.VISIBLE);
                HomeScreen.copy = ImageAdapter.uris.get(index);
            }
        });

        move.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                HomeScreen.isCopy = true;
                paste.setVisibility(View.VISIBLE);
                HomeScreen.copy = ImageAdapter.uris.get(index);
                imgAdapter.remove(index);
                gridView.setAdapter(imgAdapter);
                write();
            }
        });

        gridView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                index = position;
                if (index!= -1){
                    createVisibility(true);
                }
            }
        });
    }

    /**
     * Handle toolbar item clicks, including the back button.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed(); // Navigate back when the back button is clicked
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * After an image is open, the method is called
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("resultCode", String.valueOf(resultCode));
        Log.d("requestCode" , String.valueOf(requestCode));
        if (requestCode == READ_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            // Ensure imgAdapter is initialized
            if (imgAdapter == null) {
                imgAdapter = new ImageAdapter(this);
            }

            index++;
            Photo picture = new Photo(data.getData());
            Uri imageUri = data.getData();

            // Gain permission to access the URI
            getContentResolver().takePersistableUriPermission(imageUri,
                    (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION));

            imgAdapter.add(imageUri);
            gridView.setAdapter(imgAdapter);
            album.list.add(picture);
            write();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS) {
            // Log the permission grant result
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                Log.d("Permission", "READ_EXTERNAL_STORAGE permission granted");
            } else {
                // Permission denied
                Log.d("Permission", "READ_EXTERNAL_STORAGE permission denied");
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Reads and stores Album data
     */
    public void read() {
        imgAdapter.clear();
        try {
            FileInputStream fileInputStream = openFileInput(HomeScreen.albumName + ".list");
            if (fileInputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String lineIn;
                ArrayList<String> tags = new ArrayList<>();

                while ((lineIn = bufferedReader.readLine()) != null) {
                    if (lineIn.startsWith("TAG:")) {
                        String tag = lineIn.substring(4);
                        if (!tags.contains(tag)) {
                            tags.add(tag);
                        }
                    } else {
                        Uri uri = Uri.parse(lineIn);
                        imgAdapter.add(uri);
                    }
                }

                gridView.setAdapter(imgAdapter);
                Log.d("Read", "Data read successfully");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Saves app data
     */
    public void write(){
        try {
            ArrayList<Photo> uris = imgAdapter.getPhotos();

            String str = "";
            FileOutputStream fileOutputStream = openFileOutput(HomeScreen.albumName+".list", MODE_PRIVATE);
            for (Photo u : uris) {
                ArrayList<Tag> tags = new ArrayList<>();
                for (int i = 0; i < u.tags.size(); i++){
                    boolean b = false;
                    Tag t = u.tags.get(i);

                    for (Tag t1 :tags) {
                        if (t.type.equals(t1.type)&&t.getData().equals(t1.getData())){
                            b=true;
                            u.tags.remove(i);
                            break;
                        }
                    }
                    if (!b) {
                        tags.add(t);
                    }

                }
                if (u != null) {
                    Log.d("Write", "Photo: " + u);
                    if (str.equals("")) {
                        str = u.toString();
                    } else {
                        str = str + "\n" + u;
                    }
                } else {
                    Log.e("Write", "Photo is null");
                }
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
     * Determines which button is visible to the user
     */
    private void createVisibility(boolean vis){
        copy.setVisibility(vis ? View.VISIBLE : View.INVISIBLE);
        move.setVisibility(vis ? View.VISIBLE : View.INVISIBLE);
        display.setVisibility(vis ? View.VISIBLE : View.INVISIBLE);
        delete.setVisibility(vis ? View.VISIBLE : View.INVISIBLE);
    }
}
