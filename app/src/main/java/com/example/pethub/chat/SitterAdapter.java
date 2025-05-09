package com.example.pethub.chat;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.pethub.R;
import java.io.File;
import java.util.List;

public class SitterAdapter extends RecyclerView.Adapter<SitterAdapter.SitterViewHolder> {
    private final List<Sitter> sitters;
    private final OnSitterClickListener listener;

    public interface OnSitterClickListener {
        void onSitterClick(Sitter sitter);
    }

    public SitterAdapter(List<Sitter> sitters, OnSitterClickListener listener) {
        this.sitters = sitters;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SitterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sitter_item, parent, false);
        return new SitterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SitterViewHolder holder, int position) {
        Sitter sitter = sitters.get(position);
        String photoUri = sitter.getPhotoUri();
        if (photoUri != null) {
            File imageFile = new File(new File(holder.itemView.getContext().getFilesDir(), "profilepic"), photoUri);
            if (imageFile.exists()) {
                Glide.with(holder.itemView.getContext())
                        .load(Uri.fromFile(imageFile))
                        .placeholder(R.drawable.default_avatar)
                        .into(holder.ivSitterIcon);
            } else {
                Glide.with(holder.itemView.getContext())
                        .load(R.drawable.default_avatar)
                        .into(holder.ivSitterIcon);
            }
        } else {
            Glide.with(holder.itemView.getContext())
                    .load(R.drawable.default_avatar)
                    .into(holder.ivSitterIcon);
        }
        holder.tvName.setText(sitter.getName());
        holder.tvBio.setText(sitter.getBio());
        holder.tvSkills.setText(sitter.getSkills());
        holder.itemView.setOnClickListener(v -> listener.onSitterClick(sitter));
    }

    @Override
    public int getItemCount() {
        return sitters.size();
    }

    static class SitterViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvBio, tvSkills;
        ImageView ivSitterIcon;

        public SitterViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvBio = itemView.findViewById(R.id.tvBio);
            tvSkills = itemView.findViewById(R.id.tvSkills);
            ivSitterIcon = itemView.findViewById(R.id.ivSitterIcon);
        }
    }
}