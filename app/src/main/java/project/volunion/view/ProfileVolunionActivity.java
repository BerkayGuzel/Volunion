package project.volunion.view;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
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
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import project.companyInfo.R;
import project.companyInfo.databinding.ActivityProfileVolunionBinding;
import project.companyInfo.databinding.CompanyDialogRecyclerBinding;
import project.companyInfo.databinding.UpdatePasswordVolunionBinding;
import project.companyInfo.databinding.VolunionSelectPicBinding;
import project.volunion.adapter.CompanyListForVolunion;
import project.volunion.model.CompanyAuth;
import project.volunion.model.VolunionUserModel;
import project.volunion.util.Utils;

public class ProfileVolunionActivity extends AppCompatActivity {

    private ActivityProfileVolunionBinding binding;

    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private FirebaseUser firebaseUser;
    private Dialog dialogPic, dialogPassword;
    VolunionUserModel volunionUserModel;
    VolunionSelectPicBinding volunionSelectPicBinding;
    UpdatePasswordVolunionBinding updatePasswordVolunionBinding;
    ArrayList<CompanyAuth> companyInfoArrayList = new ArrayList<>();
    Dialog dialogCompanyList;


    Uri imageData;
    ActivityResultLauncher<Intent> activityResultLauncher;
    ActivityResultLauncher<String> permissionLauncher;

