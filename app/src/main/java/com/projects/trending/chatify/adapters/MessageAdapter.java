package com.projects.trending.chatify.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.projects.trending.chatify.R;
import com.projects.trending.chatify.models.Messages;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.projects.trending.chatify.activity.ChatActivity.rImage;
import static com.projects.trending.chatify.activity.ChatActivity.sImage;

public class MessageAdapter extends RecyclerView.Adapter {

    Context context;
    ArrayList<Messages> messagesArrayList ;

    int ITEM_SEND = 1 ;
    int ITEM_RECIVE = 2 ;

    public MessageAdapter(Context context, ArrayList<Messages> messagesArrayList) {
        this.context = context;
        this.messagesArrayList = messagesArrayList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == ITEM_SEND){
            View view = LayoutInflater.from(context).inflate(R.layout.sender_layout,parent,false);
            return new senderViewHolder(view);
        }
        else {
            View view = LayoutInflater.from(context).inflate(R.layout.reciver_layout,parent,false);
            return new reciverViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
      Messages messages = messagesArrayList.get(position);
      if(holder.getClass() == senderViewHolder.class){
        senderViewHolder viewHolder = (senderViewHolder) holder ;
        viewHolder.txtMessages.setText(messages.getMessage());

         // static variables are passing without referencing from Chat Activity
          Picasso.get().load(sImage).into(((senderViewHolder) holder).profileImage);

      }else{
          reciverViewHolder viewHolder = (reciverViewHolder) holder ;
          viewHolder.txtMessages.setText(messages.getMessage());
          Picasso.get().load(rImage).into(((reciverViewHolder) holder).profileImage);

      }
    }

    // no. of times adapter will loop
    @Override
    public int getItemCount() {
        return messagesArrayList.size();
    }

    // FOR GETTING CURRENT ITEM TYPE TO BE OF SENDER SIDE OR RECIVER SIDE
    @Override
    public int getItemViewType(int position) {
        Messages messages = messagesArrayList.get(position);
        if(FirebaseAuth.getInstance().getCurrentUser().getUid().equals(messages.getSenderId())){
            return ITEM_SEND ;
        }
        else{
            return ITEM_RECIVE ;
        }
    }



    static class senderViewHolder extends RecyclerView.ViewHolder {
        CircleImageView profileImage ;
        TextView txtMessages ;

        public senderViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImage = itemView.findViewById(R.id.chat_profile_image);
            txtMessages = itemView.findViewById(R.id.text_messages);

        }
    }

    static class reciverViewHolder extends  RecyclerView.ViewHolder {
        CircleImageView profileImage ;
        TextView txtMessages ;

        public reciverViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImage = itemView.findViewById(R.id.chat_profile_image);
            txtMessages = itemView.findViewById(R.id.text_messages);
        }
    }
}
