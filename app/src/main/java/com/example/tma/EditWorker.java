package com.example.tma;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class EditWorker extends AppCompatActivity {

    EditText fname,lname,daily,contact;
    SharedPreferences sp;
    Button save;
    FirebaseAuth fAuth;
    FirebaseDatabase fDb;
    FirebaseUser user;
    ProgressBar progressBar;
    StorageReference storageReference;
    String userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_worker);
        progressBar=findViewById(R.id.progressBar);
        daily=findViewById(R.id.TbEditWDW);
        contact=findViewById(R.id.TbEditWContact);
        save=findViewById(R.id.BtnWorkerSave);
        fAuth=FirebaseAuth.getInstance();
        fDb=FirebaseDatabase.getInstance();
        sp=getSharedPreferences("tma",MODE_PRIVATE);
        DatabaseReference reference = fDb.getReference("workers");
        String[] name=getIntent().getStringExtra("Name").toString().split(" ");
        String strfname=name[0];
        String strlname=name[1];
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressBar.setVisibility(View.VISIBLE);
                String user=getSharedPreferences("tma",MODE_PRIVATE).getString("username","");
                for (DataSnapshot data : snapshot.getChildren()) {
                    String obj=data.getValue().toString();
                    try {
                        JSONObject object=new JSONObject(obj);
                        if(object.getString("fname").equalsIgnoreCase(strfname) && object.getString("lname").equalsIgnoreCase(strlname))
                        {
                            daily.setText(object.getString("dailyWages").toString());
                            contact.setText(object.getString("contactNo").toString());
                            progressBar.setVisibility(View.GONE);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strDW=daily.getText().toString();
                String strContactNo=contact.getText().toString().trim();
                if(strDW.isEmpty())
                {
                    daily.setError("Please provide Daily Wages");
                    daily.requestFocus();
                }
                else if(strContactNo.isEmpty())
                {
                    contact.setError("Please provide Phone No");
                    contact.requestFocus();
                }
                else if(!Pattern.matches("^[6-9]\\d{9}$", contact.getText().toString().trim()))
                {
                    contact.setError("Please provide valid Phone No");
                    contact.requestFocus();
                }else {
                    progressBar.setVisibility(View.VISIBLE);
                    DatabaseReference reference = fDb.getReference("workers");
                    reference.child(strfname+" "+strlname).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            snapshot.getRef().child("dailyWages").setValue(daily.getText().toString());
                            snapshot.getRef().child("contactNo").setValue(contact.getText().toString());
                            Toast.makeText(getApplicationContext(),"Worker details Updated!", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                            startActivity(new Intent(getApplicationContext(),SupervisorWorker.class));
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(getApplicationContext(), "Worker details not updated! Try again!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(sp.getString("utype","").equals("admin")) {
            getMenuInflater().inflate(R.menu.menuadmin, menu);
        }
        if(sp.getString("utype","").equals("supervisor")) {
            getMenuInflater().inflate(R.menu.menusupervisor, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (sp.getString("utype", "").equals("admin")) {
            if(id==R.id.mnAWorkers)
            {
                Intent i=new Intent(getApplicationContext(),AdminWorker.class);
                startActivity(i);
            }
            if(id==R.id.mnAHome)
            {
                Intent i=new Intent(getApplicationContext(),AdminHome.class);
                startActivity(i);
            }
            if(id==R.id.mnAProduction)
            {
                Intent i=new Intent(getApplicationContext(),AdminProduction.class);
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
        }
        if (sp.getString("utype", "").equals("supervisor")) {
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
        }
        return super.onOptionsItemSelected(item);
    }
}