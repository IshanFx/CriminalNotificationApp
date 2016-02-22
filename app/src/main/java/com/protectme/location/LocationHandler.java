package com.protectme.location;

import android.content.Context;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;

/**
 * Created by IshanFx on 2/22/2016.
 */
public class LocationHandler implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private static GoogleApiClient mGoogleApiClient;
    public static LocationManager locationManager;
    public static android.location.Location mLastLocation;
    Context context;

    public LocationHandler(Context context){
        this.context = context;
        locationManager = (LocationManager) this.context.getSystemService(Context.LOCATION_SERVICE);

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
        if (mGoogleApiClient.isConnected()) {
           // Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();

        }
    }

    public String getLastLocation() {
        buildGoogleApiClient();
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
       // Toast.makeText(this, String.valueOf(mLastLocation.getLatitude()).toString(), Toast.LENGTH_SHORT).show();
        return String.valueOf( mLastLocation.getLatitude());
    }

    @Override
    public void onConnected(Bundle bundle) {
        getLastLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(android.location.Location location) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
