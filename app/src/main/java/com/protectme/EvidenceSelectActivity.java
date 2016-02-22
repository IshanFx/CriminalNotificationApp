package com.protectme;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
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
import com.protectme.database.RealMAdapter;
import com.protectme.handler.NetworkManager;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class EvidenceSelectActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private static final int CAPTURE_IMAGE = 100;
    private static final int CAPTURE_VIDEO = 200;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    public static final int MEDIA_TYPE_AUDIO = 3;

    public static boolean isRecordStart=false;
    public static String audioName = "";
    public static String videoName = "";
    ImageButton start, uploadrecord;
    private MediaRecorder myAudioRecorder;
    private static String outputFile = null;
    ProgressBar uploadProgress;
    String selectedImagePath;
    MediaPlayer mediaPlayer;

    private static Uri fileUri;
    private static Uri imageUri;
    private static Uri videoUri;
    private static Uri voiceUri;

    ImageView captureImageView;
    private Bitmap bitmap;
    private String encode_string;
    private static String imageName;
    private static Integer userID=0;
    private static int lastCaseId = -1;
    LocationManager locationManager;
    public static Location mLastLocation;
    public static String caseType = "E";
    LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    RealMAdapter realMAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evidence_select);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        captureImageView = (ImageView) findViewById(R.id.imageView1);

        start = (ImageButton) findViewById(R.id.btnStartRecord);

        uploadrecord = (ImageButton) findViewById(R.id.btnUploadRecord);
        uploadProgress = (ProgressBar) findViewById(R.id.uploadProgress);
        uploadProgress.setVisibility(View.INVISIBLE);
        realMAdapter = new RealMAdapter(getApplicationContext());
        userID = realMAdapter.getUserId();


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
           // Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
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
       Log.d("Evidence","Latitude"+String.valueOf(mLastLocation.getLatitude())+"Longitude"+String.valueOf(mLastLocation.getLongitude()));
    }

    public void startcapture(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE); // create a file to save the image
        imageUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri); // set the image file name
        startActivityForResult(intent, CAPTURE_IMAGE);
    }
    /*
    * Video Recording Listner
    */
    public void startVideo(View view) {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        fileUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO); // create a file to save the image
        videoUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, videoUri);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, 10000);
        startActivityForResult(intent, CAPTURE_VIDEO);
    }

    public void startVoiceRecord(View view) {
        try {
            if(!isRecordStart) {
                fileUri = getOutputMediaFileUri(MEDIA_TYPE_AUDIO);
                voiceUri = getOutputMediaFileUri(MEDIA_TYPE_AUDIO);
                myAudioRecorder = new MediaRecorder();
                myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
                myAudioRecorder.setOutputFile(voiceUri.getPath());
                myAudioRecorder.prepare();
                myAudioRecorder.start();
                isRecordStart = true;
            }
            else{
                stopVoiceRecord();
            }
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }



        Toast.makeText(getApplicationContext(), "Recording started", Toast.LENGTH_LONG).show();
    }

    public void stopVoiceRecord() {
        myAudioRecorder.stop();
        myAudioRecorder.release();
        myAudioRecorder = null;
        isRecordStart = false;
        Toast.makeText(getApplicationContext(), "Audio recorded successfully", Toast.LENGTH_LONG).show();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (requestCode == CAPTURE_IMAGE) {

                if (resultCode == RESULT_OK) {

                    Toast.makeText(this, "Images capture" + data.getData(), Toast.LENGTH_LONG).show();
                    // selectedImagePath = getAbsolutePath(data.getData());

//                    captureImageView.setImageBitmap(decodeFile(selectedImagePath));
                    captureImageView.setImageBitmap((Bitmap) data.getExtras().get("data"));

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

                    //new VideoAsync().execute();
                } else if (resultCode == RESULT_CANCELED) {
                    // User cancelled the video capture
                } else {
                    // Video capture failed, advise user
                }
            }
        } catch (Exception ex) {
            Toast.makeText(this, "Error", Toast.LENGTH_LONG).show();
            Log.d("MainError", ex.getMessage().toString());
        }
    }

    public void uploadMultipart(final Context context) {
        try {
            String uploadId =
                    new MultipartUploadRequest(context,NetworkManager.url_upload )
                            .addFileToUpload(videoUri.getPath(),"image")
                            .setNotificationConfig(new UploadNotificationConfig())
                            .setMaxRetries(2)
                            .startUpload();
        } catch (Exception exc) {
            Log.e("AndroidUploadService", exc.getMessage());
        }
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
                    "MOV_0015.mp4");
           // videoName = "VID_"+timeStamp+".mp4";
            videoName = "MOV_0015.mp4";
        }
        else if (type == MEDIA_TYPE_AUDIO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "AUD_" + timeStamp + ".mp3");
            audioName = "AUD_" + timeStamp + ".mp3";
        } else {
            return null;
        }
        //set the image name
        imageName = "IMG" + timeStamp + ".jpg";
        return mediaFile;
    }

    /*Path create end*/

    /*Voice record methods -  start*/

    public void uploadEvidence(View view) {
       /* mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(voiceUri.getPath());
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mediaPlayer.start();*/
        if(lastCaseId==-1) {
            sendLocationData();
        }
        Log.d("evicence","Save Extra");
        try {
            if(imageUri!=null){
                new CaptureAsync().execute();
              //  imageUri = null;
            }
            if (voiceUri!=null) {
               new AudioAsync().execute();
               // voiceUri = null;
            }
            if (videoUri!=null) {
                uploadMultipart(getApplicationContext());
            }
        } catch (Exception e) {
            Log.d("evicence",e.getMessage().toString());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        lastCaseId = -1;
        imageUri = null;
        voiceUri = null;
        videoUri = null;
    }

    public  void sendLocationData(){
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        StringRequest request = new StringRequest(Request.Method.POST, NetworkManager.url_saveCaseLocation, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject resposeJSON = new JSONObject(response);
                    if(resposeJSON.names().get(0).equals("caseid")){
                        lastCaseId = resposeJSON.getInt("caseid");
                        Toast.makeText(getApplicationContext(),"Last case id:"+lastCaseId,Toast.LENGTH_SHORT).show();
                    }
                }
                catch(Exception ex){

                }
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
                parameters.put("status", "P");
                parameters.put("userid",userID.toString() );
                parameters.put("type", caseType);
                parameters.put("latitude", String.valueOf(mLastLocation.getLatitude()));
                parameters.put("longitude", String.valueOf(mLastLocation.getLongitude()));
                parameters.put("caseid",String.valueOf(lastCaseId));
                return parameters;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);
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

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putString("imageuri", imageUri.toString());
        outState.putString("videouri", videoUri.toString());
        outState.putString("voiceuri", voiceUri.toString());
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        imageUri = Uri.parse(savedInstanceState.getString("imageuri"));
        videoUri = Uri.parse(savedInstanceState.getString("videouri"));
        voiceUri = Uri.parse(savedInstanceState.getString("voiceuri"));

    }

    /*Voice record methods -  end*/
    private class CaptureAsync extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            uploadProgress.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params) {
            bitmap = BitmapFactory.decodeFile(imageUri.getPath());
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
                        parameters.put("filename", imageName);
                        parameters.put("crimeid",String.valueOf(lastCaseId));
                        /*parameters.put("status", "P");
                        parameters.put("userid", "3");
                        parameters.put("type", caseType);
                        parameters.put("latitude", String.valueOf(mLastLocation.getLatitude()));
                        parameters.put("longitude", String.valueOf(mLastLocation.getLongitude()));*/

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
                FileInputStream fis = new FileInputStream(voiceUri.getPath());
                //System.out.println(file.exists() + "!!");
                //InputStream in = resource.openStream();
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                byte[] buf = new byte[1024];

                    for (int readNum; (readNum = fis.read(buf)) != -1; ) {
                        bos.write(buf, 0, readNum); //no doubt here is 0
                        //Writes len bytes from the specified byte array starting at offset off to this byte array output stream.
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
                        parameters.put("filename", audioName);
                        parameters.put("crimeid",String.valueOf(lastCaseId));
                    /*    parameters.put("status", "P");
                        parameters.put("userid",userID.toString());
                        parameters.put("type", caseType);
                        parameters.put("latitude", String.valueOf(mLastLocation.getLatitude()));
                        parameters.put("longitude", String.valueOf(mLastLocation.getLongitude()));
*/
                        return parameters;
                    }
                };
                queue.add(request);

            } catch (Exception ex) {
                Log.d("evi",ex.getMessage().toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            uploadProgress.setVisibility(View.INVISIBLE);
        }
    }
    private class VideoAsync extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            uploadProgress.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {

              //  FileInputStream fis = new FileInputStream(fileUri.getPath());
                //System.out.println(file.exists() + "!!");
                //InputStream in = resource.openStream();
               /* ByteArrayOutputStream bos = new ByteArrayOutputStream();
                byte[] buf = new byte[3000];

                for (int readNum; (readNum = fis.read(buf)) != -1; ) {
                    bos.write(buf, 0, readNum); //no doubt here is 0
                    //Writes len bytes from the specified byte array starting at offset off to this byte array output stream.
                }

                byte[] bytes = bos.toByteArray();*/
              /*  byte[] dataByte = new byte[1024];
                FileUtils.writeByteArrayToFile(new File(mediaStorageDir2.getPath()),FileUtils.readFileToByteArray(new File(fileUri.getPath())));

                dataByte = FileUtils.readFileToByteArray(new File(fileUri.getPath()));
                encode_string = Base64.encodeToString(dataByte, 0);*/

               // MultipartRequest multipartRequest = new MultipartRequest(NetworkManager.url_upload,null,null,new File(fileUri.getPath()),map);
                /*RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

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
                        parameters.put("imageName", videoName);
                        parameters.put("status", "P");
                        parameters.put("userid", "3");
                        parameters.put("type", caseType);
                        parameters.put("latitude", String.valueOf(mLastLocation.getLatitude()));
                        parameters.put("longitude", String.valueOf(mLastLocation.getLongitude()));

                        return parameters;
                    }
                };
                queue.add(request);*/
            } catch (Exception ex) {
                Log.d("evi",ex.getMessage().toString());
            }
            //Toast.makeText(MainActivity.this,"Finish",Toast.LENGTH_SHORT).show();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            uploadProgress.setVisibility(View.INVISIBLE);
        }
    }
}
