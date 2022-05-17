package project.volunion.view;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.UUID;

import project.companyInfo.databinding.ActivityCreateBinding;
import project.volunion.util.PreferencesManagerInstances;

public class CreateActivity extends AppCompatActivity {

    private ActivityCreateBinding binding;
    private FirebaseStorage firebaseStorage;
    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;
    public static String COMPANY_DATA = "COMPANIES_DATA";

    Uri imageData;
    ActivityResultLauncher<Intent> activityResultLauncher;
    ActivityResultLauncher<String> permissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initLauncher();
        initFirebase();
        btnClick();
    }

    private void initFirebase() {
        firebaseStorage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = firebaseStorage.getReference();
    }

    private void btnClick() {
        binding.selectImageBtn.setOnClickListener(this::selectImage);
        binding.createActivityBtn.setOnClickListener(createBtn -> createActivityBtn());
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
                    binding.imageView.setImageURI(imageData);

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

    private void createActivityBtn() {

        if (imageData != null) {

            //Firebase - Storage-de her gorsele farkli isim vermek icin UUID teknigi
            UUID uuid = UUID.randomUUID();
            String imageName = "images/" + uuid + ".jpg";

            storageReference.child(imageName).putFile(imageData).addOnSuccessListener(success -> {
                StorageReference newReference = firebaseStorage.getReference(imageName);

                newReference.getDownloadUrl().addOnSuccessListener(uri -> {
                    String downloadUrl = uri.toString();
                    String name = binding.commentText.getText().toString();
                    String des = binding.desText.getText().toString();

                    //anahtar kelime String - deger - Oject her hangi bir shey
                    HashMap<String, Object> postData = new HashMap<>();
                    postData.put("downloadUrl", downloadUrl);
                    postData.put("name", name);
                    postData.put("des", des);
                    postData.put("date", FieldValue.serverTimestamp());

                    putDataToFirebase(
                            LogInCompanyActivity.KURUMLAR,
                            PreferencesManagerInstances.getInstance(this).getKurumId(),
                            postData);
                });

            }).
                    addOnFailureListener(failure -> {
                        Toast.makeText(this, failure.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void putDataToFirebase(String collectionName, String documentName, HashMap<String, Object> data) {
        firebaseFirestore.collection(collectionName).document(documentName)
                .collection(COMPANY_DATA).add(data).addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Etkinlik Oluşturuldu", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, CompanyFeedActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);


                }).addOnFailureListener(exception -> {
            Toast.makeText(this, "Etkinlik Oluşturulamadı", Toast.LENGTH_SHORT).show();
        });
    }

}