package com.example.tma;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AdminProduction extends AppCompatActivity {

    ListView lvProd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_production);
        lvProd=findViewById(R.id.LvAdminProd);
        FirebaseDatabase rootNode = FirebaseDatabase.getInstance();
        DatabaseReference reference = rootNode.getReference("production");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Production> records=new ArrayList<Production>();
                for (DataSnapshot data : snapshot.getChildren()) {
                    String obj=data.getValue().toString();
                    try {
                        JSONObject object=new JSONObject(obj);
                        records.add(new Production(FirebaseAuth.getInstance().getCurrentUser().getUid(),object.getString("meter"),object.getString("taka"),object.getString("date")));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                ProductionAdapter adp=new ProductionAdapter(records,getApplicationContext());
                lvProd.setAdapter(adp);
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
            SharedPreferences sp=getSharedPreferences("tma",MODE_PRIVATE);
            SharedPreferences.Editor edit=sp.edit();
            edit.clear();
            edit.commit();
            Intent i=new Intent(getApplicationContext(),MainActivity.class);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }
}