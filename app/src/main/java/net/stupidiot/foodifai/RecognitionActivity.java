package net.stupidiot.foodifai;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.clarifai.api.ClarifaiClient;
import com.clarifai.api.RecognitionRequest;
import com.clarifai.api.RecognitionResult;
import com.clarifai.api.Tag;
import com.clarifai.api.exception.ClarifaiException;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class RecognitionActivity extends AppCompatActivity
{
    private static final String TAG = RecognitionActivity.class.getSimpleName();

    private static String APP_ID = "yzSmx6Vood4Sej97eqTsCHY5rkU-pK4qS7eQVDWR";
    private static String APP_SECRET = "uR-JBcoi8b5ti-oKeRad4AwgDwm0eQklZWOBOIsK";

    private ClarifaiClient client = new ClarifaiClient(APP_ID, APP_SECRET);

    private ImageButton uploadFromGalleryButton;
    private ImageButton uploadFromCameraButton;
    private ImageView imageView;
    private TextView textView;
    private TextView finalscore;
    private int total;
    private static int CODE_PICK = 1;
    private static int CAMERA_PICK = 1888;
    ParseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recognition);
        imageView = (ImageView) findViewById(R.id.image_view);
        textView = (TextView) findViewById(R.id.text_view);
        uploadFromGalleryButton = (ImageButton) findViewById(R.id.upload_gallery_btn);
    finalscore = (TextView) findViewById(R.id.myScore);
    finalscore.setText("");
        uploadFromGalleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Send an intent to launch the media picker.
                final Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, CODE_PICK);
            }
        });

        uploadFromCameraButton = (ImageButton) findViewById(R.id.upload_camera_btn);
        uploadFromCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, CAMERA_PICK);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == RESULT_OK)
        {
            // The user picked an image. Send it to Clarifai for recognition.
            Log.d(TAG, "User picked image: " + intent.getData());

            Bitmap bitmap = null;
            if(requestCode == CODE_PICK)
            {
                bitmap = loadBitmapFromUri(intent.getData());
            }
            else if(requestCode == CAMERA_PICK)
            {
                bitmap = (Bitmap) intent.getExtras().get("data");
            }

            if (bitmap != null) {
                Log.d(TAG, "Bitmap is not null");
                imageView.setImageBitmap(bitmap);
                textView.setText("Recognizing...");
                uploadFromGalleryButton.setEnabled(false);

                // Run recognition on a background thread since it makes a network call.
                new AsyncTask<Bitmap, Void, RecognitionResult>() {
                    @Override protected RecognitionResult doInBackground(Bitmap... bitmaps) {

                        return recognizeBitmap(bitmaps[0]);
                    }
                    @Override protected void onPostExecute(RecognitionResult result) {
                        updateUIForResult(result);
                    }
                }.execute(bitmap);
            } else {
                Log.d(TAG, "Bitmap is null");
                textView.setText("Unable to load selected image.");
            }
        }

    }

    /** Loads a Bitmap from a content URI returned by the media picker. */
    private Bitmap loadBitmapFromUri(Uri uri) {
        try {
            // The image may be large. Load an image that is sized for display. This follows best
            // practices from http://developer.android.com/training/displaying-bitmaps/load-bitmap.html
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(getContentResolver().openInputStream(uri), null, opts);
            int sampleSize = 1;
            while (opts.outWidth / (2 * sampleSize) >= imageView.getWidth() &&
                    opts.outHeight / (2 * sampleSize) >= imageView.getHeight()) {
                sampleSize *= 2;
            }

            opts = new BitmapFactory.Options();
            opts.inSampleSize = sampleSize;
            return BitmapFactory.decodeStream(getContentResolver().openInputStream(uri), null, opts);
        } catch (IOException e) {
            Log.e(TAG, "Error loading image: " + uri, e);
        }
        return null;
    }

    /** Sends the given bitmap to Clarifai for recognition and returns the result. */
    private RecognitionResult recognizeBitmap(Bitmap bitmap) {
        try {
            // Scale down the image. This step is optional. However, sending large images over the
            // network is slow and  does not significantly improve recognition performance.
            Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 320,
                    320 * bitmap.getHeight() / bitmap.getWidth(), true);

            // Compress the image as a JPEG.
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            scaled.compress(Bitmap.CompressFormat.JPEG, 90, out);
            byte[] jpeg = out.toByteArray();

            // Send the JPEG to Clarifai and return the result.
            return client.recognize(new RecognitionRequest(jpeg)).get(0);
        } catch (ClarifaiException e) {
            Log.e(TAG, "Clarifai error", e);
            return null;
        }
    }

    /** Updates the UI by displaying tags for the given result. */
    private void updateUIForResult(RecognitionResult result)
    {
        if (result != null)
        {
            if (result.getStatusCode() == RecognitionResult.StatusCode.OK)
            {
                // Display the list of tags in the UI.
        Map<String, Integer> map = LoginActivity.map;
        int sum=0,flag=0;

        currentUser = ParseUser.getCurrentUser();

                boolean containsHealthy = false, containsUnhealthy =false;
                StringBuilder b = new StringBuilder();
                for (Tag tag : result.getTags()) {
                    b.append(b.length() > 0 ? ", " : "").append(tag.getName());
                    if(map.containsKey(tag.getName().toLowerCase()))
                    {
                        flag=1;
                        if("healthy".equalsIgnoreCase(tag.getName().toLowerCase()))
                            containsHealthy = true;
                        if("unhealthy".equalsIgnoreCase(tag.getName().toLowerCase()))
                            containsUnhealthy = true;
                        sum += (map.get(tag.getName()));
                    }

                    if(containsHealthy && containsUnhealthy)
                    {
                        sum-=30;
                    }
                }
                if(flag==0)
                {
                    textView.setText(" Oops!! This doesn't seem to be food at all");
                    finalscore.setText("Current Score N/A" + "\nTotal Score " + currentUser.getNumber("Points").intValue());
                }
                if(sum>0)
                {
                    textView.setText(" Ohh yeahhh !! This food seems to be healthy .");
                    total = sum + currentUser.getNumber("Points").intValue();
                    finalscore.setText("Current Score " + sum + "\nTotal Score " + total);
                }
                else
                {
                    textView.setText("Yikes !!This food seems to be unhealthy . Does your mom know you're having this ??");
                    total = sum + currentUser.getNumber("Points").intValue();
                    finalscore.setText("Current Score " + sum + "\nTotal Score " + total);
                }

                ParseQuery<ParseUser>  query = ParseUser.getQuery();
                query.getInBackground(currentUser.getObjectId(), new GetCallback<ParseUser>() {
                    @Override
                    public void done(ParseUser parseUser, ParseException e) {
                        if(e == null){
                            parseUser.put("Points", total);
                            parseUser.saveInBackground();
                        }else{
                            Toast.makeText(getApplicationContext(),
                                    "Error",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });

                ParseQuery<ParseObject> query1 = ParseQuery.getQuery("Groups");
                query1.whereEqualTo("userName", currentUser.getUsername());
                query1.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> list, ParseException e) {
                        for (ParseObject parseObject : list){
                            parseObject.put("Points", total);
                            parseObject.saveInBackground();
                        }

                    }
                });

            } else {
                Log.e(TAG, "Clarifai: " + result.getStatusMessage());
                textView.setText("Sorry, there was an error recognizing your image.");
            }
        } else {
            textView.setText("Sorry, there was an error recognizing your image.");
        }
        uploadFromGalleryButton.setEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_recognition, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}