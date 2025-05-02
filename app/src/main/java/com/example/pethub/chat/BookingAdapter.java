package com.example.pethub.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.pethub.R;
import java.util.List;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {
    private final List<Booking> bookings;

    public BookingAdapter(List<Booking> bookings) {
        this.bookings = bookings;
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_booking, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        Booking booking = bookings.get(position);
        Glide.with(holder.itemView.getContext())
                .load(booking.getPhotoResId()+3) // Use the sitter's photoResId
                .placeholder(R.drawable.default_avatar)
                .into(holder.profilepic);
        holder.tvSitterName.setText(booking.getSitterName());
        holder.tvDate.setText(booking.getDate());
        holder.tvPetType.setText(booking.getPetType());
        if (booking.getPetType().equals("Animal")) {
            holder.tvSpecies.setText(booking.getSpecies());
        } else {
            holder.tvSpecies.setText("N/A");
        }
        holder.tvRemarks.setText(booking.getRemarks());
        holder.tvStatusBadge.setText(booking.getStatus());
    }

    @Override
    public int getItemCount() {
        return bookings.size();
    }

    static class BookingViewHolder extends RecyclerView.ViewHolder {
        TextView tvSitterName, tvDate, tvPetType, tvSpecies, tvRemarks, tvStatusBadge;
        ImageView profilepic;

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            profilepic = itemView.findViewById(R.id.profilepic); // Initialize ImageView
            tvSitterName = itemView.findViewById(R.id.tvSitterName);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvPetType = itemView.findViewById(R.id.tvPetType);
            tvSpecies = itemView.findViewById(R.id.tvSpecies);
            tvRemarks = itemView.findViewById(R.id.tvRemarks);
            tvStatusBadge = itemView.findViewById(R.id.tvStatusBadge); // Add this line

        }
    }
}