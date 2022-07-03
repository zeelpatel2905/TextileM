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
import android.widget.CheckBox;
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

public class SupervisorRawMaterial extends AppCompatActivity {

    Button addRawMaterial,qrp;
    RawMaterial r;
    ListView lvMaterial;
    SharedPreferences sp;
    ArrayList<RawMaterial> records=new ArrayList<RawMaterial>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supervisor_raw_material);
        lvMaterial=findViewById(R.id.LvRawMaterialRecord);
        qrp=findViewById(R.id.BtnSupRQRP);
        sp=getSharedPreferences("tma",MODE_PRIVATE);
        FirebaseDatabase rootNode = FirebaseDatabase.getInstance();
        DatabaseReference reference = rootNode.getReference("raw material");
        qrp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout lila1= new LinearLayout(v.getContext());
                lila1.setOrientation(LinearLayout.VERTICAL);
                final EditText startDate = new EditText(v.getContext());
                final EditText endDate = new EditText(v.getContext());
                final CheckBox q = new CheckBox(v.getContext());
                final CheckBox r = new CheckBox(v.getContext());
                final CheckBox p = new CheckBox(v.getContext());
                q.setText("Quantity");
                r.setText("Rate");
                p.setText("Price");
                startDate.setHint("From");
                endDate.setHint("To");
                lila1.addView(q);
                lila1.addView(r);
                lila1.addView(p);
                lila1.addView(startDate);
                lila1.addView(endDate);

                final AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext());
                passwordResetDialog.setTitle("Range-Wise");
                passwordResetDialog.setMessage("Enter From and To");
                passwordResetDialog.setView(lila1);

                passwordResetDialog.setPositiveButton("Go", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences.Editor edit = sp.edit();
                        edit.putString("sDate", startDate.getText().toString());
                        edit.putString("eDate", endDate.getText().toString());
                        if(q.isChecked())
                        {
                            edit.putString("Which", "quantity");
                        }
                        else if(r.isChecked())
                        {
                            edit.putString("Which", "rate");
                        }
                        else if(p.isChecked())
                        {
                            edit.putString("Which", "price");
                            edit.putString("sDate", startDate.getText().toString()+".0");
                            edit.putString("eDate", endDate.getText().toString()+".0");
                        }
                        edit.commit();
                        finish();
                        startActivity(getIntent());
                    }
                });
                passwordResetDialog.create().show();
            }
        });
        if(sp.contains("sDate") && sp.contains("eDate") && sp.contains("Which"))
        {
            if(sp.getString("utype","").equals("supervisor")) {
                reference.orderByChild(sp.getString("Which", "")).startAt(sp.getString("sDate", " ")).endAt(sp.getString("eDate", " ")).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot data : snapshot.getChildren()) {
                            String obj = data.getValue().toString();
                            try {
                                JSONObject object = new JSONObject(obj);
                                if (object.getString("sup").equalsIgnoreCase(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                    records.add(new RawMaterial(FirebaseAuth.getInstance().getCurrentUser().getUid(), object.getString("companyName"), object.getString("rate"), object.getString("quantity"), object.getString("type"), object.getString("price"), object.getString("date")));
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        RawMaterialAdapter adp = new RawMaterialAdapter(records, getApplicationContext());
                        lvMaterial.setAdapter(adp);
                        SharedPreferences.Editor edit = sp.edit();
                        edit.remove("sDate");
                        edit.remove("eDate");
                        edit.remove("Which");
                        edit.commit();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
            if(sp.getString("utype","").equals("admin")) {
                reference.orderByChild(sp.getString("Which", "")).startAt(sp.getString("sDate", " ")).endAt(sp.getString("eDate", " ")).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot data : snapshot.getChildren()) {
                            String obj = data.getValue().toString();
                            try {
                                JSONObject object = new JSONObject(obj);
                                    records.add(new RawMaterial("0", object.getString("companyName"), object.getString("rate"), object.getString("quantity"), object.getString("type"), object.getString("price"), object.getString("date")));

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        RawMaterialAdapter adp = new RawMaterialAdapter(records, getApplicationContext());
                        lvMaterial.setAdapter(adp);
                        SharedPreferences.Editor edit = sp.edit();
                        edit.remove("sDate");
                        edit.remove("eDate");
                        edit.remove("Which");
                        edit.commit();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
        }
        else {
            if(sp.getString("utype","").equals("supervisor")) {
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot data : snapshot.getChildren()) {
                            String obj = data.getValue().toString();
                            try {
                                JSONObject object = new JSONObject(obj);
                                if (object.getString("sup").equalsIgnoreCase(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                    records.add(new RawMaterial(FirebaseAuth.getInstance().getCurrentUser().getUid(), object.getString("companyName"), object.getString("rate"), object.getString("quantity"), object.getString("type"), object.getString("price"), object.getString("date")));
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        RawMaterialAdapter adp = new RawMaterialAdapter(records, getApplicationContext());
                        lvMaterial.setAdapter(adp);
                        SharedPreferences.Editor edit = sp.edit();
                        edit.remove("sDate");
                        edit.remove("eDate");
                        edit.remove("Which");
                        edit.commit();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
            if(sp.getString("utype","").equals("admin")) {
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot data : snapshot.getChildren()) {
                            String obj = data.getValue().toString();
                            try {
                                JSONObject object = new JSONObject(obj);
                                    records.add(new RawMaterial("0", object.getString("companyName"), object.getString("rate"), object.getString("quantity"), object.getString("type"), object.getString("price"), object.getString("date")));

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        RawMaterialAdapter adp = new RawMaterialAdapter(records, getApplicationContext());
                        lvMaterial.setAdapter(adp);
                        SharedPreferences.Editor edit = sp.edit();
                        edit.remove("sDate");
                        edit.remove("eDate");
                        edit.remove("Which");
                        edit.commit();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
        }
        addRawMaterial=findViewById(R.id.BtnAddRawMaterial);
        if(sp.getString("utype","").equals("admin"))
        {
            addRawMaterial.setVisibility(View.GONE);
        }
        addRawMaterial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()) {
                    Intent i = new Intent(getApplicationContext(), AddRawMaterial.class);
                    startActivity(i);
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Please verify your profile!", Toast.LENGTH_SHORT).show();
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