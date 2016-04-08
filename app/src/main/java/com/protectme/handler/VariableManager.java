package com.protectme.handler;

import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.content.Context;
import com.protectme.R;

/**
 * Created by IshanFx on 3/29/2016.
 */
public class VariableManager {
    View staticLocation;

    public static final String EVIDENCE = "Evidence";
    //public static final String EVIDENCE = "Evidence";



    public void customeToast(View view,String msg, int status) {
        Snackbar snackbar = Snackbar.make(view, msg,
                Snackbar.LENGTH_LONG);
        View snackBarView = snackbar.getView();
        if (status == 1) {
            snackBarView.setBackgroundColor(Color.parseColor("#1abc9c"));
        } else
            snackBarView.setBackgroundColor(Color.parseColor("#E74C3C"));

        snackbar.show();

     /*   LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast_layout,
                (ViewGroup) findViewById(R.id.toast_layout_root));

        TextView text = (TextView) layout.findViewById(R.id.text);
        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.TOP, 0, 12);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();*/
    }
}
