package com.example.bookit;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HotelAdapter extends RecyclerView.Adapter<HotelAdapter.ViewHolder> {

    ArrayList<Hoteldata>list;
    ArrayList<Hoteldata>fullList;
    Context context;


    public HotelAdapter(ArrayList<Hoteldata> list, Context context) {

        Collections.sort(list, (hoteldata, t1) -> hoteldata.getFloatRating() <= t1.getFloatRating() ? 1 : -1);


        this.fullList = list;
        this.list=new ArrayList<>(fullList);
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(context).inflate(R.layout.hotelitem,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Hoteldata hoteldata=list.get(position);
        Picasso.get().load(hoteldata.getImageview()).placeholder(R.drawable.hotel).into(holder.imageView);

        holder.name.setText(hoteldata.getName());
        holder.price.setText(hoteldata.getPrice());
        holder.room.setText(hoteldata.getRoom());
        holder.location.setText(hoteldata.getLocation());
        holder.rating.setText(new DecimalFormat("#.#").format(hoteldata.getFloatRating()));
        holder.ratingBar.setRating(hoteldata.getFloatRating());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context,HotelDetailsActivity.class);

                intent.putExtra("hotel",list.get(holder.getBindingAdapterPosition()));


                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public Filter getFilter() {
        return filter;
    }

    private final Filter filter=new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {

            ArrayList<Hoteldata> filteredList=new ArrayList<>();

            if(charSequence==null || charSequence.length()==0)
            {
                filteredList.addAll(fullList);
            }
            else
            {
                String filtertext=charSequence.toString().toLowerCase().trim();

                for(Hoteldata hoteldata:fullList)
                {
                    if(hoteldata.getName().toLowerCase().contains(filtertext))
                    {
                        filteredList.add(hoteldata);
                    }
                }
            }

            FilterResults results=new FilterResults();
            results.values=filteredList;
            results.count=filteredList.size();

            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

            list.clear();
            list.addAll((ArrayList)filterResults.values);
            notifyDataSetChanged();
        }
    };

    public class ViewHolder extends RecyclerView.ViewHolder{


         TextView name,price,room,location,rating;
         ImageView imageView;
         private RatingBar ratingBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name=itemView.findViewById(R.id.hotelname);
            price=itemView.findViewById(R.id.price);
            room=itemView.findViewById(R.id.rooms);
            location=itemView.findViewById(R.id.location);

            rating = itemView.findViewById(R.id.rating);
            ratingBar = itemView.findViewById(R.id.ratingbar);

            imageView=itemView.findViewById(R.id.hotelimage);

        }
    }
}
