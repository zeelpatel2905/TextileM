package com.example.tma;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

public class WorkerDetailsAdapter extends BaseAdapter {

    ArrayList<Worker> workers;
    Context c;
    TextView name,phone,type,attendance,date,salary,dw;
    CheckBox present;

    public WorkerDetailsAdapter(ArrayList<Worker> workers, Context c) {
        this.workers = workers;
        this.c = c;
    }

    @Override
    public int getCount() {
        return workers.size();
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
        convertView= LayoutInflater.from(c).inflate(R.layout.worker_detail,parent,false);

        name=convertView.findViewById(R.id.TvWorkerName);
        phone=convertView.findViewById(R.id.TvWorkerPhoneNo);
        type=convertView.findViewById(R.id.TvWorkerType);
        attendance=convertView.findViewById(R.id.TvWorkerTotalAttendance);
        date=convertView.findViewById(R.id.TvWorkerJoinDatee);
        salary=convertView.findViewById(R.id.TvWorkerTotalSalary);
        dw=convertView.findViewById(R.id.TvWorkerDW);

        name.setText("Name: "+workers.get(position).getFname()+" "+workers.get(position).getLname());
        phone.setText("Phone No: "+workers.get(position).getContactNo());
        type.setText("Type: "+workers.get(position).getType());
        salary.setText("Total Salary: "+workers.get(position).getTotalSalary());
        dw.setText("Daily Wages: "+workers.get(position).getDailyWages());

        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        Date Mydate1 = new Date();
        String finaDate=formatter.format(Mydate1);

        Date d1 = null;
        Date d2 = null;

        try {
            d1=formatter.parse(workers.get(position).getDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {
            d2=formatter.parse(finaDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long diff = d2.getTime() - d1.getTime();
        long diffDays = diff / (24 * 60 * 60 * 1000);
        attendance.setText("Total Attendance: "+String.valueOf(workers.get(position).getTotalAttendance())+"/"+(diffDays+1));
        date.setText("Join Date: "+workers.get(position).getDate());
        return convertView;
    }
}
