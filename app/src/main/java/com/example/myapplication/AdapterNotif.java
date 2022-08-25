package com.example.myapplication;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.ArrayList;

public class AdapterNotif extends RecyclerView.Adapter<AdapterNotif.ExampleViewHolder> {

    private ArrayList<FromNotification> mNotif;

    private OnItemClickListener mListener;


    public interface OnItemClickListener {
        void onItemClick(int position);
        void onDeleteClick(int position);


    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public static class ExampleViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView mTextView1;
        public TextView mTextView2;
        public CardView deleteCrd;

        public ExampleViewHolder(View itemView ,final OnItemClickListener listener) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView);
            mTextView1 = itemView.findViewById(R.id.notiPat);
            mTextView2 = itemView.findViewById(R.id.notiCapt);
            deleteCrd = itemView.findViewById(R.id.deleNotif);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });

            deleteCrd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onDeleteClick(position);
                        }
                    }
                }
            });


        }
    }

    public AdapterNotif(ArrayList<FromNotification> notif) {
        mNotif = notif;
    }

    @Override
    public ExampleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_notif_view, parent, false);
        ExampleViewHolder evh = new ExampleViewHolder(v, mListener);
        return evh;
    }

    @Override
    public void onBindViewHolder(ExampleViewHolder holder, int position) {
        FromNotification notif = mNotif.get(position);
        holder.mTextView1.setText("Patient : "+notif.getIdPatient());
        holder.mTextView2.setText("Capteur : "+notif.getIdcapteur());
        holder.imageView.setImageResource(R.drawable.ic_error_outline_black_24dp);

    }

    @Override
    public int getItemCount() {
        return mNotif.size();
    }

}
