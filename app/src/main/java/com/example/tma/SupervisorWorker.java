package com.example.tma;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

public class SupervisorWorker extends AppCompatActivity {

    Button addWorker;
    Button date,salatt,prod;
    Button month;
    Worker w;
    ListView lvWorkers;
    ArrayList<Worker> records=new ArrayList<Worker>();
    FirebaseAuth fAuth;
    SharedPreferences sp;
    FirebaseDatabase fDb;
    boolean marked = false;
    String markedA = "false";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fAuth=FirebaseAuth.getInstance();
        fDb=FirebaseDatabase.getInstance();
        sp=getSharedPreferences("tma",MODE_PRIVATE);
        setContentView(R.layout.activity_sup_worker);
        //noti(getApplicationContext());
        prod=findViewById(R.id.BtnAddWorkerProd);
        if(sp.getString("utype","").equals("admin"))
        {
            prod.setVisibility(View.GONE);
        }
        prod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getApplicationContext(),AddWorkerProduction.class);
                startActivity(i);
            }
        });
        date=findViewById(R.id.BtnSupWDateWise);
        lvWorkers=findViewById(R.id.LvWorkersRecord);
        lvWorkers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                String fname = records.get(i).getFname();
                String lname = records.get(i).getLname();
                LinearLayout lila1 = new LinearLayout(view.getContext());
                lila1.setOrientation(LinearLayout.VERTICAL);
                final EditText meter = new EditText(view.getContext());
                meter.setHint("Total Today's Meter");
                lila1.addView(meter);

                final AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(view.getContext());
                passwordResetDialog.setTitle("Worker's Today's Production");
                passwordResetDialog.setMessage("Enter Total Meters");
                passwordResetDialog.setView(lila1);

                passwordResetDialog.setPositiveButton("Go", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String strMeter=meter.getText().toString().trim();
                        if(strMeter.isEmpty())
                        {
                            meter.setError("Please provide Total Meter");
                            meter.requestFocus();
                        }
                        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
                        Date date = new Date();
                        String finaDate = formatter.format(date);
                        DatabaseReference reference = fDb.getReference("worker production");
                        marked = false;
                        reference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot data : snapshot.getChildren()) {
                                    String obj = data.getValue().toString();
                                    try {
                                        JSONObject object = new JSONObject(obj);
                                        if (object.getString("date").equalsIgnoreCase(finaDate)) {
                                            marked = true;
                                        }

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                if (!marked) {
                                    WorkerProduction p = new WorkerProduction(fAuth.getCurrentUser().getUid(),fname+lname, strMeter, finaDate);
                                    fDb.getReference("worker production").child(finaDate).setValue(p).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(getApplicationContext(), "Worker's Production added successfully!", Toast.LENGTH_SHORT).show();
                                            //StyleableToast.makeText(getApplicationContext(),"Production added successfully!",R.style.mytoast);
                                            Intent i = new Intent(getApplicationContext(), SupervisorProduction.class);
                                            startActivity(i);
                                        }
                                    });
                                }
                                if (marked) {
                                    //StyleableToast.makeText(getApplicationContext(),"Production already added!",R.style.mytoast);
                                    Toast.makeText(getApplicationContext(), "Worker's Production already added!", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(getApplicationContext(), SupervisorProduction.class));
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });
                        passwordResetDialog.create().show();
                    }
                });
            }
        });
        month=findViewById(R.id.BtnSupWMonthWise);
        salatt=findViewById(R.id.BtnSupWSalAtt);
        addWorker=findViewById(R.id.BtnAddWorker);
            if(sp.getString("utype","").equals("admin"))
            {
                addWorker.setVisibility(View.GONE);
            }
        if(sp.getString("utype","").equals("supervisor"))
        {
            registerForContextMenu(lvWorkers);
        }

        FirebaseDatabase rootNode = FirebaseDatabase.getInstance();
        DatabaseReference reference = rootNode.getReference("workers");
        salatt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout lila1= new LinearLayout(v.getContext());
                lila1.setOrientation(LinearLayout.VERTICAL);
                final EditText startDate = new EditText(v.getContext());
                final EditText endDate = new EditText(v.getContext());
                final CheckBox salary = new CheckBox(v.getContext());
                final CheckBox att = new CheckBox(v.getContext());
                salary.setText("Salary");
                att.setText("Attendance");
                startDate.setHint("From");
                endDate.setHint("To");
                lila1.addView(salary);
                lila1.addView(att);
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
                        if(salary.isChecked())
                        {
                            edit.putString("Which", "totalSalary");
                        }
                        else if(att.isChecked())
                        {
                            edit.putString("Which", "totalAttendance");
                        }
                        edit.commit();
                        finish();
                        startActivity(getIntent());
                    }
                });
                passwordResetDialog.create().show();
            }
        });
        date.setOnClickListener(new View.OnClickListener() {
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
                        edit.putString("sDate", startDate.getText().toString());
                        edit.putString("eDate", endDate.getText().toString());
                        edit.commit();
                        finish();
                        startActivity(getIntent());
                    }
                });
                passwordResetDialog.create().show();
            }
        });
        month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout lila1= new LinearLayout(v.getContext());
                lila1.setOrientation(LinearLayout.VERTICAL);
                final EditText startDate = new EditText(v.getContext());
                startDate.setHint("Enter Month (1-12)");
                lila1.addView(startDate);

                final AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext());
                passwordResetDialog.setTitle("Month-Wise");
                passwordResetDialog.setMessage("Enter Month");
                passwordResetDialog.setView(lila1);

                passwordResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences.Editor edit = sp.edit();
                        edit.putString("Month", startDate.getText().toString());
                        edit.commit();
                        finish();
                        startActivity(getIntent());
                    }
                });
                passwordResetDialog.create().show();
            }
        });
        if(sp.contains("sDate") && sp.contains("eDate") && sp.contains("Which")==false)
        {
            if(sp.getString("utype","").equals("supervisor")) {
                reference.orderByChild("date").startAt(sp.getString("sDate", " ")).endAt(sp.getString("eDate", " ")).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot data : snapshot.getChildren()) {
                            String obj = data.getValue().toString();
                            try {
                                JSONObject object = new JSONObject(obj);
                                if (object.getString("sup").equalsIgnoreCase(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                    records.add(new Worker(FirebaseAuth.getInstance().getCurrentUser().getUid(), object.getString("fname"), object.getString("lname"), object.getString("contactNo"), object.getString("type"), object.getString("dailyWages"), Double.parseDouble(object.getString("totalAttendance")), Double.parseDouble(object.getString("totalSalary")), object.getString("date")));
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        WorkerDetailsAdapter adp = new WorkerDetailsAdapter(records, getApplicationContext());
                        lvWorkers.setAdapter(adp);
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
            if(sp.getString("utype","").equals("admin")) {
                reference.orderByChild("date").startAt(sp.getString("sDate", " ")).endAt(sp.getString("eDate", " ")).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot data : snapshot.getChildren()) {
                            String obj = data.getValue().toString();
                            try {
                                JSONObject object = new JSONObject(obj);
                                    records.add(new Worker("0", object.getString("fname"), object.getString("lname"), object.getString("contactNo"), object.getString("type"), object.getString("dailyWages"), Double.parseDouble(object.getString("totalAttendance")), Double.parseDouble(object.getString("totalSalary")), object.getString("date")));

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        WorkerDetailsAdapter adp = new WorkerDetailsAdapter(records, getApplicationContext());
                        lvWorkers.setAdapter(adp);
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
        }
        else if(sp.contains("Month"))
        {
            if(sp.getString("utype","").equals("supervisor")) {
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot data : snapshot.getChildren()) {
                            String obj = data.getValue().toString();
                            try {
                                JSONObject object = new JSONObject(obj);
                                String date[] = object.getString("date").split("-");
                                String month = date[1];
                                if (object.getString("sup").equalsIgnoreCase(FirebaseAuth.getInstance().getCurrentUser().getUid()) && month.equalsIgnoreCase(sp.getString("Month", " "))) {
                                    records.add(new Worker(FirebaseAuth.getInstance().getCurrentUser().getUid(), object.getString("fname"), object.getString("lname"), object.getString("contactNo"), object.getString("type"), object.getString("dailyWages"),Double.parseDouble(object.getString("totalAttendance")), Double.parseDouble(object.getString("totalSalary")), object.getString("date")));
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        WorkerDetailsAdapter adp = new WorkerDetailsAdapter(records, getApplicationContext());
                        lvWorkers.setAdapter(adp);
                        SharedPreferences.Editor edit = sp.edit();
                        edit.remove("Month");
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
                                String date[] = object.getString("date").split("-");
                                String month = date[1];
                                if (month.equalsIgnoreCase(sp.getString("Month", " "))) {
                                    records.add(new Worker("0", object.getString("fname"), object.getString("lname"), object.getString("contactNo"), object.getString("type"), object.getString("dailyWages"), Double.parseDouble(object.getString("totalAttendance")), Double.parseDouble(object.getString("totalSalary")), object.getString("date")));
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        WorkerDetailsAdapter adp = new WorkerDetailsAdapter(records, getApplicationContext());
                        lvWorkers.setAdapter(adp);
                        SharedPreferences.Editor edit = sp.edit();
                        edit.remove("Month");
                        edit.commit();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
        }
        else if(sp.contains("sDate") && sp.contains("eDate") && sp.contains("Which"))
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
                                    records.add(new Worker(FirebaseAuth.getInstance().getCurrentUser().getUid(), object.getString("fname"), object.getString("lname"), object.getString("contactNo"), object.getString("type"), object.getString("dailyWages"),Double.parseDouble(object.getString("totalAttendance")), Double.parseDouble(object.getString("totalSalary")), object.getString("date")));
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        WorkerDetailsAdapter adp = new WorkerDetailsAdapter(records, getApplicationContext());
                        lvWorkers.setAdapter(adp);
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
                                    records.add(new Worker("0", object.getString("fname"), object.getString("lname"), object.getString("contactNo"), object.getString("type"), object.getString("dailyWages"), Double.parseDouble(object.getString("totalAttendance")), Double.parseDouble(object.getString("totalSalary")), object.getString("date")));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        WorkerDetailsAdapter adp = new WorkerDetailsAdapter(records, getApplicationContext());
                        lvWorkers.setAdapter(adp);
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
                                    records.add(new Worker(FirebaseAuth.getInstance().getCurrentUser().getUid(), object.getString("fname"), object.getString("lname"), object.getString("contactNo"), object.getString("type"), object.getString("dailyWages"), Double.parseDouble(object.getString("totalAttendance")), Double.parseDouble(object.getString("totalSalary")), object.getString("date")));
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        WorkerDetailsAdapter adp = new WorkerDetailsAdapter(records, getApplicationContext());
                        lvWorkers.setAdapter(adp);
                        SharedPreferences.Editor edit = sp.edit();
                        edit.remove("sDate");
                        edit.remove("eDate");
                        edit.remove("Which");
                        edit.remove("Month");
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

                                    records.add(new Worker("0", object.getString("fname"), object.getString("lname"), object.getString("contactNo"), object.getString("type"), object.getString("dailyWages"), Double.parseDouble(object.getString("totalAttendance")), Double.parseDouble(object.getString("totalSalary")), object.getString("date")));

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        WorkerDetailsAdapter adp = new WorkerDetailsAdapter(records, getApplicationContext());
                        lvWorkers.setAdapter(adp);
                        SharedPreferences.Editor edit = sp.edit();
                        edit.remove("sDate");
                        edit.remove("eDate");
                        edit.remove("Which");
                        edit.remove("Month");
                        edit.commit();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
        }
        addWorker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()) {
                    Intent i = new Intent(getApplicationContext(), AddWorker.class);
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
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("Select Operation");
        menu.add("Present");
        menu.add("Absent");
        menu.add("Edit");
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (item.getTitle().equals("Present")) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            int i = info.position;
            String fname = records.get(i).getFname();
            String lname = records.get(i).getLname();
            double att = records.get(i).getTotalAttendance();
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
            Date date = new Date();
            String finaDate = formatter.format(date);
            DatabaseReference reference = fDb.getReference("attendance");
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    for (DataSnapshot data : snapshot.getChildren()) {
                        String obj = data.getValue().toString();
                        try {
                            JSONObject object1 = new JSONObject(obj);
                            JSONObject object = new JSONObject(object1.getJSONObject(fname + lname).toString());
                            if (object.getString("date").equals(finaDate) && object.getString("workerName").equals(fname+lname)) {
                                Toast.makeText(getApplicationContext(), "Attendance already marked!", Toast.LENGTH_SHORT).show();
                                break;
                            }
                            else
                            {
                                Attendance a = new Attendance(fAuth.getCurrentUser().getUid(), finaDate, fname + lname, "Present");
                                fDb.getReference("attendance").child(finaDate).child(fname + lname).setValue(a).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        FirebaseDatabase rootNode = FirebaseDatabase.getInstance();
                                        DatabaseReference reference = rootNode.getReference("workers");
                                        reference.child(fname + " " + lname).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                snapshot.getRef().child("totalAttendance").setValue(String.valueOf(att + 1));
                                                Toast.makeText(getApplicationContext(), "Attendance marked successfully!", Toast.LENGTH_SHORT).show();
                                                finish();
                                                startActivity(getIntent());
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                            }
                                        });
                                    }
                                });
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
        }
        if(item.getTitle().equals("Absent"))
        {
            marked = false;
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            int i = info.position;
            String fname=records.get(i).getFname();
            String lname=records.get(i).getLname();
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
            Date date = new Date();
            String finaDate=formatter.format(date);
            DatabaseReference reference = fDb.getReference("attendance");
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot data : snapshot.getChildren()) {
                        String obj=data.getValue().toString();
                        try {
                            JSONObject object1=new JSONObject(obj);
                            JSONObject object=new JSONObject(object1.getJSONObject(fname+lname).toString());
                            if(object.getString("date").equalsIgnoreCase(finaDate) && object.getString("workerName").equalsIgnoreCase(fname+lname))
                            {
                                Toast.makeText(getApplicationContext(), "Attendance already marked!", Toast.LENGTH_SHORT).show();
                                break;
                            }
                            else
                            {
                                Attendance a=new Attendance(fAuth.getCurrentUser().getUid(),finaDate,fname+lname,"Absent");
                                fDb.getReference("attendance").child(finaDate).child(fname+lname).setValue(a).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(getApplicationContext(), "Attendance marked successfully!", Toast.LENGTH_SHORT).show();
                                        finish();
                                        startActivity(getIntent());
                                    }
                                });
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
        }
        if(item.getTitle().equals("Edit"))
        {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            int i = info.position;
            String fname=records.get(i).getFname();
            String lname=records.get(i).getLname();
            Intent in=new Intent(getApplicationContext(),EditWorker.class);
            in.putExtra("Name",fname+" "+lname);
            startActivity(in);
        }
        return super.onContextItemSelected(item);
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

    public void noti(Context context)
    {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        Date date = new Date();
        String finaDate = formatter.format(date);
        FirebaseDatabase rootNode = FirebaseDatabase.getInstance();
        DatabaseReference reference = rootNode.getReference("attendance");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean marked=false;
                for (DataSnapshot data : snapshot.getChildren()) {
                    String obj=data.getValue().toString();
                    try {
                        JSONArray arr=new JSONArray(snapshot.getValue().toString().contains(finaDate));
                        if(snapshot.getValue().toString().contains(finaDate))
                        {
                            marked = true;
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (!marked) {
                    NotificationCompat.Builder buider = new NotificationCompat.Builder(context)
                            .setSmallIcon(R.drawable.tmatrans)
                            .setContentTitle("TextileM")
                            .setContentText("Today's Attendance is Pending!")
                            .setAutoCancel(true)
                            .setPriority(NotificationCompat.PRIORITY_HIGH);

                    NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    nm.notify(0, buider.build());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
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