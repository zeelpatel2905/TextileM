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
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
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
import java.util.Date;

public class AddProduction extends AppCompatActivity {

    EditText meter,taka;
    ProgressBar progressBar;
    Button addProd;
    FirebaseAuth fAuth;
    FirebaseDatabase rootNode;
    boolean marked;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_production);
        meter=findViewById(R.id.TbProdMeter);
        taka=findViewById(R.id.TbProdTaka);
        addProd=findViewById(R.id.BtnAddProd);
        progressBar=findViewById(R.id.progressBar);
        fAuth=FirebaseAuth.getInstance();
        rootNode = FirebaseDatabase.getInstance();
        addProd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strMeter=meter.getText().toString().trim();
                String strTaka=taka.getText().toString().trim();
                if(strMeter.isEmpty())
                {
                    meter.setError("Please provide Meter");
                    meter.requestFocus();
                }
                else if(strTaka.isEmpty())
                {
                    taka.setError("Please provide Taka");
                    taka.requestFocus();
                }
                else
                {
                    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
                    Date date = new Date();
                    String finaDate = formatter.format(date);

                    DatabaseReference reference = rootNode.getReference("production");
                    marked=false;
                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot data : snapshot.getChildren()) {
                                String obj=data.getValue().toString();
                                try {
                                    JSONObject object=new JSONObject(obj);
                                    if(object.getString("date").equalsIgnoreCase(finaDate))
                                    {
                                        marked = true;
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            if (!marked) {
                                progressBar.setVisibility(View.VISIBLE);
                                Production p=new Production(fAuth.getCurrentUser().getUid(),strMeter,strTaka,finaDate);
                                DatabaseReference reference = rootNode.getReference("production");
                                reference.child(finaDate).setValue(p).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(getApplicationContext(), "Production added successfully!", Toast.LENGTH_SHORT).show();
                                        //StyleableToast.makeText(getApplicationContext(),"Production added successfully!",R.style.mytoast);
                                        progressBar.setVisibility(View.GONE);
                                        Intent i=new Intent(getApplicationContext(),SupervisorProduction.class);
                                        startActivity(i);
                                    }
                                });
                            }
                            if(marked)
                            {
                                //StyleableToast.makeText(getApplicationContext(),"Production already added!",R.style.mytoast);
                                Toast.makeText(getApplicationContext(), "Production already added!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(),SupervisorProduction.class));
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                }
            }
        });
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