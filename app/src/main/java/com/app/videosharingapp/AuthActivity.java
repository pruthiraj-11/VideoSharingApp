package com.app.videosharingapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.app.videosharingapp.databinding.ActivityAuthBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Objects;

public class AuthActivity extends AppCompatActivity {

    ActivityAuthBinding binding;
    ProgressDialog dialog;
    FirebaseAuth auth;
    GoogleSignInClient googleSignInClient;
    FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityAuthBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Objects.requireNonNull(getSupportActionBar()).hide();

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT){
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        binding.ntp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AuthActivity.this, MainActivity.class));
                finish();
            }
        });

        auth=FirebaseAuth.getInstance();
        dialog=new ProgressDialog(AuthActivity.this);
        dialog.setTitle("Login");
        dialog.setMessage("Login to your account");

//        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken(getString(R.string.default_web_client_id))
//                .requestEmail()
//                .build();
//        googleSignInClient = GoogleSignIn.getClient(AuthActivity.this, googleSignInOptions);
        binding.gin.setOnClickListener((View.OnClickListener) view -> {
            Intent intent = googleSignInClient.getSignInIntent();
            startActivityForResult(intent, 100);
        });
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            startActivity(new Intent(AuthActivity.this,MainActivity2.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }

        binding.exaccloginbtn.setOnClickListener(v -> {
            if(binding.maillogin.getText().toString().isEmpty()){
                binding.maillogin.setError("Enter your email");
                return;
            }
            if(binding.mailpass.getText().toString().isEmpty()){
                binding.mailpass.setError("Enter your password");
                return;
            }
            dialog.show();
            auth.signInWithEmailAndPassword(binding.maillogin.getText().toString(),binding.mailpass.getText().toString())
                    .addOnCompleteListener(task -> {
                        dialog.dismiss();
                        if(task.isSuccessful()){
                            Intent intent=new Intent(AuthActivity.this,MainActivity2.class);
                            startActivity(intent);
                        }
                        else {
                            Toast.makeText(AuthActivity.this, Objects.requireNonNull(task.getException()).getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            Task<GoogleSignInAccount> signInAccountTask = GoogleSignIn.getSignedInAccountFromIntent(data);
            if (signInAccountTask.isSuccessful()) {
                Toast.makeText(AuthActivity.this,"Google sign in successful",Toast.LENGTH_SHORT).show();
                try {
                    GoogleSignInAccount googleSignInAccount = signInAccountTask.getResult(ApiException.class);
                    if (googleSignInAccount != null) {
                        AuthCredential authCredential = GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(), null);
                        firebaseAuth.signInWithCredential(authCredential).addOnCompleteListener(this, task -> {
                            if (task.isSuccessful()) {
                                startActivity(new Intent(AuthActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                                Toast.makeText(AuthActivity.this,"Firebase authentication successful", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(AuthActivity.this,"Authentication Failed :" + Objects.requireNonNull(task.getException()).getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } catch (ApiException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}