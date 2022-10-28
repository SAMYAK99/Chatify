package com.projects.trending.chatify.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.projects.trending.chatify.activity.ChatActivity;
import com.projects.trending.chatify.activity.HomeActivity;
import com.projects.trending.chatify.R;
import com.projects.trending.chatify.models.Users;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    Context homeActivity ;
    ArrayList<Users> userArrayList ;

    public UserAdapter(HomeActivity homeActivity, ArrayList<Users> userArrayList) {
        this.homeActivity = homeActivity ;
        this.userArrayList = userArrayList ;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(homeActivity).inflate(R.layout.item_user_row,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Users user = userArrayList.get(position);

            holder.userName.setText(user.getName());
            holder.userStatus.setText(user.getStatus());
            Picasso.get().load(user.getImageUri()).into(holder.userImage);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(homeActivity, ChatActivity.class);
                    intent.putExtra("name", user.getName());
                    intent.putExtra("ReciverImage", user.getImageUri());
                    intent.putExtra("uid", user.getUid());
                    homeActivity.startActivity(intent);

                }
            });
        }



    @Override
    public int getItemCount() {
        return userArrayList.size();
    }



    static class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView userImage ;
        TextView userName , userStatus ;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            userImage = itemView.findViewById(R.id.proImage);
            userName = itemView.findViewById(R.id.userName);
            userStatus = itemView.findViewById(R.id.userStatus);

        }
    }
}
