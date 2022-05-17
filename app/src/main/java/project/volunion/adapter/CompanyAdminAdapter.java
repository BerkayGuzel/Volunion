package project.volunion.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import project.companyInfo.databinding.RecyclerRowAdminBinding;
import project.volunion.model.CompanyInfo;
import project.volunion.model.VolunionUserModel;
import project.volunion.util.OnClick;
import project.volunion.util.OnClickDeleteUser;

public class CompanyAdminAdapter extends RecyclerView.Adapter<CompanyAdminHolder> {

    private ArrayList<CompanyInfo> companyInfoArrayList;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth auth;
    private OnClick<CompanyInfo> onClickListener;

    public CompanyAdminAdapter(ArrayList<CompanyInfo> companyInfoArrayList, OnClick<CompanyInfo> listener) {
        this.companyInfoArrayList = companyInfoArrayList;
        firebaseFirestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        onClickListener = listener;
    }

    @NonNull
    @Override
    public CompanyAdminHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerRowAdminBinding recyclerRowBinding = RecyclerRowAdminBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new CompanyAdminHolder(recyclerRowBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull CompanyAdminHolder holder, int position) {
        holder.recyclerRowBinding.commentRow.setText(companyInfoArrayList.get(position).name);
        holder.recyclerRowBinding.descriptionRow.setText(companyInfoArrayList.get(position).description);
        Picasso.get().load(companyInfoArrayList.get(position).downloadUrl).into(holder.recyclerRowBinding.imageViewRow);
        holder.recyclerRowBinding.edit.setOnClickListener(edit -> {
                    onClickListener.onClicked(companyInfoArrayList.get(position));
                }
        );
    }

    @Override
    public int getItemCount() {
        return companyInfoArrayList.size();

    }

}

class CompanyAdminHolder extends RecyclerView.ViewHolder {

    RecyclerRowAdminBinding recyclerRowBinding;

    public CompanyAdminHolder(RecyclerRowAdminBinding recyclerRowBinding) {
        super(recyclerRowBinding.getRoot());
        this.recyclerRowBinding = recyclerRowBinding;
    }
}

