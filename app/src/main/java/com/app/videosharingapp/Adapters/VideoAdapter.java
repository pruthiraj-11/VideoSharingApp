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
        holder.binding.videoView.setVideoURI(uri);
//        holder.binding.usernameid.setText(list.get(position).getUsername());

//        Picasso.get().lo

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
            } else {
                holder.binding.videoView.resume();
                isPlaying=true;
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
