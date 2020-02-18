package com.example.aproncertified;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class InspectorAdapter extends RecyclerView.Adapter<InspectorAdapter.InspectorViewHolder> {

    Activity context;
    List<Details> list;
    private int lastSelectedPosition = -1;

    public InspectorAdapter(Activity context, List<Details> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public InspectorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.inspectors_item, parent, false);
        return new InspectorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final InspectorViewHolder holder, final int position) {

        holder.inspectoreName.setText(list.get(position).getfName() + " " + list.get(position).getlName());

        holder.selectionState.setChecked(lastSelectedPosition == position);
        holder.selectionState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lastSelectedPosition = position;
                notifyDataSetChanged();
                mOnItemChecked.itemChecked(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class InspectorViewHolder extends RecyclerView.ViewHolder{
        TextView inspectoreName;
        RadioButton selectionState;

        public InspectorViewHolder(@NonNull View itemView) {
            super(itemView);

            inspectoreName = (TextView) itemView.findViewById(R.id.inspectoreName);
            selectionState = (RadioButton) itemView.findViewById(R.id.selectionState);



        }
    }

    OnItemChecked mOnItemChecked;
    public interface OnItemChecked{
        void itemChecked(int position);
    }
    public void setOnItemChecked(OnItemChecked onItemChecked){
        mOnItemChecked = onItemChecked;
    }

}
