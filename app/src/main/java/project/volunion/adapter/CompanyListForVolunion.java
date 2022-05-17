package project.volunion.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import project.companyInfo.databinding.SpinnerLayoutBinding;
import project.volunion.model.CompanyAuth;
import project.volunion.util.OnClickList;

public class CompanyListForVolunion  extends RecyclerView.Adapter<CompanyListHolder> {

    private ArrayList<CompanyAuth> companyInfoArrayList;
    private OnClickList<CompanyAuth> companyAuthOnClickList;

    public CompanyListForVolunion(ArrayList<CompanyAuth> companyInfoArrayList, OnClickList<CompanyAuth> companyAuthOnClickList) {
        this.companyInfoArrayList = companyInfoArrayList;
        this.companyAuthOnClickList= companyAuthOnClickList;
    }


    @NonNull
    @Override
    public CompanyListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        SpinnerLayoutBinding spinnerLayoutBinding = SpinnerLayoutBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new CompanyListHolder(spinnerLayoutBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull CompanyListHolder holder, int position) {

        holder.spinnerLayoutBinding.companyName.setText(companyInfoArrayList.get(position).getName());
        holder.spinnerLayoutBinding.companyName.setOnClickListener(view->{
            companyAuthOnClickList.onClicked(companyInfoArrayList.get(position));
        });
    }

    @Override
    public int getItemCount() {
        return companyInfoArrayList.size();
    }
}

class CompanyListHolder extends RecyclerView.ViewHolder{

    SpinnerLayoutBinding spinnerLayoutBinding;
    public CompanyListHolder(SpinnerLayoutBinding spinnerLayoutBinding) {
        super(spinnerLayoutBinding.getRoot());
        this.spinnerLayoutBinding = spinnerLayoutBinding;
    }
}
