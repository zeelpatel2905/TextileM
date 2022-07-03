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
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

public class AddWorker extends AppCompatActivity {

    EditText fname,lname,contact,dailyWages;
    Button add;
    Spinner workerType;
    FirebaseAuth fAuth;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_worker);
        fAuth=FirebaseAuth.getInstance();
        progressBar=findViewById(R.id.progressBar);
        fname=findViewById(R.id.TbWorkerFName);
        lname=findViewById(R.id.TbWorkerLName);
        contact=findViewById(R.id.TbWorkerContact);
        dailyWages=findViewById(R.id.TbWorkerDailyWages);
        add=findViewById(R.id.BtnRegWorker);
        workerType=findViewById(R.id.SpWorkerType);
        String[] wType=new String[]{"Worker Type","TFO","Bobin","Palti","PasarvaWala"};
        ArrayAdapter adp=new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1,wType);
        workerType.setAdapter(adp);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strFname=fname.getText().toString().trim();
                String strLname=lname.getText().toString().trim();
                String strContact=contact.getText().toString().trim();
                String strdailyWages=dailyWages.getText().toString().trim();
                if(strFname.isEmpty())
                {
                    fname.setError("Please provide First Name");
                    fname.requestFocus();
                }
                else if(strLname.isEmpty())
                {
                    lname.setError("Please provide Last Name");
                    lname.requestFocus();
                }
                else if(!Pattern.matches("^[a-zA-Z]+$", strFname))
                {
                    fname.setError("First Name must be alphabetic");
                    fname.requestFocus();
                }
                else if(!Pattern.matches("^[a-zA-Z]+$", strLname))
                {
                    lname.setError("Last Name must be alphabetic");
                    lname.requestFocus();
                }
                else if(strContact.isEmpty())
                {
                    contact.setError("Please provide Contact No");
                    contact.requestFocus();
                }
                else if(!Pattern.matches("^[6-9]\\d{9}$", strContact))
                {
                    contact.setError("Please provide valid Phone No");
                    contact.requestFocus();
                }
                else if(workerType.getSelectedItem().toString().equals("Worker Type"))
                {
                    ((TextView)workerType.getSelectedView()).setError("Please provide Worker Type");
                }
                else if(strdailyWages.isEmpty())
                {
                    dailyWages.setError("Please provide Daily Wages");
                    dailyWages.requestFocus();
                }
                else if(!Pattern.matches("^[6-9]\\d{9}$", strContact))
                {
                    contact.setError("Please provide valid Phone No");
                    contact.requestFocus();
                }
                else
                {
                    progressBar.setVisibility(View.VISIBLE);
                    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
                    Date date = new Date();
                    String finaDate=formatter.format(date);
                    Worker w=new Worker(fAuth.getCurrentUser().getUid(),strFname,strLname,strContact,workerType.getSelectedItem().toString(),strdailyWages,0,0,finaDate);
                    FirebaseDatabase.getInstance().getReference("workers").child(strFname+" "+strLname).setValue(w).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(getApplicationContext(), "Worker added successfully!", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                            Intent i=new Intent(getApplicationContext(),SupervisorWorker.class);
                            startActivity(i);
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