package com.protectme;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioFormat;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.protectme.handler.NetworkManager;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class EvidenceSelectActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private static final int CAPTURE_IMAGE = 100;
    private static final int CAPTURE_VIDEO = 200;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    public static final int MEDIA_TYPE_AUDIO = 3;

    Button start, stop, uploadrecord;
    private MediaRecorder myAudioRecorder;
    private static String outputFile = null;
    ProgressBar uploadProgress;
    String selectedImagePath;

    private Uri fileUri;
    ImageView captureImageView;
    private Bitmap bitmap;
    private String encode_string;
    private static String imageName;

    LocationManager locationManager;
    public static Location mLastLocation;
    public static String caseType = "E";
    LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evidence_select);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        captureImageView = (ImageView) findViewById(R.id.imageView1);

        start = (Button) findViewById(R.id.btnStartRecord);
        stop = (Button) findViewById(R.id.btnStopRecord);
        uploadrecord = (Button) findViewById(R.id.btnUploadRecord);
        uploadProgress = (ProgressBar) findViewById(R.id.uploadProgress);
        uploadProgress.setVisibility(View.INVISIBLE);

        stop.setEnabled(false);
        uploadrecord.setEnabled(false);
        outputFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/recording.3gp";

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        buildGoogleApiClient();
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
        if (mGoogleApiClient.isConnected()) {
            Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    public void getLastLocation() {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        Toast.makeText(this, String.valueOf(mLastLocation.getLatitude()).toString(), Toast.LENGTH_SHORT).show();
    }

    public void startcapture(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE); // create a file to save the image
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name

        startActivityForResult(intent, CAPTURE_IMAGE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (requestCode == CAPTURE_IMAGE) {

                if (resultCode == RESULT_OK) {

                    Toast.makeText(this, "Images captued" + data.getData(), Toast.LENGTH_LONG).show();
                    // selectedImagePath = getAbsolutePath(data.getData());

//                    captureImageView.setImageBitmap(decodeFile(selectedImagePath));
                    captureImageView.setImageBitmap((Bitmap) data.getExtras().get("data"));
                    new CaptureAsync().execute();
                    // saveCaptureImage();
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, "User cancelled", Toast.LENGTH_SHORT).show();
                } else {

                }
            }
            if (requestCode == CAPTURE_VIDEO) {
                if (resultCode == RESULT_OK) {
                    // Video captured and saved to fileUri specified in the Intent
                    Toast.makeText(this, "Video saved to:\n" + data.getData(), Toast.LENGTH_LONG).show();
                    new CaptureAsync().execute();
                } else if (resultCode == RESULT_CANCELED) {
                    // User cancelled the video capture
                } else {
                    // Video capture failed, advise user
                }
            }
        } catch (Exception ex) {
            Toast.makeText(this, "Error", Toast.LENGTH_LONG).show();
            Log.d("MainError", ex.toString());
        }
    }


    public void startVideo(View view) {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        fileUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO); // create a file to save the image
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, 10000);
        startActivityForResult(intent, CAPTURE_VIDEO);
    }

    /*Path create start */
    private static Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /**
     * Create a File for saving an image or video
     */
    private static File getOutputMediaFile(int type) {
        File mediaStorageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "ProtectMEAPP");

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_" + timeStamp + ".jpg");
        }
        else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_" + timeStamp + ".mp4");
        }
        else if(type == MEDIA_TYPE_AUDIO){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "AUD_" + timeStamp + ".3gp");
        }
        else {
            return null;
        }
        //set the image name
        imageName = "IMG" + timeStamp + ".jpg";
        return mediaFile;
    }
    /*Path create end*/

    /*Voice record methods -  start*/

    public void startVoiceRecord(View view) {
        try {
            fileUri = getOutputMediaFileUri(MEDIA_TYPE_AUDIO);
            myAudioRecorder = new MediaRecorder();
            myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
            myAudioRecorder.setOutputFile(outputFile);
            myAudioRecorder.prepare();
            myAudioRecorder.start();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        start.setEnabled(false);
        stop.setEnabled(true);

        Toast.makeText(getApplicationContext(), "Recording started", Toast.LENGTH_LONG).show();
    }

    public void stopVoiceRecord(View view) {
        myAudioRecorder.stop();
        myAudioRecorder.release();
        myAudioRecorder = null;

        stop.setEnabled(false);
        uploadrecord.setEnabled(true);

        Toast.makeText(getApplicationContext(), "Audio recorded successfully", Toast.LENGTH_LONG).show();
    }

    public void uploadVoiceRecord(View view) {
        MediaPlayer m = new MediaPlayer();

        try {
            m.setDataSource(outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            m.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        m.start();
        AudioAsync audioAsync = new AudioAsync();
        audioAsync.execute();

    }


    @Override
    public void onConnected(Bundle bundle) {
        getLastLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    /*Voice record methods -  end*/

    private class CaptureAsync extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            uploadProgress.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params) {
            bitmap = BitmapFactory.decodeFile(fileUri.getPath());
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);

            byte[] array = outputStream.toByteArray();
            encode_string = Base64.encodeToString(array, 0);

            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            try {
                StringRequest request = new StringRequest(Request.Method.POST, NetworkManager.url_saveEvidenceLocation, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {


                        Map<String, String> parameters = new HashMap<String, String>();
                        parameters.put("encodeString", encode_string);
                        parameters.put("imageName", imageName);
                        parameters.put("status", "P");
                        parameters.put("userid", "3");
                        parameters.put("type", caseType);
                        parameters.put("latitude", String.valueOf(mLastLocation.getLatitude()));
                        parameters.put("longitude", String.valueOf(mLastLocation.getLongitude()));

                        return parameters;
                    }
                };
                queue.add(request);
            } catch (Exception ex) {
                // Toast.makeText(MainActivity.this,"Errr",Toast.LENGTH_SHORT).show();
            }
            //Toast.makeText(MainActivity.this,"Finish",Toast.LENGTH_SHORT).show();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            uploadProgress.setVisibility(View.INVISIBLE);
        }
    }

    public class AudioAsync extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            uploadProgress.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params) {

            try {
                FileInputStream fis = new FileInputStream(outputFile);
                //System.out.println(file.exists() + "!!");
                //InputStream in = resource.openStream();
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                byte[] buf = new byte[1024];
                try {
                    for (int readNum; (readNum = fis.read(buf)) != -1;) {
                        bos.write(buf, 0, readNum); //no doubt here is 0
                        //Writes len bytes from the specified byte array starting at offset off to this byte array output stream.
                    }
                } catch (IOException ex) {

                }
                byte[] bytes = bos.toByteArray();
                encode_string = Base64.encodeToString(bytes, 0);

                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

                StringRequest request = new StringRequest(Request.Method.POST, NetworkManager.url_saveEvidenceLocation, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {


                        Map<String, String> parameters = new HashMap<String, String>();
                        parameters.put("encodeString", encode_string);
                        parameters.put("imageName", "track.3gp");
                        parameters.put("status", "P");
                        parameters.put("userid", "3");
                        parameters.put("type", caseType);
                        parameters.put("latitude", String.valueOf(mLastLocation.getLatitude()));
                        parameters.put("longitude", String.valueOf(mLastLocation.getLongitude()));

                        return parameters;
                    }
                };
                queue.add(request);

            }
            catch (Exception ex){

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            uploadProgress.setVisibility(View.INVISIBLE);
        }
    }
}
