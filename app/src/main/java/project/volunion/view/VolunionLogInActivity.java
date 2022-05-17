package project.volunion.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;


import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

import project.companyInfo.databinding.ActivityVolunionLogInBinding;
import project.volunion.model.VolunionUserModel;
import project.volunion.util.PreferencesManagerInstances;

public class VolunionLogInActivity extends AppCompatActivity {

    private ActivityVolunionLogInBinding binding;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVolunionLogInBinding.inflate(getLayoutInflater());

        firebaseAuth = FirebaseAuth.getInstance();
        setContentView(binding.getRoot());
        clickBtn();
    }

    private void clickBtn(){
        binding.login.setOnClickListener(login->login());
        binding.forgotPassword.setOnClickListener(forgot->{
            forgot();
        });
    }

    private void forgot(){

        binding.volunionMailInput.setVisibility(View.GONE);
        binding.passwordVolunionLogInTextInput.setVisibility(View.GONE);
        binding.login.setVisibility(View.GONE);
        binding.forgotPassword.setVisibility(View.GONE);
        binding.sendEmail.setVisibility(View.VISIBLE);
        binding.textInputLayoutForgot.setVisibility(View.VISIBLE);



        binding.sendEmail.setOnClickListener(sendMail->{
            String email = binding.mailVolunionLogInForgot.getText().toString();
            if(email.isEmpty()){
                Toast.makeText(this, "Lütfen E-posta adresinizi giriniz", Toast.LENGTH_SHORT).show();
            }else {
                forgotPassword(email);
            }
        });


    }

    private void forgotPassword(String email){
        firebaseAuth.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

                Toast.makeText(getApplicationContext(), "Lütfen mailinizi kontrol edin!", Toast.LENGTH_SHORT).show();
                binding.volunionMailInput.setVisibility(View.VISIBLE);
                binding.passwordVolunionLogInTextInput.setVisibility(View.VISIBLE);
                binding.login.setVisibility(View.VISIBLE);
                binding.forgotPassword.setVisibility(View.VISIBLE);
                binding.sendEmail.setVisibility(View.GONE);
                binding.textInputLayoutForgot.setVisibility(View.GONE);
            }
        });
    }

    private void login(){

        String email = binding.mailVolunionLogIn.getText().toString();
        String password = binding.passwordVolunionLogIn.getText().toString();

        VolunionUserModel userModel = new VolunionUserModel(email, password);

        if(password.equals("") || email.equals("")){

            Toast.makeText(this, "Lütfen bilgilerinizi doğru giriniz", Toast.LENGTH_SHORT).show();

        }else {
            binding.progressBar.setVisibility(View.VISIBLE);
            firebaseAuth.signInWithEmailAndPassword(userModel.getEmail(),userModel.getPassword())
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            firebaseAuth.signInWithEmailAndPassword(userModel.getEmail(), userModel.getPassword()).addOnSuccessListener(autResult->{
                                PreferencesManagerInstances.getInstance(this).setGonulluEmail(userModel.getEmail());
                                intentToFeed();
                                binding.progressBar.setVisibility(View.GONE);
                            }).addOnFailureListener(exception->{
                                binding.progressBar.setVisibility(View.GONE);
                                Toast.makeText(this, "Kullanıcı Hatası", Toast.LENGTH_SHORT).show();
                            });

                        }else {
                            binding.progressBar.setVisibility(View.GONE);
                            Toast.makeText(this, "Giriş yaparken Hata oluştu", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void intentToFeed() {
        Intent intent = new Intent(this, VolunionFeedActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(0,0);
        finish();
    }


}