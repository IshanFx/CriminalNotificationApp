package com.protectme;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity {

    ListView historyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        historyList = (ListView) findViewById(R.id.historylistView);
        getHistoryData();
    }
    public void getHistoryData(){
        ArrayList<String> criminalList = new ArrayList();
        criminalList.add("Case 1 \nLocation :79,000,6 \nTime:12:30 Data:10/1/2016");
        criminalList.add("Case 2 \nLocation :79,000,6 \nTime:12:30 Data:10/1/2016");
        criminalList.add("Case 3 \nLocation :79,000,6 \nTime:12:30 Data:10/1/2016");
        criminalList.add("Case 4 \nLocation :79,000,6 \nTime:12:30 Data:10/1/2016");
        criminalList.add("Case 5 \nLocation :79,000,6 \nTime:12:30 Data:10/1/2016");
        ArrayAdapter<String> adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,criminalList);
        historyList.setAdapter(adapter);
    }
}
