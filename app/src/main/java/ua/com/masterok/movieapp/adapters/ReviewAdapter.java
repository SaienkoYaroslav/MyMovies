package ua.com.masterok.movieapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ua.com.masterok.movieapp.R;
import ua.com.masterok.movieapp.data.Review;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private ArrayList<Review> reviews;

    public void setReviews(ArrayList<Review> reviews) {
        this.reviews = reviews;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_item, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Review review = reviews.get(position);
        holder.tvAuthor.setText(review.getAuthor());
        holder.tvContent.setText(review.getContent());
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    class ReviewViewHolder extends RecyclerView.ViewHolder{

        private TextView tvAuthor, tvContent;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAuthor = itemView.findViewById(R.id.text_view_author);
            tvContent = itemView.findViewById(R.id.text_view_content);
        }
    }

}
