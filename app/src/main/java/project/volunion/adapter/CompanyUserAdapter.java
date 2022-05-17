package project.volunion.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import project.companyInfo.databinding.CompanyUsersRowBinding;
import project.volunion.model.VolunionUserModel;
import project.volunion.util.OnClick;
import project.volunion.util.OnClickDeleteUser;
import project.volunion.util.OnClickSendEmailUser;
import project.volunion.util.PreferencesManagerInstances;

public class CompanyUserAdapter extends RecyclerView.Adapter<CompanyHolder> {

    private ArrayList<VolunionUserModel> userModelArrayList;
    private OnClickDeleteUser<VolunionUserModel> onDeletedListener;
    private OnClickSendEmailUser<VolunionUserModel> onClickSendEmailUser;
    private OnClick<VolunionUserModel> onClick;


    public CompanyUserAdapter(ArrayList<VolunionUserModel> userModelArrayList,OnClickDeleteUser<VolunionUserModel> onDeletedListener,
                              OnClickSendEmailUser<VolunionUserModel> onClickSendEmailUser,OnClick<VolunionUserModel> onClick) {
        this.userModelArrayList = userModelArrayList;
        this.onDeletedListener = onDeletedListener;
        this.onClickSendEmailUser = onClickSendEmailUser;
        this.onClick = onClick;
    }

    @NonNull
    @Override
    public CompanyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CompanyUsersRowBinding companyUsersRowBinding = CompanyUsersRowBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new CompanyHolder(companyUsersRowBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull CompanyHolder holder, int position) {
        holder.companyUsersRowBinding.volunionName.setText(userModelArrayList.get(position).getName());
        holder.companyUsersRowBinding.volunionSurname.setText(userModelArrayList.get(position).getSurname());
        Picasso.get().load(userModelArrayList.get(position).getUrl()).into(holder.companyUsersRowBinding.profileImageProfile);
        String company = "VqtfzYa8THj3LVCxW3ww";
        if(PreferencesManagerInstances.getInstance(holder.itemView.getContext()).getKurumId().equals(company)){
            holder.companyUsersRowBinding.deleteUserFromCompany.setVisibility(View.INVISIBLE);
        }else {
           holder.companyUsersRowBinding.deleteUserFromCompany.setVisibility(View.VISIBLE);
            holder.companyUsersRowBinding.deleteUserFromCompany.setOnClickListener(delete->{
                onDeletedListener.onDeleted(userModelArrayList.get(position));
            });
        }

        holder.companyUsersRowBinding.sendEmailToVolunion.setOnClickListener(send->{
            onClickSendEmailUser.onSendMail(userModelArrayList.get(position));
        });

        holder.companyUsersRowBinding.userProfile.setOnClickListener(viewUser->{
            onClick.onClicked(userModelArrayList.get(position));
        });

    }

    @Override
    public int getItemCount() {
        return userModelArrayList.size();
    }
}

class CompanyHolder extends RecyclerView.ViewHolder {

    CompanyUsersRowBinding companyUsersRowBinding;
    public CompanyHolder(CompanyUsersRowBinding companyUsersRowBinding) {
        super(companyUsersRowBinding.getRoot());
        this.companyUsersRowBinding = companyUsersRowBinding;
    }
}
