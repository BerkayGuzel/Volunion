package project.volunion.adapter;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import project.companyInfo.R;
import project.companyInfo.databinding.MenuAlertBinding;
import project.companyInfo.databinding.RecyclerRowBinding;
import project.companyInfo.databinding.RowCilckDialogBinding;
import project.volunion.model.CompanyInfo;
import project.volunion.view.ProfileVolunionActivity;
import project.volunion.view.VolunionFeedActivity;

public class VolunionAdapters extends RecyclerView.Adapter<VolunionHolder> {

    private ArrayList<CompanyInfo> companyInfoArrayList;

    public VolunionAdapters(ArrayList<CompanyInfo> companyInfoArrayList) {
        this.companyInfoArrayList = companyInfoArrayList;
    }

    @NonNull
    @Override
    public VolunionHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerRowBinding recyclerRowBinding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.getContext()), parent,false);
        return new VolunionHolder(recyclerRowBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull VolunionHolder holder, int position) {

        holder.recyclerRowBinding.commentRow.setText(companyInfoArrayList.get(position).name);
        holder.recyclerRowBinding.descriptionRow.setText(companyInfoArrayList.get(position).description);
        Picasso.get().load(companyInfoArrayList.get(position).downloadUrl).into(holder.recyclerRowBinding.imageViewRow);

        holder.recyclerRowBinding.parentLayoutVolunionRow.setOnClickListener(click->{
            Dialog dialog = new Dialog(click.getContext());
            RowCilckDialogBinding rowCilckDialogBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(dialog.getContext()),
                    R.layout.row_cilck_dialog,
                    dialog.findViewById(R.id.layoutDialogContainer),
                    false);
            dialog.setCancelable(true);

            Picasso.get().load(companyInfoArrayList.get(position).downloadUrl).into(rowCilckDialogBinding.imageViewRow);
            rowCilckDialogBinding.commentRowDialog.setText(companyInfoArrayList.get(position).name);
            rowCilckDialogBinding.descriptionRowDialog.setText(companyInfoArrayList.get(position).description);

            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            dialog.setContentView(rowCilckDialogBinding.getRoot());
            dialog.show();
        });

    }

    @Override
    public int getItemCount() {
        return companyInfoArrayList.size();
    }
}

class VolunionHolder extends RecyclerView.ViewHolder{

    RecyclerRowBinding recyclerRowBinding;

    public VolunionHolder(RecyclerRowBinding recyclerRowBinding) {
        super(recyclerRowBinding.getRoot());
        this.recyclerRowBinding = recyclerRowBinding;

    }
}
