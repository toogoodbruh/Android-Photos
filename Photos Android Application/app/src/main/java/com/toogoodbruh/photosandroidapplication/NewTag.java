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

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class NewTag extends AppCompatActivity {
    private RadioButton loc, person;
    private RadioGroup rg;
    private EditText tagData;
    private Button send, cancel;
    private int type = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_tag);
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
                                    write();
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
                                    write();
                                    Log.d("NewTag", "Person tag added: " + tagData.getText().toString());
                                    write();
                                    setResult(RESULT_OK);
                                    finish();
                                }
                                break;
                            default:
                                break;
                        }
                    }
                } else {
                    Log.e("NewTag", "Invalid index value: " + index);
                }
            }
        });


    }

    public void write(){
        try {
            ArrayList<Photo> uris = AlbumView.imgAdapter.getPhotos();

            FileOutputStream fileOutputStream = openFileOutput(HomeScreen.albumName + ".list", MODE_APPEND); // Open the file in append mode

            for (Photo u : uris) {
                // Construct the string containing the URI and associated tags
                String str = u.getUri().toString();
                for (Tag t : u.tags){
                    str = str + "\nTAG:" + t.toString();
                }
                // Append the string to the file
                fileOutputStream.write(str.getBytes());
                fileOutputStream.write("\n".getBytes()); // Add a newline character to separate data for each photo
                Log.d("write()", "Data appended: " + str);
            }

            fileOutputStream.close(); // Close the file output stream
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