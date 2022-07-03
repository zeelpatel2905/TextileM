package com.example.tma;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

public class Register extends AppCompatActivity {

    FirebaseAuth mAuth;
    TextView tvSignIn;
    EditText fname,lname,age,contactNo,emailID,password;
    ProgressBar progressBar;
    Button reg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        controlIns();
        eventHandler();
    }

    protected void controlIns()
    {
        tvSignIn=findViewById(R.id.TvSignIn);
        reg=findViewById(R.id.BtnRegister);
        fname=findViewById(R.id.TbFname);
        lname=findViewById(R.id.TbLname);
        age=findViewById(R.id.TbAge);
        contactNo=findViewById(R.id.TbPhone);
        emailID=findViewById(R.id.TbEmail);
        password=findViewById(R.id.TbPassword);
        progressBar=findViewById(R.id.progressBar);
        mAuth=FirebaseAuth.getInstance();
    }

    protected void eventHandler()
    {
        tvSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(Register.this,MainActivity.class);
                startActivity(i);
            }
        });
        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strFname=fname.getText().toString().trim();
                String strLname=lname.getText().toString().trim();
                String strAge=age.getText().toString().trim();
                String strContactNo=contactNo.getText().toString().trim();
                String strEmailID=emailID.getText().toString().trim();
                String strPassword=password.getText().toString().trim();

                if(strFname.isEmpty())
                {
                    fname.setError("Please provide First Name");
                    fname.requestFocus();
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
                else if(Integer.parseInt(strAge)>120)
                {
                    age.setError("Please provide valid Age");
                    age.requestFocus();
                }
                else if(Integer.parseInt(strAge)<=0)
                {
                    age.setError("Please provide valid Age");
                    age.requestFocus();
                }
                else if(strContactNo.isEmpty())
                {
                    contactNo.setError("Please provide Phone No");
                    contactNo.requestFocus();
                }
                else if(!Pattern.matches("^[6-9]\\d{9}$", strContactNo))
                {
                    contactNo.setError("Please provide valid Phone No");
                    contactNo.requestFocus();
                }
                else if(strEmailID.isEmpty())
                {
                    emailID.setError("Please provide Email ID");
                    emailID.requestFocus();
                }
                else if(!Patterns.EMAIL_ADDRESS.matcher(strEmailID).matches())
                {
                    emailID.setError("Please provide valid Email ID");
                    emailID.requestFocus();
                }
                else if(strPassword.isEmpty())
                {
                    password.setError("Please provide Password");
                    password.requestFocus();
                }
                else if(strPassword.length()<6)
                {
                    password.setError("Password must be 6 character long");
                    password.requestFocus();
                }
                else if(!Pattern.matches("^[a-zA-Z0-9]+$", strPassword))
                {
                    password.setError("Password must be alphanumeric");
                    password.requestFocus();
                }
                else
                {
                    progressBar.setVisibility(View.VISIBLE);
                    String otp="";
                    String md5Pass="";
                    try {
                        MessageDigest md=MessageDigest.getInstance("MD5");
                        byte[] messageD=md.digest(strPassword.getBytes());
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
                    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
                    Date date = new Date();
                    String finaDate=formatter.format(date);
                    mAuth.createUserWithEmailAndPassword(strEmailID,strPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())
                            {
                                Supervisor s=new Supervisor(strFname,strLname,strAge,strContactNo,strEmailID,finaDate);
                                FirebaseDatabase.getInstance().getReference("users")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .setValue(s).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful())
                                        {
                                            FirebaseUser fUser=mAuth.getCurrentUser();
                                            fUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(Register.this, "Verification Email Has been Sent.", Toast.LENGTH_SHORT).show();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                }
                                            });
                                            Toast.makeText(getApplicationContext(), "User Created!", Toast.LENGTH_SHORT).show();
                                            new Thread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    try {
                                                    GMailSender sender = new GMailSender("textilemapp@gmail.com", "zeelpatel29052000");
                                                        sender.sendMail("User credentials for TextileM",
                                                                "Hello,\n\nHere this is your credentials details for login.\n\nEmailID: "+strEmailID+"\nPassword: "+strPassword+"\n\nThanks,\nYour TextileM team",
                                                                "textilemapp@gmail.com",
                                                                strEmailID);
                                                    } catch (Exception e) {
                                                        Log.e("SendMail", e.getMessage(), e);
                                                    }
                                                }
                                            }).start();
                                            Intent i=new Intent(Register.this,AdminSupervisor.class);
                                            startActivity(i);
                                        }
                                        else
                                        {
                                            Toast.makeText(getApplicationContext(), "Failed to register! Try again!", Toast.LENGTH_SHORT).show();
                                            progressBar.setVisibility(View.GONE);
                                        }
                                    }
                                });
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(), "Failed to register! Try again!", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    });
                }
            }
        });
    }
}