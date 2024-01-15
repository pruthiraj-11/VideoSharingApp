package com.app.videosharingapp.Adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.videosharingapp.Models.VideoModel;
import com.app.videosharingapp.R;
import com.app.videosharingapp.databinding.VideoItemsBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoHolder> {

    Context context;
    ArrayList<VideoModel> list;
    boolean isPlaying=false;
    public VideoAdapter(Context context, ArrayList<VideoModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public VideoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.video_items,parent,false);
        return new VideoHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoHolder holder, int position) {
        Uri uri = Uri.parse(list.get(position).getVideoURL());
        holder.binding.usernameid.setText(list.get(position).getUsername());
        holder.binding.videoView.setVideoURI(uri);

        Picasso.get().load(list.get(position).getProfileURL()).centerCrop().placeholder(R.drawable.ic_action_name).into(holder.binding.userprofilepic);

        holder.binding.videoView.setOnPreparedListener(mediaPlayer -> {
            mediaPlayer.start();
            isPlaying=true;
        });

        holder.binding.videoView.setOnCompletionListener(mediaPlayer -> {
            mediaPlayer.start();
            isPlaying=true;
        });

        holder.binding.videoView.setOnClickListener(view -> {
            if(isPlaying){
                holder.binding.videoView.pause();
                isPlaying=false;
                holder.binding.playArrow.setVisibility(View.VISIBLE);
            } else {
                holder.binding.playArrow.setVisibility(View.GONE);
                holder.binding.videoView.resume();
                isPlaying=true;
            }
        });
        holder.binding.likebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.binding.likescount.setText(String.valueOf(Integer.parseInt((String) holder.binding.likescount.getText())+1));

            }
        });
        holder.binding.dislikebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.binding.dislikecount.setText(String.valueOf(Integer.parseInt((String) holder.binding.dislikecount.getText())+1));
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class VideoHolder extends RecyclerView.ViewHolder{
        VideoItemsBinding binding;
        public VideoHolder(@NonNull View itemView) {
            super(itemView);
            binding=VideoItemsBinding.bind(itemView);
        }
    }
}
