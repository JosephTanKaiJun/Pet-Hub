// ConversationAdapter.java
package com.example.pethub.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.pethub.R;
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
        holder.itemView.setOnClickListener(v -> listener.onConversationClick(conversation));
    }

    @Override
    public int getItemCount() { return conversations.size(); }

    static class ConversationViewHolder extends RecyclerView.ViewHolder {
        TextView tvSitterName, tvLastMessage, tvTimestamp;

        public ConversationViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSitterName = itemView.findViewById(R.id.tvSitterName);
            tvLastMessage = itemView.findViewById(R.id.tvLastMessage);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
        }
    }
}