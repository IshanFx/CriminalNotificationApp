package com.protectme;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
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
import com.protectme.dao.Crime;
import com.protectme.handler.NetworkManager;
import com.protectme.handler.VariableManager;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class HistoryDetailsActivity extends AppCompatActivity {
    TextView caseId,caseTime,caseDate,caseStatus,caseType,caseLatitude;
    static Crime crimeData;
    View btnSelect;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        caseId = (TextView) findViewById(R.id.txtId);
        caseDate = (TextView) findViewById(R.id.txtDate);
        caseTime = (TextView) findViewById(R.id.txtTime);
        caseStatus = (TextView) findViewById(R.id.txtStatus);
        caseType = (TextView) findViewById(R.id.txtType);
        crimeData = (Crime) getIntent().getSerializableExtra("crimeobj");
        Log.d("Hisde", crimeData.getStatus().toString());
        assignData();
    }

    public void assignData( ){
        try {
            String dateTimeCheck = new String(crimeData.getDate().toString());
       String dateTime[] = dateTimeCheck.split("\\s+");
         switch (crimeData.getStatus().toString()) {
            case "P":
                crimeData.setStatus("Pending");
                break;
            case "A":
                crimeData.setStatus("Assign");
                break;
            case "C":
                crimeData.setStatus("Cancelled");
                break;
            case "S":
                crimeData.setStatus("Solved");
                break;
            default:
                crimeData.setStatus("Pending");
        }
        switch (crimeData.getType().toString()) {
                    case "E":
                        crimeData.setType("Evidence");
                        break;
                    case "R":
                        crimeData.setType("Static");
                        break;
                    case "K":
                        crimeData.setType("Track");
                        break;
                }
            caseId.setText(String.valueOf(crimeData.getId()));
            caseType.setText(crimeData.getType().toString());
            caseStatus.setText(crimeData.getStatus().toString());
            caseDate.setText(dateTime[0].toString());
            caseTime.setText(dateTime[1].toString());
            caseLatitude.setText(crimeData.getLatitude());
        }
        catch (Exception ex){
            Log.d("Hisde",ex.toString());
        }
    }

    public void closeCase(View view) {
        btnSelect = view;
        HistoryCloseAsync historyCloseAsync = new HistoryCloseAsync();
        historyCloseAsync.execute();
        caseStatus.setText("Cancelled");
    }



    public class HistoryCloseAsync extends AsyncTask<Crime,Void,Void> {

        @Override
        protected Void doInBackground(Crime... crimeparams) {
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            StringRequest request = new StringRequest(Request.Method.POST, NetworkManager.url_closeCase, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try{

                        JSONObject resposeJSON = new JSONObject(response);
                        if(resposeJSON.names().get(0).equals("status")){
                            new VariableManager().customeToast(btnSelect, "Case Closed", 1);
                        }
                        else{
                            new VariableManager().customeToast(btnSelect, "Case Not Closed", 0);
                        }
                    }
                    catch(Exception ex){
                        new VariableManager().customeToast(btnSelect, "Case Not Closed", 0);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    new VariableManager().customeToast(btnSelect, "Check Data Connection", 0);
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {

                    Map<String, String> parameters = new HashMap<String, String>();
                    parameters.put("caseid",String.valueOf(crimeData.getId()));
                    return parameters;
                }
            };
            request.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            queue.add(request);

            return null;
        }
    }
}
