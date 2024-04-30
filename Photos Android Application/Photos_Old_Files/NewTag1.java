package com.toogoodbruh.photosandroidapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.util.Log;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class NewTag1 extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 100;
    private RadioButton loc, person;
    private RadioGroup rg;
    private EditText tagData;
    private Button send, cancel;
    private int type = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_tag);

        // Check for permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_CODE);
        } else {
            // Permission is already granted, proceed with your code
            // You can put your existing onCreate code here
            initializeViews();
        }
    }
    // Method to initialize views and set listeners
    private void initializeViews() {
        Log.d("startup", "startup text");
        rg = (RadioGroup) findViewById(R.id.radiogroup);

        loc = (RadioButton) findViewById(R.id.location);
        person = (RadioButton) findViewById(R.id.person);

        tagData = (EditText) findViewById(R.id.data);
        send = (Button) findViewById(R.id.add);
        cancel = (Button) findViewById(R.id.cancel);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("NewTag", "Add tag button clicked"); // Add this log statement
                ArrayList<String> tags = new ArrayList<>();

                int index = SlideShowView.index; // Store the index in a local variable for debugging
                // Log the index value
                Log.d("NewTag", "Index value: " + index);

                // Check if SlideShowView.index is within a valid range
                if (index >= 0 && index < AlbumView.imgAdapter.uris.size()) {
                    for (Tag t: AlbumView.imgAdapter.uris.get(index).tags) {
                        if (!(tags.contains(t.toString()))){
                            tags.add(t.toString());
                        }
                    }

                    type = rg.getCheckedRadioButtonId();
                    String tagDataText = tagData.getText().toString(); // Retrieve the tag data from the EditText field
                    Log.d("NewTag", "Tag data input: " + tagDataText); // Log the tag data to check if it's retrieved correctly

                    if (!tagDataText.equals("")) {
                        switch (type) {
                            case 2131165275:
                                if (tags.contains("Location=" + tagData.getText().toString())) {
                                    Toast.makeText(getApplicationContext(), "This tag already exists", Toast.LENGTH_SHORT).show();
                                } else {
                                    AlbumView.imgAdapter.uris.get(index).addTag("Location=" + tagData.getText().toString());
                                    SlideShowView.tagAdapter.notifyDataSetChanged();
                                    Log.d("NewTag", "Location tag added: " + tagData.getText().toString());
                                    setResult(RESULT_OK);
                                    finish();
                                }
                                break;
                            case 2131165294:
                                if (tags.contains("Person=" + tagData.getText().toString())) {
                                    Toast.makeText(getApplicationContext(), "This tag already exists", Toast.LENGTH_SHORT).show();
                                    Log.d("NewTag", "Person tag already exists: " + tagData.getText().toString());
                                } else {
                                    AlbumView.imgAdapter.uris.get(index).addTag("Person=" + tagData.getText().toString());
                                    SlideShowView.tagAdapter.notifyDataSetChanged();
                                    Log.d("NewTag", "Person tag added: " + tagData.getText().toString());
                                    setResult(RESULT_OK);
                                    finish();
                                }
                                break;
                            default:
                                break;
                        }

                        // Call write() method here, outside the switch statement
                        write();
                    }
                } else {
                    Log.e("NewTag", "Invalid index value: " + index);
                }
            }
        });
    }
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            // Check if the permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with your code
                // You can put your existing onCreate code here
                initializeViews();
            } else {
                // Permission denied, show a message or handle it accordingly
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                // Finish the activity or handle it in another way
                finish();
            }
        }
    }

    public void write() {
        Log.d("NewTag write()", "entered");
        FileOutputStream fileOutputStream = null;
        try {
            Log.d("NewTag write()", "entered try{}");
            ArrayList<Photo> uris = AlbumView.imgAdapter.getPhotos();
            fileOutputStream = openFileOutput(HomeScreen.albumName + ".list", MODE_APPEND);

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
                Log.d("NewTag", "Data appended: " + str);
            }

            fileOutputStream.close(); // Close the file output stream
        } catch (FileNotFoundException e) {
            Log.d("NewTag write()", "FileNotFound");
            e.printStackTrace();
        } catch (IOException e) {
            Log.d("NewTag write()", "IOException");
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    }