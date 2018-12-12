package com.example.disablak.skysofttaskone;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ItemsListAdapter extends ArrayAdapter<ItemTab> {

    private LayoutInflater inflater;
    private int layout;
    private List<ItemTab> items;


    public ItemsListAdapter(Context context, int resource, ArrayList<ItemTab> items){
        super(context, resource, items);
        this.items = items;
        this.layout = resource;
        this.inflater = LayoutInflater.from(context);
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        View view = inflater.inflate(this.layout, parent, false);

        TextView textID = view.findViewById(R.id.id);
        ImageView imageView = view.findViewById(R.id.image);

        ItemTab item = items.get(position);

        textID.setText("" + item.getIdItem());
        Picasso.get().load(item.getUrlImage()).into(imageView);

        return view;
    }
}
