// ConversationAdapter.java
package com.example.pethub.chat;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.pethub.R;

import java.io.File;
import java.util.List;

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ConversationViewHolder> {
    private final List<Conversation> conversations;
    private final OnConversationClickListener listener;

    public interface OnConversationClickListener {
        void onConversationClick(Conversation conversation);
    }

    public ConversationAdapter(List<Conversation> conversations, OnConversationClickListener listener) {
        this.conversations = conversations;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ConversationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_conversation, parent, false);
        return new ConversationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ConversationViewHolder holder, int position) {
        Conversation conversation = conversations.get(position);
        holder.tvSitterName.setText(conversation.getSitterName());
        holder.tvLastMessage.setText(conversation.getLastMessage());
        holder.tvTimestamp.setText(conversation.getTimestamp());

        String photoUri = conversation.getPhotoUri();
        if (photoUri != null && !photoUri.isEmpty()) {
            // Construct the full file path
// Ensure the file path is correctly built using the stored filename
            File imageFile = new File(new File(holder.itemView.getContext().getFilesDir(), "profilepic"), photoUri);
            if (imageFile.exists()) {
                Glide.with(holder.itemView.getContext())
                        .load(Uri.fromFile(imageFile))
                        .placeholder(R.drawable.ic_profile)
                        .error(R.drawable.ic_profile)
                        .into(holder.ivProfilePic);
            } else {
                holder.ivProfilePic.setImageResource(R.drawable.ic_profile);
            }
        } else {
            holder.ivProfilePic.setImageResource(R.drawable.ic_profile);
        }

        holder.itemView.setOnClickListener(v -> listener.onConversationClick(conversation));
    }

    @Override
    public int getItemCount() { return conversations.size(); }

    static class ConversationViewHolder extends RecyclerView.ViewHolder {
        TextView tvSitterName, tvLastMessage, tvTimestamp;
        de.hdodenhof.circleimageview.CircleImageView ivProfilePic; // Add this

        public ConversationViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSitterName = itemView.findViewById(R.id.tvSitterName);
            tvLastMessage = itemView.findViewById(R.id.tvLastMessage);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
            ivProfilePic = itemView.findViewById(R.id.ivProfilePic); // Add this

        }
    }
}