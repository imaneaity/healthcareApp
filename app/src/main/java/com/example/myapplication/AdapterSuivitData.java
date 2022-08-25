package com.example.myapplication;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class AdapterSuivitData extends RecyclerView.Adapter<AdapterSuivitData.ExampleViewHolder> {

    private ArrayList<FormSuivitPatient> mSuivi;

    private OnItemClickListener mListener;


    public interface OnItemClickListener {
        void onItemClick(int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public static class ExampleViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextView1;
        public TextView mTextView2;
        public TextView mTextView3;

        public ExampleViewHolder(View itemView ,final OnItemClickListener listener) {
            super(itemView);

            mTextView1 = itemView.findViewById(R.id.idCapteurAdapt);
            mTextView2 = itemView.findViewById(R.id.date);
            mTextView3 = itemView.findViewById(R.id.data);

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

    public AdapterSuivitData(ArrayList<FormSuivitPatient> suivi) {
        mSuivi = suivi;
    }

    @Override
    public ExampleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_data_view, parent, false);
        ExampleViewHolder evh = new ExampleViewHolder(v, mListener);
        return evh;
    }

    @Override
    public void onBindViewHolder(ExampleViewHolder holder, int position) {
        FormSuivitPatient suivi = mSuivi.get(position);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentTime = sdf.format(suivi.getDate());
        holder.mTextView1.setText("Id : "+suivi.getIdCapteur());
        holder.mTextView2.setText("Date : "+currentTime);
        holder.mTextView3.setText(""+suivi.getData());
    }

    @Override
    public int getItemCount() {
        return mSuivi.size();
    }

}
