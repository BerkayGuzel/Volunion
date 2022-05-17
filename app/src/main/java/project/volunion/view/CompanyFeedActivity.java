package project.volunion.view;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import project.companyInfo.R;
import project.companyInfo.databinding.ActivityCompanyMainBinding;
import project.companyInfo.databinding.CompanyMenuAlertBinding;
import project.volunion.MainActivity;
import project.volunion.adapter.CompanyAdminAdapter;
import project.volunion.model.CompanyInfo;
import project.volunion.util.PreferencesManagerInstances;

public class CompanyFeedActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    private ArrayList<CompanyInfo> companyInfoArrayList;
    private CompanyAdminAdapter companyAdminAdapter;

    private ActivityCompanyMainBinding binding;

    Uri imageData;
    ActivityResultLauncher<Intent> activityResultLauncher;
    ActivityResultLauncher<String> permissionLauncher;
    private ImageView imgView;
    private String imgUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCompanyMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        companyInfoArrayList = new ArrayList<>();

        initRecyclerView();
        initFirebase();
        initListeners();
        initLauncher();
        getData();
    }

    private void initRecyclerView() {
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        companyAdminAdapter = new CompanyAdminAdapter(companyInfoArrayList, this::onClickCountry);
        binding.recyclerView.setAdapter(companyAdminAdapter);

    }

    private void onClickCountry(CompanyInfo company) {
        final DialogPlus dialogPlus = DialogPlus.newDialog(this)
                .setContentHolder(new ViewHolder(R.layout.update_popup))
                .setExpanded(true, 1200)
                .create();

        View view = dialogPlus.getHolderView();
        EditText name = view.findViewById(R.id.nameTextAdminRow);
        EditText des = view.findViewById(R.id.desTextAdminRow);
        imgView = view.findViewById(R.id.imageUrlAdminRow);
        Button updateBtn = view.findViewById(R.id.editPopUp);
        Button deleteBtn = view.findViewById(R.id.deletePopUp);

        imgUrl = company.downloadUrl;

        name.setText(company.name);
        des.setText(company.description);
        Picasso.get().load(company.downloadUrl).into(imgView);
        dialogPlus.show();

        imgView.setOnClickListener(this::selectImage);

        updateBtn.setOnClickListener(update -> {
            Map<String, Object> map = new HashMap<>();
            map.put("name", name.getText().toString());
            map.put("des", des.getText().toString());
            map.put("downloadUrl", imgUrl);

            firebaseFirestore.collection(LogInCompanyActivity.KURUMLAR
            ).document( PreferencesManagerInstances.getInstance(view.getContext()).getKurumId()).collection(CreateActivity.COMPANY_DATA).document(company.getDocumentId()).update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(getApplicationContext(), "Guncellendi", Toast.LENGTH_SHORT).show();
                    dialogPlus.dismiss();
                }
            });

        });

        deleteBtn.setOnClickListener(delete -> {
            firebaseFirestore.collection(LogInCompanyActivity.KURUMLAR
            ).document( PreferencesManagerInstances.getInstance(view.getContext()).getKurumId()).
                    collection(CreateActivity.COMPANY_DATA).document(company.getDocumentId())
                    .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    dialogPlus.dismiss();
                    Toast.makeText(getApplicationContext(), "Silindi", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }


    private void selectImage(View view) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Snackbar.make(view, "Galeriye gitmek için izin gerekli", Snackbar.LENGTH_INDEFINITE).setAction("Izin ver", v -> {
                    //izin isteme
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                }).show();
            } else {
                //izin vermediyse tekrar isteyecegiz
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        } else {
            //artik izin verildi galeriye gidelim
            Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            activityResultLauncher.launch(intentToGallery);
        }
    }


    private void initLauncher() {
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                Intent intentFromResult = result.getData();
                if (intentFromResult != null) {
                    imageData = intentFromResult.getData();
                    imgUrl = imageData.toString();
                    imgView.setImageURI(imageData);
                }
            }
        });

        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), result -> {

            if (result) {
                Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                activityResultLauncher.launch(intentToGallery);

            } else {
                Toast.makeText(this, "Izin gerekli!", Toast.LENGTH_SHORT).show();
            }

        });
    }


    private void initListeners() {
        binding.menuVol.setOnClickListener(v -> {
            dialog();
        });
    }

    private void initFirebase() {
        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    private void getData() {
        databaseCollection(
                LogInCompanyActivity.KURUMLAR,
                PreferencesManagerInstances.getInstance(this).getKurumId());
    }

    private void databaseCollection(String collectionName, String document) {
        firebaseFirestore.collection(collectionName)
                .document(document).collection(CreateActivity.COMPANY_DATA)
                .orderBy("date", Query.Direction.DESCENDING).addSnapshotListener((value, error) -> {

            if (error != null) {
                Toast.makeText(CompanyFeedActivity.this, "", Toast.LENGTH_SHORT).show();
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

                    String documentId = documentSnapshot.getId();
                    CompanyInfo companyInfo = new CompanyInfo(userEmail, name, des, downloadUrl);
                    companyInfo.setDocumentId(documentId);


                    companyInfoArrayList.add(companyInfo);

                }
                companyAdminAdapter.notifyDataSetChanged();
            }
        });

    }

    private void dialog() {
        Dialog dialog = new Dialog(CompanyFeedActivity.this);
        CompanyMenuAlertBinding menuAlertBinding = DataBindingUtil.inflate(
                LayoutInflater.from(dialog.getContext()),
                R.layout.company_menu_alert,
                dialog.findViewById(R.id.layoutDialogContainer),
                false);
        dialog.setCancelable(true);

        menuAlertBinding.createActivityBtn.setOnClickListener(create -> {
            Intent intent = new Intent(this, CreateActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            overridePendingTransition(0,0);
            startActivity(intent);

        });

        menuAlertBinding.companyUsers.setOnClickListener(userList -> {
            Intent intent = new Intent(this, CompaniesUsers.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            overridePendingTransition(0,0);
            startActivity(intent);
        });


        menuAlertBinding.signOutBtn.setOnClickListener(exit -> {
            if (auth != null) {
                auth.signOut();
                PreferencesManagerInstances.getInstance(this).setKurumId("");
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