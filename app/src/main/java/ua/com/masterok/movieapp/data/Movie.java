package ua.com.masterok.movieapp.data;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "movies")
public class Movie {

    // поля створюю на основі JSON'а, який повертається, обираючи всі які будуть потрібні для додатка
    // унікальний ід потрібен для того, щоб при перемиканні сортування фільмів (top Rated - popularity)
    // список фільмів не пригав, а показувався в тому ж порядку в якому додавався до БД
    // якщо @PrimaryKey поставити в поля id, тоді при перемиканні сортування фільми змінюють свої позиції
    // так як сортуються по id
    @PrimaryKey (autoGenerate = true)
    private int uniqueId;
    private int id;
    private int voteCount;
    private String title;
    private String originalTitle;
    private String overview;
    private String posterPath;
    private String bigPosterPath;
    private String backDropPath;
    private double voteAverage;
    private String releaseDate;

    public Movie(int uniqueId, int id, int voteCount, String title, String originalTitle, String overview, String posterPath, String bigPosterPath, String backDropPath, double voteAverage, String releaseDate) {
        this.uniqueId = uniqueId;
        this.id = id;
        this.voteCount = voteCount;
        this.title = title;
        this.originalTitle = originalTitle;
        this.overview = overview;
        this.posterPath = posterPath;
        this.bigPosterPath = bigPosterPath;
        this.backDropPath = backDropPath;
        this.voteAverage = voteAverage;
        this.releaseDate = releaseDate;
    }

    // помічаємо анотацією ігнор, так у БД має бути тільки один конструктор, який ініціалізує всі поля (він вже є вище)
    @Ignore
    // так як uniqueId гененрується автоматично, потрібен конструктор без цього параметру
    // цей конструктор потрбен щоб ми самі могли створювати об'єкти цього класу
    public Movie(int id, int voteCount, String title, String originalTitle, String overview, String posterPath, String bigPosterPath, String backDropPath, double voteAverage, String releaseDate) {
        this.id = id;
        this.voteCount = voteCount;
        this.title = title;
        this.originalTitle = originalTitle;
        this.overview = overview;
        this.posterPath = posterPath;
        this.bigPosterPath = bigPosterPath;
        this.backDropPath = backDropPath;
        this.voteAverage = voteAverage;
        this.releaseDate = releaseDate;
    }

    public int getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(int uniqueId) {
        this.uniqueId = uniqueId;
    }

    public int getId() {
        return id;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public String getTitle() {
        return title;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public String getOverview() {
        return overview;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public String getBigPosterPath() {
        return bigPosterPath;
    }

    public String getBackDropPath() {
        return backDropPath;
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public void setBigPosterPath(String bigPosterPath) {
        this.bigPosterPath = bigPosterPath;
    }

    public void setBackDropPath(String backDropPath) {
        this.backDropPath = backDropPath;
    }

    public void setVoteAverage(double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }
}
