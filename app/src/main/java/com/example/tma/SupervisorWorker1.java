package com.example.tma;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;

public class SupervisorWorker1 extends AppCompatActivity {

    RecyclerView rv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supervisor_worker1);
        rv=findViewById(R.id.RvWorkers);
        setRecyclerView();
    }

    private ArrayList<String> getAbsentWorkers(){
        ArrayList<String> arrayList=new ArrayList<>();

        return arrayList;
    }
    private void setRecyclerView() {
    }
}