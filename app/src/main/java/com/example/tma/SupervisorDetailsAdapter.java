package com.example.tma;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SupervisorDetailsAdapter extends BaseAdapter {

    ArrayList<Supervisor> sups;
    Context c;
    TextView name,phone,date,email,age;

    public SupervisorDetailsAdapter(ArrayList<Supervisor> sups, Context c) {
        this.sups = sups;
        this.c = c;
    }

    @Override
    public int getCount() {
        return sups.size();
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
        convertView= LayoutInflater.from(c).inflate(R.layout.supervisor_detail,parent,false);

        name=convertView.findViewById(R.id.TvSupName);
        phone=convertView.findViewById(R.id.TvSupPhone);
        date=convertView.findViewById(R.id.TvSupJoinDate);
        age=convertView.findViewById(R.id.TvSupAge);
        email=convertView.findViewById(R.id.TvSupEmail);

        name.setText("Name: "+sups.get(position).getFname()+" "+sups.get(position).getLname());
        phone.setText("Phone No: "+sups.get(position).getContactNo());
        date.setText("Join Date: "+sups.get(position).getDate());
        age.setText("Age: "+sups.get(position).getAge());
        email.setText("Email ID: "+sups.get(position).getEmailID());
        return convertView;
    }
}
