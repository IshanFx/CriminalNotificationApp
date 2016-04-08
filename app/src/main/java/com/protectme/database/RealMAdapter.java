package com.protectme.database;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.protectme.dao.Crime;
import com.protectme.dao.Family;
import com.protectme.dao.User;
import com.protectme.handler.NetworkManager;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.rx.RealmObservableFactory;

/**
 * Created by IshanFx on 2/15/2016.
 */
public class RealMAdapter {
    public static Realm protectRMDB;
    static User userData;
    Context context;
    RequestQueue queue;
    static boolean isUserAvailable = false;

    public RealMAdapter(Context context) {
        this.context = context;
        protectRMDB =
                Realm.getInstance(
                        new RealmConfiguration.Builder(context)
                                .name("protectmeappclient.realm")
                                .schemaVersion(2) // Must be bumped when the schema changes
                                .deleteRealmIfMigrationNeeded()
                                .build()
                );

        queue = Volley.newRequestQueue(context);
    }

    /*
    * Insert user to local database
    * */
    public void insertUser(User user) {
        protectRMDB.beginTransaction();
        User userObject = protectRMDB.createObject(User.class);
        userObject.setId(user.getId());
        userObject.setEmail(user.getEmail());
        userObject.setPassword(user.getPassword());
        protectRMDB.commitTransaction();
        Log.d("Realm","Insert Success "+user.getId());
    }

    /*
    * Insert New family member
    * */
    public void insertFamily(Family family) {
        protectRMDB.beginTransaction();
        Family userObject = protectRMDB.createObject(Family.class);
        userObject.setName(family.getName());
        userObject.setNumber(family.getNumber());
        protectRMDB.commitTransaction();
        Log.d("Realm","Insert Success "+family.getName()+" "+family.getNumber());
    }

// Dummy Start
    public void insertDummy(){
        protectRMDB.beginTransaction();
        Crime userObject = protectRMDB.createObject(Crime.class);
        userObject.setId(1);
        userObject.setStatus("OOPS");
        protectRMDB.commitTransaction();
        Log.d("Realm","Insert Sucess");
    }

    public List<Crime> getDummy(){
        List<Crime> list = new ArrayList<>();
        RealmResults<Crime> results = protectRMDB.where(Crime.class).findAll();
        for (Crime s:results){
            list.add(s);
            Log.d("Dummy",s.toString());
        }
        return  list;
    }
    /*
    * Get Family Numbers
    * */
    public List<Family> getFamily(){
        List<Family> list = new ArrayList<>();
        RealmResults<Family> results = protectRMDB.where(Family.class).findAll();
        for (Family s:results
        ){
            list.add(s);
            Log.d("Dummy",s.getNumber().toString());
        }
        return  list;
    }

    public void removeFamily(){
        RealmResults<Family> results = protectRMDB.where(Family.class).findAll();
        protectRMDB.beginTransaction();
        results.clear();
        protectRMDB.commitTransaction();
    }

    /*
    * Check Code
    * */
    public void removeDummy(){
        RealmResults<Crime> results = protectRMDB.where(Crime.class).findAll();
        protectRMDB.beginTransaction();
        results.clear();
        protectRMDB.commitTransaction();
    }
//Dummy End
    public Integer getUserId(){
        User results = protectRMDB.where(User.class).findFirst();
        return  results.getId();
    }

    public void removeUser() {
        RealmResults<User> results = protectRMDB.where(User.class).findAll();
        protectRMDB.beginTransaction();
        results.clear();
        Log.d("Realm", "Remove Sucess "+results.size() );
        protectRMDB.commitTransaction();
    }

