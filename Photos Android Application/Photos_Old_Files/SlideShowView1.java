package com.toogoodbruh.photosandroidapplication;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.annotation.NonNull;
import android.Manifest;


import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;


public class SlideShowView extends AppCompatActivity {
    private TextView tagTextView;
    public static final int RESULT_CODE = 100;

    private static final int PERMISSION_REQUEST_CODE = 100;
    public static int index;
    private int tagIndex = -1;
    public static Button add;
    private Button prev, next, delete;
    public ImageView imgView;
    public static GridView gridView;
    public static ArrayAdapter tagAdapter;


    @Override
    protected void onResume() {
        super.onResume();

        // Initialize the delete button
        delete = findViewById(R.id.Del_Tag);

        if (ImageAdapter.uris != null && index < ImageAdapter.uris.size() && ImageAdapter.uris.get(index).tags.size() == 0) {
            //delete.setVisibility(View.INVISIBLE);
            delete.setVisibility(View.INVISIBLE);
        } else {
            delete.setVisibility(View.VISIBLE);
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slide_show_view);

        // Check for permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Request the READ_EXTERNAL_STORAGE permission on devices running Android 6.0 (API level 23) and above
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted, request it
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST_CODE);
            } else {
                // Permission is already granted, proceed with your code
                initializeViews();
            }
        } else {
            // For versions below Android 6.0, no specific permission is needed for reading from external storage
            initializeViews();
        }
    }
    // Method to initialize views and set listeners
    private void initializeViews() {
        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Enable the back button
        index = AlbumView.index;

        imgView = findViewById(R.id.imageView);
        tagTextView = findViewById(R.id.tagTextView);
        gridView = findViewById(R.id.gridView);

        updateTags();
        tagAdapter = new ArrayAdapter<Tag>(this, android.R.layout.simple_list_item_1, ImageAdapter.uris.get(index).tags);

        gridView.setAdapter(tagAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                tagIndex = i;
            }
        });

        prev = findViewById(R.id.prev);
        next = findViewById(R.id.next);
        add = findViewById(R.id.Add_Tag);
        delete = findViewById(R.id.Del_Tag);

        if (ImageAdapter.uris.get(index).tags.size() == 0) {
            delete.setVisibility(View.INVISIBLE);
        }
        else {
            delete.setVisibility(View.VISIBLE);
        }

        if (AlbumView.imgAdapter.getCount() == 1) {

            prev.setVisibility(View.INVISIBLE);
            next.setVisibility(View.INVISIBLE);
        }
        else if (index == 0) {
            prev.setVisibility(View.INVISIBLE);
        }
        else if (index == AlbumView.imgAdapter.getCount() - 1) {
            next.setVisibility(View.INVISIBLE);
        }

        try {
            InputStream pictureInputStream = getContentResolver().openInputStream(ImageAdapter.uris.get(index).getUri());
            Bitmap currPic = BitmapFactory.decodeStream(pictureInputStream);
            imgView.setImageBitmap(currPic);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SlideShowView.this, NewTag.class);
                startActivityForResult(intent, 1); // Use startActivityForResult()
            }
        });
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (index > 0) {
                    index--;
                    if (ImageAdapter.uris.get(index).tags.size() == 0) {
                        delete.setVisibility(View.INVISIBLE);
                    } else {
                        delete.setVisibility(View.VISIBLE);
                    }

                    if (next.getVisibility() == View.INVISIBLE) {
                        next.setVisibility(View.VISIBLE);
                    }
                    if (index == 0) {
                        prev.setVisibility(View.INVISIBLE);
                    }
                    try {
                        InputStream pictureInputStream = getContentResolver().openInputStream(ImageAdapter.uris.get(index).getUri());
                        Bitmap currPic = BitmapFactory.decodeStream(pictureInputStream);
                        imgView.setImageBitmap(currPic);

                        tagAdapter = new ArrayAdapter<Tag>(getApplicationContext(), android.R.layout.simple_list_item_1, ImageAdapter.uris.get(index).tags);

                        gridView.setAdapter(tagAdapter);
                    } catch (FileNotFoundException e2) {
                        e2.printStackTrace();
                    }
                }
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (index < AlbumView.imgAdapter.getCount() - 1) {
                    index++;
                    if (ImageAdapter.uris.get(index).tags.size() == 0) {
                        delete.setVisibility(View.INVISIBLE);
                    } else {
                        delete.setVisibility(View.VISIBLE);
                    }

                    if (prev.getVisibility() == View.INVISIBLE) {
                        prev.setVisibility(View.VISIBLE);
                    }
                    if (index == AlbumView.imgAdapter.getCount() - 1) {
                        next.setVisibility(View.INVISIBLE);
                    }
                    try {
                        InputStream pictureInputStream = getContentResolver().openInputStream(ImageAdapter.uris.get(index).getUri());
                        Bitmap currPic = BitmapFactory.decodeStream(pictureInputStream);
                        imgView.setImageBitmap(currPic);

                        tagAdapter = new ArrayAdapter<Tag>(getApplicationContext(), android.R.layout.simple_list_item_1, ImageAdapter.uris.get(index).tags);

                        gridView.setAdapter(tagAdapter);
                    } catch (FileNotFoundException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });


        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tagIndex != -1) {
                    ImageAdapter.uris.get(index).tags.remove(tagIndex);
                    tagAdapter = new ArrayAdapter<Tag>(getApplicationContext(), android.R.layout.simple_list_item_1, ImageAdapter.uris.get(index).tags);

                    gridView.setAdapter(tagAdapter);

                    write();
                }

                if (ImageAdapter.uris.get(index).tags.size() == 0) {
                    delete.setVisibility(View.INVISIBLE);
                }
            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            // Check if the permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with your code
                initializeViews();
            } else {
                // Permission denied, show a message or handle it accordingly
                Toast.makeText(this, "Permission denied, cannot read data", Toast.LENGTH_SHORT).show();
                // Finish the activity or handle it in another way
                //finish();
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                // Handle the result from NewTag activity if needed
                // For example, you can update the UI or perform any other actions
                write();
            }
        }
    }

    // Override onOptionsItemSelected to handle back button click event
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Navigate up when the back button is clicked
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Method to update tags TextView
    /*private void updateTags() {
        // Check if there are tags associated with the current image
        if (AlbumView.imgAdapter.uris.get(index).tags.size() > 0) {
            StringBuilder tagsBuilder = new StringBuilder("Tags:\n");
            for (Tag tag : AlbumView.imgAdapter.uris.get(index).tags) {
                tagsBuilder.append(tag.getData()).append("\n");
            }
            // Set the text of the tag TextView
            tagTextView.setText(tagsBuilder.toString());
            tagTextView.setVisibility(View.VISIBLE);
        } else {
            // If there are no tags, hide the tag TextView
            tagTextView.setVisibility(View.GONE);
        }
    }*/
    private void updateTags() {
        // Check if there are tags associated with the current image
        if (ImageAdapter.uris.get(index).tags.size() > 0) {
            StringBuilder tagsBuilder = new StringBuilder("Tags:\n");
            for (Tag tag : ImageAdapter.uris.get(index).tags) {
                tagsBuilder.append(tag.getData()).append("\n");
            }
            // Set the text of the tag TextView
            tagTextView.setText(tagsBuilder.toString());
            tagTextView.setVisibility(View.VISIBLE);
        } else {
            // If there are no tags, hide the tag TextView
            tagTextView.setVisibility(View.GONE);
        }
    }

    public void write() {
        Log.d("SlideShowView write()", "entered method");
        try {
            Log.d("SlideShowView write()", "entered try{}");
            ArrayList<Photo> uris = AlbumView.imgAdapter.getPhotos();
            FileOutputStream fileOutputStream = openFileOutput(HomeScreen.albumName + ".list", MODE_PRIVATE);

            for (Photo u : uris) {
                // Construct the string containing the URI and associated tags
                StringBuilder strBuilder = new StringBuilder(u.getUri().toString());
                for (Tag t : u.tags) {
                    strBuilder.append("\nTAG:").append(t.toString());
                }
                String str = strBuilder.toString();

                // Append the string to the file
                fileOutputStream.write(str.getBytes());
                fileOutputStream.write("\n".getBytes()); // Add a newline character to separate data for each photo
                Log.d("SlideShowView", "Data written: " + str);
            }

            fileOutputStream.close(); // Close the file output stream
        } catch (FileNotFoundException e) {
            Log.d("SlideShowView write()", "FileNotFound");
            e.printStackTrace();
        } catch (IOException e) {
            Log.d("SlideShowView write()", "IOException");
            e.printStackTrace();
        }
    }

}