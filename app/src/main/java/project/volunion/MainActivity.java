package project.volunion;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import project.companyInfo.databinding.ActivityMainBinding;
import project.volunion.util.PreferencesManagerInstances;
import project.volunion.view.AdminCreateCompanyActivity;
import project.volunion.view.CompanyFeedActivity;
import project.volunion.view.ContactUsActivity;
import project.volunion.view.LogInCompanyActivity;
import project.volunion.view.VolunionFeedActivity;
import project.volunion.view.VolunionLogInActivity;
import project.volunion.view.VolunionSignUpActivity;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private Intent intent;
    private FirebaseAuth auth;
    private FirebaseUser firebaseUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        clickBtn();
    }

    @Override
    protected void onStart() {
        super.onStart();
        initFirebase();
        checkUser();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkAdmin();
    }

    private void checkAdmin() {

    }

    private void initFirebase() {
        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
    }

    private void checkUser() {
        if (firebaseUser != null) {
            binding.voluntarilySignUpBtn.setVisibility(View.GONE);
            binding.voluntarilyLogInBtn.setVisibility(View.GONE);
            binding.companyLogInBtn.setVisibility(View.GONE);
            binding.contactUsBtn.setVisibility(View.GONE);
            binding.logOut.setVisibility(View.VISIBLE);
            binding.dashboard.setVisibility(View.VISIBLE);
        }
    }

    private void clickBtn() {
        binding.voluntarilyLogInBtn.setOnClickListener(volunionLogIn -> voluntarilyLogIn());

        binding.voluntarilySignUpBtn.setOnClickListener(volunionSignUp -> volunionSignUpMethod());

        binding.companyLogInBtn.setOnClickListener(companyLog -> companyLogIn());

        binding.contactUsBtn.setOnClickListener(contact -> contactUs());

        binding.logOut.setOnClickListener(logout -> logout());

        binding.dashboard.setOnClickListener(dashboard -> checkCompany());
    }

    private void voluntarilyLogIn() {
        intent = new Intent(this, VolunionLogInActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        overridePendingTransition(0,0);
        startActivity(intent);
    }

    private void volunionSignUpMethod() {
        intent = new Intent(this, VolunionSignUpActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        overridePendingTransition(0,0);
        startActivity(intent);

    }

    private void companyLogIn() {
        intent = new Intent(this, LogInCompanyActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        overridePendingTransition(0,0);
        startActivity(intent);

    }

    private void contactUs() {
        intent = new Intent(this, ContactUsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        overridePendingTransition(0,0);
        startActivity(intent);
    }

    private void logout() {
        auth.signOut();
        PreferencesManagerInstances.getInstance(this).setKurumId("");
        PreferencesManagerInstances.getInstance(this).setGonulluEmail("");
        PreferencesManagerInstances.getInstance(this).setAdminEmail("");
        binding.voluntarilySignUpBtn.setVisibility(View.VISIBLE);
        binding.voluntarilyLogInBtn.setVisibility(View.VISIBLE);
        binding.companyLogInBtn.setVisibility(View.VISIBLE);
        binding.contactUsBtn.setVisibility(View.VISIBLE);
        binding.logOut.setVisibility(View.GONE);
        binding.dashboard.setVisibility(View.GONE);
    }

    private void checkCompany() {

        if (!PreferencesManagerInstances.getInstance(this).getKurumId().equals("")) {
            intent = new Intent(this, CompanyFeedActivity.class);
        } else if (!PreferencesManagerInstances.getInstance(this).getAdminEmail().equals("")) {
            intent = new Intent(getApplicationContext(), AdminCreateCompanyActivity.class);
        } else if(!PreferencesManagerInstances.getInstance(this).getGonulluEmail().equals("")) {
            intent = new Intent(this, VolunionFeedActivity.class);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(0,0);
        finish();
    }
}