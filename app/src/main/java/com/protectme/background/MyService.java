package com.protectme.background;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.protectme.handler.NetworkManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by IshanFx on 2/5/2016.
 */
public class MyService extends Service {
    private static final String TAG = "BroadcastService";
    public static final String BROADCAST_ACTION = "com.protectme.background";
    private final Handler handler = new Handler();
    public  static String userCount = "0";
    Intent intent;
    int counter = 0;
    RequestQueue queue;


    @Override
    public void onCreate() {

        intent = new Intent(BROADCAST_ACTION);


    }
    @Override
    public void onStart(Intent intent, int startId) {

    }



    private Runnable sendUpdatesToUI = new Runnable() {
        public void run() {
            DisplayLoggingInfo();
            handler.postDelayed(this, 5000); // 5 seconds
        }
    };
    private void DisplayLoggingInfo() {
        // Log.d(TAG, "entered DisplayLoggingInfo");
        try {
            queue = Volley.newRequestQueue(getApplicationContext());
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, NetworkManager.url_getCaseDetails , null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                userCount = (String) response.get("casecount");

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // Handle error
                        }
                    });

            queue.add(jsonObjectRequest);

        }
        catch (Exception ex) {
        }

        intent.putExtra("time", new Date().toLocaleString());
        intent.putExtra("counter", userCount);
        sendBroadcast(intent);
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handler.removeCallbacks(sendUpdatesToUI);
        handler.postDelayed(sendUpdatesToUI, 1000); // 1 second

        Toast.makeText(getApplicationContext(),"start Service",Toast.LENGTH_SHORT).show();
        // Thread thread = new Thread(new MyThreadClass(startId));
        //  thread.start();
        /*int i=0;
        synchronized (this){
            while (i<10){

            }
        }*/
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(getApplicationContext(),"stop Service",Toast.LENGTH_SHORT).show();

    }


    public class HistoryAsync extends AsyncTask<Void,String,String> {




        @Override
        protected String doInBackground(Void... params) {
            try {
                queue = Volley.newRequestQueue(getApplicationContext());
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, NetworkManager.url_getCaseDetails , null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    userCount = (String) response.get("casecount");

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // Handle error
                            }
                        });

                queue.add(jsonObjectRequest);
                return "ok";
            }
            catch (Exception ex){
                return ex.getMessage();
            }

        }





    }
}
