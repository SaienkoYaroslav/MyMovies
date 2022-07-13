package ua.com.masterok.movieapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ua.com.masterok.movieapp.adapters.MovieAdapter;
import ua.com.masterok.movieapp.data.FavouriteMovie;
import ua.com.masterok.movieapp.data.MainViewModel;
import ua.com.masterok.movieapp.data.Movie;

public class FavouriteActivity extends AppCompatActivity {

    private RecyclerView rvFavouriteMovies;
    private MovieAdapter adapter;
    private MainViewModel viewModel;

    // для відображення меню, потрбно перевизначити цей метод
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
        setContentView(R.layout.activity_favourite);
        init();
        adapter();
    }

    void adapter() {
        rvFavouriteMovies.setLayoutManager(new GridLayoutManager(this, 2));
        rvFavouriteMovies.setAdapter(adapter);
        LiveData<List<FavouriteMovie>> favouriteMoviesFromLD = viewModel.getFavouriteMovies();
        favouriteMoviesFromLD.observe(this, new Observer<List<FavouriteMovie>>() {
            @Override
            public void onChanged(List<FavouriteMovie> favouriteMovies) {
                List<Movie> movies = new ArrayList<>();
                if (favouriteMovies != null) {
                    movies.addAll(favouriteMovies);
                    // параметром не можна передати favouriteMovies з LiveDat'и, хоч це і спадкоємець Movie
                    // через це робиться абракадабра вище
                    adapter.setMovies(movies);
                }
            }
        });
        adapter.setOnPosterClickListener(new MovieAdapter.OnPosterClickListener() {
            @Override
            public void onPosterClick(int position) {
                Movie movie = adapter.getMovies().get(position);
                Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
                intent.putExtra("id", movie.getId());
                startActivity(intent);
            }
        });
    }

    void init() {
        rvFavouriteMovies = findViewById(R.id.recycler_view_favourite_movies);
        adapter = new MovieAdapter();
        viewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()).create(MainViewModel.class);
    }
}