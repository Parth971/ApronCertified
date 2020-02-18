package com.example.aproncertified;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.MyViewHolder> {

    List<ReviewClass> reviewClassList;
    List<String> keys;
    Activity context;
    String currentId;

    public ReviewsAdapter(List<ReviewClass> reviewClassList, List<String> keys, String currentId, Activity context) {
        this.reviewClassList = reviewClassList;
        this.context = context;
        this.keys = keys;
        this.currentId = currentId;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View v = layoutInflater.inflate(R.layout.review_item, parent, false);


        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {

        if(keys.get(position).equals(currentId) || currentId.equals("jjPsZ3cIP3O0jEZwDvTOK4vBEbv1") || currentId.equals("m0GQzz2q5BOWds4xu1CzX2j6wOB3")){
            holder.removeReview.setVisibility(View.VISIBLE);
        }

        if(reviewClassList.get(position).getFeedback().equals("")){
            holder.revierContent.setVisibility(View.GONE);
        }
        else {
            holder.revierContent.setText(reviewClassList.get(position).getFeedback());
        }
        if(keys.get(position).equals(currentId)){
            holder.name.setText("Me");
        }
        else {
            holder.name.setText(reviewClassList.get(position).getName());
        }

        holder.date.setText(reviewClassList.get(position).getDate());
        holder.rating.setRating(reviewClassList.get(position).getRating());

        final PopupMenu popup = new PopupMenu(context, holder.removeReview);
        popup.inflate(R.menu.remove_review);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.removeReview:
                        mReviewClickListener.onRemoveItemClicked(position);
                        return true;
                    default:
                        return false;
                }
            }
        });

        holder.removeReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup.show();
            }
        });




    }
    ReviewClickListener mReviewClickListener;
    public interface ReviewClickListener{
        void onRemoveItemClicked(int position);
    }

    public void OnclickListener(ReviewClickListener reviewClickListener){
        mReviewClickListener = reviewClickListener;
    }

    @Override
    public int getItemCount() {
        return reviewClassList.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }


    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView name;
        RatingBar rating;
        TextView date;
        TextView revierContent;
        ImageButton removeReview;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.name);
            rating = (RatingBar) itemView.findViewById(R.id.rating);
            date = (TextView) itemView.findViewById(R.id.date);
            revierContent = (TextView) itemView.findViewById(R.id.revierContent);
            removeReview = (ImageButton) itemView.findViewById(R.id.removeReview);
        }
    }


}