    public boolean checkFirstLogin(User user)  {
        boolean status = false;
        userData = new User();
        userData.setEmail(user.getEmail());
        userData.setPassword(user.getPassword());


            //new LoginAsync().execute();

                StringRequest request = new StringRequest(Request.Method.POST, NetworkManager.url_getLoginVerify, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.d("Realm", "responce"+response.toString());
                            JSONObject resposeJSON = new JSONObject(response);
                            if(resposeJSON.names().get(0).equals("check")){
                                if (Boolean.valueOf(resposeJSON.getString("check"))) {
                                    isUserAvailable = true;
                                    Log.d("Realm", "responce okkk");
                                } else {
                                    isUserAvailable = false;
                                }
                            }

                        } catch (Exception ex) {

                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Log.d("Realm", "pass Available");
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                        Map<String, String> parameters = new HashMap<String, String>();
                        parameters.put("username", userData.getEmail());
                        parameters.put("password", userData.getPassword());

                        return parameters;
                    }
                };
               // request.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                queue.add(request);


           /* queue = Volley.newRequestQueue(getApplicationContext());
            StringRequest request = new StringRequest(Request.Method.POST, NetworkManager.url_getLoginVerify, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject resposeJSON = new JSONObject(response);
                        Log.d("Realm", "responce ");

                                 if(resposeJSON.names().get(0).equals("check")){
                                     if (Boolean.valueOf(resposeJSON.getString("check"))) {
                                         isUserAvailable = true;
                                     } else {
                                         isUserAvailable = false;
                                     }
                                 }
                    } catch (Exception ex) {
                        Log.d("Realm", "json err");
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    isUserAvailable = false;
                    Log.d("Realm","err res");
                    // Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                    Map<String, String> parameters = new HashMap<String, String>();

                    parameters.put("username",userData.getEmail());
                    parameters.put("password", userData.getPassword());

                    return parameters;
                }
            };
            request.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            queue.add(request);*/

        /*JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, NetworkManager.url_getLoginVerify, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                Log.d("Realm","responce ");
                                if (response.names().get(0).equals("check")) {
                                    if (Boolean.valueOf(response.getString("check"))) {
                                        isUserAvailable = true;
                                    } else {
                                        isUserAvailable = false;
                                    }
                                }

                            } catch (JSONException e) {
                                Log.d("Realm","json err");
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            isUserAvailable = false;
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    //SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                    Map<String, String> parameters = new HashMap<String, String>();
                    parameters.put("username", userData.getEmail());
                    parameters.put("password", userData.getPassword());
                    return parameters;
                }


            };
            queue.add(jsonObjectRequest);*/


      /*  if (isUserAvailable) {
            Log.d("Realm", "user Available");
            //removeUser();
          //  insertUser(userData);
            status = true;

        } else {
            Log.d("Realm", "user Not found");
        }*/
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.d("Realm", "End");
        return isUserAvailable;
    }

    public boolean checkSecondLogin() {
        User results = protectRMDB.where(User.class).findFirst();
        if (results == null) {
            Log.d("Realm", "local user Not found");
            return false;

        } else {
            Log.d("Realm", "local user found");
            return true;
        }
    }

    public class LoginAsync extends AsyncTask<Void,Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            Log.d("Realm", "Async start");
            synchronized (this) {
                StringRequest request = new StringRequest(Request.Method.POST, NetworkManager.url_getLoginVerify, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject resposeJSON = new JSONObject(response);
                            if(resposeJSON.names().get(0).equals("check")){
                                if (Boolean.valueOf(resposeJSON.getString("check"))) {
                                    isUserAvailable = true;
                                } else {
                                    isUserAvailable = false;
                                }
                            }

                        } catch (Exception ex) {
                            isUserAvailable = false;
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Log.d("Realm", "pass Available");
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                        Map<String, String> parameters = new HashMap<String, String>();
                        parameters.put("username", userData.getEmail());
                        parameters.put("password", userData.getPassword());

                        return parameters;
                    }
                };
                request.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                queue.add(request);
            }
            return isUserAvailable;
        }

        @Override
        protected void onPostExecute(Boolean sucess) {
            if(sucess){

            }
            //super.onPostExecute(aBoolean);
        }
    }
}
