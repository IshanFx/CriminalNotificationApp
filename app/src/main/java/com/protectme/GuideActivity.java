package com.protectme;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.protectme.handler.NetworkManager;

import java.util.HashMap;
import java.util.Map;

public class GuideActivity extends AppCompatActivity {

    RequestQueue queue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        queue = Volley.newRequestQueue(getApplicationContext());

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
