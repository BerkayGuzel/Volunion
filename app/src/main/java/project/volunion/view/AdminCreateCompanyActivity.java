package project.volunion.view;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import project.companyInfo.databinding.ActivityCreateCompanyBinding;
import project.volunion.MainActivity;
import project.volunion.model.CompanyAuth;
import project.volunion.util.PreferencesManagerInstances;

public class AdminCreateCompanyActivity extends AppCompatActivity {

    ActivityCreateCompanyBinding binding;
    String email, password, enterCompanyName;
    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateCompanyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        assert user != null;
        String mail = user.getEmail();
        PreferencesManagerInstances.getInstance(this).setAdminEmail(mail);

        binding.addCompany.setOnClickListener(addCompany ->
            companyAdd());
        binding.exitAdminProfile.setOnClickListener(exit -> {
            auth.signOut();
            PreferencesManagerInstances.getInstance(this).setAdminEmail("");
            Intent intent = new Intent(getApplication(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            overridePendingTransition(0,0);
            startActivity(intent);
        });
    }

    private void companyAdd() {
        String companyID = firebaseFirestore.collection(LogInCompanyActivity.KURUMLAR).document().getId();
        email = binding.mailCompanyLogIn.getText().toString();
        password = binding.passwordCompanyLogIn.getText().toString();
        enterCompanyName = binding.companyName.getText().toString();
        binding.progressBar.setVisibility(View.VISIBLE);
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {

            if (task.isSuccessful()) {
                CompanyAuth companyAuth = new CompanyAuth(companyID, email, password, enterCompanyName);
                companyAutModel(companyAuth);
                binding.progressBar.setVisibility(View.GONE);

            } else {
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(this, "Kayıt zamanı Hata oluştu", Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void companyAutModel(CompanyAuth companyAuth) {
        HashMap<String, Object> postCompanyData = new HashMap<>();
        postCompanyData.put("id", companyAuth.getId());
        postCompanyData.put("mail", companyAuth.getEmail());
        postCompanyData.put("password", companyAuth.getPassword());
        postCompanyData.put("name", companyAuth.getName());
        putNewCompanyDataToFirebase(postCompanyData);
    }

    private void putNewCompanyDataToFirebase(HashMap<String, Object> postCompanyData) {
        firebaseFirestore.collection(LogInCompanyActivity.KURUMLAR).add(postCompanyData).addOnSuccessListener(documentReference ->
            Toast.makeText(this, enterCompanyName + " Kurumu oluşturuldu ", Toast.LENGTH_SHORT).show());
    }
}