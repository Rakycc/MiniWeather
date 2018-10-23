package com.example.xinyuwu.bean;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.xinyuwu.miniweather.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends ArrayAdapter<City> {
    private int resourceId=R.layout.city_item;
    public MyAdapter(Context context,List<City> objects){
        super(context,R.layout.city_item,objects);
    }
    @Override
    public View getView(int position,View convertView,ViewGroup parent){
        City city=getItem(position);
        //Log.d("MyFruit","1");
        View view;
        if(convertView==null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
        }
        else{
            view=convertView;
        }
        //view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false5);
        TextView CityName=(TextView) view.findViewById(R.id.city_name);
        TextView CityNumber=(TextView)view.findViewById(R.id.city_number);
        CityName.setText(city.getCity());
        CityNumber.setText(city.getNumber());
        return view;

    }
}