package com.protectme;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;

import com.protectme.dao.Crime;

public class HistoryDetailsActivity extends AppCompatActivity {
    TextView caseId,caseTime,caseDate,caseStatus,caseType,caseLatitude;

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
        Crime crimeData= (Crime) getIntent().getSerializableExtra("crimeobj");
        Log.d("Hisde", crimeData.toString());
        assignData(crimeData);
    }

    public void assignData(Crime crime){
        try {
            String dateTimeCheck = new String(crime.getDate().toString());
       String dateTime[] = dateTimeCheck.split("\\s+");
        /* switch (crime.getStatus().toString()) {
            case "P":
                crime.setStatus("Pending");
                break;
            case "A":
                crime.setStatus("Active");
                break;
            case "C":
                crime.setStatus("Cancelled");
                break;
            case "S":
                crime.setStatus("Solved");
                break;
            default:
                crime.setStatus("Pending");
        }*/
        switch (crime.getType().toString()) {
                    case "E":
                        crime.setType("Evidence");
                        break;
                    case "R":
                        crime.setType("Static");
                        break;
                    case "K":
                        crime.setType("Track");
                        break;
                }
            caseId.setText(String.valueOf(crime.getId()));
            caseType.setText(crime.getType().toString());
            //caseStatus.setText(crime.getStatus().toString());
            caseDate.setText(dateTime[0].toString());
            caseTime.setText(dateTime[1].toString());
            caseLatitude.setText(crime.getLatitude());
        }
        catch (Exception ex){
            Log.d("Hisde",ex.toString());
        }
    }

}
