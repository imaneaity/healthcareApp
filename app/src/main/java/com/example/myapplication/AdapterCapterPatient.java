package com.example.myapplication;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.ArrayList;

public class AdapterCapterPatient extends RecyclerView.Adapter<AdapterCapterPatient.ExampleViewHolder> {

    private ArrayList<FormCapteur> mCapteurs;

    private OnItemClickListener mListener;


    public interface OnItemClickListener {
        void onItemClick(int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public static class ExampleViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImageView;
        public TextView mTextView1;
        public TextView mTextView2;

        public ExampleViewHolder(View itemView ,final OnItemClickListener listener) {
            super(itemView);

            mImageView = itemView.findViewById(R.id.imageView);
            mTextView1 = itemView.findViewById(R.id.idCapteur);
            mTextView2 = itemView.findViewById(R.id.fontionCapteur);

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


        }
    }

    public AdapterCapterPatient(ArrayList<FormCapteur> capteurs) {
        mCapteurs = capteurs;
    }

    @Override
    public ExampleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.capteur_adapter_view_patient, parent, false);
        ExampleViewHolder evh = new ExampleViewHolder(v, mListener);
        return evh;
    }

    @Override
    public void onBindViewHolder(ExampleViewHolder holder, int position) {
        FormCapteur capteur = mCapteurs.get(position);
        holder.mImageView.setImageResource(R.drawable.ic_devices_other_black_24dp);
        holder.mTextView1.setText("Id : "+capteur.getIdCapteur());
        holder.mTextView2.setText("Nom : "+capteur.getFocntion());
    }

    @Override
    public int getItemCount() {
        return mCapteurs.size();
    }

}
