package project.volunion.view;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;
import project.companyInfo.databinding.ActivityContactUsBinding;
import project.volunion.util.Utils;

public class ContactUsActivity extends AppCompatActivity {

    private ActivityContactUsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityContactUsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.sendMail.setOnClickListener(send->{
            String message = binding.contactEditText.getText().toString();
            intent(message);
        });

    }

    private void intent(String message){
        String []addresses ={""};
        String subject = "Volunion APP";
        addresses[0] = Utils.ADMIN_MAIL;
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, message);

        try {
            startActivity(Intent.createChooser(intent, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "Yüklü e-posta istemcisi yok", Toast.LENGTH_SHORT).show();
        }
    }
}