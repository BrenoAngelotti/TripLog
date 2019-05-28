package com.angelotti.triplog.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.angelotti.triplog.Model.Trip;
import com.angelotti.triplog.R;

import java.text.SimpleDateFormat;
import java.util.List;

public class TripListAdapter extends RecyclerView.Adapter {

    List<Trip> trips;
    Context context;
    private View.OnClickListener clickListener;
    private View.OnLongClickListener longClickListener;

    public TripListAdapter(List<Trip> trips, View.OnClickListener clickListener, View.OnLongClickListener longClickListener, Context context){
        this.trips = trips;
        this.context = context;
        this.clickListener = clickListener;
        this.longClickListener = longClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new TripListViewHolder(LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_trip, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        TripListViewHolder holder = (TripListViewHolder) viewHolder;

        Trip trip = trips.get(i);

        if(false /*trip.getType() != null*/){
            holder.llTypeName.setVisibility(View.VISIBLE);
            holder.viewTypeColor.setVisibility(View.VISIBLE);
            int color = Color.parseColor(trip.getType().getColor());
            holder.viewTypeColor.setBackgroundColor(color);
            holder.llTypeName.setBackgroundColor(color);
            holder.txvTypeName.setText(trip.getType().getName());
        }
        else{
            holder.viewTypeColor.setVisibility(View.GONE);
            holder.llTypeName.setVisibility(View.GONE);
        }

        holder.txvTripTitle.setText(trip.getTitle());
        holder.txvTripDescription.setText(trip.getDescription());
        if(trips.get(i).getDate() != null){
            holder.txvTripDate.setVisibility(View.VISIBLE);
            holder.txvTripDate.setText(new SimpleDateFormat(context.getString(R.string.format_date)).format(trip.getDate()));
        }
        else {
            holder.txvTripDate.setVisibility(View.GONE);
        }

        /*//Image mock
        if(i == 0) {
            holder.imgTripPicture.setImageDrawable(context.getDrawable(R.drawable.cp_test));
        }
        else if(i == 1) {
            holder.imgTripPicture.setImageDrawable(context.getDrawable(R.drawable.nyc_test));
        }
        else if(i == 2) {
            holder.imgTripPicture.setImageDrawable(context.getDrawable(R.drawable.sp_test));
        }*/
        //else{
            holder.imgTripPicture.setVisibility(View.GONE);
        //}
    }


    @Override
    public int getItemCount() {
        return trips.size();
    }

    public class TripListViewHolder extends RecyclerView.ViewHolder {

        final ImageView imgTripPicture;
        final TextView txvTripTitle;
        final TextView txvTripDescription;
        final TextView txvTripDate;
        final LinearLayout viewTypeColor;
        final LinearLayout llTypeName;
        final TextView txvTypeName;

        TripListViewHolder(@NonNull View itemView) {
            super(itemView);
            imgTripPicture = itemView.findViewById(R.id.img_trip_picture);
            txvTripTitle = itemView.findViewById(R.id.txv_trip_title);
            txvTripDescription = itemView.findViewById(R.id.txv_trip_description);
            txvTripDate = itemView.findViewById(R.id.txv_trip_date);
            viewTypeColor = itemView.findViewById(R.id.view_type_color);
            llTypeName = itemView.findViewById(R.id.ll_type_name);
            txvTypeName = itemView.findViewById(R.id.txv_type_name);

            itemView.setOnClickListener(clickListener);
            itemView.setOnLongClickListener(longClickListener);

            itemView.setTag(this);
        }
    }
}

