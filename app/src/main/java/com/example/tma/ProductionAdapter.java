package com.example.tma;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ProductionAdapter extends BaseAdapter {

    ArrayList<Production> production;
    Context c;
    TextView meter,taka,date;

    public ProductionAdapter(ArrayList<Production> production, Context c) {
        this.production = production;
        this.c = c;
    }
    @Override
    public int getCount() {
        return production.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView= LayoutInflater.from(c).inflate(R.layout.production_detail,parent,false);

        meter=convertView.findViewById(R.id.TvProdMeter);
        taka=convertView.findViewById(R.id.TvProdTaka);
        date=convertView.findViewById(R.id.TvProdDate);

        taka.setText("Total Taka: "+production.get(position).getTaka());
        meter.setText("Total Meter: "+production.get(position).getMeter());
        date.setText("Date: "+production.get(position).getDate());

        return convertView;
    }
}
