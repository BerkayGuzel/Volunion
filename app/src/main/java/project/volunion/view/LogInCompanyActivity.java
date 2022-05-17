package project.volunion.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

import project.companyInfo.databinding.ActivityLogInCompanyBinding;
import project.volunion.util.PreferencesManagerInstances;
import project.volunion.util.Utils;

public class LogInCompanyActivity extends AppCompatActivity {

    public static String KURUMLAR = "COMPANIES";

    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    String id, companyMail, companyPassword, name;
    String email, password;


    private ActivityLogInCompanyBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLogInCompanyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        binding.mailCompanyLogIn.setHint("Kurum Mail");
        binding.passwordCompanyLogIn.setHint("Kurum Şifre");
        btnClick();

        binding.addCompany.setOnClickListener(admin -> {
            binding.loginCompanyBtn.setVisibility(View.GONE);
            binding.forgotPasswordBtnCompanyLogIn.setVisibility(View.GONE);
            binding.toolbarText.setText("ADMİN GİRİŞ");
            binding.enterCompany.setVisibility(View.VISIBLE);
            binding.mailCompanyLogIn.setHint("Admin Mail");
            binding.passwordCompanyLogIn.setHint("Admin Şifre");
            binding.addCompany.setVisibility(View.GONE);
            binding.enterAdmin.setVisibility(View.VISIBLE);

        });

        binding.enterAdmin.setOnClickListener(adminEnter -> {
            binding.progressBar.setVisibility(View.VISIBLE);
            adminPage();
        });

        binding.enterCompany.setOnClickListener(enterCompany -> {
            binding.enterAdmin.setVisibility(View.GONE);
            binding.loginCompanyBtn.setVisibility(View.VISIBLE);
            binding.forgotPasswordBtnCompanyLogIn.setVisibility(View.VISIBLE);
            binding.toolbarText.setText("KURUM GİRİŞİ");
            binding.enterCompany.setVisibility(View.GONE);
            binding.mailCompanyLogIn.setHint("Kurum Mail");
            binding.passwordCompanyLogIn.setHint("Kurum Şifre");
        });

    }

    private void adminPage() {

        String emaill = binding.mailCompanyLogIn.getText().toString();
        String passwordd = binding.passwordCompanyLogIn.getText().toString();
        if (emaill.equals(Utils.ADMIN_MAIL) && passwordd.equals(Utils.ADMIN_PASSWOD)) {
            auth.signInWithEmailAndPassword(emaill, passwordd).addOnCompleteListener(autResult -> {
                binding.progressBar.setVisibility(View.GONE);
                PreferencesManagerInstances.getInstance(this).setGonulluEmail("");
                PreferencesManagerInstances.getInstance(this).setKurumId("");
                PreferencesManagerInstances.getInstance(this).setKurumBilgi("");
                Intent intent = new Intent(getApplication(), AdminCreateCompanyActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            });
        }else {
            Toast.makeText(this, "Giriş yapılamadı", Toast.LENGTH_SHORT).show();
            binding.progressBar.setVisibility(View.GONE);

        }
    }

    private void btnClick() {
        binding.loginCompanyBtn.setOnClickListener(login -> login());

        binding.forgotPasswordBtnCompanyLogIn.setOnClickListener(view -> {
            setViewForForgot();
        });

        binding.changePasswordBtn.setOnClickListener(sendMail -> {
            sendMail();
        });
    }

    private void login() {
        email = binding.mailCompanyLogIn.getText().toString();
        password = binding.passwordCompanyLogIn.getText().toString();
        checkCompany(email, password);
    }

    private void setViewForForgot() {
        binding.companyMailLogInTextInput.setVisibility(View.GONE);
        binding.passwordCompanyLogInTextInput.setVisibility(View.GONE);
        binding.loginCompanyBtn.setVisibility(View.GONE);
        binding.forgotPasswordBtnCompanyLogIn.setVisibility(View.GONE);
        binding.changePasswordBtn.setVisibility(View.VISIBLE);
        binding.ifForgotWriteMailCompanyLoginTextInput.setVisibility(View.VISIBLE);
    }

    private void sendMail() {
        String mail = binding.mailCompanyLogInForgot.getText().toString();
        if (mail.isEmpty()) {
            Toast.makeText(this, "Lütfen doğru mail addresi giriniz", Toast.LENGTH_SHORT).show();
        } else {
            auth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(LogInCompanyActivity.this, "Mailinize gönderildi", Toast.LENGTH_SHORT).show();
                    binding.companyMailLogInTextInput.setVisibility(View.VISIBLE);
                    binding.passwordCompanyLogInTextInput.setVisibility(View.VISIBLE);
                    binding.loginCompanyBtn.setVisibility(View.VISIBLE);
                    binding.forgotPasswordBtnCompanyLogIn.setVisibility(View.VISIBLE);
                    binding.changePasswordBtn.setVisibility(View.GONE);
                    binding.ifForgotWriteMailCompanyLoginTextInput.setVisibility(View.GONE);
                }
            });
        }
    }

    private void checkCompany(String email, String password) {
        binding.progressBar.setVisibility(View.VISIBLE);
        firebaseFirestore.collection(KURUMLAR).addSnapshotListener((value, error) -> {
            if (value != null) {
                for (DocumentSnapshot documentSnapshot : value.getDocuments()) {
                    Map<String, Object> data = documentSnapshot.getData();
                    assert data != null;
                    id = (String) data.get("id");
                    companyMail = (String) data.get("mail");
                    companyPassword = (String) data.get("password");
                    name = (String) data.get("name");
                    if (email.equals(companyMail) && password.equals(companyPassword)) {
                        auth.signInWithEmailAndPassword(email, password).addOnSuccessListener(autResult -> {
                            binding.progressBar.setVisibility(View.GONE);
                            PreferencesManagerInstances.getInstance(this).setKurumId(documentSnapshot.getId());
                            Intent intent = new Intent(this, CompanyFeedActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        }).addOnFailureListener(exception -> {
                            binding.progressBar.setVisibility(View.GONE);
                            Toast.makeText(this, "Giriş başarısız", Toast.LENGTH_SHORT).show();
                        });
                    }
                }
            } else {
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(this, "Kayıtlı Kurum bulunamadı" , Toast.LENGTH_SHORT).show();
            }
        });
    }
}