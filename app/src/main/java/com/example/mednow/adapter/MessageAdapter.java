package com.example.mednow.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mednow.R;
import com.example.mednow.model.Chat;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;
import java.util.Objects;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private static final int MSG_TYPE_SENDER = 0;
    private static final int MSG_TYPE_RECEIVER = 1;
    private LayoutInflater layoutInflater;
    private List<Chat> chats;

    public MessageAdapter(Context context, List<Chat> chats) {
        this.layoutInflater = LayoutInflater.from(context);
        this.chats = chats;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(viewType == MSG_TYPE_SENDER ? layoutInflater.inflate(R.layout.card_chemist_chat_sender,parent,false) : layoutInflater.inflate(R.layout.card_chemist_chat_receiver,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.textViewMsg.setText(chats.get(position).getMessage());
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView textViewMsg;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewMsg = itemView.findViewById(R.id.customer_chat_text_view_show_msg);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return chats.get(position).getSenderId().equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()) ? MSG_TYPE_SENDER : MSG_TYPE_RECEIVER;
    }
}
