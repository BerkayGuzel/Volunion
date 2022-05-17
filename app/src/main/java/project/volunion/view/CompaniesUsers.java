package project.volunion.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import project.companyInfo.R;
import project.companyInfo.databinding.ActivityCompaniesUsersBinding;
import project.companyInfo.databinding.AreYouSureDialogBinding;
import project.companyInfo.databinding.MenuAlertBinding;
import project.companyInfo.databinding.UserViewDialogBinding;
import project.volunion.MainActivity;
import project.volunion.adapter.CompanyUserAdapter;
import project.volunion.model.VolunionUserModel;
import project.volunion.util.PreferencesManagerInstances;
import project.volunion.util.Utils;

public class CompaniesUsers extends AppCompatActivity {

    ActivityCompaniesUsersBinding binding;
    private FirebaseFirestore firebaseFirestore;
    private ArrayList<VolunionUserModel> userModelArrayList;
    CompanyUserAdapter companyAdminAdapter;
    Dialog dialog;
    String selectedCity, selectedTown ;
    private ArrayList<String> citiesList, townListIzmir, townListIstanbul,townListAnkara, emptyList;
    private ArrayAdapter<String> citiesAdapter, townAdapter;
    private VolunionUserModel volunionUserModel;
    public static String CITY_KEY = "city";
    public static String TOWN_KEY = "town";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCompaniesUsersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        userModelArrayList = new ArrayList<>();
        initFirebase();
        initRecyclerView();
        initAdapter();

    }

    private void searchCity(String cityKey) {
        selectedCity = binding.spinnerCity.getSelectedItem().toString();
        if(selectedCity.equals("Tüm İller")){
            Toast.makeText(this, "Tüm İllerdeki Gönüllüler", Toast.LENGTH_SHORT).show();
        }else {
          setListData(cityKey,selectedCity);
        }

    }

    private void searchTown(String townKey) {
        selectedTown = binding.spinnerTown.getSelectedItem().toString();
        if(selectedCity.equals("Tüm İlçeler")){
            Toast.makeText(this, "Tüm İlçlerdeki Gönüllüler", Toast.LENGTH_SHORT).show();
        }else {
            setListData(townKey,selectedTown);
        }

    }

    private void setListData(String key, String value){
        firebaseFirestore.collection(Utils.KURUM_GONULLULERI).whereEqualTo(key,value)
                .get().addOnCompleteListener(value1 -> {
                    userModelArrayList.clear();
                    for (DocumentSnapshot documentSnapshot : value1.getResult()) {
                        Map<String, Object> data = documentSnapshot.getData();

                        assert data != null;
                        String userSelectedCompanyID = (String) data.get("company");
                        String userID = (String) data.get("id");
                        String userEmail = (String) data.get("email");
                        String picURL = (String) data.get("volunionPicUrl");
                        String name = (String) data.get("name");
                        String surname = (String) data.get("surname");
                        String job = (String) data.get("job");
                        String companyName = (String) data.get("companyName");
                        String town = (String) data.get("town");
                        String city = (String) data.get("city");


                        String documentId = documentSnapshot.getId();

                        volunionUserModel = new VolunionUserModel(name, surname, userID, userEmail,
                                userSelectedCompanyID, picURL, documentId, job, companyName, town, city);

                        if (PreferencesManagerInstances.getInstance(getApplicationContext()).getKurumId().equals(userSelectedCompanyID)) {

                            userModelArrayList.add(volunionUserModel);
                            selectAlphabe();

                        }

                    }
                    companyAdminAdapter.notifyDataSetChanged();

                });

    }

    private void selectAlphabe(){
        Collections.sort(userModelArrayList, (o1, o2) -> o1.getName().compareToIgnoreCase(o2.getName()));
    }




    private void initAdapter() {
        citiesList = new ArrayList<>();
        citiesList.add("Tüm İller");
        citiesList.add("Ankara");
        citiesList.add("İzmir");
        citiesList.add("İstanbul");

        citiesAdapter = new ArrayAdapter<>(this, R.layout.spinner_layout, citiesList);
        binding.spinnerCity.setAdapter(citiesAdapter);

        emptyList = new ArrayList<>();
        emptyList.add("Tüm İlçeler");

        townListIzmir = new ArrayList<>();
        townListIzmir.add("Tüm İlçeler");
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
        townListIstanbul.add("Tüm İlçeler");
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
        townListAnkara.add("Tüm İlçeler");
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
                    townAdapter = new ArrayAdapter<>(getApplication(), R.layout.spinner_layout, emptyList);
                    getData();
                }

                if (position == 1) {
                    townAdapter = new ArrayAdapter<>(getApplication(), R.layout.spinner_layout, townListAnkara);
                    searchCity(CITY_KEY);
                }
                if (position == 2) {
                    townAdapter = new ArrayAdapter<>(getApplication(), R.layout.spinner_layout, townListIzmir);
                    searchCity(CITY_KEY);
                }
                if (position == 3) {
                    townAdapter = new ArrayAdapter<>(getApplication(), R.layout.spinner_layout, townListIstanbul);
                    searchCity(CITY_KEY);
                }

                binding.spinnerTown.setAdapter(townAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                getData();

            }
        });
        binding.spinnerTown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==0){
                   getData();
                }else {
                    searchTown(TOWN_KEY);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }


    private void getData() {
        firebaseFirestore.collection(Utils.KURUM_GONULLULERI)
                .addSnapshotListener((value, error) -> {

                    if (error != null) {
                        Toast.makeText(CompaniesUsers.this, "", Toast.LENGTH_SHORT).show();
                    }
                    if (value != null) {
                        userModelArrayList.clear();
                        for (DocumentSnapshot documentSnapshot : value.getDocuments()) {
                            Map<String, Object> data = documentSnapshot.getData();

                            assert data != null;
                            String userSelectedCompanyID = (String) data.get("company");
                            String userID = (String) data.get("id");
                            String userEmail = (String) data.get("email");
                            String picURL = (String) data.get("volunionPicUrl");
                            String name = (String) data.get("name");
                            String surname = (String) data.get("surname");
                            String job = (String) data.get("job");
                            String companyName = (String) data.get("companyName");
                            String town = (String) data.get("town");
                            String city = (String) data.get("city");


                            String documentId = documentSnapshot.getId();

                            volunionUserModel = new VolunionUserModel(name, surname, userID, userEmail,
                                    userSelectedCompanyID, picURL, documentId, job, companyName, town, city);

                            if (PreferencesManagerInstances.getInstance(this).getKurumId().equals(userSelectedCompanyID)) {
                                userModelArrayList.add(volunionUserModel);
                                selectAlphabe();
                            }

                        }
                        companyAdminAdapter.notifyDataSetChanged();
                    }
                });

    }

    private void initRecyclerView() {
        binding.editVolunionFromCompanyRecycler.setLayoutManager(new LinearLayoutManager(this));
        companyAdminAdapter = new CompanyUserAdapter(userModelArrayList,
                this::onDeletedListener,
                this::onSendEmailListener,
                this::onClickUserView);
        binding.editVolunionFromCompanyRecycler.setAdapter(companyAdminAdapter);

    }

    private void onDeletedListener(VolunionUserModel volunionUserModel) {

        Dialog dialog = new Dialog(CompaniesUsers.this);
        AreYouSureDialogBinding areYouSureDialogBinding = DataBindingUtil.inflate(
                LayoutInflater.from(dialog.getContext()),
                R.layout.are_you_sure_dialog,
                dialog.findViewById(R.id.layoutDialogContainer),
                false);
        dialog.setCancelable(true);

        areYouSureDialogBinding.positiveBtn.setOnClickListener(positive -> {
            String company = "Gönüllü Havuzu";
            String companyId = "VqtfzYa8THj3LVCxW3ww";


            Map<String, Object> map = new HashMap<>();
            map.put("companyName", company);
            map.put("company", companyId);
            volunionUserModel.setCompanyID(companyId);
            volunionUserModel.setCompanyName(company);

            firebaseFirestore.collection(Utils.KURUM_GONULLULERI).document(volunionUserModel.getDocumentId())
                    .update(map).addOnSuccessListener(delete ->{
                    dialog.dismiss();
                    Toast.makeText(this, "Gönüllü çıkarıldı", Toast.LENGTH_SHORT).show();
                    });

        });


        areYouSureDialogBinding.negativeBtn.setOnClickListener(exit -> {
            dialog.dismiss();
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.setContentView(areYouSureDialogBinding.getRoot());
        dialog.show();



    }


    private void onSendEmailListener(VolunionUserModel volunionUserModel) {
        String[] kurumEmail = {""};
        kurumEmail[0] = volunionUserModel.getEmail();
        sendEmailToCompany(
                kurumEmail,
                volunionUserModel.getCompanyName());
    }

    private void onClickUserView(VolunionUserModel volunionUserModel) {
        dialog(volunionUserModel);
    }

    public void sendEmailToCompany(String[] addresses, String subject) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        try {
            overridePendingTransition(0,0);
            startActivity(Intent.createChooser(intent, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "Yüklü e-posta istemcisi yok.", Toast.LENGTH_SHORT).show();
        }
    }

    private void dialog(VolunionUserModel volunionUserModel) {
        dialog = new Dialog(CompaniesUsers.this);
        UserViewDialogBinding userViewDialogBinding = DataBindingUtil.inflate(
                LayoutInflater.from(dialog.getContext()),
                R.layout.user_view_dialog,
                dialog.findViewById(R.id.layoutDialogContainer),
                false);
        dialog.setCancelable(true);

        Picasso.get().load(volunionUserModel.getUrl()).into(userViewDialogBinding.profileImageProfile);
        userViewDialogBinding.volunionName.setText(volunionUserModel.getName());
        userViewDialogBinding.volunionSurname.setText(volunionUserModel.getSurname());
        userViewDialogBinding.volunionJob.setText(volunionUserModel.getJob());
        userViewDialogBinding.volunionCity.setText(volunionUserModel.getCity());
        userViewDialogBinding.volunionTown.setText(volunionUserModel.getTown());
        userViewDialogBinding.volunionCompany.setText(volunionUserModel.getCompanyName());


        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.setContentView(userViewDialogBinding.getRoot());
        dialog.show();

    }

    private void initFirebase() {
        firebaseFirestore = FirebaseFirestore.getInstance();
    }
}