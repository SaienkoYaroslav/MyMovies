package ua.com.masterok.movieapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import ua.com.masterok.movieapp.R;
import ua.com.masterok.movieapp.data.Movie;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private List<Movie> movies;
    private OnPosterClickListener onPosterClickListener;

    private OnReachEndListener onReachEndListener;

    public MovieAdapter() {
        movies = new ArrayList<>();
    }

    public interface OnPosterClickListener {
        void onPosterClick(int position);
    }

    // слухач який буде говорити нам коли ми долистали до кінця, щоб ми могли почати завантажувати наступну сторінку
    public interface OnReachEndListener {
        void onReachEnd();
    }

    public void setOnPosterClickListener(OnPosterClickListener onPosterClickListener) {
        this.onPosterClickListener = onPosterClickListener;
    }

    public void setOnReachEndListener(OnReachEndListener onReachEndListener) {
        this.onReachEndListener = onReachEndListener;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_item, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        // підгружаємо дані в ресайклВтю з наступної сторінки сайту
        // ставимо -4, щоб дані починали завантажуватись завчасно
        if (movies.size() >= 20 && position == movies.size() - 4 && onReachEndListener != null) {
            onReachEndListener.onReachEnd();
        }

        Movie movie = movies.get(position);
        // робота з Пікассо
        Picasso.get().load(movie.getPosterPath()).into(holder.ivSmallPoster);
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    class MovieViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivSmallPoster;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            ivSmallPoster = itemView.findViewById(R.id.image_view_small_poster);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onPosterClickListener != null) {
                        onPosterClickListener.onPosterClick(getAdapterPosition());
                    }
                }
            });

        }
    }

    public void clear() {
        this.movies.clear();
        notifyDataSetChanged();
    }

    public void setMovies(List<Movie> movies) {
        this.movies = movies;
        notifyDataSetChanged();
    }

    public List<Movie> getMovies() {
        return movies;
    }

    // щоб при прокручуванні фільми додавались а не замінювались
    public void addMovies(List<Movie> movies) {
        this.movies.addAll(movies);
        notifyDataSetChanged();

    }

}
