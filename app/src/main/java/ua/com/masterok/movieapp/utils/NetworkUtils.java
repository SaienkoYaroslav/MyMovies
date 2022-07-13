package ua.com.masterok.movieapp.utils;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

// тут будемо працювати з мережею
public class NetworkUtils {

    private static final String BASE_URL = "https://api.themoviedb.org/3/discover/movie";
    // трейлери
    private static final String BASE_URL_VIDEOS = "https://api.themoviedb.org/3/movie/%s/videos";
    // відгуки
    private static final String BASE_URL_REVIEWS = "https://api.themoviedb.org/3/movie/%s/reviews";

    // параметри для запроса
    private static final String PARAMS_API_KEY = "api_key";
    private static final String PARAMS_LANGUAGE = "language";
    private static final String PARAMS_SORT_BY = "sort_by";
    private static final String PARAMS_PAGE = "page";
    private static final String PARAMS_MIN_VOTE_COUNT = "vote_count.gte";

    // значення для запроса
    private static final String API_KEY = "02bc9e70c1d80084eda5bc8e98296211";
    private static final String SORT_BY_POPULARITY = "popularity.desc";
    private static final String SORT_BY_TOP_RATED = "vote_average.desc";
    private static final String MIN_VOTE_COUNT_VALUE = "1000";

    // змінні для розуміння яке сортування використати
    public static final int POPULARITY = 0;
    public static final int TOP_RATED = 1;

    // метод який формує запрос
    // Uri uri = Uri.parse(BASE_URL).buildUpon() - отримали строку як адресу до якої можна прикріпляти параметри
    // .appendQueryParameter() - за допомогою цього методу прикріпляємо параметри (параметр, значення)
    public static URL buildUrl(int sortBy, int page, String language) {
        URL result = null;
        String methodOfSort;
        if (sortBy == POPULARITY) {
            methodOfSort = SORT_BY_POPULARITY;
        } else {
            methodOfSort = SORT_BY_TOP_RATED;
        }
        Uri uri = Uri.parse(BASE_URL).buildUpon()
                .appendQueryParameter(PARAMS_API_KEY, API_KEY)
                .appendQueryParameter(PARAMS_LANGUAGE, language)
                .appendQueryParameter(PARAMS_SORT_BY, methodOfSort)
                .appendQueryParameter(PARAMS_MIN_VOTE_COUNT, MIN_VOTE_COUNT_VALUE)
                .appendQueryParameter(PARAMS_PAGE, Integer.toString(page))
                .build();
        try {
            result = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return result;
    }

    // метод для формування запросу для відгуків
    public static URL buildUrlToReviews(int id, String language) {
        Uri uri = Uri.parse(String.format(BASE_URL_REVIEWS, id)).buildUpon()
                .appendQueryParameter(PARAMS_LANGUAGE, language)
                .appendQueryParameter(PARAMS_API_KEY, API_KEY)
                .build();
        try {
            return new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // отримуємо JSON з відгуками
    public static JSONObject getJSONForReviews(int id, String language) {
        JSONObject result = null;
        URL url = buildUrlToReviews(id, language);
        try {
            result = new JSONLoadTAsk().execute(url).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }

    // метод для формування запросу для трейлерів
    public static URL buildUrlToVideos(int id, String language) {
        Uri uri = Uri.parse(String.format(BASE_URL_VIDEOS, id)).buildUpon()
                .appendQueryParameter(PARAMS_API_KEY, API_KEY)
                .appendQueryParameter(PARAMS_LANGUAGE, language)
                .build();
        try {
            return new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // отримуємо JSON з трейлерами
    public static JSONObject getJSONForVideos(int id, String language) {
        JSONObject result = null;
        URL url = buildUrlToVideos(id, language);
        try {
            result = new JSONLoadTAsk().execute(url).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }

    // отримуємо JSON з фільмами
    public static JSONObject getJSONFromNetwork(int sortBy, int page, String language) {
        JSONObject result = null;
        URL url = buildUrl(sortBy, page, language);
        try {
            result = new JSONLoadTAsk().execute(url).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }

    // Асінк таск лоадер потрібен замість звичайного асінк таску, тому що коли ми завантажуємо дані з інтернету
    // через звичайний асінк таск і користувач перевиртає екран, завантаження переривається і програма може крашнутись
    // public тому що потрібен доступ з MainActivity
    // в <> вказується тип який повертає клас
    public static class JSONLoader extends AsyncTaskLoader<JSONObject> {

        // посилання в завантажувач прийнято передавати в бандлі
        private Bundle bundle;

        private OnStartLoadingListener onStartLoadingListener;

        // слухач щоб розуміти коли почалось завантаження, потрібен для метода onReachEnd() в адаптері
        public interface OnStartLoadingListener {
            void onStartLoading();
        }

        public void setOnStartLoadingListener(OnStartLoadingListener onStartLoadingListener) {
            this.onStartLoadingListener = onStartLoadingListener;
        }

        // передаємо бандл в конструктор
        public JSONLoader(@NonNull Context context, Bundle bundle) {
            super(context);
            this.bundle = bundle;
        }

        // для того щоб при ініціалізація завантажувача відбувалось завантаження, необхідно перевизначити метод
        // onStartLoading() і в ньому викликати метод forceLoad();
        @Override
        protected void onStartLoading() {
            super.onStartLoading();
            // додаємо лістенер
            if (onStartLoadingListener != null) {
                onStartLoadingListener.onStartLoading();
            }
                forceLoad();
        }

        // в цьому методі потрібно отримати ЮРЛ звідки ми хочемо завантажити дані
        @Nullable
        @Override
        public JSONObject loadInBackground() {
            // перевірка на нал
            if (bundle == null) {
                return null;
            }
            // спочатку отримаємо ЮРЛ як строку
            String urlAsString = bundle.getString("url");
            // отримуємо об'єкт ЮРЛ
            URL url = null;
            try {
                url = new URL(urlAsString);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            // далі отримуємо дані, так само як і в асінк таск
            JSONObject result = null;
            if (url == null) {
                return null;
            }
            StringBuilder stringBuilder = new StringBuilder();
            HttpURLConnection connection = null;
            try {
                connection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = connection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();
                while (line != null) {
                    stringBuilder.append(line);
                    line = reader.readLine();
                }
                result = new JSONObject(stringBuilder.toString());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
            return result;
        }



    }


    // асінкТаск для методів, які завантажують JSON'и з інтернету
    private static class JSONLoadTAsk extends AsyncTask<URL, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(URL... urls) {
            JSONObject result = null;
            if (urls == null || urls.length == 0) {
                return result;
            }
            StringBuilder stringBuilder = new StringBuilder();
            HttpURLConnection connection = null;
            try {
                connection = (HttpURLConnection) urls[0].openConnection();
                InputStream inputStream = connection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();
                while (line != null) {
                    stringBuilder.append(line);
                    line = reader.readLine();
                }
                result = new JSONObject(stringBuilder.toString());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
            return result;
        }
    }

}
