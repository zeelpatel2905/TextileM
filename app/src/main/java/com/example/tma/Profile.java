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

public class Profile extends AppCompatActivity {

    EditText fname,lname,age,contact;
    SharedPreferences sp;
    Button save,resetpass,resendCode;
    TextView verifyMsg;
    FirebaseAuth fAuth;
    FirebaseDatabase fDb;
    FirebaseUser user;
    FirebaseDatabase rootNode;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        progressBar=findViewById(R.id.progressBar);
        fname=findViewById(R.id.TbPFname);
        lname=findViewById(R.id.TbPLname);
        age=findViewById(R.id.TbPAge);
        contact=findViewById(R.id.TbPContact);
        save=findViewById(R.id.BtnPSave);
        resetpass=findViewById(R.id.BtnPResetPass);
        fAuth=FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();
        rootNode=FirebaseDatabase.getInstance();
        fDb=FirebaseDatabase.getInstance();
        resendCode = findViewById(R.id.resendCode);
        verifyMsg = findViewById(R.id.verifyMsg);
        sp=getSharedPreferences("tma",MODE_PRIVATE);


        DatabaseReference reference = rootNode.getReference("users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressBar.setVisibility(View.VISIBLE);
                String user=getSharedPreferences("tma",MODE_PRIVATE).getString("username","");
                for (DataSnapshot data : snapshot.getChildren()) {
                    String obj=data.getValue().toString();
                    try {
                        JSONObject object=new JSONObject(obj);
                        if(object.getString("emailID").equalsIgnoreCase(user))
                        {
                            fname.setText(object.getString("fname").toString());
                            lname.setText(object.getString("lname").toString());
                            age.setText(object.getString("age").toString());
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
                String strFname=fname.getText().toString().trim();
                String strLname=lname.getText().toString().trim();
                String strAge=age.getText().toString();
                String strContactNo=contact.getText().toString().trim();
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
                else if(strAge.isEmpty())
                {
                    age.setError("Please provide Age");
                    age.requestFocus();
                }
                else if(strContactNo.isEmpty())
                {
                    contact.setError("Please provide Phone No");
                    contact.requestFocus();
                }
                else if (!Pattern.matches("^[A-Za-z]+$", fname.getText().toString().trim())) {
                    fname.setError("First Name must be alphabetic");
                    fname.requestFocus();
                }
                else if (!Pattern.matches("^[A-Za-z]+$", lname.getText().toString().trim())) {
                    lname.setError("Last Name must be alphabetic");
                    lname.requestFocus();
                }
                else if(Integer.parseInt(strAge)<=0)
                {
                    age.setError("Please provide valid Age");
                    age.requestFocus();
                }
                else if(!Pattern.matches("^[6-9]\\d{9}$", contact.getText().toString().trim()))
                {
                    contact.setError("Please provide valid Phone No");
                    contact.requestFocus();
                }else {
                    progressBar.setVisibility(View.VISIBLE);
                    //FirebaseDatabase rootNode = FirebaseDatabase.getInstance();
                    DatabaseReference reference = rootNode.getReference("users");
                    reference.child(fAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            snapshot.getRef().child("fname").setValue(fname.getText().toString());
                            snapshot.getRef().child("lname").setValue(lname.getText().toString());
                            snapshot.getRef().child("age").setValue(age.getText().toString());
                            snapshot.getRef().child("contactNo").setValue(contact.getText().toString());
                            Toast.makeText(getApplicationContext(), "Profile Updated!", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                            finish();
                            startActivity(getIntent());
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(getApplicationContext(), "Profile not updated! Try again!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });


        if(!user.isEmailVerified()){
            verifyMsg.setVisibility(View.VISIBLE);
            resendCode.setVisibility(View.VISIBLE);

            resendCode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {

                    user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(v.getContext(), "Verification Email Has been Sent.", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("tag", "onFailure: Email not sent " + e.getMessage());
                        }
                    });
                }
            });
        }

        resetpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText resetPassword = new EditText(v.getContext());

                final AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext());
                passwordResetDialog.setTitle("Change Password ?");
                passwordResetDialog.setMessage("Enter New Password > 6 Characters long.");
                passwordResetDialog.setView(resetPassword);

                passwordResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // extract the email and send reset link
                        String newPassword = resetPassword.getText().toString();
                        if (newPassword.isEmpty()) {
                            Toast.makeText(getApplicationContext(), "Please provide Password", Toast.LENGTH_SHORT).show();
                        } else if (newPassword.length() < 6) {
                            Toast.makeText(getApplicationContext(), "Password must be 6 character long", Toast.LENGTH_SHORT).show();
                        } else if (!Pattern.matches("^[a-zA-Z0-9]+$", newPassword)) {
                            Toast.makeText(getApplicationContext(), "Password must be alphanumeric", Toast.LENGTH_SHORT).show();
                        } else {
                            user.updatePassword(newPassword).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(Profile.this, "Password Changed Successfully.", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(Profile.this, "Password Change Failed.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });

                passwordResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // close
                    }
                });

                passwordResetDialog.create().show();
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