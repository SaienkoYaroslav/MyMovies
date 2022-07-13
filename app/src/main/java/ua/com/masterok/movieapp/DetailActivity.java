package ua.com.masterok.movieapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

import ua.com.masterok.movieapp.adapters.ReviewAdapter;
import ua.com.masterok.movieapp.adapters.TrailerAdapter;
import ua.com.masterok.movieapp.data.FavouriteMovie;
import ua.com.masterok.movieapp.data.MainViewModel;
import ua.com.masterok.movieapp.data.Movie;
import ua.com.masterok.movieapp.data.Review;
import ua.com.masterok.movieapp.data.Trailer;
import ua.com.masterok.movieapp.utils.JSONUtils;
import ua.com.masterok.movieapp.utils.NetworkUtils;

public class DetailActivity extends AppCompatActivity {

    private ImageView ivBigPoster, ivAddToFavorite;
    private TextView tvTitle, tvOriginalTitle, tvRating, tvRealiseDate, tvOverview;
    private MainViewModel viewModel;
    private RecyclerView rvTrailers, rvReviews;
    private ScrollView scrollViewInfo;

    private static String language;

    private ReviewAdapter reviewAdapter;
    private TrailerAdapter trailerAdapter;

    private int id;
    private Movie movie;
    private FavouriteMovie favouriteMovie;

    // для відображення меню, потрібно перевизначити цей метод
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Спочатку отримуємо менюІнфлейтер
        MenuInflater inflater = getMenuInflater();
        // надуваємо наше меню
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // для реагування на натискання на пункти меню потрібно перевизначити цей метод
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch(id) {
            case R.id.item_main:
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                break;
            case R.id.item_favourite:
                Intent intentToFavourite = new Intent(getApplicationContext(), FavouriteActivity.class);
                startActivity(intentToFavourite);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        init();
        intent();
        adapter();
        onClickChangeFavorite();

    }

    void adapter() {
        rvReviews.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        rvTrailers.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        rvReviews.setAdapter(reviewAdapter);
        rvTrailers.setAdapter(trailerAdapter);
        trailerAdapter.setOnTrailerClickListener(new TrailerAdapter.OnTrailerClickListener() {
            @Override
            public void onTrailerClick(String url) {
                Intent intentToTrailer = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intentToTrailer);
            }
        });
        JSONObject jsonObjectTrailers = NetworkUtils.getJSONForVideos(movie.getId(), language);
        JSONObject jsonObjectReviews = NetworkUtils.getJSONForReviews(movie.getId(), language);
        ArrayList<Trailer> trailers = JSONUtils.getTrailersFromJSON(jsonObjectTrailers);
        ArrayList<Review> reviews = JSONUtils.getReviewsFromJSON(jsonObjectReviews);
        trailerAdapter.setTrailers(trailers);
        reviewAdapter.setReviews(reviews);
        // перемотуємо скролВью на початок
        scrollViewInfo.smoothScrollTo(0,0);
    }

    void intent() {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("id")) {
            id = intent.getIntExtra("id", -1);
        } else {
            finish();
        }
        movie = viewModel.getMovieById(id);
        Picasso.get().load(movie.getBigPosterPath()).placeholder(R.drawable.place_holder_movie).into(ivBigPoster);
        tvTitle.setText(movie.getTitle());
        tvOriginalTitle.setText(movie.getOriginalTitle());
        tvOverview.setText(movie.getOverview());
        tvRealiseDate.setText(movie.getReleaseDate());
        tvRating.setText(Double.toString(movie.getVoteAverage()));
        setFavouriteMovie();
    }

    void init() {
        ivBigPoster = findViewById(R.id.image_view_detail_activity_big_poster);
        ivAddToFavorite = findViewById(R.id.image_view_add_to_favorite);
        tvTitle = findViewById(R.id.text_view_title);
        tvOriginalTitle = findViewById(R.id.text_view_original_title);
        tvRating = findViewById(R.id.text_view_rating);
        tvRealiseDate = findViewById(R.id.text_view_realise_date);
        tvOverview = findViewById(R.id.text_view_overview);
        rvReviews = findViewById(R.id.recycler_view_reviews);
        rvTrailers = findViewById(R.id.recycler_view_trailers);
        scrollViewInfo = findViewById(R.id.scroll_view_info);

        language = Locale.getDefault().getLanguage();

        viewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()).create(MainViewModel.class);
        reviewAdapter = new ReviewAdapter();
        trailerAdapter = new TrailerAdapter();
    }

    void onClickChangeFavorite() {
        ivAddToFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (favouriteMovie == null) {
                    viewModel.insertFavouriteMovie(new FavouriteMovie(movie));
                    Toast.makeText(DetailActivity.this, R.string.added_to_favourite, Toast.LENGTH_SHORT).show();
                } else {
                    viewModel.deleteFavouriteMovie(favouriteMovie);
                    Toast.makeText(DetailActivity.this, R.string.deleted_from_favourite, Toast.LENGTH_SHORT).show();
                }
                setFavouriteMovie();
            }
        });
    }

    private void setFavouriteMovie() {
        favouriteMovie = viewModel.getFavouriteMovieById(id);
        if (favouriteMovie == null) {
            ivAddToFavorite.setImageResource(R.drawable.favourite_add_to);
        } else {
            ivAddToFavorite.setImageResource(R.drawable.favourite_remove);
        }
    }

}