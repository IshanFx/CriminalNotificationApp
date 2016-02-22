package com.protectme;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.protectme.background.MyService;
import com.protectme.handler.NetworkManager;

import java.util.HashMap;
import java.util.Map;

public class GuideActivity extends AppCompatActivity {
    private Intent intent;
   TextView txt;
    RequestQueue queue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        queue = Volley.newRequestQueue(getApplicationContext());
        intent = new Intent(this, MyService.class);
        txt = (TextView) findViewById(R.id.textView2);
    }
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateUI(intent);
        }
    };
    @Override
    public void onResume() {
        super.onResume();
        startService(intent);
        registerReceiver(broadcastReceiver, new IntentFilter(MyService.BROADCAST_ACTION));
    }

    private void updateUI(Intent intent) {
        String counter = intent.getStringExtra("counter");
        String time = intent.getStringExtra("time");
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_info_black_24dp)
                        .setContentTitle("New Alert")
                        .setContentText(counter);
// Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent();

// The stack builder object will contain an artificial back stack for the
// started Activity.
// This ensures that navigating backward from the Activity leads out of
// your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
// Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(GuideActivity.class);
// Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
        mNotificationManager.notify(1, mBuilder.build());
        txt.setText(counter);
    }
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return true;
    }


    public void testvolly(View view) {
        GuideAsync guideAsync = new GuideAsync();
        guideAsync.execute();
        unregisterReceiver(broadcastReceiver);
    }

    public class GuideAsync extends AsyncTask<Void,Void,String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                StringRequest request = new StringRequest(Request.Method.POST, NetworkManager.url_saveCaseLocation, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                       // Toast.makeText(MainActivity.this, "Responce error", Toast.LENGTH_SHORT).show();
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> parameters = new HashMap<String, String>();
                        parameters.put("userid", "www");
                        parameters.put("date", "asdda");
                        parameters.put("status", "affffff");
                        return parameters;
                    }
                };
                queue.add(request);
            }
            catch (Exception ex){
               // Toast.makeText(MainActivity.this,"Errr",Toast.LENGTH_SHORT).show();
            }
            //Toast.makeText(MainActivity.this,"Finish",Toast.LENGTH_SHORT).show();
            return "ok";
        }

        @Override
        protected void onPostExecute(String aVoid) {
         Toast.makeText(getApplicationContext(),aVoid,Toast.LENGTH_SHORT).show();
        }
    }
}
