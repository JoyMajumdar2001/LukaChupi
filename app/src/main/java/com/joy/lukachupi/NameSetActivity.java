package com.joy.lukachupi;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class NameSetActivity extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name_set);
        FirebaseApp.initializeApp(this);
        LinearLayout mainLayout = findViewById(R.id.mainlay);
        try {
            mainLayout.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }catch(Exception e){
            e.printStackTrace();
        }

        final EditText displayName = findViewById(R.id.edittextName);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();
        if (user == null){
            startActivity(new Intent(this,MainActivity.class));
            finish();
        }else {
            displayName.setText(user.getDisplayName());
        }

        Button setName = findViewById(R.id.buttonSetName);
        setName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(displayName.getText().toString().trim().isEmpty()) {
                    Toast.makeText(NameSetActivity.this, "Error : Empty Name", Toast.LENGTH_SHORT).show();

                }else{
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    HashMap<String,Object> hmap = new HashMap<>();
                    hmap.put("name",displayName.getText().toString());
                    if (user != null) {
                        db.collection(user.getUid()).document("personal").set(hmap);
                    }
                    startActivity(new Intent(NameSetActivity.this,HomeActivity.class));
                    finish();

                }
            }
        });
    }
}