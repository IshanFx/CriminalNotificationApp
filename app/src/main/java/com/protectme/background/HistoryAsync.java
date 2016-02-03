package com.protectme.background;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * About : Manage the network part to grab data from parse database
 * Created by IshanFx on 1/27/2016.
 */
public class HistoryAsync extends AsyncTask<Void,Void,List<ParseObject>> {
    ListView historyList;
    Context context;
    ProgressBar progress;
    int len=0;
    Button btn;
    List<ParseObject> dataList = null;

    public HistoryAsync(Context context,ListView histroryList,Button btn){
        this.context = context;
        this.historyList = histroryList;
        this.btn = btn;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected List<ParseObject> doInBackground(Void... params) {

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Case");
        query.whereEqualTo("userid", 1);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> scoreList, ParseException e) {
                if (e == null) {
                    dataList = scoreList;
                    Log.d("score", "Retrieved " + scoreList.size() + " scores");
                } else {
                    len = -1;
                    Log.d("score", "Error: " + e.getMessage());
                }
            }
        });
        return dataList;
    }

    @Override
    protected void onPostExecute(List<ParseObject> list) {
        ArrayList<String> criminalList = new ArrayList();
        criminalList.add("Case size" + list.size());

        ArrayAdapter<String> adapter = new ArrayAdapter(context,android.R.layout.simple_list_item_1,criminalList);
        historyList.setAdapter(adapter);
        //btn.setText(aVoid.toString());
    }


}
