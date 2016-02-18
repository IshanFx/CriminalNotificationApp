package com.protectme;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.protectme.dao.Crime;
import com.protectme.database.RealMAdapter;
import com.protectme.handler.NetworkManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.Realm;

public class HistoryActivity extends AppCompatActivity {


    public static ArrayList<String> criminalList = new ArrayList();

    RequestQueue queue;
    private SwipeRefreshLayout swipeContainer;
    RealMAdapter realMAdapter;
    Realm realm;
    public static Integer userId = 0;
    ListView historyList;
    ArrayAdapter<Crime> crimeAdapter;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        realMAdapter = new RealMAdapter(getApplicationContext());
        realm = RealMAdapter.protectRMDB;
        userId = realMAdapter.getUserId();


        List<Crime> list = realMAdapter.getDummy();

        crimeAdapter = new HistoryAdapter(this, 1, list);
        historyList = (ListView) findViewById(android.R.id.list);
        historyList.setAdapter(crimeAdapter);

        /*ArrayAdapter<String> adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,new ArrayList());
        historyList.setAdapter(adapter);
        new HistoryAsync().execute();*/

        //getHistoryData();
       /* historyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });*/

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                new HistoryAsync().execute();
            }
        });

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

    }

    public void getHistoryData() {

        /*criminalList.add("Case 1 \nLocation :79,000,6 \nTime:12:30 Data:10/1/2016");
        criminalList.add("Case 2 \nLocation :79,000,6 \nTime:12:30 Data:10/1/2016");
        criminalList.add("Case 3 \nLocation :79,000,6 \nTime:12:30 Data:10/1/2016");
        criminalList.add("Case 4 \nLocation :79,000,6 \nTime:12:30 Data:10/1/2016");
        criminalList.add("Case 5 \nLocation :79,000,6 \nTime:12:30 Data:10/1/2016");*/
        try {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, NetworkManager.url_getHistroryLocation, null,
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
                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                            ArrayAdapter<String> adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, criminalList);

                            //historyList.setAdapter(adapter);
                            Toast.makeText(getApplicationContext(), String.valueOf(criminalList.size()), Toast.LENGTH_SHORT).show();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

            queue.add(jsonObjectRequest);
            //Toast.makeText(getApplicationContext(),String.valueOf(criminalList.size()),Toast.LENGTH_SHORT).show();


        } catch (Exception ex) {

        }

    }




    public class HistoryAsync extends AsyncTask<Void, Crime, String> {

        ArrayAdapter<Crime> adapter;
        List<Crime> list;
        @Override
        protected void onPreExecute() {
            adapter = (HistoryAdapter) historyList.getAdapter();
            adapter.clear();
        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                queue = Volley.newRequestQueue(getApplicationContext());
                StringRequest jsonObjectRequest = new StringRequest(Request.Method.POST, NetworkManager.url_getHistroryLocation,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    Log.d("history", response.toString());
                                    JSONObject crimejsonObject =  new JSONObject(response);
                                    JSONArray array = crimejsonObject.getJSONArray("cases");
                                    Log.d("history",String.valueOf( array.length()));
                                    for (int i = 0; i < array.length(); i++) {


                                        JSONObject jsonObject = array.getJSONObject(i);


                                       Crime crime = new Crime();
                                        crime.setId(jsonObject.getInt("id"));
                                        crime.setType(jsonObject.getString("type"));
                                        crime.setDate(jsonObject.getString("date"));
                                        crime.setStatus(jsonObject.getString("status"));
                                        crime.setLatitude(jsonObject.getString("latitude"));
                                        crime.setLongitude(jsonObject.getString("longitude"));
                                        publishProgress(crime);
                                       /*  realm.beginTransaction();*/
                                     /*   Crime crime = realm.createObject(Crime.class);
                                        crime.setId(jsonObject.getInt("id"));
                                        crime.setType(jsonObject.getString("type"));
                                        crime.setDate(jsonObject.getString("date"));
                                        crime.setStatus(jsonObject.getString("status"));
                                        crime.setLatitude(jsonObject.getString("latitude"));
                                        crime.setLongitude(jsonObject.getString("longitude"));
                                        realm.commitTransaction();*/
                                       /* Log.d("history",jsonObject.toString());
                                        list.add(crime);*/
                                    }
                                } catch (Exception e) {
                                    Log.d("history", e.toString());
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d("history1",error.toString());
                            }
                        }) {
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {

                                Map<String, String> parameters = new HashMap<String, String>();
                                parameters.put("userid",userId.toString());
                                return parameters;
                            }
                        };
                jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                queue.add(jsonObjectRequest);
                return "ok";
            } catch (Exception ex) {
                return ex.getMessage();
            }
            /*for (int i=0;i<10;i++){
                publishProgress(i);
            }*/

        }

        @Override
        protected void onPostExecute(String txt) {

            swipeContainer.setRefreshing(false);
        }

        @Override
        protected void onProgressUpdate(Crime... values) {
            Log.d("history",String.valueOf( values[0].getId()));
            adapter.add(values[0]);
            //super.onProgressUpdate(values);
        }


    }

    class HistoryAdapter extends ArrayAdapter<Crime> {
        Context context;
        List<Crime> objects;

        public HistoryAdapter(Context context, int resource, List<Crime> objects) {
            super(context, resource, objects);
            this.context = context;
            this.objects = objects;

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Crime crime = objects.get(position);
            Log.d("Dummy", crime.toString());
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.listview_item, null);
            TextView txt = (TextView) view.findViewById(R.id.txtTitle);
            TextView txtDate = (TextView) view.findViewById(R.id.txtDate);
            TextView txtType  = (TextView) view.findViewById(R.id.txtType);
            txt.setText(String.valueOf(crime.getId()));
            txtDate.setText(crime.getDate());
            txtType.setText(crime.getType());
            ImageView img = (ImageView) view.findViewById(R.id.imgIcon);
            int res = context.getResources().getIdentifier("logo", "drawable", context.getPackageName());
            img.setImageResource(res);
            return view;
        }
    }
}
