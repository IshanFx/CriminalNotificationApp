package com.protectme;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.protectme.handler.NetworkManager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class EvidenceSelectActivity extends AppCompatActivity {
    private static final int CAPTURE_IMAGE = 100;
    private static final int CAPTURE_VIDEO = 200;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    Button start,stop, uploadrecord;
    private MediaRecorder myAudioRecorder;
    private String outputFile = null;

    String selectedImagePath;

    private Uri fileUri;
    ImageView captureImageView;
    private Bitmap bitmap;
    private String encode_string;
    private static String imageName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evidence_select);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        captureImageView = (ImageView) findViewById(R.id.imageView1);

        start =(Button)findViewById(R.id.btnStartRecord);
        stop  =(Button)findViewById(R.id.btnStopRecord);
        uploadrecord =(Button)findViewById(R.id.btnUploadRecord);

        stop.setEnabled(false);
        uploadrecord.setEnabled(false);
        outputFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/recording.3gp";;

        myAudioRecorder=new MediaRecorder();
        myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        myAudioRecorder.setOutputFile(outputFile);
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
                Toast.makeText(this, "1", Toast.LENGTH_SHORT).show();
                if (resultCode == RESULT_OK) {
                    Toast.makeText(this, "2", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(this, "Video saved to:\n" +
                            data.getData(), Toast.LENGTH_LONG).show();
                } else if (resultCode == RESULT_CANCELED) {
                    // User cancelled the video capture
                } else {
                    // Video capture failed, advise user
                }
            }
        }
        catch(Exception ex){
            Toast.makeText(this,"Error",Toast.LENGTH_LONG).show();
            Log.d("MainError",ex.toString());
        }
    }

    private void saveCaptureImage() {
        bitmap = BitmapFactory.decodeFile(fileUri.getPath());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream);

        byte[] array = outputStream.toByteArray();
        encode_string = Base64.encodeToString(array,0);
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
                    parameters.put("encodeString",encode_string);
                    parameters.put("imageName",imageName);
                    return parameters;
                }
            };
            queue.add(request);
        }
        catch (Exception ex){
            // Toast.makeText(MainActivity.this,"Errr",Toast.LENGTH_SHORT).show();
        }
        //Toast.makeText(MainActivity.this,"Finish",Toast.LENGTH_SHORT).show();

    }

    public void startVideo(View view) {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        fileUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO); // create a file to save the image
        intent.putExtra(MediaStore.EXTRA_OUTPUT,fileUri);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,10000);
        startActivityForResult(intent, CAPTURE_VIDEO);
    }

    /*Path create start */
    private static Uri getOutputMediaFileUri(int type){
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /** Create a File for saving an image or video */
    private static File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "ProtectMEAPP");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
        } else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_"+ timeStamp + ".mp4");
        } else {
            return null;
        }
        //set the image name
        imageName = "IMG"+timeStamp+".jpg";
        return mediaFile;
    }
    /*Path create end*/

    /*Custome path create start*/
    public String getAbsolutePath(Uri uri) {
        String[] projection = { MediaStore.MediaColumns.DATA };
        @SuppressWarnings("deprecation")
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }

    public Bitmap decodeFile(String path) {
        try {
            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, o);
            // The new size we want to scale to
            final int REQUIRED_SIZE = 70;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE && o.outHeight / scale / 2 >= REQUIRED_SIZE)
                scale *= 2;

            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeFile(path, o2);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;

    }


    /*Voice record methods -  start*/

    public void startVoiceRecord(View view) {
        try {
            myAudioRecorder.prepare();
            myAudioRecorder.start();
        }

        catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        catch (IOException e) {
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
        myAudioRecorder  = null;

        stop.setEnabled(false);
        uploadrecord.setEnabled(true);

        Toast.makeText(getApplicationContext(), "Audio recorded successfully",Toast.LENGTH_LONG).show();
    }

    public void uploadVoiceRecord(View view) {
        MediaPlayer m = new MediaPlayer();

        try {
            m.setDataSource(outputFile);
        }

        catch (IOException e) {
            e.printStackTrace();
        }

        try {
            m.prepare();
        }

        catch (IOException e) {
            e.printStackTrace();
        }

        m.start();
        Toast.makeText(getApplicationContext(), "Playing audio", Toast.LENGTH_LONG).show();
    }

    /*Voice record methods -  end*/

    private class CaptureAsync extends AsyncTask<Void,Void,Void> {

        @Override
        protected Void doInBackground(Void... params) {
            bitmap = BitmapFactory.decodeFile(fileUri.getPath());
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream);

            byte[] array = outputStream.toByteArray();
            encode_string = Base64.encodeToString(array,0);

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
                        parameters.put("encodeString",encode_string);
                        parameters.put("imageName",imageName);
                        return parameters;
                    }
                };
                queue.add(request);
            }
            catch (Exception ex){
                // Toast.makeText(MainActivity.this,"Errr",Toast.LENGTH_SHORT).show();
            }
            //Toast.makeText(MainActivity.this,"Finish",Toast.LENGTH_SHORT).show();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            //imageUpload();
        }

        private void imageUpload() {
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
                        parameters.put("encodeString",encode_string);
                        parameters.put("imageName",imageName);
                        return parameters;
                    }
                };
                queue.add(request);
            }
            catch (Exception ex){
                // Toast.makeText(MainActivity.this,"Errr",Toast.LENGTH_SHORT).show();
            }
            //Toast.makeText(MainActivity.this,"Finish",Toast.LENGTH_SHORT).show();
        }

    }
}
