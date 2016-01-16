package com.protectme;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.protectme.handler.SMSManager;

import android.location.Location;
import android.location.LocationManager;
import android.widget.Button;
import android.widget.Toast;

public class HomeActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, OnConnectionFailedListener, LocationListener{

    LocationManager locationManager;
    Location mLastLocation;
    LocationRequest mLocationRequest;
    Boolean startRealTimeTrack = false;
    private GoogleApiClient mGoogleApiClient;
    Button btnKidnapClick;
    static int startCycle = 1;
    SMSManager smsManage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        btnKidnapClick = (Button) findViewById(R.id.btnKidnap);
        smsManage = new SMSManager();
        locationManager =
                (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        buildGoogleApiClient();
        createLocationRequest();
    }
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
        if (mGoogleApiClient.isConnected()) {
            Toast.makeText(this,"Connected",Toast.LENGTH_SHORT).show();
        }
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.settings) {
            Intent intent = new Intent(this,SettingsActivity.class);
            startActivity(intent);
        }
        if(id==R.id.guide_setting){
            Intent intent = new Intent(this,GuideActivity.class);
            startActivity(intent);
        }
        if(id==R.id.history_setting){
            Intent intent = new Intent(this,HistoryActivity.class);
            startActivity(intent);
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
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }
    private void updateUI() {
        if(startRealTimeTrack) {
            btnKidnapClick.setText(String.valueOf(mLastLocation.getLatitude()) + "\n " + String.valueOf(mLastLocation.getLongitude()));
            if(startCycle==1){
                smsManage.sendSMS("Latitude:"+String.valueOf(mLastLocation.getLatitude())+"Longitude:"+String.valueOf(mLastLocation.getLongitude()));
                startCycle+=2;
                Log.d("SMSLogin",String.valueOf(startCycle) );
            }
        }

        }

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
        if(startRealTimeTrack){
            startRealTimeTrack = false;
            btnKidnapClick.setBackgroundResource(R.drawable.home_button_normal);
            btnKidnapClick.setText(getResources().getText(R.string.kidnapbtn,null));
        }
        else{
            startRealTimeTrack = true;
            btnKidnapClick.setBackgroundResource(R.drawable.home_button_selected);
        }
    }
}
