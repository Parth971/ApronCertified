package com.example.aproncertified;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class AdminAdapter extends RecyclerView.Adapter<AdminAdapter.AdminViewHolder> {

    Activity context;
    List<FormDetails> formDetails;
    List<String> userId;

    public AdminAdapter(Activity context, List<FormDetails> formDetails, List<String> userId) {
        this.context = context;
        this.formDetails = formDetails;
        this.userId = userId;
    }

    @NonNull
    @Override
    public AdminViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.admin_list, parent, false);
        return new AdminViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final AdminViewHolder holder, final int position) {
        holder.pendingName.setText(formDetails.get(position).getfNameOfSeller() + " " + formDetails.get(position).getlNameOfSeller());
        holder.pendingBusinessName.setText(formDetails.get(position).getBusinessName());
        if(formDetails.get(position).getInspectorAssigned()!=null){
            DatabaseReference dr = FirebaseDatabase.getInstance().getReference();
            dr.child("Users").child(formDetails.get(position).getInspectorAssigned()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Details d = dataSnapshot.getValue(Details.class);
                    holder.assignedInspector.setText("Assigned to : " + d.getfName() + " " + d.getlName());
                    holder.checkbox.setChecked(false);
                    holder.checkbox.setEnabled(false);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        else {
            holder.mItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClicked.goToItem(position);
                }
            });
            holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        mOnItemClicked.addItemToList(position);
                    }
                    else {
                        mOnItemClicked.removeItemFromList(position);
                    }
                }
            });
        }


    }

    @Override
    public int getItemCount() {
        return formDetails.size();
    }


    class AdminViewHolder extends RecyclerView.ViewHolder{
        TextView pendingName, pendingBusinessName, assignedInspector;
        View mItemView;
        CheckBox checkbox;
        public AdminViewHolder(@NonNull View itemView) {
            super(itemView);
            mItemView = itemView;
            pendingName = (TextView) itemView.findViewById(R.id.pendingName);
            pendingBusinessName = (TextView) itemView.findViewById(R.id.pendingBusinessName);
            assignedInspector = (TextView) itemView.findViewById(R.id.assignedInspector);
            checkbox = (CheckBox) itemView.findViewById(R.id.checkbox);
        }
    }
    OnItemClicked mOnItemClicked;
    public interface OnItemClicked{
        void goToItem(int position);
        void addItemToList(int position);
        void removeItemFromList(int position);
    }
    public void setOnItemClicked(OnItemClicked onItemClicked){
        mOnItemClicked = onItemClicked;
    }
}
