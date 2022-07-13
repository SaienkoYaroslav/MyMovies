package ua.com.masterok.movieapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ua.com.masterok.movieapp.R;
import ua.com.masterok.movieapp.data.Trailer;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder> {

    private ArrayList<Trailer> trailers;
    private OnTrailerClickListener onTrailerClickListener;

    public void setTrailers(ArrayList<Trailer> trailers) {
        this.trailers = trailers;
        notifyDataSetChanged();
    }


    public interface OnTrailerClickListener {
        void onTrailerClick(String url);
    }
    public void setOnTrailerClickListener(OnTrailerClickListener onTrailerClickListener) {
        this.onTrailerClickListener = onTrailerClickListener;
    }

    @NonNull
    @Override
    public TrailerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trailer_item, parent, false);
        return new TrailerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrailerViewHolder holder, int position) {
        Trailer trailer = trailers.get(position);
        holder.tvNameOfVideo.setText(trailer.getName());
    }

    @Override
    public int getItemCount() {
        return trailers.size();
    }

    class TrailerViewHolder extends RecyclerView.ViewHolder {

        private TextView tvNameOfVideo;

        public TrailerViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNameOfVideo = itemView.findViewById(R.id.text_view_name_of_video);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onTrailerClickListener != null) {
                        onTrailerClickListener.onTrailerClick(trailers.get(getAdapterPosition()).getKey());
                    }
                }
            });
        }
    }

}
