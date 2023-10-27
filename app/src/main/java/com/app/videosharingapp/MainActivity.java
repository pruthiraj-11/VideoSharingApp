package com.app.videosharingapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.app.videosharingapp.Models.UserModel;
import com.app.videosharingapp.databinding.ActivityMainBinding;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements ConnectionReceiver.ReceiverListener {

    ActivityMainBinding binding;
    private FirebaseAuth auth;
    FirebaseDatabase database;
    ProgressDialog dialog;
    GoogleSignInClient googleSignInClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT){
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        binding.loginacc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,AuthActivity.class));
                finish();
            }
        });

        boolean isNetworkAvail=checkConnection();
        auth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        dialog=new ProgressDialog(MainActivity.this);
        dialog.setTitle("Creating Account");
        dialog.setMessage("We are creating your account");
        binding.signinbtn.setOnClickListener(v -> {
            if(binding.mailfield.getText().toString().isEmpty()&&binding.passfield.getText().toString().isEmpty()&& Objects.requireNonNull(binding.uname.getText()).toString().isEmpty()){
                binding.uname.setError("Can't be blank");
                binding.mailfield.setError("Can't be blank");
                binding.passfield.setError("Can't be blank");
                binding.retypepass.setError("Can't be blank");
            } else {
                if(isNetworkAvail){
                    dialog.show();
                    String email = String.valueOf(binding.mailfield.getText());
                    String pass = String.valueOf(binding.passfield.getText());
                    auth.createUserWithEmailAndPassword(email, pass).
                            addOnCompleteListener(task -> {
                                dialog.dismiss();
                                if (task.isSuccessful()) {
                                    UserModel user=new UserModel(Objects.requireNonNull(binding.uname.getText()).toString(),email,pass);
                                    String id= Objects.requireNonNull(task.getResult().getUser()).getUid();
                                    database.getReference().child("Users").child(id).setValue(user);
                                    final Snackbar snackBar = Snackbar.make(findViewById(android.R.id.content),"User created",Snackbar.LENGTH_LONG);
                                    snackBar.setAction("OK", v1 -> snackBar.dismiss());
                                    snackBar.show();
                                    binding.uname.setText("");
                                    binding.mailfield.setText("");
                                    binding.passfield.setText("");
                                    binding.retypepass.setText("");
                                } else {
                                    final Snackbar snackBar = Snackbar.make(findViewById(android.R.id.content), Objects.requireNonNull(Objects.requireNonNull(task.getException()).getMessage()),Snackbar.LENGTH_SHORT);
                                    snackBar.setAction("Try again", v12 -> snackBar.dismiss());
                                    snackBar.show();
                                }
                            });
                } else {
                    Toast.makeText(getApplicationContext(),"No connection",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean checkConnection() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.new.conn.CONNECTIVITY_CHANGE");
        registerReceiver(new ConnectionReceiver(), intentFilter);
        ConnectionReceiver.Listener = this;
        ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isConnected = networkInfo != null && networkInfo.isConnectedOrConnecting();
        showSnackBar(isConnected);
        return isConnected;
    }
    private void showSnackBar(boolean isConnected) {
        String message;
        int color;
        if (isConnected) {
            message = "Back online";
            color = Color.WHITE;
        } else {
            message = "Enable mobile data";
            color = Color.RED;
        }
        Snackbar snackbar = Snackbar.make(binding.linearLayoutup, message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }
    @Override
    protected void onResume() {
        super.onResume();
        checkConnection();
    }
    @Override
    protected void onPause() {
        super.onPause();
        checkConnection();
    }

    @Override
    public void onNetworkChange(boolean isConnected) {
        showSnackBar(isConnected);
    }
}