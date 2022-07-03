package com.example.tma;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RawMaterialAdapter extends BaseAdapter {

    ArrayList<RawMaterial> materials;
    Context c;
    TextView company,quantity,rate,totalPrice,type;

    public RawMaterialAdapter(ArrayList<RawMaterial> materials, Context c) {
        this.materials = materials;
        this.c = c;
    }

    @Override
    public int getCount() {
        return materials.size();
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
        convertView= LayoutInflater.from(c).inflate(R.layout.raw_material_details,parent,false);

        company=convertView.findViewById(R.id.TvRawMCompany);
        quantity=convertView.findViewById(R.id.TvRawMQuantity);
        rate=convertView.findViewById(R.id.TvRawMRate);
        type=convertView.findViewById(R.id.TvRawMType);
        totalPrice=convertView.findViewById(R.id.TvRawMPrice);

        company.setText("Company Name: "+materials.get(position).getCompanyName());
        quantity.setText("Quantity per tons: "+materials.get(position).getQuantity());
        type.setText("Type: "+materials.get(position).getType());
        rate.setText("Rate per kg: "+materials.get(position).getRate());
        totalPrice.setText("Total Price: "+materials.get(position).getPrice());

        return convertView;
    }
}
