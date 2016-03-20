package com.protectme;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.protectme.dao.Crime;

import java.util.List;

/**
 * Created by IshanFx on 2/9/2016.
 */
public class CrimeAdapter extends ArrayAdapter<Crime> {

    Context context;
    int layoutResourceId;
    List<Crime> list= null;

    public CrimeAdapter(Context context, int layoutResourceId, List<Crime> list) {
        super(context, layoutResourceId, list);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.list = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Crime crime = list.get(position);

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.listview_item,null);

           // TextView txt = (TextView) view.findViewById(R.id.txtTitle);
          //  txt.setText(crime.getId());
            ImageView img = (ImageView) view.findViewById(R.id.imgIcon);
            int res = context.getResources().getIdentifier("icon","drawable",context.getPackageName());
            img.setImageResource(res);

        return view;
    }

}
