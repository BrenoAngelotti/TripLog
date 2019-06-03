package com.angelotti.triplog.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.angelotti.triplog.Model.Type;
import com.angelotti.triplog.R;

import java.util.List;

public class TypeListAdapter extends RecyclerView.Adapter {

    List<Type> types;
    Context context;
    private View.OnClickListener clickListener;
    private View.OnLongClickListener longClickListener;

    public TypeListAdapter(List<Type> types, View.OnClickListener clickListener, View.OnLongClickListener longClickListener, Context context){
        this.types = types;
        this.clickListener = clickListener;
        this.longClickListener = longClickListener;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new TypeListAdapter.TypeListViewHolder(LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_type, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        TypeListViewHolder holder = (TypeListViewHolder) viewHolder;

        Type type = types.get(i);
        holder.txvTypeName.setText(type.getName());
        holder.viewTypeColor.setBackgroundColor(Color.parseColor(type.getColor()));
    }

    @Override
    public int getItemCount() {
        return types.size();
    }

    public class TypeListViewHolder extends RecyclerView.ViewHolder {
        final View viewTypeColor;
        final TextView txvTypeName;

        public TypeListViewHolder(View itemView) {
            super(itemView);

            viewTypeColor = itemView.findViewById(R.id.view_type_color);
            txvTypeName = itemView.findViewById(R.id.txv_type_name);

            itemView.setOnClickListener(clickListener);
            itemView.setOnLongClickListener(longClickListener);

            itemView.setTag(this);
        }
    }
}
