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
import com.protectme.dao.Family;

import java.util.List;

/**
 * Created by IshanFx on 3/20/2016.
 */
public class FamilyAdapter  extends ArrayAdapter<Family> {
    Context context;
    int layoutResourceId;
    List<Family> list= null;

    public FamilyAdapter(Context context, int layoutResourceId, List<Family> list) {
        super(context, layoutResourceId, list);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.list = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Family family = list.get(position);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.listview_family,null);

        TextView txtName = (TextView) view.findViewById(R.id.txtlistFamilyName);
        txtName.setText(family.getName());
        TextView txtNumber = (TextView) view.findViewById(R.id.txtlistFamilyNumber);
        txtNumber.setText(family.getNumber());
        return view;
    }

}
