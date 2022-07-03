package com.example.tma;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AdminSupervisor extends AppCompatActivity {

    ListView lvSups;
    ArrayList<Supervisor> records=new ArrayList<Supervisor>();
    FirebaseAuth fAuth;
    Button supDate,addSup;
    SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_supervisor);
        fAuth=FirebaseAuth.getInstance();
        addSup=findViewById(R.id.BtnAdminAddSup);
        addSup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getApplicationContext(),Register.class);
                startActivity(i);
            }
        });
        supDate=findViewById(R.id.BtnAdminSupDateWise);
        sp=getSharedPreferences("tma",MODE_PRIVATE);
        lvSups=findViewById(R.id.LvAdminSupervisor);
        registerForContextMenu(lvSups);
        supDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout lila1= new LinearLayout(v.getContext());
                lila1.setOrientation(LinearLayout.VERTICAL);
                final EditText startDate = new EditText(v.getContext());
                final EditText endDate = new EditText(v.getContext());
                startDate.setHint("From Date (dd-MM-yyyy)");
                endDate.setHint("To Date (dd-MM-yyyy)");
                lila1.addView(startDate);
                lila1.addView(endDate);

                final AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext());
                passwordResetDialog.setTitle("Join Date-Wise");
                passwordResetDialog.setMessage("Enter From Date and To Date");
                passwordResetDialog.setView(lila1);

                passwordResetDialog.setPositiveButton("Go", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences.Editor edit = sp.edit();
                        edit.putString("sDate",startDate.getText().toString());
                        edit.putString("eDate", endDate.getText().toString());
                        edit.commit();
                        finish();
                        startActivity(getIntent());
                    }
                });
                passwordResetDialog.create().show();
            }
        });
        FirebaseDatabase rootNode = FirebaseDatabase.getInstance();
        DatabaseReference reference = rootNode.getReference("users");
        if(sp.contains("sDate") && sp.contains("eDate"))
        {
            reference.orderByChild("date").startAt(sp.getString("sDate"," ")).endAt(sp.getString("eDate"," ")).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot data : snapshot.getChildren()) {
                        String obj = data.getValue().toString();
                        try {
                            JSONObject object = new JSONObject(obj);
                            records.add(new Supervisor(object.getString("fname"), object.getString("lname"), object.getString("age"), object.getString("contactNo"), object.getString("emailID"), object.getString("date")));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    SupervisorDetailsAdapter adp=new SupervisorDetailsAdapter(records,getApplicationContext());
                    lvSups.setAdapter(adp);
                    SharedPreferences.Editor edit = sp.edit();
                    edit.remove("sDate");
                    edit.remove("eDate");
                    edit.commit();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        else {
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot data : snapshot.getChildren()) {
                        String obj = data.getValue().toString();
                        try {
                            JSONObject object = new JSONObject(obj);
                            records.add(new Supervisor(object.getString("fname"), object.getString("lname"), object.getString("age"), object.getString("contactNo"), object.getString("emailID"), object.getString("date")));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    SupervisorDetailsAdapter adp = new SupervisorDetailsAdapter(records, getApplicationContext());
                    lvSups.setAdapter(adp);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
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