package com.protectme;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.protectme.handler.NetworkManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity {

    ListView historyList;
    public static ArrayList<String> criminalList = new ArrayList();
    Button btn;
    RequestQueue queue;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        historyList = (ListView) findViewById(R.id.historylistView);
        btn = (Button) findViewById(R.id.btnLoad);
        queue = Volley.newRequestQueue(getApplicationContext());

        ArrayAdapter<String> adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,new ArrayList());
        historyList.setAdapter(adapter);
        new HistoryAsync().execute();

        //getHistoryData();
        historyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

    }
    public void getHistoryData(){

        /*criminalList.add("Case 1 \nLocation :79,000,6 \nTime:12:30 Data:10/1/2016");
        criminalList.add("Case 2 \nLocation :79,000,6 \nTime:12:30 Data:10/1/2016");
        criminalList.add("Case 3 \nLocation :79,000,6 \nTime:12:30 Data:10/1/2016");
        criminalList.add("Case 4 \nLocation :79,000,6 \nTime:12:30 Data:10/1/2016");
        criminalList.add("Case 5 \nLocation :79,000,6 \nTime:12:30 Data:10/1/2016");*/
        try {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, NetworkManager.url_getHistroryLocation , null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                           // Toast.makeText(getApplicationContext(),"ok",Toast.LENGTH_SHORT).show();
                            try {
                                JSONArray array = response.getJSONArray("Cases");
                               // Toast.makeText(getApplicationContext(),,Toast.LENGTH_SHORT).show();
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject jsonObject = array.getJSONObject(1);
                                   // Toast.makeText(getApplicationContext(),jsonObject.getString("STATUS"),Toast.LENGTH_SHORT).show();
                                    criminalList.add(jsonObject.getString("STATUS"));

                                }
                            } catch (JSONException e) {
                               Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                            ArrayAdapter<String> adapter = new ArrayAdapter(getApplicationContext(),android.R.layout.simple_list_item_1,criminalList);

                            historyList.setAdapter(adapter);
                            Toast.makeText(getApplicationContext(),String.valueOf(criminalList.size()),Toast.LENGTH_SHORT).show();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(),error.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });

            queue.add(jsonObjectRequest);
           //Toast.makeText(getApplicationContext(),String.valueOf(criminalList.size()),Toast.LENGTH_SHORT).show();


        }
        catch (Exception ex){

        }

    }

    public void loadData(View view) {


    }



    public class HistoryAsync extends AsyncTask<Void,String,String> {

        ArrayAdapter<String> adapter;

        @Override
        protected void onPreExecute() {
            adapter = (ArrayAdapter<String>) historyList.getAdapter();
           // adapter.add("ghghh");
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,NetworkManager.url_getHistroryLocation , null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    JSONArray array = response.getJSONArray("cases");
                                    for (int i = 0; i < array.length(); i++) {

                                        JSONObject jsonObject = array.getJSONObject(i);
                                        publishProgress(jsonObject.getString("status"));

                                    }
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
            /*for (int i=0;i<10;i++){
                publishProgress(i);
            }*/

        }

        @Override
        protected void onPostExecute(String txt) {
            Toast.makeText(getApplicationContext(),txt,Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onProgressUpdate(String... values) {
            adapter.add(values[0]);

            //super.onProgressUpdate(values);
        }


    }
}
