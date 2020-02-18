package com.example.aproncertified;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.List;

public class MyPagesAdapter extends PagerAdapter {

    Activity context;
    List<Offers> list;
    View v;

    public MyPagesAdapter(Activity context, List<Offers> list, View v) {
        this.context = context;
        this.list = list;
        this.v = v;
    }

    @Override
    public int getCount() {
        return list.size()+1;
    }
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        if(position==0){

            ((ViewPager) container).addView(v, 0);
            return  v;
        }else {
            position = position-1;
            View view = LayoutInflater.from(context).inflate(R.layout.page, null);

            TextView firstName = (TextView) view.findViewById(R.id.firstName);
            TextView lastName = (TextView) view.findViewById(R.id.lastName);

            firstName.setText(list.get(position).getOffer());

            ((ViewPager) container).addView(view, 0);
            return view;
        }

    }
    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0==(View)arg1;
    }
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((View) object);
        object=null;
    }
}