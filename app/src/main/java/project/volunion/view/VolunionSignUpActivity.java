package project.volunion.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import project.companyInfo.R;
import project.companyInfo.databinding.ActivityVolunionSignUpBinding;
import project.volunion.model.CompanyAuth;
import project.volunion.model.VolunionUserModel;
import project.volunion.util.PreferencesManagerInstances;
import project.volunion.util.Utils;

public class VolunionSignUpActivity extends AppCompatActivity {

    private ActivityVolunionSignUpBinding binding;
    private String selectedCities, selectedTowns, selectedCompany;
    private ArrayAdapter<String> citiesAdapter, townAdapter, companyAdapter;
    private ArrayList<String> citiesList, townListIzmir, townListIstanbul,townListAnkara, companyList;
    private ArrayList<CompanyAuth> companyAuths = new ArrayList<>();

    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVolunionSignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initFirebase();
        initAdapter();
        initCompany();
        clickBtn();
    }

    private void initFirebase() {
        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    private void initAdapter() {
        citiesList = new ArrayList<>();
        citiesList.add("Ankara");
        citiesList.add("İzmir");
        citiesList.add("İstanbul");

        citiesAdapter = new ArrayAdapter<>(this, R.layout.spinner_layout, citiesList);
        binding.spinnerCity.setAdapter(citiesAdapter);

        townListIzmir = new ArrayList<>();
        townListIzmir.add("Buca");
        townListIzmir.add("Bornova");
        townListIzmir.add("Bostanlı");
        townListIzmir.add("Karşıyaka");
        townListIzmir.add("Çiğli");
        townListIzmir.add("Bayraklı");
        townListIzmir.add("Gaziemir");
        townListIzmir.add("Ödemiş");
        townListIzmir.add("Selçuk");
        townListIzmir.add("Torbalı ");

        townListIstanbul = new ArrayList<>();
        townListIstanbul.add("Beşiktaş");
        townListIstanbul.add("Kadıköy");
        townListIstanbul.add("Bebek");
        townListIstanbul.add("Şişli");
        townListIstanbul.add("Bağcılar");
        townListIstanbul.add("Bakırköy");
        townListIstanbul.add("Başakşehir");
        townListIstanbul.add("BaşakşehirBaşakşehir");
        townListIstanbul.add("Kartal");
        townListIstanbul.add("Maltepe");

        townListAnkara = new ArrayList<>();
        townListAnkara.add("Çankaya");
        townListAnkara.add("Çamlıdere");
        townListAnkara.add("Gölbaşı");
        townListAnkara.add("Ulus");
        townListAnkara.add("Elmadağ");
        townListAnkara.add("Etimesgut");
        townListAnkara.add("Güdül");
        townListAnkara.add("Haymana");
        townListAnkara.add("Kalecik");
        townListAnkara.add("Keçiören");

        binding.spinnerCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                if (position == 0) {

                    townAdapter = new ArrayAdapter<>(getApplication(), R.layout.spinner_layout, townListAnkara);
                }

                if (position == 1) {

                    townAdapter = new ArrayAdapter<>(getApplication(), R.layout.spinner_layout, townListIzmir);
                }
                if (position == 2) {
                    townAdapter = new ArrayAdapter<>(getApplication(), R.layout.spinner_layout, townListIstanbul);
                }

                binding.spinnerTown.setAdapter(townAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void initCompany() {
        companyList = new ArrayList<>();
        firebaseFirestore.collection(LogInCompanyActivity.KURUMLAR).addSnapshotListener((value, error) -> {
            if (error != null) {
                Toast.makeText(this, "Listeye erişemedik", Toast.LENGTH_SHORT).show();
            }
            if (value != null) {
                companyAuths.clear();
                companyList.clear();
                for (DocumentSnapshot documentSnapshot : value.getDocuments()) {
                    Map<String, Object> data = documentSnapshot.getData();
                    assert data != null;
                    CompanyAuth company = new CompanyAuth(
                            documentSnapshot.getId(),
                            (String) data.get("mail"),
                            (String) data.get("password"),
                            (String) data.get("name")
                    );
                    companyAuths.add(company);
                    companyList.add(company.getName());
                }
                companyAdapter = new ArrayAdapter<>(getApplication(), R.layout.spinner_layout, companyList);
                binding.spinnerCompany.setAdapter(companyAdapter);
            }
        });
    }

    private void clickBtn() {
        binding.signUpBtn.setOnClickListener(signUp -> {
            singUpBtn();
        });
    }

    private void singUpBtn() {
        String name = binding.nameVolunionSignUp.getText().toString();
        String surname = binding.surnameVolunionSignUp.getText().toString();
        String job = binding.jobVolunionSignUp.getText().toString();
        String email = binding.mailVolunionSignUp.getText().toString();
        String password = binding.passwordVolunionSignUp.getText().toString();
        selectedCities = binding.spinnerCity.getSelectedItem().toString();
        selectedTowns = binding.spinnerTown.getSelectedItem().toString();
        selectedCompany = getCompanyId();
        String companyName = binding.spinnerCompany.getSelectedItem().toString();

        VolunionUserModel userModel = new VolunionUserModel(
                name, surname, job, email, password, selectedCities,
                selectedTowns,
                selectedCompany,
                companyName);

        if (email.isEmpty()) {
            binding.mailVolunionSignUp.setError("Lütfen e-posta adresinizi giriniz");
        }

        if (name.isEmpty()) {
            binding.nameVolunionSignUp.setError("Lütfen adınızı giriniz");
        }

        if (surname.isEmpty()) {
            binding.surnameVolunionSignUp.setError("Lütfen soyisminizi giriniz");
        }

        if (job.isEmpty()) {
            binding.nameVolunionSignUp.setError("Lütfen mesleğinizi giriniz");
        }

        if (password.isEmpty()) {
            binding.passwordVolunionSignUp.setError("Lütfen şifrenizi giriniz");
        }

        if (email.equals("") || password.equals("") || name.equals("") || surname.equals("") ||
                job.equals("") || selectedCities.equals("") || selectedTowns.equals("") || selectedCompany.equals("")) {

            Toast.makeText(this, "Lütfen boş alanları doldurun", Toast.LENGTH_SHORT).show();

        } else {
            binding.progressBar.setVisibility(View.VISIBLE);
            auth.createUserWithEmailAndPassword(userModel.getEmail(),userModel.getPassword())
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful()){

                            String volunionId = userModel.setId(Objects.requireNonNull(auth.getCurrentUser()).getUid());

                            HashMap<String, Object> postDataVolunion = new HashMap<>();
                            postDataVolunion.put("id", volunionId);
                            postDataVolunion.put("name", userModel.getName());
                            postDataVolunion.put("surname", userModel.getSurname());
                            postDataVolunion.put("job", userModel.getJob());
                            postDataVolunion.put("email", userModel.getEmail());
                            postDataVolunion.put("city", userModel.getCity());
                            postDataVolunion.put("town", userModel.getTown());
                            postDataVolunion.put("company", userModel.getCompanyID());
                            postDataVolunion.put("companyName", userModel.getCompanyName());
                            postDataVolunion.put("volunionPicUrl", userModel.getDocumentId());


                            putVolunionDataToFirebase(Utils.KURUM_GONULLULERI, postDataVolunion);
                            PreferencesManagerInstances.getInstance(this).setGonulluEmail(userModel.getEmail());
                            binding.progressBar.setVisibility(View.VISIBLE);

                        }else {
                            binding.progressBar.setVisibility(View.GONE);
                            Toast.makeText(this, "Kayıt oluştururken Hata oluştu", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void putVolunionDataToFirebase(String collectionName, HashMap<String, Object> data) {
        firebaseFirestore.collection(collectionName).add(data).addOnSuccessListener(documentReference -> {
            intentToFeed();
        }).addOnFailureListener(exception -> {
            Toast.makeText(this, "Hata", Toast.LENGTH_SHORT).show();
        });

    }

    private void intentToFeed(){
        Intent intent = new Intent(VolunionSignUpActivity.this, VolunionFeedActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(0,0);
        finish();
    }

    private String getCompanyId() {
        String selectedCompany = binding.spinnerCompany.getSelectedItem().toString();

        for (CompanyAuth company : companyAuths) {
            if (company.getName().equals(selectedCompany)) {
                return company.getId();
            }
        }
        return companyAuths.get(0).getId();
    }
}


