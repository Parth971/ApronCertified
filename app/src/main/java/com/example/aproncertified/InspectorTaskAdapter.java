package com.example.aproncertified;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class InspectorTaskAdapter extends RecyclerView.Adapter<InspectorTaskAdapter.AdminViewHolder> {

    Activity context;
    List<FormDetails> formDetails;


    public InspectorTaskAdapter(Activity context, List<FormDetails> formDetails) {
        this.context = context;
        this.formDetails = formDetails;
    }

    @NonNull
    @Override
    public AdminViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.inspector_task_list, parent, false);
        return new AdminViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final AdminViewHolder holder, final int position) {
        holder.pendingName.setText(formDetails.get(position).getfNameOfSeller() + " " + formDetails.get(position).getlNameOfSeller());
        holder.pendingBusinessName.setText(formDetails.get(position).getBusinessName());
        holder.mItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClicked.goToItem(position);
            }
        });


    }

    @Override
    public int getItemCount() {
        return formDetails.size();
    }


    class AdminViewHolder extends RecyclerView.ViewHolder{
        TextView pendingName, pendingBusinessName;
        View mItemView;
        public AdminViewHolder(@NonNull View itemView) {
            super(itemView);
            mItemView = itemView;
            pendingName = (TextView) itemView.findViewById(R.id.pendingName);
            pendingBusinessName = (TextView) itemView.findViewById(R.id.pendingBusinessName);
        }
    }
    OnItemClicked mOnItemClicked;
    public interface OnItemClicked{
        void goToItem(int position);
    }
    public void setOnItemClicked(OnItemClicked onItemClicked){
        mOnItemClicked = onItemClicked;
    }
}
