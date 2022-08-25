package com.example.myapplication;

import android.content.Context;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.ArrayList;

public class PatientRecylAdapter extends RecyclerView.Adapter<PatientRecylAdapter.ExampleViewHolder> implements Filterable {

    private ArrayList<FormulairePatient> mPatients;
    private ArrayList<FormulairePatient> mPatientFull;

    private OnItemClickListener mListener;
    private static Context context;


    public interface OnItemClickListener {
        void onItemClick(int position);
        void onDeleteClick(int position);
        void onTransferClick(int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public static class ExampleViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImageView;
        public TextView mTextView1;
        public TextView mTextView2;
        public CardView deleteCrd;
        public CardView transferCrd;

        public ExampleViewHolder(View itemView ,final OnItemClickListener listener) {
            super(itemView);

            mImageView = itemView.findViewById(R.id.imageView);
            mTextView1 = itemView.findViewById(R.id.nameRec);
            mTextView2 = itemView.findViewById(R.id.descrptionRec);
            deleteCrd = itemView.findViewById(R.id.delePat);
            transferCrd = itemView.findViewById(R.id.transfere);

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

            transferCrd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onTransferClick(position);
                        }
                    }
                }
            });

        }
    }

    public PatientRecylAdapter(ArrayList<FormulairePatient> patients) {
        mPatients = patients;
        mPatientFull = new ArrayList<FormulairePatient>(patients);
    }

    @Override
    public ExampleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.patient_adpter_cardview, parent, false);
        ExampleViewHolder evh = new ExampleViewHolder(v, mListener);
        return evh;
    }

    @Override
    public void onBindViewHolder(ExampleViewHolder holder, int position) {
        FormulairePatient patient = mPatients.get(position);
        holder.mImageView.setImageResource(R.drawable.ic_account_box_black_24dp);
        holder.mTextView1.setText("Nom : "+patient.getNom());
        holder.mTextView2.setText("Prenom : "+patient.getPrenom());
    }

    @Override
    public int getItemCount() {
        return mPatients.size();
    }
    @Override
    public Filter getFilter() {
        return patieFilter;
    }


    private Filter patieFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<FormulairePatient> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(mPatientFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (FormulairePatient item : mPatientFull) {
                    if (item.getNom().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mPatients.clear();
            mPatients.addAll((ArrayList) results.values);
            notifyDataSetChanged();
        }
    };
}
