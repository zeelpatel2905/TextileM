package com.example.tma;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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

public class AddRawMaterial extends AppCompatActivity {

    EditText company,rate,quantity,price;
    Button add;
    Spinner rawType;
    FirebaseAuth fAuth;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_raw_material);
        fAuth=FirebaseAuth.getInstance();
        progressBar=findViewById(R.id.progressBar);
        company=findViewById(R.id.TbRawCompany);
        rate=findViewById(R.id.TbRawRate);
        quantity=findViewById(R.id.TbRawQuantity);
        price=findViewById(R.id.TbRawPrice);
        add=findViewById(R.id.BtnAddRaw);
        rawType=findViewById(R.id.SpRawType);
        String[] wType=new String[]{"Material Type","Vichitra","50-24Grey","50-48Grey"};
        ArrayAdapter adp=new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1,wType);
        rawType.setAdapter(adp);
        rate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!quantity.getText().toString().isEmpty() && !rate.getText().toString().isEmpty())
                {
                    price.setText(String.valueOf(Double.parseDouble(quantity.getText().toString()) * ((Double.parseDouble(rate.getText().toString()))*1000)));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        quantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!quantity.getText().toString().isEmpty() && !rate.getText().toString().isEmpty())
                {
                    price.setText(String.valueOf(Double.parseDouble(quantity.getText().toString()) * ((Double.parseDouble(rate.getText().toString()))*1000)));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strCompany=company.getText().toString().trim();
                String strRate=rate.getText().toString().trim();
                String strQuantity=quantity.getText().toString().trim();
                if(strCompany.isEmpty())
                {
                    company.setError("Please provide Company Name");
                    company.requestFocus();
                }
                else if(strRate.isEmpty())
                {
                    rate.setError("Please provide Rate");
                    rate.requestFocus();
                }
                else if(strQuantity.isEmpty())
                {
                    quantity.setError("Please provide Contact No");
                    quantity.requestFocus();
                }
                else if(Integer.parseInt(strQuantity)<=0)
                {
                    quantity.setError("Please provide valid Quantity");
                    quantity.requestFocus();
                }
                else if(Integer.parseInt(strRate)<=0)
                {
                    rate.setError("Please provide valid Rate");
                    rate.requestFocus();
                }
                else if(rawType.getSelectedItem().toString().equals("Material Type"))
                {
                    ((TextView)rawType.getSelectedView()).setError("Please provide Material Type");
                }
                else
                {
                    Double Tprice=Double.parseDouble(strQuantity)*(Double.parseDouble(strRate)*907.185);
                    progressBar.setVisibility(View.VISIBLE);
                    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
                    Date date = new Date();
                    RawMaterial r=new RawMaterial(fAuth.getCurrentUser().getUid(),strCompany,strRate,strQuantity,rawType.getSelectedItem().toString(),String.valueOf(Double.parseDouble(quantity.getText().toString()) * ((Double.parseDouble(rate.getText().toString()))*1000)),formatter.format(date));
                    FirebaseDatabase.getInstance().getReference("raw material").child(rawType.getSelectedItem().toString()+" "+formatter.format(date)).setValue(r).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(getApplicationContext(), "Raw Material added successfully!", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                            Intent i=new Intent(getApplicationContext(),SupervisorRawMaterial.class);
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