package com.protectme;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.protectme.dao.Family;
import com.protectme.database.RealMAdapter;

import java.util.List;

public class FamilyActivity extends AppCompatActivity {
    Context context;
    static RealMAdapter realMAdapter;
    private SwipeRefreshLayout swipeContainer;
    FamilyAdapter familyAdapter;
    ListView familyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        context = this;
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        realMAdapter = new RealMAdapter(getApplicationContext());
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater li = LayoutInflater.from(context);
                View dialogView = li.inflate(R.layout.familyprompt, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        context);
                // set title
                alertDialogBuilder.setTitle("Add Member");

                // set custom_dialog.xml to alertdialog builder
                alertDialogBuilder.setView(dialogView);
                final EditText txtName = (EditText) dialogView
                        .findViewById(R.id.txtfamilyname);
                final EditText txtNumber = (EditText) dialogView.findViewById(R.id.txtfamilynumber);

                // set dialog message
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("Add",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        Family family = new Family();
                                        family.setName(txtName.getText().toString());
                                        family.setNumber(txtNumber.getText().toString());
                                        realMAdapter.insertFamily(family);
                                    }
                                })
                        .setNegativeButton("Close",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        dialog.cancel();
                                    }
                                });
                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();
                // show it
                alertDialog.show();


            }
        });

        List<Family> list = realMAdapter.getFamily();

        // depHomeAdapter = new DepHomeAdapter(this, 1, list);
        familyAdapter = new FamilyAdapter(this, 1, list);
        familyList = (ListView) findViewById(android.R.id.list);
        familyList.setAdapter(familyAdapter);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.

            }
        });

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }



}