    private String companyId;
    private String volunionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileVolunionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        volunionUserModel = new VolunionUserModel();
        initFirebase();
        getData();
        clickBtn();
        initLauncher();
        initCompany();

    }


    private void clickBtn() {
        binding.profileImageProfile.setOnClickListener(profile -> {
            dialogPhoto();
        });

        binding.changePassword.setOnClickListener(password -> {
            dialogPassword();
        });

        binding.sendEmailToCompany.setOnClickListener(send -> {
            firebaseFirestore.collection(LogInCompanyActivity.KURUMLAR)
                    .addSnapshotListener((value, error) -> {
                        if (error != null) {
                            Toast.makeText(this, "Listeye erişemedik", Toast.LENGTH_SHORT).show();
                        }
                        if (value != null) {
                            for (DocumentSnapshot documentSnapshot : value.getDocuments()) {
                                Map<String, Object> data = documentSnapshot.getData();
                                assert data != null;
                                CompanyAuth company = new CompanyAuth(
                                        documentSnapshot.getId(),
                                        (String) data.get("mail"),
                                        (String) data.get("password"),
                                        (String) data.get("name"));
                                if (documentSnapshot.getId().equals(volunionUserModel.getCompanyID())) {

                                    Log.e("YER", "email: " + company.getEmail());
                                    Log.e("YER", "name: " + company.getName());
                                    String[] kurumEmail = {""};
                                    kurumEmail[0] = company.getEmail();
                                    sendEmailToCompany(
                                            kurumEmail,
                                            company.getName());
                                }
                            }
                        }
                    });
        });

        binding.changeCompany.setOnClickListener(changeCompany -> {
            dialog();
        });
    }

    private void dialog() {

        dialogCompanyList = new Dialog(ProfileVolunionActivity.this);
        CompanyDialogRecyclerBinding companyDialogRecyclerBinding = DataBindingUtil.inflate(
                LayoutInflater.from(dialogCompanyList.getContext()),
                R.layout.company_dialog_recycler,
                dialogCompanyList.findViewById(R.id.layoutDialogContainer),
                false);
        dialogCompanyList.setCancelable(true);
        companyDialogRecyclerBinding.companyListDialogRecycler.setLayoutManager(new LinearLayoutManager(this));
        CompanyListForVolunion companyListForVolunionAdapter = new CompanyListForVolunion(companyInfoArrayList, this::onClickCountry);
        companyDialogRecyclerBinding.companyListDialogRecycler.setAdapter(companyListForVolunionAdapter);


        dialogCompanyList.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialogCompanyList.setContentView(companyDialogRecyclerBinding.getRoot());
        dialogCompanyList.show();
    }

    private void onClickCountry(CompanyAuth companyAuth) {
        String selectedCompany = companyAuth.getName();
        String selectedCompanyID = companyAuth.getId();
        Map<String, Object> map = new HashMap<>();
        map.put("companyName", selectedCompany);
        map.put("company", selectedCompanyID);
        volunionUserModel.setCompanyName(selectedCompany);
        volunionUserModel.setCompanyID(selectedCompanyID);

        firebaseFirestore.collection(
                Utils.KURUM_GONULLULERI
        ).document(volunionUserModel.getDocumentId()).update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(getApplicationContext(), "Guncellendi", Toast.LENGTH_SHORT).show();
                dialogCompanyList.dismiss();
            }
        });

    }

    private void initCompany() {

        firebaseFirestore.collection(LogInCompanyActivity.KURUMLAR).addSnapshotListener((value, error) -> {
            if (error != null) {
                Toast.makeText(this, "Listeye erişemedik", Toast.LENGTH_SHORT).show();
            }
            if (value != null) {
                companyInfoArrayList.clear();

                for (DocumentSnapshot documentSnapshot : value.getDocuments()) {
                    Map<String, Object> data = documentSnapshot.getData();
                    assert data != null;
                    CompanyAuth company = new CompanyAuth(
                            documentSnapshot.getId(),
                            (String) data.get("mail"),
                            (String) data.get("password"),
                            (String) data.get("name")
                    );
                    companyInfoArrayList.add(company);

                }

            }
        });
    }


    public void sendEmailToCompany(String[] addresses, String subject) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        try {
            startActivity(Intent.createChooser(intent, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "Yüklü e-posta istemcisi yok", Toast.LENGTH_SHORT).show();
        }
    }

    private void selectPhoto(View view) {
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

    private void dialogPhoto() {

        dialogPic = new Dialog(ProfileVolunionActivity.this);
        volunionSelectPicBinding = DataBindingUtil.inflate(
                LayoutInflater.from(dialogPic.getContext()),
                R.layout.volunion_select_pic,
                dialogPic.findViewById(R.id.layoutDialogContainer),
                false);
        dialogPic.setCancelable(true);

        volunionSelectPicBinding.selectPic.setOnClickListener(this::selectPhoto);


        volunionSelectPicBinding.updatePhoto.setOnClickListener(update -> {
            if (imageData != null) {
                putData();
            } else {
                Toast.makeText(this, "Resim seçilmedi", Toast.LENGTH_SHORT).show();
                dialogPic.dismiss();
            }
        });

        dialogPic.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialogPic.setContentView(volunionSelectPicBinding.getRoot());
        dialogPic.show();

    }

    private void initFirebase() {
        firebaseStorage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = firebaseStorage.getReference();
    }

    private void getData() {
        firebaseFirestore.collection(Utils.KURUM_GONULLULERI).addSnapshotListener((value, error) -> {

            if (value != null) {
                for (DocumentSnapshot documentSnapshot : value.getDocuments()) {
                    Map<String, Object> data = documentSnapshot.getData();


                    assert data != null;
                    FirebaseUser user = auth.getCurrentUser();
                    assert user != null;
                    String userId = user.getUid();

                    volunionId = (String) data.get("id");
                    String name = (String) data.get("name");
                    String surname = (String) data.get("surname");
                    String job = (String) data.get("job");
                    String city = (String) data.get("city");
                    String town = "/ " + data.get("town");
                    String companyName = (String) data.get("companyName");
                    companyId = (String) data.get("company");
                    String downloadUrl = (String) data.get("volunionPicUrl");

                    if (userId.equals(volunionId)) {

                        volunionUserModel.setCompanyID(companyId);
                        volunionUserModel.setDocumentId(documentSnapshot.getId());
                        binding.volunionCompany.setText(companyName);
                        binding.volunionName.setText(name);
                        binding.volunionSurname.setText(surname);
                        binding.volunionJob.setText(job);
                        binding.volunionCity.setText(city);
                        binding.volunionTown.setText(town);

                        if (downloadUrl != null) {
                            Picasso.get().load(downloadUrl).into(binding.profileImageProfile);
                        } else {
                            binding.profileImageProfile.setImageResource(R.drawable.choose_pic);
                        }
                    }
                }
            } else {
                assert error != null;
                Toast.makeText(ProfileVolunionActivity.this, "Hata oluştu" + error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initLauncher() {

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {

            if (result.getResultCode() == RESULT_OK) {
                Intent intentFromResult = result.getData();
                if (intentFromResult != null) {

                    imageData = intentFromResult.getData();
                    volunionSelectPicBinding.selectPic.setImageURI(imageData);

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

    private void putData() {
        UUID uuid = UUID.randomUUID();
        String imageName = "volunion/" + uuid + ".jpg";

        storageReference.child(imageName).putFile(imageData).addOnSuccessListener(success -> {

            StorageReference newReference = firebaseStorage.getReference(imageName);
            newReference.getDownloadUrl().addOnSuccessListener(uri -> {
                String downloadUrl = uri.toString();


                FirebaseUser user = auth.getCurrentUser();
                assert user != null;

                HashMap<String, Object> postData = new HashMap<>();
                postData.put("volunionPicUrl", downloadUrl);
                firebaseFirestore.collection(Utils.KURUM_GONULLULERI).document(
                        volunionUserModel.getDocumentId())
                        .update(postData).addOnSuccessListener(unused ->
                        Toast.makeText(getApplicationContext(), "Fotoğraf başarı ile yüklendi", Toast.LENGTH_SHORT).show());
                dialogPic.dismiss();
            });
        }).
                addOnFailureListener(failure -> {
                    Toast.makeText(this, failure.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void changePassword() {

        String oldPassword = updatePasswordVolunionBinding.oldPassword.getText().toString();
        String newPassword = updatePasswordVolunionBinding.newPassword.getText().toString();

        AuthCredential authCredential = EmailAuthProvider.getCredential(firebaseUser.getEmail(), oldPassword);
        firebaseUser.reauthenticate(authCredential).addOnSuccessListener(unused -> {
            firebaseUser.updatePassword(newPassword).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    dialogPassword.dismiss();
                    Toast.makeText(ProfileVolunionActivity.this, "Şifre güncellendi", Toast.LENGTH_SHORT).show();
                }
            });
        }).addOnFailureListener(e -> {
            dialogPassword.dismiss();
        });
    }

    private void dialogPassword() {
        dialogPassword = new Dialog(ProfileVolunionActivity.this);
        updatePasswordVolunionBinding = DataBindingUtil.inflate(
                LayoutInflater.from(dialogPassword.getContext()),
                R.layout.update_password_volunion,
                dialogPassword.findViewById(R.id.layoutDialogContainer),
                false);
        dialogPassword.setCancelable(true);

        updatePasswordVolunionBinding.updatePassword.setOnClickListener(updatePassword -> {
            changePassword();
        });

        dialogPassword.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialogPassword.setContentView(updatePasswordVolunionBinding.getRoot());
        dialogPassword.show();
    }

}
