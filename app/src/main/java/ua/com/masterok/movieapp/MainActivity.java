package ua.com.masterok.movieapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ua.com.masterok.movieapp.adapters.MovieAdapter;
import ua.com.masterok.movieapp.data.MainViewModel;
import ua.com.masterok.movieapp.data.Movie;
import ua.com.masterok.movieapp.utils.JSONUtils;
import ua.com.masterok.movieapp.utils.NetworkUtils;

// МейнАктивіті буде стежити за асінклоадером, його станами і тд. Для цього потрібно імплемінтувати інтерфейс
// implements LoaderManager.LoaderCallbacks<JSONObject>, де в <> передається тип який отримуємо з завантажувача
// І потрібно перевизначити три методи: onCreateLoader, onLoadFinished, onLoaderReset
public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<JSONObject> {

    private RecyclerView rvPosters;
    private MovieAdapter adapter;
    private Switch switchSort;
    private TextView tvTopRated;
    private TextView tvPopularity;
    private MainViewModel viewModel;
    private ProgressBar progressBar;

    private static String language;

    //поля для завантажувача
    private static final int LOADER_ID = 420; // параметр id для метода onCreateLoader(), значення може бути любим
    private LoaderManager loaderManager;

    // поля для підгрузки даних
    private static int page = 1;
    private static int methodOfSort;
    private static boolean isLoading = false; // завантажились дані чи ні

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
        switch (id) {
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
        setContentView(R.layout.activity_main);
        init();
        adapter();
        onClickSwitchSort();
        onClickTextViewTopRated();
        onClickTextViewPopularity();
    }

    void init() {
        rvPosters = findViewById(R.id.recycle_view_movies);
        switchSort = findViewById(R.id.switch_sort);
        tvTopRated = findViewById(R.id.text_view_top_rated);
        tvPopularity = findViewById(R.id.text_view_popularity);
        progressBar = findViewById(R.id.progress_bar_loading);

        // отримуємо мову яка зараз використовується на пристрої
        language = Locale.getDefault().getLanguage();

        viewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()).create(MainViewModel.class);
        loaderManager = LoaderManager.getInstance(this);
    }

    void adapter() {
        adapter = new MovieAdapter();
        rvPosters.setLayoutManager(new GridLayoutManager(getApplicationContext(), getColumnCount()));
        rvPosters.setAdapter(adapter);
        // щоб завантажились дані
        switchSort.setChecked(true);
        adapter.setOnPosterClickListener(new MovieAdapter.OnPosterClickListener() {
            @Override
            public void onPosterClick(int position) {
                Movie movie = adapter.getMovies().get(position);
                Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
                intent.putExtra("id", movie.getId());
                startActivity(intent);
            }
        });
        adapter.setOnReachEndListener(new MovieAdapter.OnReachEndListener() {
            @Override
            public void onReachEnd() {
                if (!isLoading) {
                    downloadData(methodOfSort, page);
                }
            }
        });
        LiveData<List<Movie>> moviesFromLiveData = viewModel.getMovies();
        moviesFromLiveData.observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(List<Movie> movies) {
                // якщо відсутній інтернет
                if (page == 1) {
                    adapter.setMovies(movies);
                }
            }
        });
    }

    void onClickSwitchSort() {
        switchSort.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                page = 1;
                setMethodOfSort(isChecked);
            }
        });
        switchSort.setChecked(false);
    }

    void onClickTextViewTopRated() {
        tvTopRated.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setMethodOfSort(true);
                switchSort.setChecked(true);
            }
        });
    }

    void onClickTextViewPopularity() {
        tvPopularity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setMethodOfSort(false);
                switchSort.setChecked(false);
            }
        });
    }

    private void setMethodOfSort(boolean isTopRated) {
        if (isTopRated) {
            tvTopRated.setTextColor(getResources().getColor(R.color.teal_200));
            tvPopularity.setTextColor(getResources().getColor(R.color.white));
            methodOfSort = NetworkUtils.TOP_RATED;
        } else {
            tvPopularity.setTextColor(getResources().getColor(R.color.teal_200));
            tvTopRated.setTextColor(getResources().getColor(R.color.white));
            methodOfSort = NetworkUtils.POPULARITY;
        }
        downloadData(methodOfSort, page);
    }

    // метод для запуску завантажувача
    private void downloadData(int methodOfSort, int page) {
        URL url = NetworkUtils.buildUrl(methodOfSort, page, language);
        Bundle bundle = new Bundle();
        bundle.putString("url", url.toString());
        // запуск завантажувача
        // .restartLoader() - перевіряє чи вже існує завантажувач, якщо не існує він його створить, викликавши метод
        // initLoader(), а якщо завантажувач вже є, він його перезапустить
        // слохач реалізован в МейнАктивіті через імпліментсЮ тому тритій параметр this
        loaderManager.restartLoader(LOADER_ID, bundle, this);
    }

    @NonNull
    @Override
    public Loader<JSONObject> onCreateLoader(int id, @Nullable Bundle args) {
        // створюємо завантажувач
        NetworkUtils.JSONLoader jsonLoader = new NetworkUtils.JSONLoader(this, args);
        jsonLoader.setOnStartLoadingListener(new NetworkUtils.JSONLoader.OnStartLoadingListener() {
            @Override
            public void onStartLoading() {
                progressBar.setVisibility(View.VISIBLE);
                //коли завантаження починається
                isLoading = true;
            }
        });
        return jsonLoader;
    }

    // дані які ми отримуємо по закінченню завантаження передаються в цей метод
    @Override
    public void onLoadFinished(@NonNull Loader<JSONObject> loader, JSONObject data) {
        // отримуємо з  JSONObject'a фільми (Movie)
        ArrayList<Movie> movies = JSONUtils.getMoviesFromJSON(data);
        // коли приходять нові дані, старі видаляються і масив заповнюється новими даними
        // а в адаптері вони додаються до попередніх даних
        if (movies != null && !movies.isEmpty()) {
            if (page == 1) {
                viewModel.deleteAllMovies();
                adapter.clear();
            }
            for (Movie movie : movies) {
                viewModel.insertMovie(movie);
            }
            adapter.addMovies(movies);
            page++;
        }
        // коли завантаження закінчилось
        isLoading = false;
        progressBar.setVisibility(View.INVISIBLE);
        // коли завантаження завершено потрібно знищити завантажувач
        loaderManager.destroyLoader(LOADER_ID);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<JSONObject> loader) {

    }

    // метод який визначає к-сть колонок в GridLayoutManager() в залежності від орієнтації екрану
    private int getColumnCount() {
        // Щоб вирахувати число колонок потрібно взяти ширину екрану в пікселях розділити на 185 і якщо це число більше ніж 2,
        // використовувати отримане значення, а якщо менше то 2

        // отримуємо об'єкт DisplayMetrics
        DisplayMetrics displayMetrics = new DisplayMetrics();
        // отримуємо хар-ки нашого екрану і поміщаємо їх в дісплейМетрікс
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        // displayMetrics.widthPixels - так ми отримуємо пікселі. Але нам потрібні апартононезалежні пікселі (dp)
        // для цього, пікселі потрібно розділити на щільність
        int width = (int) (displayMetrics.widthPixels / displayMetrics.density);
        // отримуємо к-сть колонок
        return width / 185 > 2 ? width / 185 : 2;
    }

}