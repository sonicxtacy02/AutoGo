package com.soniquesoftwaredesign.sx14r.autogo;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
public class AutoGoAlertsCustomList extends ArrayAdapter<String>{
    private final Activity context;
    private final String[] type;
    private final String[] date;
    private final Integer[] imageId;
    public AutoGoAlertsCustomList(Activity context, String[] date,
                      String[] type, Integer[] imageId) {
        super(context, R.layout.ag_alerts_table, type);
        this.context = context;
        this.date = date;
        this.type = type;
        this.imageId = imageId;
    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.ag_alerts_table, null, true);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.txt);
        TextView subText = (TextView) rowView.findViewById(R.id.subtxt);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.img);
        txtTitle.setText(type[position]);
        subText.setText(date[position]);
        imageView.setImageResource(imageId[position]);
        return rowView;
    }
}