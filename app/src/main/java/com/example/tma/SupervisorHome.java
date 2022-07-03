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

public class SupervisorHome extends AppCompatActivity {
    SharedPreferences sp;
    Button wCount,rawCount,prodCount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supervisor_home);
        wCount=findViewById(R.id.TvWorkerCount);
        rawCount=findViewById(R.id.TvRawCount);
        prodCount=findViewById(R.id.TvProdCount);
        wCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getApplicationContext(),SupervisorWorker.class);
                startActivity(i);
            }
        });
        prodCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getApplicationContext(),SupervisorProduction.class);
                startActivity(i);
            }
        });
        rawCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getApplicationContext(),SupervisorRawMaterial.class);
                startActivity(i);
            }
        });
        FirebaseDatabase rootNode = FirebaseDatabase.getInstance();
        DatabaseReference reference = rootNode.getReference("workers");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count=0;
                for (DataSnapshot data : snapshot.getChildren()) {
                    String obj=data.getValue().toString();
                    try {
                        JSONObject object=new JSONObject(obj);
                        if(object.getString("sup").equalsIgnoreCase(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                        {
                            count++;
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                wCount.setText(count+"                  Workers");
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
                        if(object.getString("sup").equalsIgnoreCase(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                        {
                            count++;
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                rawCount.setText(count+"                            Raw Material");
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
                        if(object.getString("sup").equalsIgnoreCase(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                        {
                            count++;
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                prodCount.setText(count+"                            Production");
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        sp=getSharedPreferences("tma",MODE_PRIVATE);
        if(!sp.contains("username"))
        {
            if(!sp.getString("utype","").equals("supervisor"))
            {
                Intent i=new Intent(getApplicationContext(),MainActivity.class);
                startActivity(i);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menusupervisor,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        if(id==R.id.mnSProfile)
        {
            Intent i=new Intent(getApplicationContext(),Profile.class);
            startActivity(i);
        }
        if(id==R.id.mnSLogout)
        {
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

    @Override
    public void onBackPressed() {
    }
}