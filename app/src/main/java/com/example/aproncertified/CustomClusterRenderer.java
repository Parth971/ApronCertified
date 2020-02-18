package com.example.aproncertified;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

public class CustomClusterRenderer<T extends MyItem> extends DefaultClusterRenderer<T> {

    private final Context mContext;

    public CustomClusterRenderer(Context context, GoogleMap map,
                                 ClusterManager<T> clusterManager) {
        super(context, map, clusterManager);

        mContext = context;
    }

    @Override protected void onBeforeClusterItemRendered(T item,
                                                         MarkerOptions markerOptions) {

        final BitmapDescriptor markerDescriptor = BitmapDescriptorFactory.fromBitmap(getMarkerIconWithLabel(item.getTitle(),0f));

        markerOptions.icon(markerDescriptor).snippet(item.getSnippet()).visible(true);

    }

    public Bitmap getMarkerIconWithLabel(String label, float angle) {
        IconGenerator iconGenerator = new IconGenerator(mContext);
        View markerView = LayoutInflater.from(mContext).inflate(R.layout.lay_marker, null);
        ImageView imgMarker = markerView.findViewById(R.id.img_marker);
        TextView tvLabel = markerView.findViewById(R.id.tv_label);
        imgMarker.setImageResource(R.drawable.marker);
        imgMarker.setRotation(angle);
        tvLabel.setText(label);
        iconGenerator.setContentView(markerView);
        iconGenerator.setBackground(null);
        return iconGenerator.makeIcon(label);
    }
}