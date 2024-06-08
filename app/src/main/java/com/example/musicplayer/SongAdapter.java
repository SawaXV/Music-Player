package com.example.musicplayer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {

    private final List<SongCard> data;
    private final LayoutInflater layoutInflater;
    private final ItemClickListener itemClickListener;

    public SongAdapter(Context context, List<SongCard> data, ItemClickListener itemClickListener){
        this.data = data;
        this.itemClickListener = itemClickListener;
        this.layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public SongAdapter.SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.song_card, parent, false);
        return new SongViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SongAdapter.SongViewHolder holder, int position) {
        holder.bind(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    public class SongViewHolder extends RecyclerView.ViewHolder{
        TextView textView;

        SongViewHolder(View itemView){
            super(itemView);
            textView = itemView.findViewById(R.id.songNameCard);

            itemView.setOnClickListener(view -> itemClickListener.onItemClick(textView.getText().toString()));
        }



        void bind(final SongCard songCard){
            textView.setText(songCard.songName);
        }
    }
}