package com.joy.lukachupi;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity {
    private String uID;
    private MsgAdapter adapter;
    static final int REQUEST_CODE = 123;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        if (ContextCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) +
                ContextCompat.checkSelfPermission(HomeActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(HomeActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) || ActivityCompat.shouldShowRequestPermissionRationale(HomeActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)){
                AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                builder.setTitle("Grant these permissions");
                builder.setMessage("Read & Write Storage");
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ActivityCompat.requestPermissions(
                                HomeActivity.this,
                                new String[]{
                                        Manifest.permission.READ_EXTERNAL_STORAGE,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                                },
                                REQUEST_CODE
                        );
                    }
                });
                builder.setNegativeButton("Cancel",null);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }else {
                ActivityCompat.requestPermissions(
                        HomeActivity.this,
                        new String[]{
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                        },
                        REQUEST_CODE
                );
            }
        }else {
            init();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE){
            if ((grantResults.length>0) &&
                    (grantResults[0] + grantResults[1] == PackageManager.PERMISSION_GRANTED)){
                init();
            }else {
                ActivityCompat.requestPermissions(
                        HomeActivity.this,
                        new String[]{
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                        },
                        REQUEST_CODE
                );
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void init(){
        FirebaseApp.initializeApp(this);
        final LinearLayout mainLayout = findViewById(R.id.homeMainLayout);
        try {
            mainLayout.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } catch (Exception e) {
            e.printStackTrace();
        }
        MobileAds.initialize(HomeActivity.this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        AdView adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        CircleImageView imageDP = findViewById(R.id.imageDP);
        final TextView textUserName = findViewById(R.id.textUserName);
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            Picasso.get().load(user.getPhotoUrl()).placeholder(R.drawable.ic_face).into(imageDP);
            uID = user.getUid();
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        assert user != null;
        DocumentReference documentReference = db.collection(user.getUid()).document("personal");
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    assert documentSnapshot != null;
                    if (documentSnapshot.exists()) {
                        Map<String, Object> getHMap = documentSnapshot.getData();
                        assert getHMap != null;
                        textUserName.setText(Objects.requireNonNull(getHMap.get("name")).toString());
                    }
                } else {
                    Toast.makeText(HomeActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ImageView imageMenu = findViewById(R.id.imageMenu);
        imageMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(HomeActivity.this, view);
                popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());
                popupMenu.show();

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()){
                            case R.id.link:
                                Intent intent = new Intent();
                                intent.setAction(Intent.ACTION_SEND);
                                intent.putExtra(Intent.EXTRA_TEXT, "Hey!, Send me secret messages on LukaChupi https://lukachupi-messenger.web.app/index.html?user=" + uID);
                                intent.setType("text/plane");
                                startActivity(intent);
                                return  true;
                            case R.id.privacy:
                                Intent i = new Intent(Intent.ACTION_VIEW);
                                i.setData(Uri.parse("https://lukachupi-messenger.web.app/privacy.html"));
                                startActivity(i);
                                return true;
                            case R.id.terms:
                                Intent ij = new Intent(Intent.ACTION_VIEW);
                                ij.setData(Uri.parse("https://lukachupi-messenger.web.app/terms.html"));
                                startActivity(ij);
                                return true;
                            case R.id.signout:
                                mAuth.signOut();
                                startActivity(new Intent(HomeActivity.this,MainActivity.class));
                                finish();
                                return true;
                            default:
                                return false;
                        }
                    }
                });
            }
        });

        CardView cardCopy = findViewById(R.id.cardCopy);
        CardView cardShare = findViewById(R.id.cardShare);

        cardCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ClipboardManager) Objects.requireNonNull(getSystemService(getApplicationContext().CLIPBOARD_SERVICE))).setPrimaryClip(
                        ClipData.newPlainText("clipboard",
                                "https://lukachupi-messenger.web.app/index.html?user=" + uID));
                Toast.makeText(HomeActivity.this, "Copied", Toast.LENGTH_SHORT).show();
            }
        });

        cardShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, "Hey!, Send me secret messages on LukaChupi https://lukachupi-messenger.web.app/index.html?user=" + uID);
                intent.setType("text/plane");
                startActivity(intent);
            }
        });

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        RecyclerView recycleMain = findViewById(R.id.recyclerviewMain);
        Query query = firebaseFirestore.collection(uID).document("msg").collection("all");

        FirestoreRecyclerOptions<MsgModel> options = new FirestoreRecyclerOptions.Builder<MsgModel>()
                .setQuery(query,MsgModel.class)
                .build();
        adapter = new MsgAdapter(options);

        recycleMain.setHasFixedSize(true);
        recycleMain.setLayoutManager(new LinearLayoutManager(this));
        recycleMain.setAdapter(adapter);


        File file = new File(Environment.getExternalStorageDirectory().toString() + "/.lukachupi/");
        if (!file.exists()) {
            boolean bool = file.mkdirs();
            if (!bool) {
                Toast.makeText(this, "Failed to make directory", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}