package ua.com.masterok.movieapp.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

// entities = {Movie.class, FavouriteMovies.class} - в нас дві таблиці
// Версія 2, тому що БД змінилась коли ми додали другу таблицю, так як аппка вже запускалась з однією таблицею
@Database(entities = {Movie.class, FavouriteMovie.class}, version = 3, exportSchema = false)
public abstract class MovieDatabase extends RoomDatabase {

    private static final String DB_NAME = "movies.db";
    private static MovieDatabase database;

    private static final Object LOCK = new Object();

    public static MovieDatabase getInstance(Context context) {
        synchronized (LOCK) {
            if (database == null) {
                // .fallbackToDestructiveMigration() - видаляє всі дані і записує їх заново, потрібно робити коли змінюється версія БД
                database = Room.databaseBuilder(context, MovieDatabase.class, DB_NAME).fallbackToDestructiveMigration().build();
            }
        }
        return database;
    }

    public abstract MovieDao movieDao();

}
