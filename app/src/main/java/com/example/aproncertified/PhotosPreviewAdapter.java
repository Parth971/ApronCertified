package com.example.aproncertified;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class PhotosPreviewAdapter extends RecyclerView.Adapter<PhotosPreviewAdapter.PreviewViewHolder> {

    private static final String TAG = "PhotosPreviewAdapter";
    
    Context context;
    List<Uri> paths;

    public PhotosPreviewAdapter(Context context, List<Uri> paths) {
        this.context = context;
        this.paths = paths;
    }

    @NonNull
    @Override
    public PreviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.photo_preview_item, parent, false);

        return new PreviewViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PreviewViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: "+ paths.get(position).toString());
        Glide.with(context)
                .load(paths.get(position))
                .into(holder.photoItem);



        holder.removeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPhotoListener.onRemovePhoto(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return paths.size();
    }

    public class PreviewViewHolder extends RecyclerView.ViewHolder{
        ImageView photoItem;
        ImageButton removeImage;

        public PreviewViewHolder(@NonNull View itemView) {
            super(itemView);
            photoItem = itemView.findViewById(R.id.photoItem);
            removeImage = itemView.findViewById(R.id.removeImage);

        }
    }

    PhotoListener mPhotoListener;

    public interface PhotoListener{
        void onRemovePhoto(int position);
    }

    public void OnclickListener(PhotoListener photoListener){
        mPhotoListener = photoListener;
    }


}
