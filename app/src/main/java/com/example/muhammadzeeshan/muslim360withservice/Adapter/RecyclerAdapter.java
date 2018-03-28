package com.example.muhammadzeeshan.muslim360withservice.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.muhammadzeeshan.muslim360withservice.Model.Timings;
import com.example.muhammadzeeshan.muslim360withservice.R;

import java.util.ArrayList;

/**
 * Created by Muhammad Mubeen on 8/7/2017.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> {

    ArrayList<Timings> arrayList = new ArrayList();

    public RecyclerAdapter(ArrayList<Timings> arrayList) {
        this.arrayList = arrayList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.customlayout, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        holder.date.setText("Date: "  + arrayList.get(position).getDate() + " - 02 - 2018");
        holder.fajar.setText("Fajar at: " + arrayList.get(position).getFajr());
        holder.zuhur.setText("Dhuhr at: " + arrayList.get(position).getDhuhr());
        holder.asar.setText("Asr at: " + arrayList.get(position).getAsr());
        holder.maghrib.setText("Maghrib at: " + arrayList.get(position).getMaghrib());
        holder.isha.setText("Isha at: " + arrayList.get(position).getIsha());
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView date, fajar, zuhur, asar, maghrib, isha;

        public MyViewHolder(View itemView) {
            super(itemView);

            date = (TextView) itemView.findViewById(R.id.date);
            fajar = (TextView) itemView.findViewById(R.id.fajr);
            zuhur = (TextView) itemView.findViewById(R.id.zuhur);
            asar = (TextView) itemView.findViewById(R.id.asr);
            maghrib = (TextView) itemView.findViewById(R.id.maghrib);
            isha = (TextView) itemView.findViewById(R.id.isha);
        }
    }
}
