package com.protectme;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import io.realm.internal.android.JsonUtils;

public class HistoryActivity extends AppCompatActivity {


    public static ArrayList<String> criminalList = new ArrayList();

    RequestQueue queue;
    private SwipeRefreshLayout swipeContainer;
    RealMAdapter realMAdapter;
    Realm realm;
    public static Integer userId = 0;
    ListView historyList;
    ArrayAdapter<Crime> crimeAdapter;
    Integer crimeId=0;
    Crime crimeSelect;
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
        Log.d("userID",userId.toString());

        List<Crime> list = realMAdapter.getDummy();

        crimeAdapter = new HistoryAdapter(this, 1, list);
        historyList = (ListView) findViewById(android.R.id.list);
        historyList.setAdapter(crimeAdapter);

        /*ArrayAdapter<String> adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,new ArrayList());
        historyList.setAdapter(adapter);
        new HistoryAsync().execute();*/

        //getHistoryData();
        historyList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                crimeSelect = (Crime) historyList.getItemAtPosition(position);

                AlertDialog.Builder builder = new AlertDialog.Builder(HistoryActivity.this);
                builder.setTitle("Close case");

                builder.setMessage("Are your sure to close this case");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        new HistoryCloseAsync().execute();
                        Toast.makeText(getApplicationContext(), "OK", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });

                AlertDialog dialog = builder.create();

                dialog.show();
                return false;
            }
        });

        historyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                crimeSelect = (Crime) historyList.getItemAtPosition(position);

                Intent intent = new Intent(getApplicationContext(),HistoryDetailsActivity.class);
                intent.putExtra("crimeobj", crimeSelect);
                startActivity(intent);
            }
        });

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



    /*
    * Get History data for one user
    * */
    public class HistoryAsync extends AsyncTask<Void, JSONObject, JSONArray> {

        ArrayAdapter<Crime> adapter;
        JSONArray jsonArray1;
        List<Crime> list = new ArrayList<>();
        @Override
        protected void onPreExecute() {
            adapter = (HistoryAdapter) historyList.getAdapter();
            adapter.clear();
        }

        @Override
        protected JSONArray doInBackground(Void... params) {
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

                                    JSONObject jsonObject = null;
                                    for (int i = 0; i < array.length(); i++) {
                                        jsonObject = array.getJSONObject(i);
                                        publishProgress(jsonObject);

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
                return jsonArray1;
            } catch (Exception ex) {
                return null;
            }

        }

        @Override
        protected void onPostExecute(JSONArray array) {
            super.onPostExecute(array);
            /*try {
                JSONArray jsonArray = array;

                Log.d("history",String.valueOf( jsonArray.length()));
                for (int i = 0; i < jsonArray.length(); i++) {
                  JSONObject  jsonObject = jsonArray.getJSONObject(i);
                    Crime crime = new Crime();
                    crime.setId(jsonObject.getInt("id"));
                    crime.setStatus(jsonObject.getString("status"));
                    crime.setDate(jsonObject.getString("date"));
                    crime.setType(jsonObject.getString("type"));
                    crime.setLatitude(jsonObject.getString("latitude"));
                    crime.setLongitude(jsonObject.getString("longitude"));
                    adapter.add(crime);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }*/
            swipeContainer.setRefreshing(false);
        }

        @Override
        protected void onProgressUpdate(JSONObject... jsonObject) {
           // Log.d("history",String.valueOf( values[0].getId()));
            Crime crime = new Crime();
            try {
                crime.setStatus(jsonObject[0].getString("status"));

                crime.setDate(jsonObject[0].getString("date"));
                crime.setType(jsonObject[0].getString("type"));
                crime.setId(jsonObject[0].getInt("id"));
                adapter.add(crime);
                //super.onProgressUpdate(values);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


    }

    /*
    * Fill the list view using histrory data
    * */
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

            String crimeType = "";
            Log.d("Dummy", crime.toString());
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.listview_item, null);
            //TextView txtCrimeId = (TextView) view.findViewById(R.id.txtTitle);
            TextView txtDate = (TextView) view.findViewById(R.id.txtDate);
            TextView txtTime = (TextView) view.findViewById(R.id.txtTime);
            TextView txtType  = (TextView) view.findViewById(R.id.txtType);
           // TextView txtStatus = (TextView) findViewById(R.id.txtStatus);
            try {

                String dateTimeCheck = new String(crime.getDate());
                String dT[] = dateTimeCheck.split("\\s+");

                ImageView img = (ImageView) view.findViewById(R.id.imgIcon);
                int res = context.getResources().getIdentifier("logo", "drawable", context.getPackageName());
                img.setImageResource(res);

               // txtCrimeId.setText("ID:" + String.valueOf(crime.getId()));
                txtDate.setText(dT[0].toString());
                //txtDate.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Lato-Black.ttf"));
                txtTime.setText("Time:" + dT[1].toString());
              //  txtStatus.setText("Status:"+crime.getLatitude());
                txtType.setText("Type:"+crime.getType());
            }
            catch(Exception ex){
                Log.d("Dummy",ex.toString());
            }
            return view;
        }
    }

    /*
    * Close the crime
    * */
    public class HistoryCloseAsync extends AsyncTask<Crime,Void,Void>{

        @Override
        protected Void doInBackground(Crime... crimeparams) {
             RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                         StringRequest request = new StringRequest(Request.Method.POST, NetworkManager.url_closeCase, new Response.Listener<String>() {
                             @Override
                             public void onResponse(String response) {
                                 try{
                                     JSONObject resposeJSON = new JSONObject(response);
                                     if(resposeJSON.names().get(0).equals("status")){

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
                                 parameters.put("caseid",String.valueOf(crimeSelect.getId()));
                                 return parameters;
                             }
                         };
                         request.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                         queue.add(request);

            return null;
        }
    }
}
