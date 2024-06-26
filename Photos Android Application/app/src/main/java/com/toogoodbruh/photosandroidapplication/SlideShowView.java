package com.toogoodbruh.photosandroidapplication;



import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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




import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;


public class SlideShowView extends AppCompatActivity {
    private TextView tagTextView;
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

        if (AlbumView.imgAdapter.uris.get(index).tags.size() == 0) {
            delete.setVisibility(View.INVISIBLE);
        } else {

            delete.setVisibility(View.VISIBLE);
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slide_show_view);

        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Enable the back button
        index = AlbumView.index;

        imgView = (ImageView) findViewById(R.id.imageView);
        tagTextView = findViewById(R.id.tagTextView);
        gridView = (GridView) findViewById(R.id.gridView);

        updateTags();
        tagAdapter = new ArrayAdapter<Tag>(this, android.R.layout.simple_list_item_1, AlbumView.imgAdapter.uris.get(index).tags);

        gridView.setAdapter(tagAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                tagIndex = i;
            }
        });

        prev = (Button) findViewById(R.id.prev);
        next = (Button) findViewById(R.id.next);
        add = (Button) findViewById(R.id.Add_Tag);
        delete = (Button) findViewById(R.id.Del_Tag);

        if (AlbumView.imgAdapter.uris.get(index).tags.size() == 0) {
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
            InputStream pictureInputStream = getContentResolver().openInputStream(AlbumView.imgAdapter.uris.get(index).getUri());
            Bitmap currPic = BitmapFactory.decodeStream(pictureInputStream);
            imgView.setImageBitmap(currPic);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        /*add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SlideShowView.this, NewTag.class);
                startActivityForResult(intent, 1); // Use startActivityForResult()
            }
        });

         */
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (index > 0) {
                    index--;
                    if (AlbumView.imgAdapter.uris.get(index).tags.size() == 0) {
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
                        InputStream pictureInputStream = getContentResolver().openInputStream(AlbumView.imgAdapter.uris.get(index).getUri());
                        Bitmap currPic = BitmapFactory.decodeStream(pictureInputStream);
                        imgView.setImageBitmap(currPic);

                        tagAdapter = new ArrayAdapter<Tag>(getApplicationContext(), android.R.layout.simple_list_item_1, AlbumView.imgAdapter.uris.get(index).tags);

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
                    if (AlbumView.imgAdapter.uris.get(index).tags.size() == 0) {
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
                        InputStream pictureInputStream = getContentResolver().openInputStream(AlbumView.imgAdapter.uris.get(index).getUri());
                        Bitmap currPic = BitmapFactory.decodeStream(pictureInputStream);
                        imgView.setImageBitmap(currPic);

                        tagAdapter = new ArrayAdapter<Tag>(getApplicationContext(), android.R.layout.simple_list_item_1, AlbumView.imgAdapter.uris.get(index).tags);

                        gridView.setAdapter(tagAdapter);
                    } catch (FileNotFoundException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), NewTag.class);
                startActivity(intent);
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tagIndex != -1) {
                    AlbumView.imgAdapter.uris.get(index).tags.remove(tagIndex);
                    tagAdapter = new ArrayAdapter<Tag>(getApplicationContext(), android.R.layout.simple_list_item_1, AlbumView.imgAdapter.uris.get(index).tags);
                    gridView.setAdapter(tagAdapter);

                    write(); // Call the write method after a tag is deleted
                }

                if (AlbumView.imgAdapter.uris.get(index).tags.size() == 0) {
                    delete.setVisibility(View.INVISIBLE);
                }
            }
        });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                // Retrieve the tag data from the intent
                String tagData = data.getStringExtra("tagData");
                if (tagData != null) {
                    // Add the tag to the current photo
                    AlbumView.imgAdapter.uris.get(index).addTag(tagData);
                    // Update the UI to reflect the changes
                    updateTags();

                    // Call the write method to save the data
                    write();
                }
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
        Log.d("SlideShowView", "updateTags() called");
        // Check if there are tags associated with the current image
        if (AlbumView.imgAdapter.uris.get(index).tags.size() > 0) {
            StringBuilder tagsBuilder = new StringBuilder("Tags:\n");
            for (Tag tag : AlbumView.imgAdapter.uris.get(index).tags) {
                tagsBuilder.append(tag.getData()).append("\n");
            }
            // Log the tags for the current image
            Log.d("SlideShowView", "Tags for current image: " + tagsBuilder.toString());
            // Set the text of the tag TextView
            tagTextView.setText(tagsBuilder.toString());
            tagTextView.setVisibility(View.VISIBLE);
        } else {
            // If there are no tags, hide the tag TextView
            tagTextView.setVisibility(View.GONE);
        }

        // Call the write method to save the data
        write();
    }
    public void write() {
        Log.d("SlideShowView write()", "method entered");
        try {
            ArrayList<Photo> photos = AlbumView.imgAdapter.getPhotos();

            FileOutputStream fileOutputStream = openFileOutput(HomeScreen.albumName + ".list", MODE_PRIVATE | MODE_APPEND); // Open the file in write mode, create if not exist

            for (Photo photo : photos) {
                String str = photo.getUri().toString(); // Start with the photo URI

                // Append each tag associated with the photo
                for (Tag tag : photo.getTags()) {
                    str += "\nTAG:" + tag.getData();
                }

                // Append a newline character to separate data for each photo
                str += "\n";

                // Write the constructed string to the file
                fileOutputStream.write(str.getBytes());

                Log.d("write()", "Data appended: " + str);
            }

            fileOutputStream.close(); // Close the file output stream
        } catch (IOException e) {
            Log.d("write()", "Failed to append data");
            e.printStackTrace();
        }
    }


    /*public void write(){
        try {
            ArrayList<Photo> uris = AlbumView.imgAdapter.getPhotos();
            ArrayList<Tag> tags = new ArrayList<>();

            String str = "";
            FileOutputStream fileOutputStream = openFileOutput(HomeScreen.albumName+".list", MODE_PRIVATE);
            for (Photo u : uris) {
                if (str.equals("")) {
                    str = u.toString();

                } else {
                    str = str + "\n" + u.toString();
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

     */

}