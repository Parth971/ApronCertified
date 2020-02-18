package com.example.aproncertified;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CheckupApprovalAdapter extends RecyclerView.Adapter<CheckupApprovalAdapter.CheckupApprovalViewHolder> {

    private static final String TAG = "CheckupApprovalAdapter";
    
    Context context;
    List<InspectorClass> inspectorClassList;

    public CheckupApprovalAdapter(Context context, List<InspectorClass> inspectorClassList) {
        this.context = context;
        this.inspectorClassList = inspectorClassList;
    }

    @NonNull
    @Override
    public CheckupApprovalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.checkup_approval_item, parent, false);

        return new CheckupApprovalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CheckupApprovalViewHolder holder, int position) {
        holder.state.setText("Passed:");
        int i =  strParser(inspectorClassList.get(position).getStrChefMarks())
                + strParser(inspectorClassList.get(position).getStrFoodMarks())
                + strParser(inspectorClassList.get(position).getStrLocationMarks());
        holder.rank.setText(i/3 + "/10");
//        String.format(java.util.Locale.US,"%.2f", floatValue);
        holder.date.setText(inspectorClassList.get(position).getDate());


    }
    public int strParser(String s){
        String temp;
        temp = s.replace("/10", "");
        return Integer.valueOf(temp);
    }

    @Override
    public int getItemCount() {
        return inspectorClassList.size();
    }

    public class CheckupApprovalViewHolder extends RecyclerView.ViewHolder{
        TextView state, rank, date;

        public CheckupApprovalViewHolder(@NonNull View itemView) {
            super(itemView);

            state = (TextView) itemView.findViewById(R.id.state);
            rank = (TextView) itemView.findViewById(R.id.rank);
            date = (TextView) itemView.findViewById(R.id.date);

        }
    }

}
