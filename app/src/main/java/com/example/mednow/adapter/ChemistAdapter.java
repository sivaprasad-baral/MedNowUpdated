package com.example.mednow.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mednow.R;
import com.example.mednow.model.Partner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ChemistAdapter extends RecyclerView.Adapter<ChemistAdapter.ViewHolder> implements Filterable {

    private Context context;
    private List<Partner> partners;
    private List<Partner> partnersAll;
    private PharmacyClickListener pharmacyClickListener;

    public ChemistAdapter(Context context, List<Partner> partners, PharmacyClickListener pharmacyClickListener) {
        this.context = context;
        this.partners = partners;
        this.partnersAll = new ArrayList<>(partners);
        this.pharmacyClickListener = pharmacyClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.card_chemist_list,parent,false), pharmacyClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Partner partner = partners.get(position);
        holder.textViewPharmacyName.setText(partner.getPharmacyName());
        if(partner.getPharmacyImg() != null) {
            Glide.with(context).load(Uri.parse(partner.getPharmacyImg())).into(holder.imageViewPharmacyImg);
        } else {
            Glide.with(context).load(R.drawable.image_not_available).into(holder.imageViewPharmacyImg);
        }
    }

    @Override
    public int getItemCount() {
        return partners.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<Partner> filteredPartners = new ArrayList<>();
                if(constraint == null || constraint.length() == 0) {
                    filteredPartners.addAll(partnersAll);
                } else {
                    String search = constraint.toString().toLowerCase().trim();
                    for(Partner partner : partnersAll) {
                        if(partner.getPharmacyName().toLowerCase().startsWith(search)) {
                            filteredPartners.add(partner);
                        }
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredPartners;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                partners.clear();
                partners.addAll((Collection<? extends Partner>) results.values);
                notifyDataSetChanged();
            }
        };
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView textViewPharmacyName;
        ImageView imageViewPharmacyImg;
        PharmacyClickListener pharmacyClickListener;

        ViewHolder(@NonNull View itemView, PharmacyClickListener pharmacyClickListener) {
            super(itemView);
            textViewPharmacyName = itemView.findViewById(R.id.home_fragment_text_view_pharmacy_name);
            imageViewPharmacyImg = itemView.findViewById(R.id.home_fragment_image_view_pharmacy_image);
            this.pharmacyClickListener = pharmacyClickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            pharmacyClickListener.onPharmacyClick(getAdapterPosition());
        }
    }

    public interface PharmacyClickListener {
        void onPharmacyClick(int position);
    }
}