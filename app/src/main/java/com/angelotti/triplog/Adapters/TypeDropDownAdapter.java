package com.angelotti.triplog.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.angelotti.triplog.Model.Type;
import com.angelotti.triplog.R;

import java.util.List;

public class TypeDropDownAdapter extends BaseAdapter {
    Context context;
    List<Type> types;
    LayoutInflater inflater;

    private static class PaisHolder {
        public View viewTypeColor;
        public TextView txvTypeName;
    }

    public TypeDropDownAdapter(Context context, List<Type> types) {
        this.context = context;
        this.types = types;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return types.size();
    }

    @Override
    public Type getItem(int i) {
        return types.get(i);
    }

    @Override
    public long getItemId(int i) {
        return types.get(i).getId();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        PaisHolder holder;

        if (view == null){

            view = inflater.inflate(R.layout.item_type_drop_down, viewGroup, false);

            holder = new PaisHolder();

            holder.viewTypeColor = view.findViewById(R.id.view_type_color);
            holder.txvTypeName = view.findViewById(R.id.txv_type_name);

            view.setTag(holder);

        }else{

            holder = (PaisHolder) view.getTag();
        }

        Type type = types.get(i);
        holder.viewTypeColor.setBackgroundColor(Color.parseColor(type.getColor()));
        holder.txvTypeName.setText(type.getName());

        return view;
    }
}
