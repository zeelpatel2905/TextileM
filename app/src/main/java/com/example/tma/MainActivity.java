package com.example.tma;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth fAuth;
    FirebaseDatabase rootNode;
    DatabaseReference reference;
    ImageView ivLogo;
    TextView tvFP;
    EditText username,password;
    Button login;
    boolean validUser;
    ProgressBar progressBar;
    SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loginCheck();
        controlIns();
        bindData();
        eventHandler();
    }
    protected void loginCheck()
    {
        progressBar=findViewById(R.id.progressBar);
        sp=getSharedPreferences("tma",MODE_PRIVATE);
        if(sp.contains("username"))
        {
            progressBar.setVisibility(View.VISIBLE);
            if(sp.getString("utype","").equals("admin"))
            {
                progressBar.setVisibility(View.GONE);
                Intent i=new Intent(MainActivity.this, AdminHome.class);
                startActivity(i);
            }
        }
        if(sp.contains("username"))
        {
            progressBar.setVisibility(View.VISIBLE);
            if(sp.getString("utype","").equals("supervisor"))
            {
                progressBar.setVisibility(View.GONE);
                Intent i=new Intent(MainActivity.this, SupervisorHome.class);
                startActivity(i);
            }
        }
    }

    protected void controlIns()
    {
        ivLogo=findViewById(R.id.IvMainLogo);
        tvFP=findViewById(R.id.TvForgotPassword);
        username=findViewById(R.id.TbEmailPhone);
        password=findViewById(R.id.TbPasswordLogin);
        login=findViewById(R.id.BtnLogin);
        fAuth=FirebaseAuth.getInstance();
        sp=getSharedPreferences("tma",MODE_PRIVATE);
    }

    protected void bindData()
    {
        ivLogo.setImageResource(R.drawable.tmatrans);
    }

    protected void eventHandler()
    {
        tvFP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText resetMail = new EditText(v.getContext());
                final AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext());
                passwordResetDialog.setTitle("Forgot Password ?");
                passwordResetDialog.setMessage("Enter Your Email To Received Reset Link.");
                passwordResetDialog.setView(resetMail);

                passwordResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // extract the email and send reset link
                        String mail = resetMail.getText().toString();
                        fAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(MainActivity.this, "Reset Link Sent To Your Email.", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this, "Error ! Reset Link is Not Sent" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                });

                passwordResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // close the dialog
                    }
                });

                passwordResetDialog.create().show();
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = username.getText().toString().trim();
                String pass = password.getText().toString().trim();
                String md5Pass="";
                try {
                    MessageDigest md=MessageDigest.getInstance("MD5");
                    byte[] messageD=md.digest(pass.getBytes());
                    BigInteger no=new BigInteger(1,messageD);
                    md5Pass=no.toString(16);
                    while(md5Pass.length()<32)
                    {
                        md5Pass="0"+md5Pass;
                    }
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
                String finalMd5Pass = md5Pass;
                if (user.equalsIgnoreCase("admin")) {
                    progressBar.setVisibility(View.VISIBLE);
                    validUser = false;
                    rootNode = FirebaseDatabase.getInstance();
                    reference = rootNode.getReference("users");
                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot data : snapshot.getChildren()) {
                                String obj=data.getValue().toString();
                                try {
                                    JSONObject object=new JSONObject(obj);
                                    if(object.getString("username").equalsIgnoreCase(user) && object.getString("password").equalsIgnoreCase(pass))
                                    {
                                        SharedPreferences.Editor edit = sp.edit();
                                        edit.clear();
                                        edit.putString("username", user);
                                        edit.putString("utype", "admin");
                                        edit.commit();
                                        Toast.makeText(MainActivity.this, "Logged in successfully!", Toast.LENGTH_SHORT).show();
                                        Intent i = new Intent(MainActivity.this, AdminHome.class);
                                        startActivity(i);
                                        validUser = true;
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            if (!validUser) {
                                Toast.makeText(MainActivity.this,"Invalid credentials!", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                } else {
                    if (user.isEmpty()) {
                        username.setError("Email ID is required");
                        username.requestFocus();
                    } else if (!Patterns.EMAIL_ADDRESS.matcher(user).matches() && !Pattern.matches("^[6-9]\\d{9}$", user)) {
                        username.setError("Please provide valid Email");
                        username.requestFocus();
                    } else if (pass.isEmpty()) {
                        password.setError("Password is required");
                        password.requestFocus();
                    } else {
                        progressBar.setVisibility(View.VISIBLE);
                        validUser = false;
                        fAuth.signInWithEmailAndPassword(user,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    SharedPreferences.Editor edit = sp.edit();
                                    edit.clear();
                                    edit.putString("username", user);
                                    edit.putString("utype", "supervisor");
                                    edit.commit();
                                    Toast.makeText(MainActivity.this, "Logged in successfully!", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(getApplicationContext(), SupervisorHome.class));
                                }else {
                                    Toast.makeText(MainActivity.this,"Invalid credentials!", Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                }

                            }
                        });
                    }
                }
            }
        });
    }
}