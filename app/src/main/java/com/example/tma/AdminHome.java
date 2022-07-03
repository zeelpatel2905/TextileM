package com.example.tma;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

public class AdminHome extends AppCompatActivity {

    SharedPreferences sp;
    Button supCount,workerCount,prodCount,rawCount;
    FirebaseDatabase rootNode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);
        supCount=findViewById(R.id.TvSupCount);
        workerCount=findViewById(R.id.TvAWorkerCount);
        prodCount=findViewById(R.id.TvARawCount);
        rawCount=findViewById(R.id.TvAProdCount);
        rootNode = FirebaseDatabase.getInstance();
        sp=getSharedPreferences("tma",MODE_PRIVATE);
        if(!sp.contains("username"))
        {
            if(!sp.getString("utype","").equals("admin"))
            {
                Intent i=new Intent(AdminHome.this,MainActivity.class);
                startActivity(i);
            }
        }
        controlIns();
        eventHandler();
    }

    protected void controlIns()
    {
    }

    protected void eventHandler()
    {
        supCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getApplicationContext(),AdminSupervisor.class);
                startActivity(i);
            }
        });
        workerCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getApplicationContext(),SupervisorWorker.class);
                startActivity(i);
            }
        });
        prodCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getApplicationContext(),SupervisorRawMaterial.class);
                startActivity(i);
            }
        });
        rawCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getApplicationContext(),SupervisorProduction.class);
                startActivity(i);
            }
        });
        DatabaseReference reference = rootNode.getReference("workers");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count=0;
                for (DataSnapshot data : snapshot.getChildren()) {
                    String obj=data.getValue().toString();
                    try {
                        JSONObject object=new JSONObject(obj);
                            count++;

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                workerCount.setText(count+"                  Workers");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        reference = rootNode.getReference("users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count=0;
                for (DataSnapshot data : snapshot.getChildren()) {
                    String obj=data.getValue().toString();
                    try {
                        JSONObject object=new JSONObject(obj);
                        count++;

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                supCount.setText(count-1+"                  SUPERVISORS");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        reference = rootNode.getReference("production");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count=0;
                for (DataSnapshot data : snapshot.getChildren()) {
                    String obj=data.getValue().toString();
                    try {
                        JSONObject object=new JSONObject(obj);
                        count++;

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                rawCount.setText(count+"                  PRODUCTION");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        reference = rootNode.getReference("raw material");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count=0;
                for (DataSnapshot data : snapshot.getChildren()) {
                    String obj=data.getValue().toString();
                    try {
                        JSONObject object=new JSONObject(obj);
                        count++;

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                prodCount.setText(count+"                            RAW MATERIALS");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menuadmin,menu);
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
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
    }
}