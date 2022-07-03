package com.example.tma;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AddWorkerProduction extends AppCompatActivity {

    EditText meter;
    Button add;
    FirebaseAuth fAuth;
    ProgressBar progressBar;
    Spinner workers;
    double dailyWages;
    SharedPreferences sp;
    FirebaseDatabase rootNode;
    ArrayList<String> w=new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_worker_production);
        sp=getSharedPreferences("tma",MODE_PRIVATE);
        meter=findViewById(R.id.TbWProdMeter);
        add=findViewById(R.id.BtnAddWP);
        workers=findViewById(R.id.SpinnerWorkers);
        fAuth=FirebaseAuth.getInstance();
        rootNode = FirebaseDatabase.getInstance();
        DatabaseReference reference = rootNode.getReference("workers");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    String obj=data.getValue().toString();
                    try {
                        JSONObject object=new JSONObject(obj);
                        if(object.getString("sup").equalsIgnoreCase(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                        {
                            w.add(object.getString("fname")+" "+object.getString("lname"));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                ArrayAdapter adp=new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1,w);
                workers.setAdapter(adp);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        progressBar=findViewById(R.id.progressBar);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strMeter=meter.getText().toString().trim();
                if(strMeter.isEmpty())
                {
                    meter.setError("Please provide Meter");
                    meter.requestFocus();
                }
                else if(Integer.parseInt(strMeter)<=0)
                {
                    meter.setError("Please provide valid Meter");
                    meter.requestFocus();
                }
                else
                {
                    progressBar.setVisibility(View.VISIBLE);
                    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
                    Date date = new Date();
                    WorkerProduction r=new WorkerProduction(fAuth.getCurrentUser().getUid(),workers.getSelectedItem().toString(),strMeter,formatter.format(date));
                    FirebaseDatabase.getInstance().getReference("worker production").child(formatter.format(date)).child(workers.getSelectedItem().toString()).setValue(r).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            reference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot data : snapshot.getChildren()) {
                                        String obj=data.getValue().toString();
                                        try {
                                            JSONObject object=new JSONObject(obj);
                                            if(object.getString("sup").equalsIgnoreCase(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                                            {
                                                dailyWages=Double.parseDouble(object.getString("dailyWages"));
                                            }

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    DatabaseReference reference = rootNode.getReference("workers");
                                    reference.child(workers.getSelectedItem().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                                            Double Tsalary=Double.parseDouble(strMeter)*dailyWages;
                                            snapshot.getRef().child("totalSalary").setValue(String.valueOf(Tsalary));
                                            Toast.makeText(getApplicationContext(), "Worker's production added successfully!", Toast.LENGTH_SHORT).show();
                                            progressBar.setVisibility(View.GONE);
                                            startActivity(new Intent(getApplicationContext(),SupervisorWorker.class));
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                        }
                                    });
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });
                        }
                    });
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(sp.getString("utype","").equals("supervisor"))
        {
            getMenuInflater().inflate(R.menu.menusupervisor,menu);
        }
        if(sp.getString("utype","").equals("admin"))
        {
            getMenuInflater().inflate(R.menu.menuadmin,menu);
        }
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        if(id==R.id.mnARawMaterial)
        {
            Intent i=new Intent(getApplicationContext(),SupervisorRawMaterial.class);
            startActivity(i);
        }
        if(id==R.id.mnAWorkers)
        {
            Intent i=new Intent(getApplicationContext(),SupervisorWorker.class);
            startActivity(i);
        }
        if(id==R.id.mnAHome)
        {
            Intent i=new Intent(getApplicationContext(),AdminHome.class);
            startActivity(i);
        }
        if(id==R.id.mnAProduction)
        {
            Intent i=new Intent(getApplicationContext(),SupervisorProduction.class);
            startActivity(i);
        }
        if(id==R.id.mnASupervisors)
        {
            Intent i=new Intent(getApplicationContext(),AdminSupervisor.class);
            startActivity(i);
        }
        if(id==R.id.mnALogout)
        {
            SharedPreferences.Editor edit=sp.edit();
            edit.clear();
            edit.commit();
            Intent i=new Intent(getApplicationContext(),MainActivity.class);
            startActivity(i);
        }
        if(id==R.id.mnSProfile)
        {
            Intent i=new Intent(getApplicationContext(),Profile.class);
            startActivity(i);
        }
        if(id==R.id.mnSLogout)
        {
            SharedPreferences sp=getSharedPreferences("tma",MODE_PRIVATE);
            SharedPreferences.Editor edit=sp.edit();
            edit.clear();
            edit.commit();
            Intent i=new Intent(getApplicationContext(),MainActivity.class);
            startActivity(i);
        }
        if(id==R.id.mnSWoker)
        {
            Intent i=new Intent(getApplicationContext(), SupervisorWorker.class);
            startActivity(i);
        }
        if(id==R.id.mnSHome)
        {
            Intent i=new Intent(getApplicationContext(), SupervisorHome.class);
            startActivity(i);
        }
        if(id==R.id.mnSProduction)
        {
            Intent i=new Intent(getApplicationContext(), SupervisorProduction.class);
            startActivity(i);
        }
        if(id==R.id.mnSRawM)
        {
            Intent i=new Intent(getApplicationContext(), SupervisorRawMaterial.class);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }
}