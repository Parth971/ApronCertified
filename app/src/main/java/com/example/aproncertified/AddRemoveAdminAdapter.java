package com.example.aproncertified;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AddRemoveAdminAdapter extends RecyclerView.Adapter<AddRemoveAdminAdapter.AddAdminViewHolder> {

    Activity context;
    List<Details> list;
    String action;

    public AddRemoveAdminAdapter(Activity context, List<Details> list, String action) {
        this.context = context;
        this.list = list;
        this.action = action;
    }

    @NonNull
    @Override
    public AddAdminViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.add_remove_admin_item, parent, false);
        return new AddAdminViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddAdminViewHolder holder, final int position) {
        holder.btn.setText(action);
        holder.userName.setText(list.get(position).getfName() + " " + list.get(position).getlName());
        holder.userEmail.setText(list.get(position).getEmail());
        holder.btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnBtnClicked.action(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    class AddAdminViewHolder extends RecyclerView.ViewHolder{
        TextView userName, userEmail;
        Button btn;

        public AddAdminViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = (TextView) itemView.findViewById(R.id.userName);
            userEmail = (TextView) itemView.findViewById(R.id.userEmail);
            btn = (Button) itemView.findViewById(R.id.btn);
        }
    }

    OnBtnClicked mOnBtnClicked;
    public interface OnBtnClicked{
        void action(int position);
    }
    public void setOnAddClicked(OnBtnClicked onBtnClicked){
        mOnBtnClicked = onBtnClicked;
    }
}
