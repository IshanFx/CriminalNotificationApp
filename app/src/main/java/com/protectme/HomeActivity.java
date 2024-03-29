package com.protectme;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
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
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.protectme.database.RealMAdapter;
import com.protectme.handler.NetworkManager;
import com.protectme.handler.SMSManager;
import com.protectme.handler.VariableManager;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HomeActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, OnConnectionFailedListener, LocationListener {
    View btn_selected
            ;
    LocationManager locationManager;
    public static Location mLastLocation, mLastStaticLocation;
    public static String caseType = "";
    LocationRequest mLocationRequest;

    Boolean startRealTimeTrack = false;
    private GoogleApiClient mGoogleApiClient;
    ImageButton btnKidnapClick;
    ImageButton btnRobberyClick;
    ImageButton btnEvidenceClick;
    TextView txtaddress;
    TextView txtLatitude;
    TextView txtLongitude;
    static int startCycle = 1;
    SMSManager smsManage;

    public static int lastCaseId = -1;
    RequestQueue queue;
    RealMAdapter realMAdapter;
    public static Integer userID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        queue = Volley.newRequestQueue(getApplicationContext());
        btnKidnapClick = (ImageButton) findViewById(R.id.btnKidnap);
        btnRobberyClick = (ImageButton) findViewById(R.id.btnRobbery);
        txtLatitude = (TextView) findViewById(R.id.txtLatitude);
        txtLongitude = (TextView) findViewById(R.id.txtLongitude);
        txtaddress = (TextView) findViewById(R.id.txtaddress);
        realMAdapter = new RealMAdapter(getApplicationContext());
        userID = realMAdapter.getUserId();

        smsManage = new SMSManager();
        locationManager =
                (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        buildGoogleApiClient();
        createLocationRequest();
        btnEvidenceClick = (ImageButton) findViewById(R.id.btnEvidence);
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


    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        if (id == R.id.family_setting) {
            Intent familyIntent = new Intent(this, FamilyActivity.class);
            startActivity(familyIntent);
        }


        if (id == R.id.history_setting) {
            Intent historyIntent = new Intent(this, HistoryActivity.class);
            startActivity(historyIntent);
        }
        if (id == R.id.logout_setting) {
            realMAdapter.removeUser();
            Intent loginIntent = new Intent(this, LoginActivity.class);
            startActivity(loginIntent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnected(Bundle bundle) {
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        updateUI();

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    protected void startLocationUpdates() {
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
        } catch (SecurityException ex) {

        }
    }

    /*
    * change the UI display when location change
    * */
    private void updateUI() {
        if (startRealTimeTrack) {
            txtLatitude.setText(String.valueOf(mLastLocation.getLatitude()));
            txtLongitude.setText(String.valueOf(mLastLocation.getLongitude()));

            caseType = "K";
            LocationAsync loc = new LocationAsync();
            loc.execute();

           /* if (startCycle == 1) {
                smsManage.sendSMS("Latitude:" + String.valueOf(mLastLocation.getLatitude()) + "Longitude:" + String.valueOf(mLastLocation.getLongitude()));
                startCycle += 2;
                Log.d("SMSLogin", String.valueOf(startCycle));

            }*/
        }
    }

    /*
    * Stop the location tacking.
    * */
    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
        }
    }

    public void startRealtimeTrack(View view) {
        btn_selected = view;
       /* LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast_layout,
                (ViewGroup) findViewById(R.id.toast_layout_root));
*/

       /* TextView text = (TextView) layout.findViewById(R.id.text);
        text.setText("This is a custom toast");

        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();*/
        if (startRealTimeTrack) {
            startRealTimeTrack = false;
            btnKidnapClick.setBackgroundResource(R.color.colorPrimaryDark);
            // btnKidnapClick.setBackgroundResource(R.drawable.home_button_normal);
            //customeToast();
            lastCaseId = -1;
        } else {
            startRealTimeTrack = true;
            btnKidnapClick.setBackgroundResource(android.R.color.holo_blue_light);
            new VariableManager().customeToast(btn_selected,"Case Opened",1);
            // btnKidnapClick.setBackgroundResource(R.drawable.home_button_selected);

        }
    }

    public void evidenceHomeAvtivity(View view) {
        Intent actEvidence = new Intent(this, EvidenceSelectActivity.class);
        startActivity(actEvidence);
    }

    public void startStaticLocationTrack(View view) {
        btn_selected
                = view;
        boolean isPass = true;
        Log.d("Accuracy", "Not");
        // while (isPass) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

        if (mLastLocation != null) {
            Log.d("Accuracy", "Inside");
            Log.d("Accuracy", String.valueOf(mLastLocation.getAccuracy()));
            if (mLastLocation.getAccuracy() < 1050) {
                txtLatitude.setText(String.valueOf(mLastLocation.getLatitude()));
                txtLongitude.setText(String.valueOf(mLastLocation.getLongitude()));
                Log.d("Accuracy", String.valueOf(mLastLocation.getAccuracy()));
                caseType = "R";

                //if(NetworkManager.checkNetwork(getApplicationContext())){
                    LocationAsync loc = new LocationAsync();
                    loc.execute();
               // }
              //  else{
                    new SMSManager().sendSMS("Latitude:"+mLastLocation.getLatitude()+"Longitude:"+mLastLocation.getLongitude(),getApplicationContext());
              //  }

                isPass = false;
            }
        }
        if (mLastLocation == null) {
            Log.d("Accuracy", "Not");
        }
        //new AddressAsync().execute();
        getUserAddress(mLastLocation.getLatitude(),mLastLocation.getLongitude());
        //}

    }

    public class LocationAsync extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                StringRequest request = new StringRequest(Request.Method.POST, NetworkManager.url_saveCaseLocation, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject resposeJSON = new JSONObject(response);
                            if (resposeJSON.names().get(0).equals("caseid")) {
                                if (caseType == "K") {
                                    lastCaseId = resposeJSON.getInt("caseid");

                                    //Toast.makeText(getApplicationContext(), "Last case id:" + lastCaseId, Toast.LENGTH_SHORT).show();
                                } else {
                                    new VariableManager().customeToast(btn_selected
                                            , "Case Opened", 1);

                                    //Toast.makeText(getApplicationContext(), "Last case id:" + lastCaseId, Toast.LENGTH_SHORT).show();
                                    lastCaseId = -1;
                                }
                            }
                        } catch (Exception ex) {
                            new VariableManager().customeToast(btn_selected
                                    , "Case Not Opened,Check Network", 0);

                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                       new VariableManager().customeToast(btn_selected
                               ,"Check Data Connection",0);
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {

                        Map<String, String> parameters = new HashMap<String, String>();
                        parameters.put("status", "P");
                        parameters.put("userid", userID.toString());
                        parameters.put("type", caseType);
                        parameters.put("latitude", String.valueOf(mLastLocation.getLatitude()));
                        parameters.put("longitude", String.valueOf(mLastLocation.getLongitude()));
                        parameters.put("caseid", String.valueOf(lastCaseId));
                        return parameters;
                    }
                };
                request.setRetryPolicy(new DefaultRetryPolicy(20000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                queue.add(request);
            } catch (Exception ex) {
                new VariableManager().customeToast(btn_selected
                        , "Case Not Opened", 0);
            }
            //Toast.makeText(MainActivity.this,"Finish",Toast.LENGTH_SHORT).show();
            return null;
        }
    }


    public void getUserAddress(Double latitude,Double longitude){

        StringRequest request = new StringRequest(Request.Method.GET,"https://maps.googleapis.com/maps/api/geocode/json?latlng="+latitude+","+longitude+"&key=AIzaSyD5S1_sclTRhSA2crRAdGLmJ-2Vp7dBajE", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject resposeJSON = new JSONObject(response);
                    JSONArray jsonArray =  resposeJSON.getJSONArray("results");
                    resposeJSON = jsonArray.getJSONObject(0);
                    txtaddress.setText(resposeJSON.get("formatted_address").toString());
                    //  resposeJSON = jsonArray.optJSONObject(0);
                    Log.d("Address", resposeJSON.get("formatted_address").toString());
                } catch (Exception ex) {
                    Log.d("Address",ex.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                new VariableManager().customeToast(btn_selected
                        ,"Check Data Connection",0);
            }
        });
        request.setRetryPolicy(new DefaultRetryPolicy(20000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);
    }

    public class AddressAsync extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... params) {
            StringRequest request = new StringRequest(Request.Method.GET,"https://maps.googleapis.com/maps/api/geocode/json?latlng=6.9106853,79.9716076&key=AIzaSyD5S1_sclTRhSA2crRAdGLmJ-2Vp7dBajE", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject resposeJSON = new JSONObject(response);
                        JSONArray jsonArray =  resposeJSON.getJSONArray("results");
                        resposeJSON = jsonArray.getJSONObject(0);
                      //  resposeJSON = jsonArray.optJSONObject(0);
                        Log.d("Address", resposeJSON.get("formatted_address").toString());
                    } catch (Exception ex) {

                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    new VariableManager().customeToast(btn_selected
                            ,"Check Data Connection",0);
                }
            });
            request.setRetryPolicy(new DefaultRetryPolicy(20000, 3, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            queue.add(request);
            return null;
        }
    }


}
