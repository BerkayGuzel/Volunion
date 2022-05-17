package project.volunion.view;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

import project.companyInfo.R;
import project.companyInfo.databinding.ActivityVolunionFeedBinding;
import project.companyInfo.databinding.MenuAlertBinding;
import project.volunion.MainActivity;
import project.volunion.adapter.VolunionAdapters;
import project.volunion.model.CompanyInfo;
import project.volunion.model.VolunionUserModel;
import project.volunion.util.PreferencesManagerInstances;
import project.volunion.util.Utils;

public class VolunionFeedActivity extends AppCompatActivity {

    private ActivityVolunionFeedBinding binding;

    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    private ArrayList<CompanyInfo> companyInfoArrayList;
    private VolunionAdapters volunionAdapters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVolunionFeedBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        companyInfoArrayList = new ArrayList<>();
        initRecyclerView();
        initFirebase();
        getData();
        binding.menuVol.setOnClickListener(menu -> {
            dialog();
        });

    }

    private void initRecyclerView() {
        binding.recyclerViewVolunion.setLayoutManager(new LinearLayoutManager(this));
        volunionAdapters = new VolunionAdapters(companyInfoArrayList);
        binding.recyclerViewVolunion.setAdapter(volunionAdapters);

    }

    private void initFirebase() {
        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    private void getData() {
        databaseCheckCollection(Utils.KURUM_GONULLULERI);
    }

    private void databaseCheckCollection(String collectionName) {
        firebaseFirestore.collection(collectionName)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Toast.makeText(VolunionFeedActivity.this, "", Toast.LENGTH_SHORT).show();
                    }
                    if (value != null) {
                        for (DocumentSnapshot documentSnapshot : value.getDocuments()) {

                            Map<String, Object> data = documentSnapshot.getData();

                            assert data != null;
                            String selectedCompany = (String) data.get("company");
                            String email = (String) data.get("email");

                            assert email != null;
                            if (email.equals(PreferencesManagerInstances.getInstance(this).getGonulluEmail())) {
                                databaseCollection(LogInCompanyActivity.KURUMLAR,
                                       selectedCompany);
                            }
                        }
                    }
                });

    }

    private void databaseCollection(String collectionName, String document) {
        firebaseFirestore.collection(collectionName)
                .document(document).collection(CreateActivity.COMPANY_DATA)
                .orderBy("date", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                if (error != null) {
                    Toast.makeText(VolunionFeedActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
                if (value != null) {
                    companyInfoArrayList.clear();
                    for (DocumentSnapshot documentSnapshot : value.getDocuments()) {
                        Map<String, Object> data = documentSnapshot.getData();

                        assert data != null;
                        String userEmail = (String) data.get("useremail");
                        String name = (String) data.get("name");
                        String des = (String) data.get("des");
                        String downloadUrl = (String) data.get("downloadUrl");

                        CompanyInfo companyInfo = new CompanyInfo(userEmail, name, des, downloadUrl);
                        companyInfoArrayList.add(companyInfo);
                    }
                    volunionAdapters.notifyDataSetChanged();
                }
            }
        });

    }


    private void dialog() {

        Dialog dialog = new Dialog(VolunionFeedActivity.this);
        MenuAlertBinding menuAlertBinding = DataBindingUtil.inflate(
                LayoutInflater.from(dialog.getContext()),
                R.layout.menu_alert,
                dialog.findViewById(R.id.layoutDialogContainer),
                false);
        dialog.setCancelable(true);

        menuAlertBinding.volunionProfile.setOnClickListener(volunion -> {
            Intent intent = new Intent(this, ProfileVolunionActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

        });


        menuAlertBinding.signOutBtn.setOnClickListener(exit -> {
            if (auth != null) {
                auth.signOut();
                PreferencesManagerInstances.getInstance(this).setGonulluEmail("");
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                overridePendingTransition(0,0);
                startActivity(intent);
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.setContentView(menuAlertBinding.getRoot());
        dialog.show();

    }

}